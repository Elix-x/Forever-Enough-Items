package at.feldim2425.moreoverlays.itemsearch;

import mezz.jei.api.*;
import mezz.jei.input.IKeyable;

import javax.annotation.Nonnull;

@JEIPlugin
public class JeiModule extends BlankModPlugin {

    public static IItemListOverlay overlay;
    public static IKeyable keyableOverlay;

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        if(jeiRuntime.getItemListOverlay() instanceof IKeyable){
            keyableOverlay = (IKeyable)jeiRuntime.getItemListOverlay();
        }
        overlay = jeiRuntime.getItemListOverlay();
    }
}
