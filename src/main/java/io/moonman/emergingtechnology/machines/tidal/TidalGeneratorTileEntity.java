package io.moonman.emergingtechnology.machines.tidal;

import io.moonman.emergingtechnology.config.EmergingTechnologyConfig;
import io.moonman.emergingtechnology.handlers.EnergyStorageHandler;
import io.moonman.emergingtechnology.handlers.GeneratorEnergyStorageHandler;
import io.moonman.emergingtechnology.helpers.machines.TidalHelper;
import io.moonman.emergingtechnology.init.Reference;
import io.moonman.emergingtechnology.machines.MachineTileBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;
import li.cil.oc.api.network.SimpleComponent;

@Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "opencomputers")
public class TidalGeneratorTileEntity extends MachineTileBase implements ITickable, SimpleComponent {

    public EnergyStorageHandler energyHandler = new EnergyStorageHandler(Reference.TIDAL_ENERGY_CAPACITY, 1000,
            1000) {
        @Override
        public void onContentsChanged() {
            markDirty();
            super.onContentsChanged();
        }
    };

    public GeneratorEnergyStorageHandler generatorEnergyHandler = new GeneratorEnergyStorageHandler(energyHandler) {

    };

    private int tick = 0;
    private int energy = 0;

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY)
            return true;

        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY)
            return (T) this.generatorEnergyHandler;
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.energyHandler.readFromNBT(compound);

        this.setEnergy(compound.getInteger("GuiEnergy"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("GuiEnergy", this.getEnergy());
        this.energyHandler.writeToNBT(compound);

        return compound;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void update() {

        if (this.isClient()) {
            return;
        }

        generate();
        spreadEnergy();
    }

    public void generate() {
        if (TidalHelper.isGeneratorInWater(getWorld(), getPos())) {

            int energy = EmergingTechnologyConfig.ELECTRICS_MODULE.TIDALGENERATOR.tidalEnergyGenerated;

            if (TidalHelper.isGeneratorAtOptimalDepth(getPos())) {
                energy *= 2;
            }

            this.energyHandler.receiveEnergy(energy, false);
        }
    }

    private void spreadEnergy() {
        for (EnumFacing side : EnumFacing.VALUES) {
            TileEntity tileEntity = world.getTileEntity(pos.offset(side));

            if (tileEntity != null) {
                IEnergyStorage otherStorage = tileEntity.getCapability(CapabilityEnergy.ENERGY, side.getOpposite());

                if (otherStorage != null) {
                    if (otherStorage.canReceive()) {
                        if (this.getEnergy() > 0) {
                            int energySpread = otherStorage.receiveEnergy(this.getEnergy(), false);
                            this.energyHandler.extractEnergy(energySpread, false);
                        }
                    }
                }
            }
        }
    }

    // Getters

    public int getEnergy() {
        return this.energyHandler.getEnergyStored();
    }

    // Setters

    private void setEnergy(int quantity) {
        this.energy = quantity;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.pos) != this ? false
                : player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D,
                        (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    public int getField(int id) {
        switch (id) {
        case 0:
            return this.getEnergy();
        default:
            return 0;
        }
    }

    public void setField(int id, int value) {
        switch (id) {
        case 0:
            this.setEnergy(value);
            break;

        }
    }

    // OpenComputers

    @Optional.Method(modid = "opencomputers")
    @Override
    public String getComponentName() {
        return "etech_tidal_generator";
    }
}