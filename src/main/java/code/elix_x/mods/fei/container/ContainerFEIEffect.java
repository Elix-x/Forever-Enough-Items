package code.elix_x.mods.fei.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerFEIEffect extends Container {

	public IInventory inventory;

	public final Map<Potion, Pair<Integer, Integer>> effects = new HashMap<>();

	public boolean dirty;

	public ContainerFEIEffect(InventoryPlayer playerInv){
		this.inventory = new InventoryBasic("Potion", true, 2){

			@Override
			public ItemStack removeStackFromSlot(int index){
				return getStackInSlot(index);
			}

			@Override
			public void setInventorySlotContents(int index, ItemStack stack){
				if(!stack.isEmpty()) super.setInventorySlotContents(index, stack);
			}

			public void markDirty(){
				super.markDirty();
				ContainerFEIEffect.this.onCraftMatrixChanged(this);
			}
		};
		inventory.setInventorySlotContents(0, new ItemStack(Items.POTIONITEM));
		this.addSlotToContainer(new Slot(this.inventory, 0, 15, 47){

			@Override
			public boolean isItemValid(ItemStack stack){
				return false;
			}

		});

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
	
	private void setCurrentItem(Item item){
		getSlot(0).putStack(new ItemStack(item, getCurrentStack().getCount(), getCurrentStack().getMetadata()));
	}

	private void transferEffects(ItemStack itemstack){
		if(itemstack.hasTagCompound()) itemstack.getTagCompound().setTag("CustomPotionEffects", new NBTTagList());
		PotionUtils.appendEffects(itemstack, Collections2.transform(effects.entrySet(), new Function<Entry<Potion, Pair<Integer, Integer>>, PotionEffect>(){

			@Override
			public PotionEffect apply(Entry<Potion, Pair<Integer, Integer>> e){
				return new PotionEffect(e.getKey(), e.getValue().getLeft() * 20, e.getValue().getRight());
			}

		}));
	}

	protected void broadcastData(IContainerListener crafting){
		for(Entry<Potion, Pair<Integer, Integer>> e : effects.entrySet()){
			crafting.sendProgressBarUpdate(this, Potion.getIdFromPotion(e.getKey()), (e.getValue().getLeft() << 8) | (e.getValue().getRight() & 255));
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
		Pair<Integer, Integer> e = effects.get(id);
		if(e == null || (e.getLeft() != data >> 8 || e.getRight() != (data & 255))){
			effects.put(Potion.getPotionById(id), new ImmutablePair<Integer, Integer>(data >> 8, data & 255));
			dirty = true;
		}
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	public void onCraftMatrixChanged(IInventory inventoryIn){
		if(inventoryIn == this.inventory){
			ItemStack itemstack = inventoryIn.getStackInSlot(0);
			if(itemstack == null){
				itemstack = new ItemStack(Items.POTIONITEM);
				inventoryIn.setInventorySlotContents(0, itemstack);
			}
			transferEffects(itemstack);
		}
		super.onCraftMatrixChanged(inventoryIn);
	}

	/**
	 * Handles the given Button-click on the server, currently only used by enchanting. Name is for legacy.
	 */
	public boolean enchantItem(EntityPlayer playerIn, int id){
		switch(id & 7){
			case 0:
				setCurrentItem(Items.POTIONITEM);
				break;
			case 1:
				setCurrentItem(Items.SPLASH_POTION);
				break;
			case 2:
				setCurrentItem(Items.LINGERING_POTION);
				break;
			case 3:
				for(Entry<Potion, Pair<Integer, Integer>> e : effects.entrySet()){
					playerIn.addPotionEffect(new PotionEffect(e.getKey(), e.getValue().getLeft() * 20, e.getValue().getRight()));
				}
				break;
			case 4:
				id = id >> 3;
				int data = id >> 8;
				effects.put(Potion.getPotionById(id & 255), new ImmutablePair(data >> 8, data & 255));
				onCraftMatrixChanged(inventory);
				break;
		}
		return true;
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
				slot.putStack(ItemStack.EMPTY);
			} else{
				slot.onSlotChanged();
			}

			if(itemstack1.getCount() == itemstack.getCount()){
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, itemstack1);
			System.out.println("take");
			if(index == 0){
				slot.putStack(itemstack1.copy());
			}
		}

		return itemstack;
	}

}
