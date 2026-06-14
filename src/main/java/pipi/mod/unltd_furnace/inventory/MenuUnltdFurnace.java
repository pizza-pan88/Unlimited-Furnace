package pipi.mod.unltd_furnace.inventory;

import static pipi.mod.unltd_furnace.UnltdFurnaceConstants.*;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import pipi.mod.unltd_furnace.object.UnltdFurnaceMenuTypes;
import pipi.mod.unltd_furnace.tileentity.RecipeDefiner;
import pipi.mod.unltd_furnace.tileentity.TileEntityUnltdFurnaceBase;

public class MenuUnltdFurnace extends AbstractContainerMenu {
	
	private RecipeDefiner<AbstractCookingRecipe> recipeDefiner = new RecipeDefiner.Cooking();
	protected final Container container;
	protected final ContainerData data;
	protected final Level level;
	public MenuUnltdFurnace(int windowId, Inventory inventory, FriendlyByteBuf buf) {
		this(windowId, inventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(DATA_COUNT));
	}

	public MenuUnltdFurnace(int windowId, Inventory inventory, Container container, ContainerData data) {
		super(UnltdFurnaceMenuTypes.UNLTD_FURNACE.get(), windowId);
		checkContainerSize(container, SLOT_COUNT);
		checkContainerDataCount(data, DATA_COUNT);
		this.container = container;
		this.data = data;
		this.level = inventory.player.level();
		this.initSlots(inventory, container, data);
		if(container instanceof TileEntityUnltdFurnaceBase furnace) {
			this.recipeDefiner = furnace.getRecipeDefiner();
		}
	}
	
	public void setRecipeDefiner(RecipeDefiner<AbstractCookingRecipe> recipeDefiner) {
		this.recipeDefiner = recipeDefiner;
	}

	public RecipeType<? extends AbstractCookingRecipe> getRecipeType() {
		if(this.recipeDefiner == null) {
			return RecipeType.SMELTING;
		}
		ItemStack definer = this.getSlot(SLOT_RECIPE_DEFINER).getItem();
		return this.recipeDefiner.getRecipeType(definer, RecipeType.SMELTING);
	}
	
	public boolean isRecipeDefiner(ItemStack stack) {
		return this.recipeDefiner == null ? false : this.recipeDefiner.isDefiner(stack);
	}
	
	private void initSlots(Inventory inventory, Container container, ContainerData data) {
		this.addSlot(new Slot(container, 0, 56, 17));
		this.addSlot(new SlotUnltdFurnaceFuel(this, container, 1, 56, 53));
		this.addSlot(new SlotUnltdFurnaceResult(inventory.player, container, 2, 116, 35));
		this.addSlot(new SlotUnltdFurnaceDefiner(this, container, 3, 152, 60));
	
		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		
		for(int k = 0; k < 9; ++k) {
			this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
		}
		
		this.addDataSlots(data);
	}
	
	public boolean stillValid(Player player) {
		return this.container.stillValid(player);
	}

	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack oldStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		
		if(slot != null && slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			oldStack = slotStack.copy();
			if(index == SLOT_OUTPUT || index == SLOT_RECIPE_DEFINER) {
				if(!this.moveItemStackTo(slotStack, 4, 40, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(slotStack, oldStack);
			} else if(index != SLOT_FUEL && index != SLOT_INPUT) {
				if(this.canSmelt(slotStack)) {
					if(!this.moveItemStackTo(slotStack, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if(this.isFuel(slotStack)) {
					if(!this.moveItemStackTo(slotStack, 1, 2, false)) {
						return ItemStack.EMPTY;
					}
				} else if(this.isRecipeDefiner(slotStack)) {
					if(!this.moveItemStackTo(slotStack, 3, 4, false)) {
						return ItemStack.EMPTY;
					}
				} else if(index >= 4 && index < 31) {
					if(!this.moveItemStackTo(slotStack, 31, 40, false)) {
						return ItemStack.EMPTY;
					}
				} else if(index >= 31 && index < 40 && !this.moveItemStackTo(slotStack, 4, 31, false)) {
					return ItemStack.EMPTY;
				}
			} else if(!this.moveItemStackTo(slotStack, 4, 40, false)) {
				return ItemStack.EMPTY;
			}

			if(slotStack.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if(slotStack.getCount() == oldStack.getCount()) {
				return ItemStack.EMPTY;
			}
			
			slot.onTake(player, slotStack);
		}

		return oldStack;
	}

	protected boolean canSmelt(ItemStack stack) {
		return this.level.getRecipeManager().getRecipeFor(getRecipeType(), new SimpleContainer(stack), this.level).isPresent();
	}

	protected boolean isFuel(ItemStack stack) {
		return TileEntityUnltdFurnaceBase.isFuel(stack, getRecipeType());
	}

	public int getBurnProgress() {
		int i = this.data.get(DATA_COOKING_PROGRESS);
		int j = this.data.get(DATA_COOKING_TOTAL_TIME);
		return (j != 0 && i != 0) ? (i * 24 / j) : 0;
	}
	
	public int getLitProgress() {
		int i = this.data.get(DATA_LIT_DURATION);
		if (i == 0) {
			i = 200;
		}
		
		return this.data.get(DATA_LIT_TIME) * 13 / i;
	}

	public boolean isLit() {
		return this.data.get(DATA_LIT_TIME) > 0;
	}

}
