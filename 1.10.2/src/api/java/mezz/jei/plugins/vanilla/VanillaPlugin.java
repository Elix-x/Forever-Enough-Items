package mezz.jei.plugins.vanilla;

import javax.annotation.Nullable;

import mezz.jei.Internal;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IItemBlacklist;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import mezz.jei.plugins.vanilla.brewing.BrewingRecipeCategory;
import mezz.jei.plugins.vanilla.brewing.BrewingRecipeHandler;
import mezz.jei.plugins.vanilla.brewing.BrewingRecipeMaker;
import mezz.jei.plugins.vanilla.brewing.PotionSubtypeInterpreter;
import mezz.jei.plugins.vanilla.crafting.CraftingRecipeCategory;
import mezz.jei.plugins.vanilla.crafting.ShapedOreRecipeHandler;
import mezz.jei.plugins.vanilla.crafting.ShapedRecipesHandler;
import mezz.jei.plugins.vanilla.crafting.ShapelessOreRecipeHandler;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipesHandler;
import mezz.jei.plugins.vanilla.crafting.TippedArrowRecipeHandler;
import mezz.jei.plugins.vanilla.crafting.TippedArrowRecipeMaker;
import mezz.jei.plugins.vanilla.furnace.FuelRecipeHandler;
import mezz.jei.plugins.vanilla.furnace.FuelRecipeMaker;
import mezz.jei.plugins.vanilla.furnace.FurnaceFuelCategory;
import mezz.jei.plugins.vanilla.furnace.FurnaceSmeltingCategory;
import mezz.jei.plugins.vanilla.furnace.SmeltingRecipeHandler;
import mezz.jei.plugins.vanilla.furnace.SmeltingRecipeMaker;
import mezz.jei.plugins.vanilla.ingredients.FluidStackHelper;
import mezz.jei.plugins.vanilla.ingredients.FluidStackListFactory;
import mezz.jei.plugins.vanilla.ingredients.FluidStackRenderer;
import mezz.jei.plugins.vanilla.ingredients.ItemStackHelper;
import mezz.jei.plugins.vanilla.ingredients.ItemStackListFactory;
import mezz.jei.plugins.vanilla.ingredients.ItemStackRenderer;
import mezz.jei.util.Log;
import mezz.jei.util.StackHelper;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.fluids.FluidStack;

@JEIPlugin
public class VanillaPlugin extends BlankModPlugin {
	public static IJeiHelpers jeiHelpers;

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
		subtypeRegistry.useNbtForSubtypes(
				Items.ENCHANTED_BOOK
		);

		subtypeRegistry.registerNbtInterpreter(Items.TIPPED_ARROW, PotionSubtypeInterpreter.INSTANCE);
		subtypeRegistry.registerNbtInterpreter(Items.POTIONITEM, PotionSubtypeInterpreter.INSTANCE);
		subtypeRegistry.registerNbtInterpreter(Items.SPLASH_POTION, PotionSubtypeInterpreter.INSTANCE);
		subtypeRegistry.registerNbtInterpreter(Items.LINGERING_POTION, PotionSubtypeInterpreter.INSTANCE);
		subtypeRegistry.registerNbtInterpreter(Items.BANNER, new ISubtypeRegistry.ISubtypeInterpreter() {
			@Nullable
			@Override
			public String getSubtypeInfo(ItemStack itemStack) {
				EnumDyeColor baseColor = ItemBanner.getBaseColor(itemStack);
				return baseColor.toString();
			}
		});
		subtypeRegistry.registerNbtInterpreter(Items.SPAWN_EGG, new ISubtypeRegistry.ISubtypeInterpreter() {
			@Nullable
			@Override
			public String getSubtypeInfo(ItemStack itemStack) {
				return ItemMonsterPlacer.getEntityIdFromItem(itemStack);
			}
		});
	}

	@Override
	public void registerIngredients(IModIngredientRegistration ingredientRegistration) {
		StackHelper stackHelper = Internal.getStackHelper();
		ingredientRegistration.register(ItemStack.class, ItemStackListFactory.create(stackHelper), new ItemStackHelper(stackHelper), new ItemStackRenderer());
		ingredientRegistration.register(FluidStack.class, FluidStackListFactory.create(), new FluidStackHelper(), new FluidStackRenderer());
	}

	@Override
	public void register(IModRegistry registry) {
		IIngredientRegistry ingredientRegistry = registry.getIngredientRegistry();
		jeiHelpers = registry.getJeiHelpers();

		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		registry.addRecipeCategories(
				new CraftingRecipeCategory(guiHelper),
				new FurnaceFuelCategory(guiHelper),
				new FurnaceSmeltingCategory(guiHelper),
				new BrewingRecipeCategory(guiHelper)
		);

		registry.addRecipeHandlers(
				new ShapedOreRecipeHandler(),
				new ShapedRecipesHandler(),
				new ShapelessOreRecipeHandler(guiHelper),
				new ShapelessRecipesHandler(guiHelper),
				new TippedArrowRecipeHandler(),
				new FuelRecipeHandler(),
				new SmeltingRecipeHandler(),
				new BrewingRecipeHandler()
		);

		registry.addRecipeClickArea(GuiCrafting.class, 88, 32, 28, 23, VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeClickArea(GuiBrewingStand.class, 97, 16, 14, 30, VanillaRecipeCategoryUid.BREWING);
		registry.addRecipeClickArea(GuiFurnace.class, 78, 32, 28, 23, VanillaRecipeCategoryUid.SMELTING, VanillaRecipeCategoryUid.FUEL);

		IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();

		recipeTransferRegistry.addRecipeTransferHandler(ContainerWorkbench.class, VanillaRecipeCategoryUid.CRAFTING, 1, 9, 10, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerFurnace.class, VanillaRecipeCategoryUid.SMELTING, 0, 1, 3, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerFurnace.class, VanillaRecipeCategoryUid.FUEL, 1, 1, 3, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerBrewingStand.class, VanillaRecipeCategoryUid.BREWING, 0, 4, 5, 36);

		registry.addRecipeCategoryCraftingItem(new ItemStack(Blocks.CRAFTING_TABLE), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeCategoryCraftingItem(new ItemStack(Blocks.FURNACE), VanillaRecipeCategoryUid.SMELTING, VanillaRecipeCategoryUid.FUEL);
		registry.addRecipeCategoryCraftingItem(new ItemStack(Items.BREWING_STAND), VanillaRecipeCategoryUid.BREWING);

		registry.addRecipes(CraftingManager.getInstance().getRecipeList());
		registry.addRecipes(SmeltingRecipeMaker.getFurnaceRecipes(jeiHelpers));
		registry.addRecipes(FuelRecipeMaker.getFuelRecipes(ingredientRegistry, jeiHelpers));
		registry.addRecipes(BrewingRecipeMaker.getBrewingRecipes(ingredientRegistry));
		registry.addRecipes(TippedArrowRecipeMaker.getTippedArrowRecipes());

		IItemBlacklist itemBlacklist = registry.getJeiHelpers().getItemBlacklist();
		// Game freezes when loading player skulls, see https://bugs.mojang.com/browse/MC-65587
		itemBlacklist.addItemToBlacklist(new ItemStack(Items.SKULL, 1, 3));
	}
}
