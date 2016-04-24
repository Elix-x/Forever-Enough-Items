package code.elix_x.mods.fei.api.gui;

import java.io.IOException;

import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.Rectangle;

import code.elix_x.excore.utils.client.cursor.CursorHelper;
import code.elix_x.excore.utils.client.gui.BasicGuiScreen;
import code.elix_x.mods.fei.ForeverEnoughItemsBase;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class ElementPositionSizeSettingsGuiScreen extends BasicGuiScreen {

	public static final Cursor moveCursor;
	public static final Cursor resizeCursor;

	static {
		moveCursor = CursorHelper.createCursor(new ResourceLocation(ForeverEnoughItemsBase.MODID, "textures/cursors/move.png"));
		resizeCursor = CursorHelper.createCursor(new ResourceLocation(ForeverEnoughItemsBase.MODID, "textures/cursors/resize.png"));
	}

	protected Rectangle element;
	protected boolean resizeable;

	protected boolean moving;
	protected boolean resizing;
	protected int grabRelX;
	protected int grabRelY;
	protected int grabX;
	protected int grabY;

	public ElementPositionSizeSettingsGuiScreen(GuiScreen parent, Rectangle element, boolean resizeable){
		super(parent, 0, 0);
		this.element = element;
		this.resizeable = resizeable;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		drawBackground(0);
		if(moving){
			element.setX(Math.max(mouseX - grabRelX, 0));
			element.setY(Math.max(mouseY - grabRelY, 0));
			if(element.getX() + element.getWidth() > width) element.setX(element.getX() - (element.getX() + element.getWidth() - width));
			if(element.getY() + element.getHeight() > height) element.setY(element.getY() - (element.getY() + element.getHeight() - height));
			CursorHelper.setCursor(moveCursor);
		} else if(resizing){
			element.setWidth(Math.max(grabRelX + mouseX - grabX, 0));
			element.setHeight(Math.max(grabRelY + mouseY - grabY, 0));
			if(element.getX() + element.getWidth() > width) element.setWidth(width - element.getX());
			if(element.getY() + element.getHeight() > height) element.setHeight(height - element.getY());
			element.setWidth(checkSizeX(element.getWidth()));
			element.setHeight(checkSizeY(element.getHeight()));
			CursorHelper.setCursor(resizeCursor);
		} else if(element.contains(mouseX, mouseY)){
			Rectangle rr = new Rectangle(element);
			rr.setWidth(rr.getWidth() - 2);
			rr.setHeight(rr.getHeight() - 2);
			if(resizeable && !rr.contains(mouseX, mouseY)){
				CursorHelper.setCursor(resizeCursor);
			} else {
				CursorHelper.setCursor(moveCursor);
			}
		} else {
			CursorHelper.resetCursor();
		}
		drawElement();
	}

	@Override
	protected void onClose(){
		CursorHelper.resetCursor();
	}

	protected int checkSizeX(int prevSize){
		return prevSize;
	}

	protected int checkSizeY(int prevSize){
		return prevSize;
	}

	protected void drawElement(){

	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
		if(button == 0 && element.contains(mouseX, mouseY)){
			Rectangle rr = new Rectangle(element);
			rr.setWidth(rr.getWidth() - 2);
			rr.setHeight(rr.getHeight() - 2);
			if(resizeable && !rr.contains(mouseX, mouseY)){
				resizing = true;
			} else {
				moving = true;
			}
			grabRelX = mouseX - element.getX();
			grabRelY = mouseY - element.getY();
			grabX = mouseX;
			grabY = mouseY;
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button){
		if(button == 0){
			if(resizing){
				resizing = false;
				element.setWidth(Math.max(grabRelX + mouseX - grabX, 0));
				element.setHeight(Math.max(grabRelY + mouseY - grabY, 0));
				if(element.getX() + element.getWidth() > width) element.setWidth(width - element.getX());
				if(element.getY() + element.getHeight() > height) element.setHeight(height - element.getY());
				element.setWidth(checkSizeX(element.getWidth()));
				element.setHeight(checkSizeY(element.getHeight()));
			} else if(moving){
				moving = false;
				element.setX(Math.max(mouseX - grabRelX, 0));
				element.setY(Math.max(mouseY - grabRelY, 0));
				if(element.getX() + element.getWidth() > width) element.setX(element.getX() - (element.getX() + element.getWidth() - width));
				if(element.getY() + element.getHeight() > height) element.setY(element.getY() - (element.getY() + element.getHeight() - height));
			}
			grabRelX = -1;
			grabRelY = -1;
			grabX = -1;
			grabY = -1;
		}
	}

	@Override
	protected void keyTyped(char c, int key) throws IOException {
		int i = -1;
		if(key == Keyboard.KEY_NUMPAD1) i = 0;
		if(key == Keyboard.KEY_NUMPAD2) i = 1;
		if(key == Keyboard.KEY_NUMPAD3) i = 2;
		if(key == Keyboard.KEY_NUMPAD4) i = 3;
		if(key == Keyboard.KEY_NUMPAD5) i = 4;
		if(key == Keyboard.KEY_NUMPAD6) i = 5;
		if(key == Keyboard.KEY_NUMPAD7) i = 6;
		if(key == Keyboard.KEY_NUMPAD8) i = 7;
		if(key == Keyboard.KEY_NUMPAD9) i = 8;
		if(i != -1){
			switch(i / 3){
			case 0:
				element.setY(height - element.getHeight());
				break;
			case 1:
				element.setY((height - element.getHeight()) / 2);
				break;
			case 2:
				element.setY(0);
				break;
			}
			switch(i % 3){
			case 0:
				element.setX(0);
				break;
			case 1:
				element.setX((width - element.getWidth()) / 2);
				break;
			case 2:
				element.setX(width - element.getWidth());
				break;
			}
		} else {
			super.keyTyped(c, key);
		}
	}

}
