package io.moonman.emergingtechnology.machines.processor;

import io.moonman.emergingtechnology.EmergingTechnology;
import io.moonman.emergingtechnology.config.EmergingTechnologyConfig;
import io.moonman.emergingtechnology.gui.GuiHelper;
import io.moonman.emergingtechnology.gui.GuiHelper.GuiIndicator;
import io.moonman.emergingtechnology.gui.GuiHelper.GuiPosition;
import io.moonman.emergingtechnology.init.Reference;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class ProcessorGui extends GuiContainer
{
	private static final ResourceLocation TEXTURES = new ResourceLocation(EmergingTechnology.MODID + ":textures/gui/processorgui.png");
	private final InventoryPlayer player;
	private final ProcessorTileEntity tileEntity;

	private String NAME = "Processor";

	private static final int XSIZE = 175;
	private static final int YSIZE = 165;

	// Standard positions for labels
	private static final GuiPosition TOP_LEFT_POS = GuiHelper.getTopLeft();
	private static final GuiPosition TOP_RIGHT_POS = GuiHelper.getTopRight(XSIZE, 44);
	private static final GuiPosition MIDDLE_RIGHT_POS = GuiHelper.getMiddleRight(XSIZE, 44);
	private static final GuiPosition INVENTORY_POS = GuiHelper.getInventory(YSIZE);
    
    // Draws textures on gui
	public ProcessorGui(InventoryPlayer player, ProcessorTileEntity tileEntity) 
	{
		super(new ProcessorContainer(player, tileEntity));
		this.player = player;
		this.tileEntity = tileEntity;
		this.xSize = XSIZE;
		this.ySize = YSIZE;
	}

	@Override
	public void initGui() {
		super.initGui();
	}

	@Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
    }
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) 
	{
		this.mc.getTextureManager().bindTexture(TEXTURES);

		int energy = this.getEnergyScaled(37);
		int fluid = this.getFluidScaled(37);
		int progress = this.getProgressScaled(34);

		this.drawTexturedModalRect(TOP_RIGHT_POS.x, TOP_RIGHT_POS.y, 176, 0, fluid, 7);
		this.drawTexturedModalRect(MIDDLE_RIGHT_POS.x, MIDDLE_RIGHT_POS.y, 176, 9, energy, 7);
		this.drawTexturedModalRect(39, 38, 176, 18, progress, 10);

		this.fontRenderer.drawString(NAME, TOP_LEFT_POS.x, TOP_LEFT_POS.y, GuiHelper.LABEL_COLOUR);
		this.fontRenderer.drawString(GuiHelper.inventoryLabel(this.player), INVENTORY_POS.x, INVENTORY_POS.y, GuiHelper.LABEL_COLOUR);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		this.mc.getTextureManager().bindTexture(TEXTURES);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
	}

	private int getEnergyScaled(int scaled)
    {
		return (int) (tileEntity.getField(0) * scaled / Reference.PROCESSOR_ENERGY_CAPACITY);
	}
	
	private int getFluidScaled(int scaled)
    {
		return (int) (tileEntity.getField(1) * scaled / Reference.PROCESSOR_FLUID_CAPACITY);
	}

	private int getProgressScaled(int scaled)
    {
		return (int) (tileEntity.getField(2) * scaled / EmergingTechnologyConfig.POLYMERS_MODULE.PROCESSOR.processorBaseTimeTaken);
	}
}