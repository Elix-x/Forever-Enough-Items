package at.feldim2425.moreoverlays.itemsearch;

import mezz.jei.ItemFilter;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.input.IKeyable;

import javax.annotation.Nonnull;

@JEIPlugin
public class JeiModule implements IModPlugin {

    public static ItemFilter filter;
    public static IKeyable keyableOverlay;

    @Override
    public void register(@Nonnull IModRegistry registry) {
        filter = new ItemFilter(registry.getItemRegistry());
    }


    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        if(jeiRuntime.getItemListOverlay() instanceof IKeyable){
            keyableOverlay = (IKeyable)jeiRuntime.getItemListOverlay();
        }
    }
}
