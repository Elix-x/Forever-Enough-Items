package code.elix_x.mods.fei.client.gui.container;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Mouse;

import code.elix_x.excore.utils.client.gui.elements.ButtonGuiElement;
import code.elix_x.excore.utils.client.gui.elements.IGuiElement;
import code.elix_x.excore.utils.client.gui.elements.IGuiElementsHandler;
import code.elix_x.excore.utils.client.gui.elements.IntegralLogorithmicSliderGuiElement;
import code.elix_x.excore.utils.client.gui.elements.ListGuiElement;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.excore.utils.math.IntUtils;
import code.elix_x.mods.fei.container.ContainerFEIEffect;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class GuiFEIEffect extends GuiContainer implements IGuiElementsHandler<IGuiElement<GuiFEIEffect>> {

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
	private final ContainerFEIEffect container;
	ItemStack last;

	EffectsList list;
	IntegralLogorithmicSliderGuiElement<GuiFEIEffect> durationSlider;
	IntegralLogorithmicSliderGuiElement<GuiFEIEffect> amplifierSlider;
	Potion current;

	List<IGuiElement> elements = new ArrayList();

	public GuiFEIEffect(InventoryPlayer playerInv){
		super(new ContainerFEIEffect(playerInv));
		this.playerInventory = playerInv;
		this.container = (ContainerFEIEffect) this.inventorySlots;
	}

	@Override
	public void add(IGuiElement<GuiFEIEffect> element){
		elements.add(element);
	}

	@Override
	public IGuiElement<GuiFEIEffect> getFocused(){
		return null;
	}

	@Override
	public void setFocused(IGuiElement<GuiFEIEffect> element){

	}

	@Override
	public void looseFocus(){

	}

	@Override
	public void initGui(){
		super.initGui();
		elements.clear();
		int prevScroll = list != null ? list.getScrollDistance() : 0;
		list = new EffectsList("List", (this.width - this.xSize) / 2 + 60, (this.height - this.ySize) / 2 + 14, 108, 57);
		list.setScrollDistance(prevScroll);
		add(list);
		if(current != null){
			add(durationSlider);
			add(amplifierSlider);
		}
		int x = guiLeft + 17;
		int y = guiTop + 13;
		add(new ButtonGuiElement("Potion", x, y, 16, 16, 0, 0, ""){

			@Override
			public void drawGuiPostPost(IGuiElementsHandler handler, GuiScreen gui, int mouseX, int mouseY){
				if(inside(mouseX, mouseY))
					drawTooltipWithBackgroundTranslate(fontRendererObj, mouseX - guiLeft, mouseY - guiTop, false, true, "fei.gui.effect.potion");
			}

			@Override
			public void onButtonPressed(){
				super.onButtonPressed();
				GuiFEIEffect.this.mc.playerController.sendEnchantPacket(container.windowId, 0);
			}

		});
		add(new ButtonGuiElement("Splash", x + 16, y, 16, 16, 0, 0, ""){

			@Override
			public void drawGuiPostPost(IGuiElementsHandler handler, GuiScreen gui, int mouseX, int mouseY){
				if(inside(mouseX, mouseY))
					drawTooltipWithBackgroundTranslate(fontRendererObj, mouseX - guiLeft, mouseY - guiTop, false, true, "fei.gui.effect.splash");
			}

			@Override
			public void onButtonPressed(){
				super.onButtonPressed();
				GuiFEIEffect.this.mc.playerController.sendEnchantPacket(container.windowId, 1);
			}

		});
		add(new ButtonGuiElement("Lingering", x, y + 16, 16, 16, 0, 0, ""){

			@Override
			public void drawGuiPostPost(IGuiElementsHandler handler, GuiScreen gui, int mouseX, int mouseY){
				if(inside(mouseX, mouseY))
					drawTooltipWithBackgroundTranslate(fontRendererObj, mouseX - guiLeft, mouseY - guiTop, false, true, "fei.gui.effect.lingering");
			}

			@Override
			public void onButtonPressed(){
				super.onButtonPressed();
				GuiFEIEffect.this.mc.playerController.sendEnchantPacket(container.windowId, 2);
			}

		});
		add(new ButtonGuiElement("Apply", x + 16, y + 16, 16, 16, 0, 0, ""){

			@Override
			public void drawGuiPostPost(IGuiElementsHandler handler, GuiScreen gui, int mouseX, int mouseY){
				if(inside(mouseX, mouseY))
					drawTooltipWithBackgroundTranslate(fontRendererObj, mouseX - guiLeft, mouseY - guiTop, false, true, "fei.gui.effect.apply");
			}

			@Override
			public void onButtonPressed(){
				super.onButtonPressed();
				GuiFEIEffect.this.mc.playerController.sendEnchantPacket(container.windowId, 3);
			}

		});
		for(IGuiElement element : elements)
			element.initGui(this, this);
		container.dirty = false;
	}

	public void setDurationSlider(IntegralLogorithmicSliderGuiElement<GuiFEIEffect> durationSlider){
		elements.remove(this.durationSlider);
		this.durationSlider = durationSlider;
		if(this.durationSlider != null) add(this.durationSlider);
	}

	public void setAmplifierSlider(IntegralLogorithmicSliderGuiElement<GuiFEIEffect> amplifierSlider){
		elements.remove(this.amplifierSlider);
		this.amplifierSlider = amplifierSlider;
		if(this.amplifierSlider != null) add(this.amplifierSlider);
	}

	public void updateScreen(){
		super.updateScreen();
		if(container.dirty){
			initGui();
			container.dirty = false;
		}
	}

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
		for(IGuiElement element : elements){
			element.drawGuiPre(this, this, mouseX, mouseY);
		}
		for(IGuiElement element : elements){
			element.drawGuiPost(this, this, mouseX, mouseY);
		}
		list.drawGuiPre(this, this, mouseX, mouseY);
		list.drawGuiPost(this, this, mouseX, mouseY);
		if(durationSlider != null) durationSlider.drawGuiPost(this, this, mouseX, mouseY);
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		this.fontRendererObj.drawString(net.minecraft.util.text.translation.I18n.translateToLocal("container.brewing"), 12, 5, 4210752);
		this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
		for(IGuiElement element : elements){
			element.drawGuiPostPost(this, this, mouseX, mouseY);
		}
	}

	@Override
	public void handleMouseInput() throws IOException{
		if(Mouse.getEventDWheel() != 0)
			list.handleMouseEvent(this, this, Mouse.getEventX() * this.width / this.mc.displayWidth, this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1, Mouse.getEventDWheel());
		super.handleMouseInput();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException{
		for(IGuiElement element : elements)
			if(element.handleMouseEvent(this, this, mouseX, mouseY, true, mouseButton)) return;
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int mouseButton){
		for(IGuiElement element : elements)
			if(element.handleMouseEvent(this, this, mouseX, mouseY, false, mouseButton)) return;
		super.mouseReleased(mouseX, mouseY, mouseButton);
	}

	public class EffectsList extends ListGuiElement<GuiFEIEffect> {

		public EffectsList(String name, int xPos, int yPos, int width, int height){
			super(name, xPos, yPos, width, height, 19, 0, 0, new RGBA(0));
			clickTimeThreshold = Integer.MAX_VALUE;
			clickDistanceThreshold = 8;
			for(Potion potion : Potion.REGISTRY){
				add(new EnchantmenetListElement(potion));
			}
		}

		public class EnchantmenetListElement extends ListElement {

			private Potion potion;

			public EnchantmenetListElement(Potion potion){
				this.potion = potion;
			}

			@Override
			public void initGui(GuiFEIEffect handler, GuiScreen gui, int index, int x, int relY){

			}

			@Override
			public boolean handleMouseEvent(GuiFEIEffect handler, GuiScreen gui, int index, int x, int relY, int mouseX, int mouseY, boolean down, int key){
				if(inside(relY, mouseX, mouseY)){
					current = potion;
					setDurationSlider(new IntegralLogorithmicSliderGuiElement("Duration Slider", x + 55, EffectsList.this.getBottom() + 2, EffectsList.this.width / 2 - 2, 8, 0, 0, 2, 1, 0xFFF, container.effects.containsKey(potion) ? container.effects.get(potion).getLeft() : 1, true){

						@Override
						public int getValue(){
							return super.getValue();
						}

						@Override
						protected void checkSliderValue(){
							super.checkSliderValue();
							Pair p = container.effects.get(potion);
							container.effects.put(potion, new ImmutablePair(getValue(), p != null ? p.getRight() : 1));
							GuiFEIEffect.this.mc.playerController.sendEnchantPacket(container.windowId, 4 | (Potion.getIdFromPotion(potion) << 3) | ((int) (p != null ? p.getRight() : 1) << 11) | (getValue() << 19));
						}

					});
					setAmplifierSlider(new IntegralLogorithmicSliderGuiElement("Amplifier Slider", x + 1, EffectsList.this.getBottom() + 2, EffectsList.this.width / 2 - 2, 8, 0, 0, 2, 1, 255, container.effects.containsKey(potion) ? container.effects.get(potion).getRight() : 1, true){

						@Override
						public int getValue(){
							return super.getValue() - 1;
						}

						@Override
						protected void checkSliderValue(){
							super.checkSliderValue();
							Pair p = container.effects.get(potion);
							container.effects.put(potion, new ImmutablePair(p != null ? p.getLeft() : 1, getValue()));
							GuiFEIEffect.this.mc.playerController.sendEnchantPacket(container.windowId, 4 | (Potion.getIdFromPotion(potion) << 3) | (getValue() << 11) | ((int) (p != null ? p.getLeft() : 1) << 19));
						}

					});
					return true;
				} else return false;
			}

			@Override
			public void drawGuiPost(GuiFEIEffect handler, GuiScreen gui, int index, int x, int relY, int mouseX, int mouseY){
				int i = (GuiFEIEffect.this.width - GuiFEIEffect.this.xSize) / 2;
				int j = (GuiFEIEffect.this.height - GuiFEIEffect.this.ySize) / 2;
				int i1 = i + 60;
				int j1 = i1 + 20;
				gui.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

				if(current == potion){
					GuiFEIEffect.this.drawTexturedModalRect(i1, relY, 0, 185, 108, 19);
				} else if(inside(relY, mouseX, mouseY)){
					GuiFEIEffect.this.drawTexturedModalRect(i1, relY, 0, 204, 108, 19);
				} else{
					GuiFEIEffect.this.drawTexturedModalRect(i1, relY, 0, 166, 108, 19);
				}

				GuiFEIEffect.this.drawTexturedModalRect(i1 + 1, j + 15 + relY, 16, 223, 16, 16);

				Pair<Integer, Integer> p = container.effects.get(potion);
				int duration = p != null ? p.getLeft() : 0;
				int amplifier = p != null ? p.getRight() : 0;
				drawString(fontRendererObj, net.minecraft.util.text.translation.I18n.translateToLocal(potion.getName()) + " " + (amplifier > 0 ? (amplifier < 3999 ? IntUtils.translateIntToRoman(amplifier) : amplifier) : "-") + " " + (duration > 0 ? Potion.getPotionDurationString(new PotionEffect(potion, duration * 20), 1) : "-"), x + 1, relY + 1, new RGBA(1f, 1f, 1f));
			}

		}

	}

}
