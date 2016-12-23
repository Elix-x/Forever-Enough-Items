package code.elix_x.mods.fei.net;

import javax.annotation.Nullable;

import org.apache.commons.lang3.mutable.MutableObject;

import code.elix_x.excore.utils.net.gui.SmartGuiHandler;
import code.elix_x.mods.fei.client.gui.container.GuiFEIEffect;
import code.elix_x.mods.fei.client.gui.container.GuiFEIEnchantment;
import code.elix_x.mods.fei.container.ContainerFEIEffect;
import code.elix_x.mods.fei.container.ContainerFEIEnchantment;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public enum FEIGuiType implements SmartGuiHandler.SmartGuiHandlerElement {

	REPAIR{

		@Override
		public Object getClientGuiElement(EntityPlayer player, World world, int x, int y, int z){
			GuiRepair repair = new GuiRepair(player.inventory, world);
			repair.inventorySlots = getServerGuiElement(player, world, x, y, z);
			return repair;
		}

		@Override
		public ContainerRepair getServerGuiElement(final EntityPlayer player, World world, int x, int y, int z){
			final MutableObject<ContainerRepair> container = new MutableObject<>();
			ContainerRepair repair = new ContainerRepair(player.inventory, world, player){

				protected Slot addSlotToContainer(Slot slot){
					if(slot.getSlotIndex() == 2){
						final Slot sslot = slot;
						slot = new Slot(slot.inventory, slot.getSlotIndex(), slot.xPos, slot.yPos){

							public boolean isItemValid(@Nullable ItemStack stack){
								return sslot.isItemValid(stack);
							}

							public boolean canTakeStack(EntityPlayer playerIn){
								return container.getValue().maximumCost > 0 && this.getHasStack();
							}

							public void onPickupFromSlot(EntityPlayer player, ItemStack stack){
								boolean c = player.capabilities.isCreativeMode;
								player.capabilities.isCreativeMode = true;
								sslot.onTake(player, stack);
								player.capabilities.isCreativeMode = c;
							}

						};
					}
					return super.addSlotToContainer(slot);
				}

				public boolean canInteractWith(EntityPlayer playerIn){
					return true;
				}

				public void updateRepairOutput(){
					boolean c = player.capabilities.isCreativeMode;
					player.capabilities.isCreativeMode = true;
					super.updateRepairOutput();
					player.capabilities.isCreativeMode = c;
				}

			};
			container.setValue(repair);
			repair.inventorySlots.get(0).putStack(player.inventory.getItemStack());
			player.inventory.setItemStack(ItemStack.EMPTY);
			return repair;
		}

	},

	ENCHANT{

		@Override
		public Object getClientGuiElement(EntityPlayer player, World world, int x, int y, int z){
			return new GuiFEIEnchantment(player.inventory);
		}

		@Override
		public Object getServerGuiElement(EntityPlayer player, World world, int x, int y, int z){
			ContainerFEIEnchantment enchant = new ContainerFEIEnchantment(player.inventory);
			enchant.inventorySlots.get(0).putStack(player.inventory.getItemStack());
			enchant.transferEnchantmentFrom(player.inventory.getItemStack());
			player.inventory.setItemStack(ItemStack.EMPTY);
			return enchant;
		}

	},
	EFFECT{

		@Override
		public Object getClientGuiElement(EntityPlayer player, World world, int x, int y, int z){
			return new GuiFEIEffect(player.inventory);
		}

		@Override
		public Object getServerGuiElement(EntityPlayer player, World world, int x, int y, int z){
			return new ContainerFEIEffect(player.inventory);
		}

	}

}