package mezz.jei.transfer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import mezz.jei.Internal;
import mezz.jei.JustEnoughItems;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.config.SessionData;
import mezz.jei.gui.Focus;
import mezz.jei.gui.ingredients.GuiItemStackGroup;
import mezz.jei.network.packets.PacketRecipeTransfer;
import mezz.jei.util.Log;
import mezz.jei.util.StackHelper;
import mezz.jei.util.Translator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class PlayerRecipeTransferHandler implements IRecipeTransferHandler<ContainerPlayer> {
	private final StackHelper stackHelper;
	private final IRecipeTransferHandlerHelper handlerHelper;
	private final IRecipeTransferInfo<ContainerPlayer> transferHelper;

	public PlayerRecipeTransferHandler(IRecipeTransferHandlerHelper handlerHelper) {
		this.stackHelper = Internal.getStackHelper();
		this.handlerHelper = handlerHelper;
		this.transferHelper = new BasicRecipeTransferInfo<ContainerPlayer>(ContainerPlayer.class, VanillaRecipeCategoryUid.CRAFTING, 1, 4, 5, 36);
	}

	@Override
	public Class<ContainerPlayer> getContainerClass() {
		return transferHelper.getContainerClass();
	}

	@Override
	public String getRecipeCategoryUid() {
		return transferHelper.getRecipeCategoryUid();
	}

	@Nullable
	@Override
	public IRecipeTransferError transferRecipe(ContainerPlayer container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
		if (!SessionData.isJeiOnServer()) {
			String tooltipMessage = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.no.server");
			return handlerHelper.createUserErrorWithTooltip(tooltipMessage);
		}

		Map<Integer, Slot> inventorySlots = new HashMap<Integer, Slot>();
		for (Slot slot : transferHelper.getInventorySlots(container)) {
			inventorySlots.put(slot.slotNumber, slot);
		}

		Map<Integer, Slot> craftingSlots = new HashMap<Integer, Slot>();
		for (Slot slot : transferHelper.getRecipeSlots(container)) {
			craftingSlots.put(slot.slotNumber, slot);
		}

		IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
		int inputCount = 0;
		{
			// indexes that do not fit into the player crafting grid
			Set<Integer> badIndexes = ImmutableSet.of(2, 5, 6, 7, 8);

			int inputIndex = 0;
			for (IGuiIngredient<ItemStack> ingredient : itemStackGroup.getGuiIngredients().values()) {
				if (ingredient.isInput()) {
					if (!ingredient.getAllIngredients().isEmpty()) {
						inputCount++;
						if (badIndexes.contains(inputIndex)) {
							String tooltipMessage = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.too.large.player.inventory");
							return handlerHelper.createUserErrorWithTooltip(tooltipMessage);
						}
					}
					inputIndex++;
				}
			}
		}

		// compact the crafting grid into a 2x2 area
		List<IGuiIngredient<ItemStack>> guiIngredients = new ArrayList<IGuiIngredient<ItemStack>>();
		for (IGuiIngredient<ItemStack> guiIngredient : itemStackGroup.getGuiIngredients().values()) {
			if (guiIngredient.isInput()) {
				guiIngredients.add(guiIngredient);
			}
		}
		IGuiItemStackGroup playerInvItemStackGroup = new GuiItemStackGroup(new Focus<ItemStack>(null));
		int[] playerGridIndexes = {0, 1, 3, 4};
		for (int i = 0; i < 4; i++) {
			IGuiIngredient<ItemStack> ingredient = guiIngredients.get(playerGridIndexes[i]);
			playerInvItemStackGroup.init(i, true, 0, 0);
			playerInvItemStackGroup.set(i, ingredient.getAllIngredients());
		}

		Map<Integer, ItemStack> availableItemStacks = new HashMap<Integer, ItemStack>();
		int filledCraftSlotCount = 0;
		int emptySlotCount = 0;

		for (Slot slot : craftingSlots.values()) {
			final ItemStack stack = slot.getStack();
			if (stack != null) {
				if (!slot.canTakeStack(player)) {
					Log.error("Recipe Transfer helper {} does not work for container {}. Player can't move item out of Crafting Slot number {}", transferHelper.getClass(), container.getClass(), slot.slotNumber);
					return handlerHelper.createInternalError();
				}
				filledCraftSlotCount++;
				availableItemStacks.put(slot.slotNumber, stack.copy());
			}
		}

		for (Slot slot : inventorySlots.values()) {
			final ItemStack stack = slot.getStack();
			if (stack != null) {
				availableItemStacks.put(slot.slotNumber, stack.copy());
			} else {
				emptySlotCount++;
			}
		}

		// check if we have enough inventory space to shuffle items around to their final locations
		if (filledCraftSlotCount - inputCount > emptySlotCount) {
			String message = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.inventory.full");
			return handlerHelper.createUserErrorWithTooltip(message);
		}

		StackHelper.MatchingItemsResult matchingItemsResult = stackHelper.getMatchingItems(availableItemStacks, playerInvItemStackGroup.getGuiIngredients());

		if (matchingItemsResult.missingItems.size() > 0) {
			String message = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.missing");
			matchingItemsResult = stackHelper.getMatchingItems(availableItemStacks, itemStackGroup.getGuiIngredients());
			return handlerHelper.createUserErrorForSlots(message, matchingItemsResult.missingItems);
		}

		List<Integer> craftingSlotIndexes = new ArrayList<Integer>(craftingSlots.keySet());
		Collections.sort(craftingSlotIndexes);

		List<Integer> inventorySlotIndexes = new ArrayList<Integer>(inventorySlots.keySet());
		Collections.sort(inventorySlotIndexes);

		// check that the slots exist and can be altered
		for (Map.Entry<Integer, Integer> entry : matchingItemsResult.matchingItems.entrySet()) {
			int craftNumber = entry.getKey();
			int slotNumber = craftingSlotIndexes.get(craftNumber);
			if (slotNumber < 0 || slotNumber >= container.inventorySlots.size()) {
				Log.error("Recipes Transfer Helper {} references slot {} outside of the inventory's size {}", transferHelper.getClass(), slotNumber, container.inventorySlots.size());
				return handlerHelper.createInternalError();
			}
		}

		if (doTransfer) {
			PacketRecipeTransfer packet = new PacketRecipeTransfer(matchingItemsResult.matchingItems, craftingSlotIndexes, inventorySlotIndexes, maxTransfer);
			JustEnoughItems.getProxy().sendPacketToServer(packet);
		}

		return null;
	}
}
