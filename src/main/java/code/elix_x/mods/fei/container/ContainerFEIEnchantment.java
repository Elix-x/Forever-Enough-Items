package code.elix_x.mods.fei.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerFEIEnchantment extends Container {

	public IInventory inventory;

	public final Map<Enchantment, Integer> enchantments = new HashMap<>();
	private boolean prevNoStack;
	public boolean lock;

	public boolean dirty;

	public ContainerFEIEnchantment(InventoryPlayer playerInv){
		this.inventory = new InventoryBasic("Enchant", true, 2){

			public int getInventoryStackLimit(){
				return 64;
			}

			public void markDirty(){
				super.markDirty();
				ContainerFEIEnchantment.this.onCraftMatrixChanged(this);
			}
		};
		this.addSlotToContainer(new Slot(this.inventory, 0, 15, 47));

		for(int i = 0; i < 3; ++i){
			for(int j = 0; j < 9; ++j){
				this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for(int k = 0; k < 9; ++k){
			this.addSlotToContainer(new Slot(playerInv, k, 8 + k * 18, 142));
		}
	}

	public ItemStack getCurrentStack(){
		return getSlot(0).getStack();
	}

	private void transferEnchantmentsTo(ItemStack itemstack){
		if(itemstack.getItem() == Items.BOOK) itemstack = new ItemStack(Items.ENCHANTED_BOOK, itemstack.getCount(), itemstack.getMetadata());
		if(itemstack.isItemEnchanted()) itemstack.getTagCompound().removeTag("ench");

		for(Entry<Enchantment, Integer> e : enchantments.entrySet()){
			EnchantmentData enchantmentdata = new EnchantmentData(e.getKey(), e.getValue());
			if(enchantmentdata.enchantmentLevel > 0){
				if(itemstack.getItem() == Items.ENCHANTED_BOOK)
					Items.ENCHANTED_BOOK.addEnchantment(itemstack, enchantmentdata);
				else itemstack.addEnchantment(enchantmentdata.enchantmentobj, enchantmentdata.enchantmentLevel);
			}
		}
	}

	public void transferEnchantmentFrom(ItemStack itemstack){
		enchantments.clear();
		enchantments.putAll(EnchantmentHelper.getEnchantments(itemstack));
	}

	protected void broadcastData(IContainerListener crafting){
		crafting.sendProgressBarUpdate(this, 0, lock ? 1 : 0);
		for(Entry<Enchantment, Integer> e : enchantments.entrySet()){
			crafting.sendProgressBarUpdate(this, Enchantment.getEnchantmentID(e.getKey()) + 1, e.getValue());
		}
	}

	public void addListener(IContainerListener listener){
		super.addListener(listener);
		this.broadcastData(listener);
	}

	/**
	 * Looks for changes made in the container, sends them to every listener.
	 */
	@Override
	public void detectAndSendChanges(){
		super.detectAndSendChanges();
		for(IContainerListener icontainerlistener : listeners)
			this.broadcastData(icontainerlistener);
	}

	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data){
		if(id == 0){
			if(lock != (data == 1)){
				lock = data == 1;
				dirty = true;
			}
		} else if(!(enchantments.containsKey(Enchantment.getEnchantmentByID(id - 1)) && enchantments.get(Enchantment.getEnchantmentByID(id - 1)) == data)){
			enchantments.put(Enchantment.getEnchantmentByID(id - 1), data);
			dirty = true;
		}
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	public void onCraftMatrixChanged(IInventory inventoryIn){
		if(inventoryIn == this.inventory){
			ItemStack itemstack = inventoryIn.getStackInSlot(0);
			if(itemstack != null){
				if(!lock && prevNoStack && itemstack.isItemEnchanted()){
					transferEnchantmentFrom(itemstack);
				} else{
					transferEnchantmentsTo(itemstack);
				}
				prevNoStack = false;
			} else{
				prevNoStack = true;
			}
		}
		super.onCraftMatrixChanged(inventoryIn);
	}

	/**
	 * Handles the given Button-click on the server, currently only used by enchanting. Name is for legacy.
	 */
	public boolean enchantItem(EntityPlayer playerIn, int id){
		if(id == 0) lock = false;
		else if(id == 1) lock = true;
		else{
			id -= 2;
			enchantments.put(Enchantment.getEnchantmentByID(id & 0xffff), (id >> 16) & 0xffff);
			onCraftMatrixChanged(inventory);
		}
		return true;
	}

	/**
	 * Called when the container is closed.
	 */
	public void onContainerClosed(EntityPlayer player){
		super.onContainerClosed(player);

		if(!player.world.isRemote){
			for(int i = 0; i < this.inventory.getSizeInventory(); ++i){
				ItemStack itemstack = this.inventory.removeStackFromSlot(i);

				if(itemstack != null){
					player.dropItem(itemstack, false);
				}
			}
		}
	}

	public boolean canInteractWith(EntityPlayer playerIn){
		return true;
	}

	/**
	 * Take a stack from the specified inventory slot.
	 */
	@Nullable
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index){
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(index);

		if(slot != null && slot.getHasStack()){
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if(index == 0){
				if(!this.mergeItemStack(itemstack1, 2, 38, true)){
					return null;
				}
			} else{
				if(((Slot) this.inventorySlots.get(0)).getHasStack() || !((Slot) this.inventorySlots.get(0)).isItemValid(itemstack1)){
					return null;
				}

				if(itemstack1.hasTagCompound() && itemstack1.getCount() == 1){
					((Slot) this.inventorySlots.get(0)).putStack(itemstack1.copy());
					itemstack1.setCount(0);
				} else if(itemstack1.getCount() >= 1){
					((Slot) this.inventorySlots.get(0)).putStack(new ItemStack(itemstack1.getItem(), 1, itemstack1.getMetadata()));
					itemstack1.shrink(1);
				}
			}

			if(itemstack1.getCount() == 0){
				slot.putStack((ItemStack) null);
			} else{
				slot.onSlotChanged();
			}

			if(itemstack1.getCount() == itemstack.getCount()){
				return null;
			}

			slot.onTake(playerIn, itemstack1);
		}

		return itemstack;
	}

}
