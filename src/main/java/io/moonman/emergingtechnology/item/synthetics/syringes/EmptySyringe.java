package io.moonman.emergingtechnology.item.synthetics.syringes;

import java.util.List;

import io.moonman.emergingtechnology.helpers.StackHelper;
import io.moonman.emergingtechnology.item.synthetics.SyringeItemBase;
import io.moonman.emergingtechnology.providers.ModTissueProvider;
import io.moonman.emergingtechnology.util.Lang;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EmptySyringe extends SyringeItemBase {

    private static final String _name = "empty";

    public EmptySyringe() {
        super(_name, "");
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced)
    {
        tooltip.add(Lang.get(Lang.EMPTY_SYRINGE_DESC));
        tooltip.add(Lang.get(Lang.INTERACT_RIGHT_CLICK));
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target,
            EnumHand hand) {

        if (playerIn.world.isRemote) {
            return false;
        }

        String entityId = EntityList.getKey(target).toString();

        ItemStack itemStack = ModTissueProvider.getSyringeItemStackByEntityId(entityId);

        if (!StackHelper.isItemStackEmpty(itemStack)) {
            ItemStack newItemStack = stack.copy();
            newItemStack.shrink(1);
            playerIn.setHeldItem(hand, newItemStack);
            playerIn.addItemStackToInventory(itemStack);
            return true;
        }

        return false;
    }

    public String getItemStackDisplayName(ItemStack stack)
    {
        return "Empty Tissue Syringe";
    }
}