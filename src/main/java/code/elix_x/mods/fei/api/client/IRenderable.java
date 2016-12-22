package code.elix_x.mods.fei.api.client;

import org.lwjgl.util.Rectangle;

import code.elix_x.excore.utils.client.gui.elements.GuiElement;
import code.elix_x.excore.utils.client.render.ItemStackRenderer;
import code.elix_x.excore.utils.color.RGBA;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;

public interface IRenderable {

	public void render(Rectangle element, Minecraft minecraft);

	public static class ResourceLocationRenderable implements IRenderable {

		private final ResourceLocation texture;

		public ResourceLocationRenderable(ResourceLocation texture){
			this.texture = texture;
		}

		@Override
		public void render(Rectangle element, Minecraft minecraft){
			minecraft.getTextureManager().bindTexture(texture);
			GuiElement.drawTexturedRect(element, new Vec2f(0, 0), new Vec2f(1, 1));
		}

	}

	public static class StringRenderable implements IRenderable {

		private final String text;
		private final FontRenderer fontRenderer;
		private final RGBA color;

		public StringRenderable(String text, FontRenderer fontRenderer, RGBA color){
			this.text = text;
			this.fontRenderer = fontRenderer;
			this.color = color;
		}

		public StringRenderable(String text, RGBA color){
			this(text, null, color);
		}

		@Override
		public void render(Rectangle element, Minecraft minecraft){
			FontRenderer fontRenderer = (this.fontRenderer != null ? this.fontRenderer : minecraft.fontRendererObj);
			fontRenderer.drawString(text, element.getX() + (element.getWidth() - fontRenderer.getStringWidth(text)) / 2, element.getY() + (element.getHeight() - 8) / 2, color.argb());
		}

	}

	public static class ItemStackRenderable implements IRenderable {

		private final ItemStack itemstack;

		public ItemStackRenderable(ItemStack itemstack){
			this.itemstack = itemstack;
		}

		@Override
		public void render(Rectangle element, Minecraft minecraft){
			ItemStackRenderer.renderItemStack(minecraft, element.getX() + (element.getWidth() - 16) / 2, element.getY() + (element.getHeight() - 16) / 2, itemstack);
		}

	}

}
