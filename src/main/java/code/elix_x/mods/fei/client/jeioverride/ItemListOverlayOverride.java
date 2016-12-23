package code.elix_x.mods.fei.client.jeioverride;

import java.util.List;

import code.elix_x.excomms.reflection.ReflectionHelper.AClass;
import code.elix_x.excomms.reflection.ReflectionHelper.AField;
import mezz.jei.ItemFilter;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.config.Config;
import mezz.jei.gui.GuiProperties;
import mezz.jei.gui.ItemListOverlay;
import mezz.jei.gui.ItemListOverlayInternal;
import net.minecraft.client.gui.GuiScreen;

public class ItemListOverlayOverride extends ItemListOverlay {

	public static final AField<ItemListOverlay, ItemListOverlayInternal> internal = new AClass<>(ItemListOverlay.class).<ItemListOverlayInternal>getDeclaredField("internal").setAccessible(true);

	private final IIngredientRegistry ingredientRegistry;

	private boolean canGiveItems;
	private boolean canDeleteItems;

	private int searchFieldWidth;
	private int searchFieldHeight;

	public ItemListOverlayOverride(ItemFilter itemFilter, List<IAdvancedGuiHandler<?>> advancedGuiHandlers, IIngredientRegistry ingredientRegistry, boolean canGiveItems, boolean canDeleteItems, int searchFieldWidth, int searchFieldHeight){
		super(itemFilter, advancedGuiHandlers, ingredientRegistry);
		this.ingredientRegistry = ingredientRegistry;
		this.canGiveItems = canGiveItems;
		this.canDeleteItems = canDeleteItems;
		this.searchFieldWidth = searchFieldWidth;
		this.searchFieldHeight = searchFieldHeight;
	}

	@Override
	public ItemListOverlayInternal create(GuiScreen guiScreen){
		if(Config.isOverlayEnabled()){
			GuiProperties guiProperties = GuiProperties.create(guiScreen);
			if(guiProperties != null){
				final int columns = ItemListOverlayInternal.getColumns(guiProperties);
				if(columns >= 4){
					if(getInternal() != null){
						close();
					}
					internal.set(this, new ItemListOverlayInternalOverride(this, ingredientRegistry, guiScreen, guiProperties, canGiveItems, canDeleteItems, searchFieldWidth, searchFieldHeight));
					return getInternal();
				}
			}
		}

		close();
		return null;
	}

}
