package code.elix_x.mods.fei.client.gui.container;

import java.io.IOException;
import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.glu.Project;

import code.elix_x.excomms.color.RGBA;
import code.elix_x.excomms.math.IntUtils;
import code.elix_x.excore.utils.client.gui.elements.CheckBoxGuiElement;
import code.elix_x.excore.utils.client.gui.elements.GuiElement;
import code.elix_x.excore.utils.client.gui.elements.IGuiElement;
import code.elix_x.excore.utils.client.gui.elements.IGuiElementsHandler;
import code.elix_x.excore.utils.client.gui.elements.IntegralLogorithmicSliderGuiElement;
import code.elix_x.excore.utils.client.gui.elements.ListGuiElement;
import code.elix_x.mods.fei.container.ContainerFEIEnchantment;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GuiFEIEnchantment extends GuiContainer implements IGuiElementsHandler<IGuiElement<GuiFEIEnchantment>> {

	/** The ResourceLocation containing the Enchantment GUI texture location */
	private static final ResourceLocation ENCHANTMENT_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/enchanting_table.png");
	/**
	 * The ResourceLocation containing the texture for the Book rendered above the enchantment table
	 */
	private static final ResourceLocation ENCHANTMENT_TABLE_BOOK_TEXTURE = new ResourceLocation("textures/entity/enchanting_table_book.png");
	/**
	 * The ModelBook instance used for rendering the book on the Enchantment table
	 */
	private static final ModelBook MODEL_BOOK = new ModelBook();
	/** The player inventory currently bound to this GuiEnchantment instance. */
	private final InventoryPlayer playerInventory;
	/** A Random instance for use with the enchantment gui */
	private final Random random = new Random();
	private final ContainerFEIEnchantment container;
	public int ticks;
	public float flip;
	public float oFlip;
	public float flipT;
	public float flipA;
	public float open;
	public float oOpen;
	ItemStack last = ItemStack.EMPTY;

	CheckBoxGuiElement<GuiFEIEnchantment> checkbox;
	EnchantmentsList list;
	IntegralLogorithmicSliderGuiElement<GuiFEIEnchantment> slider;
	Enchantment current;

	public GuiFEIEnchantment(InventoryPlayer playerInv){
		super(new ContainerFEIEnchantment(playerInv));
		this.playerInventory = playerInv;
		this.container = (ContainerFEIEnchantment) this.inventorySlots;
	}

	@Override
	public void add(IGuiElement<GuiFEIEnchantment> element){

	}

	@Override
	public IGuiElement<GuiFEIEnchantment> getFocused(){
		return null;
	}

	@Override
	public void setFocused(IGuiElement<GuiFEIEnchantment> element){

	}

	@Override
	public void looseFocus(){

	}

	@Override
	public void initGui(){
		super.initGui();
		checkbox = new CheckBoxGuiElement("Checkbox", guiLeft + 35, guiTop + 47, 16, 16, 0, 0, container.lock){

			@Override
			public void setChecked(boolean checked){
				super.setChecked(checked);
				container.lock = this.checked;
				mc.playerController.sendEnchantPacket(container.windowId, this.checked ? 1 : 0);
			}

		};
		int prevScroll = list != null ? list.getScrollDistance() : 0;
		list = new EnchantmentsList("List", (this.width - this.xSize) / 2 + 60, (this.height - this.ySize) / 2 + 14, 108, 57);
		list.setScrollDistance(prevScroll);
		list.initGui(this, this);
	}

	public void updateScreen(){
		super.updateScreen();
		this.tickBook();
		if(container.dirty){
			initGui();
			container.dirty = false;
		}
	}

	public void tickBook(){
		ItemStack itemstack = this.inventorySlots.getSlot(0).getStack();

		if(!ItemStack.areItemStacksEqual(itemstack, this.last)){
			this.last = itemstack;

			while(true){
				this.flipT += (float) (this.random.nextInt(4) - this.random.nextInt(4));

				if(this.flip > this.flipT + 1.0F || this.flip < this.flipT - 1.0F){
					break;
				}
			}
		}

		++this.ticks;
		this.oFlip = this.flip;
		this.oOpen = this.open;

		this.open += 0.2F;

		this.open = MathHelper.clamp(this.open, 0.0F, 1.0F);
		float f1 = (this.flipT - this.flip) * 0.4F;
		float f = 0.2F;
		f1 = MathHelper.clamp(f1, -0.2F, 0.2F);
		this.flipA += (f1 - this.flipA) * 0.9F;
		this.flip += this.flipA;
	}

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
		GlStateManager.pushMatrix();
		GlStateManager.matrixMode(5889);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		ScaledResolution scaledresolution = new ScaledResolution(this.mc);
		GlStateManager.viewport((scaledresolution.getScaledWidth() - 320) / 2 * scaledresolution.getScaleFactor(), (scaledresolution.getScaledHeight() - 240) / 2 * scaledresolution.getScaleFactor(), 320 * scaledresolution.getScaleFactor(), 240 * scaledresolution.getScaleFactor());
		GlStateManager.translate(-0.34F, 0.23F, 0.0F);
		Project.gluPerspective(90.0F, 1.3333334F, 9.0F, 80.0F);
		float f = 1.0F;
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.translate(0.0F, 3.3F, -16.0F);
		GlStateManager.scale(1.0F, 1.0F, 1.0F);
		float f1 = 5.0F;
		GlStateManager.scale(5.0F, 5.0F, 5.0F);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_BOOK_TEXTURE);
		GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
		float f2 = this.oOpen + (this.open - this.oOpen) * partialTicks;
		GlStateManager.translate((1.0F - f2) * 0.2F, (1.0F - f2) * 0.1F, (1.0F - f2) * 0.25F);
		GlStateManager.rotate(-(1.0F - f2) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
		float f3 = this.oFlip + (this.flip - this.oFlip) * partialTicks + 0.25F;
		float f4 = this.oFlip + (this.flip - this.oFlip) * partialTicks + 0.75F;
		f3 = (f3 - (float) MathHelper.fastFloor((double) f3)) * 1.6F - 0.3F;
		f4 = (f4 - (float) MathHelper.fastFloor((double) f4)) * 1.6F - 0.3F;

		if(f3 < 0.0F){
			f3 = 0.0F;
		}

		if(f4 < 0.0F){
			f4 = 0.0F;
		}

		if(f3 > 1.0F){
			f3 = 1.0F;
		}

		if(f4 > 1.0F){
			f4 = 1.0F;
		}

		GlStateManager.enableRescaleNormal();
		MODEL_BOOK.render((Entity) null, 0.0F, f3, f4, f2, 0.0F, 0.0625F);
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.matrixMode(5889);
		GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		list.drawGuiPre(this, this, mouseX, mouseY);
		checkbox.drawGuiPost(this, this, mouseX, mouseY);
		list.drawGuiPost(this, this, mouseX, mouseY);
		if(slider != null) slider.drawGuiPost(this, this, mouseX, mouseY);
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		this.fontRenderer.drawString(net.minecraft.util.text.translation.I18n.translateToLocal("container.enchant"), 12, 5, 4210752);
		this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);

		if(checkbox.inside(mouseX, mouseY))
			GuiElement.drawTooltipWithBackgroundTranslate(fontRenderer, mouseX - guiLeft, mouseY - guiTop, false, true, new RGBA(1f, 1f, 1f), "fei.gui.enchant.lock");
	}

	@Override
	public void handleMouseInput() throws IOException{
		if(Mouse.getEventDWheel() != 0)
			list.handleMouseEvent(this, this, Mouse.getEventX() * this.width / this.mc.displayWidth, this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1, Mouse.getEventDWheel());
		super.handleMouseInput();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException{
		if(!checkbox.handleMouseEvent(this, this, mouseX, mouseY, true, mouseButton))
			if(!list.handleMouseEvent(this, this, mouseX, mouseY, true, mouseButton))
				if(slider == null || !slider.handleMouseEvent(this, this, mouseX, mouseY, true, mouseButton))
					super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int mouseButton){
		if(slider == null || !slider.handleMouseEvent(this, this, mouseX, mouseY, false, mouseButton)){
			if(slider != null && !slider.inside(mouseX, mouseY)){
				current = null;
				slider = null;
			}
			if(!list.handleMouseEvent(this, this, mouseX, mouseY, false, mouseButton))
				super.mouseReleased(mouseX, mouseY, mouseButton);
		}
	}

	public class EnchantmentsList extends ListGuiElement<GuiFEIEnchantment> {

		public EnchantmentsList(String name, int xPos, int yPos, int width, int height){
			super(name, xPos, yPos, width, height, 19, 0, 0, new RGBA(0));
			clickTimeThreshold = Integer.MAX_VALUE;
			clickDistanceThreshold = 8;
			for(Enchantment enchantment : Enchantment.REGISTRY){
				add(new EnchantmenetListElement(enchantment));
			}
		}

		public class EnchantmenetListElement extends ListElement {

			private Enchantment enchantment;

			public EnchantmenetListElement(Enchantment enchantment){
				this.enchantment = enchantment;
			}

			@Override
			public void initGui(GuiFEIEnchantment handler, GuiScreen gui, int index, int x, int relY){

			}

			@Override
			public boolean handleMouseEvent(GuiFEIEnchantment handler, GuiScreen gui, int index, int x, int relY, int mouseX, int mouseY, boolean down, int key){
				if(inside(relY, mouseX, mouseY)){
					current = enchantment;
					slider = new IntegralLogorithmicSliderGuiElement("Slider", x + 1, EnchantmentsList.this.getBottom() + 2, EnchantmentsList.this.width - 2, 8, 0, 0, 2, 1, 255, container.enchantments.containsKey(enchantment) ? container.enchantments.get(enchantment) : 1, true){

						@Override
						public int getValue(){
							return super.getValue() - 1;
						}

						@Override
						protected void checkSliderValue(){
							super.checkSliderValue();
							container.enchantments.put(enchantment, getValue());
							GuiFEIEnchantment.this.mc.playerController.sendEnchantPacket(container.windowId, (getValue() << 16 | Enchantment.getEnchantmentID(enchantment)) + 2);
						}

					};
					return true;
				} else return false;
			}

			@Override
			public void drawGuiPost(GuiFEIEnchantment handler, GuiScreen gui, int index, int x, int relY, int mouseX, int mouseY){
				int i = (GuiFEIEnchantment.this.width - GuiFEIEnchantment.this.xSize) / 2;
				int j = (GuiFEIEnchantment.this.height - GuiFEIEnchantment.this.ySize) / 2;
				int i1 = i + 60;
				int j1 = i1 + 20;
				gui.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

				if(current == enchantment){
					GuiFEIEnchantment.this.drawTexturedModalRect(i1, relY, 0, 185, 108, 19);
				} else if(inside(relY, mouseX, mouseY)){
					GuiFEIEnchantment.this.drawTexturedModalRect(i1, relY, 0, 204, 108, 19);
				} else{
					GuiFEIEnchantment.this.drawTexturedModalRect(i1, relY, 0, 166, 108, 19);
				}

				GuiFEIEnchantment.this.drawTexturedModalRect(i1 + 1, j + 15 + relY, 16, 223, 16, 16);

				drawString(fontRenderer, net.minecraft.util.text.translation.I18n.translateToLocal(enchantment.getName()) + " " + (container.enchantments.containsKey(enchantment) && container.enchantments.get(enchantment) > 0 ? (container.enchantments.get(enchantment) < 3999 ? IntUtils.translateIntToRoman(container.enchantments.get(enchantment)) : container.enchantments.get(enchantment)) : "-"), x + 1, relY + 1, new RGBA(1f, 1f, 1f));
			}

		}

	}

}
