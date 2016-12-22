package code.elix_x.mods.fei.utils;

import code.elix_x.mods.fei.api.client.IRenderable.ItemStackRenderable;
import code.elix_x.mods.fei.net.FEIGuiType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class EnchantFEIUtil extends FEIInternalGuiDisplayUtil {

	public EnchantFEIUtil(){
		super("Enchant", "fei.gui.override.grid.utils.enchant", null, FEIGuiType.ENCHANT);
		ItemStack itemstack = new ItemStack(Items.DIAMOND_SWORD);
		itemstack.addEnchantment(Enchantment.getEnchantmentByLocation("minecraft:sharpness"), 5);
		renderable = new ItemStackRenderable(itemstack);
	}

}
