package pipi.mod.unltd_furnace.tileentity;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import pipi.mod.unltd_furnace.UnlimitedFurnace;
import pipi.mod.unltd_furnace.block.BlockUnltdFurnace;

public abstract class TileEntityUnltdFurnaceBase extends TileEntityLockable implements ITickable, ISidedInventory {
	private static final int SLOT_INPUT = 0;
	private static final int SLOT_FUEL = 1;
	private static final int SLOT_OUTPUT = 2;
	private static final int[] SLOTS_TOP = new int[] {SLOT_INPUT};
	private static final int[] SLOTS_BOTTOM = new int[] {SLOT_OUTPUT, SLOT_FUEL};
	private static final int[] SLOTS_SIDES = new int[] {SLOT_FUEL};
    
	private NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(3, ItemStack.EMPTY);
	private int furnaceBurnTime;
	private int currentItemBurnTime;
	private int cookTime;
	private int totalCookTime;
	private String customName;
	
	/** かまどの名前 */
	public abstract String getContainerName();

	/** かまどの情報 */
	public abstract UnltdFurnaceInfo getFurnaceInfo();
	
	public int getSizeInventory() {
		return this.stacks.size();
	}

	public boolean isEmpty() {
		for (ItemStack stack : this.stacks) {
			if (!stack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public ItemStack getStackInSlot(int index) {
		return this.stacks.get(index);
	}

	public ItemStack decrStackSize(int index, int count) {
		return ItemStackHelper.getAndSplit(this.stacks, index, count);
	}

	public ItemStack removeStackFromSlot(int index) {
		return ItemStackHelper.getAndRemove(this.stacks, index);
	}

	public void setInventorySlotContents(int index, ItemStack stack) {
		ItemStack currentStack = this.stacks.get(index);
		boolean sameStack = !stack.isEmpty() && stack.isItemEqual(currentStack) && ItemStack.areItemStackTagsEqual(stack, currentStack);
		this.stacks.set(index, stack);

		if (stack.getCount() > this.getInventoryStackLimit()) {
			stack.setCount(this.getInventoryStackLimit());
		}

		if (index == 0 && !sameStack) {
			this.totalCookTime = this.getFurnaceInfo().getCookTime(stack);
			this.cookTime = 0;
			this.markDirty();
		}
	}

	public String getName() {
		return this.hasCustomName() ? this.customName : this.getContainerName();
	}

	public boolean hasCustomName() {
		return this.customName != null && !this.customName.isEmpty();
	}

	public void setCustomInventoryName(String customName) {
		this.customName = customName;
	}
	
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.stacks = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(compound, this.stacks);
		this.furnaceBurnTime = compound.getInteger("BurnTime");
		this.cookTime = compound.getInteger("CookTime");
		this.totalCookTime = compound.getInteger("CookTimeTotal");
		this.currentItemBurnTime = TileEntityFurnace.getItemBurnTime(this.stacks.get(1));
		
		if (compound.hasKey("CustomName", 8)) {
			this.customName = compound.getString("CustomName");
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("BurnTime", (short)this.furnaceBurnTime);
		compound.setInteger("CookTime", (short)this.cookTime);
		compound.setInteger("CookTimeTotal", (short)this.totalCookTime);
		ItemStackHelper.saveAllItems(compound, this.stacks);

		if (this.hasCustomName()) {
			compound.setString("CustomName", this.customName);
		}
		return compound;
	}

	public int getInventoryStackLimit() {
		return this.getFurnaceInfo().unltdStack ? Integer.MAX_VALUE : 64;
	}

	public boolean isBurning() {
		return this.furnaceBurnTime > 0;
	}

	@Override
	public void update() {
		boolean defaultBurnState = this.isBurning();
		boolean updated = false;
		
		if (this.isBurning()) {
			--this.furnaceBurnTime;
		}

		if (!this.world.isRemote) {
			ItemStack input = this.stacks.get(0);
			ItemStack fuel = this.stacks.get(1);
			
			if (this.isBurning() || !fuel.isEmpty() && !input.isEmpty()) {
				
				boolean canSmelt = this.canSmelt();
				if (!this.isBurning() && canSmelt) {
					this.furnaceBurnTime = this.getFurnaceInfo().getFuelBurnTime(fuel);
					this.currentItemBurnTime = this.furnaceBurnTime;

					if (this.isBurning()) {
						updated = true;
						
						if (!fuel.isEmpty()) {
							Item fuelItem = fuel.getItem();
							if(!this.getFurnaceInfo().saveFuel) {
								fuel.shrink(1);
							}
							
							if (fuel.isEmpty()) {
								ItemStack fuelContainer = fuelItem.getContainerItem(fuel);
								this.stacks.set(1, fuelContainer);
							}
						}
					}
				}

				if(this.isBurning() && canSmelt) {
					++this.cookTime;
					
					if(this.cookTime >= this.totalCookTime) {
						this.cookTime = 0;
						this.totalCookTime = this.getFurnaceInfo().getCookTime(input);
						this.smeltItem();
						updated = true;
					}
				} else {
					this.cookTime = 0;
				}
			} else if (!this.isBurning() && this.cookTime > 0) {
				this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.totalCookTime);
			}

			// 燃えてる状態が切り替わってたら更新
			boolean currentBurnState = this.isBurning();
			if (defaultBurnState != currentBurnState) {
				updated = true;
				BlockUnltdFurnace.setState(currentBurnState, this.world, this.pos);
			}
		}

		if (updated) {
			this.markDirty();
		}
	}
    
	private boolean canSmelt() {
		ItemStack input = this.getStackInSlot(SLOT_INPUT);
		if (input.isEmpty()) {
			return false;
		} else {
			ItemStack result = FurnaceRecipes.instance().getSmeltingResult(input);
			if (result.isEmpty()) {
				return false;
			} else {
				ItemStack output = this.getStackInSlot(SLOT_OUTPUT);
				if (output.isEmpty()) {
					return true;
				} else if (!output.isItemEqual(result)) {
					return false;
				} else {
					int smelting = this.getFurnaceInfo().smeltingAll ? input.getCount() : 1;
					int smelted = result.getCount() * smelting;
					int limit = this.getFurnaceInfo().unltdStack ? Integer.MAX_VALUE : output.getMaxStackSize();
					// オーバーフロー対策
					int capacity = limit - output.getCount();
					return capacity >= smelted;
				}
			}
		}
	}
	
	public void smeltItem() {
		if (this.canSmelt()) {
			ItemStack input = this.getStackInSlot(SLOT_INPUT);
			ItemStack result = FurnaceRecipes.instance().getSmeltingResult(input);
			ItemStack output = this.getStackInSlot(SLOT_OUTPUT);

			// 1回で精錬する個数
			int smelting = this.getFurnaceInfo().smeltingAll ? input.getCount() : 1;
			int smelted = result.getCount() * smelting;
			if(output.isEmpty()) {
				ItemStack resultCopy = result.copy();
				resultCopy.setCount(smelted);
				this.setInventorySlotContents(SLOT_OUTPUT, resultCopy);
			} else if (output.getItem() == result.getItem()) {
				output.grow(smelted);
			}

			// 濡れたスポンジから水を取り出す
			ItemStack fuel = this.getStackInSlot(SLOT_FUEL);
			boolean isWetSponge = input.getItem() == Item.getItemFromBlock(Blocks.SPONGE) && input.getMetadata() == 1;
			IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(fuel);
			if(isWetSponge && fluidHandler != null) {
				FluidStack water = new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);
				fluidHandler.fill(water, true);
				//this.setInventorySlotContents(SLOT_FUEL, new ItemStack(Items.WATER_BUCKET));
			}
			
			if(!this.getFurnaceInfo().saveInput) {
				input.shrink(smelting);
			}
		}
	}

	public boolean isUsableByPlayer(EntityPlayer player) {
		if (this.world.getTileEntity(this.pos) != this) {
			return false;
		} else {
			return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
		}
	}

	public void openInventory(EntityPlayer player) {}

	public void closeInventory(EntityPlayer player) {}

	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if (index == SLOT_OUTPUT) {
			return false;
		} else if (index != SLOT_FUEL) {
			return true;
		} else {
			ItemStack fuel = this.stacks.get(SLOT_FUEL);
			return TileEntityFurnace.isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack) && fuel.getItem() != Items.BUCKET;
		}
	}

	public int[] getSlotsForFace(EnumFacing side) {
		if (side == EnumFacing.DOWN) {
			return SLOTS_BOTTOM;
		} else {
			return side == EnumFacing.UP ? SLOTS_TOP : SLOTS_SIDES;
		}
	}

	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return this.isItemValidForSlot(index, itemStackIn);
	}

	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		if (direction == EnumFacing.DOWN && index == SLOT_FUEL) {
			IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(stack);
			
			// 液体いりバケツ以外は省く
			if(fluidHandler == null || fluidHandler.drain(Fluid.BUCKET_VOLUME, false) == null) {
				return false;
			}
		}
		return true;
	}
	

	public String getGuiID() {
		return UnlimitedFurnace.locateStr("unlimited_furnace");
	}
	
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerFurnace(playerInventory, this);
	}

	public int getField(int id) {
		switch (id) {
		case 0:
			return this.furnaceBurnTime;
		case 1:
			return this.currentItemBurnTime;
		case 2:
			return this.cookTime;
		case 3:
			return this.totalCookTime;
		default:
			return 0;
		}
	}

	public void setField(int id, int value) {
		switch (id) {
		case 0:
			this.furnaceBurnTime = value;
			break;
		case 1:
			this.currentItemBurnTime = value;
			break;
		case 2:
			this.cookTime = value;
			break;
		case 3:
			this.totalCookTime = value;
		}
	}

	public int getFieldCount() {
		return 4;
	}

	public void clear() {
		this.stacks.clear();
	}

	IItemHandler handlerTop = new SidedInvWrapper(this, EnumFacing.UP);
	IItemHandler handlerBottom = new SidedInvWrapper(this, EnumFacing.DOWN);
	IItemHandler handlerSide = new SidedInvWrapper(this, EnumFacing.WEST);

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == EnumFacing.DOWN)
				return (T) handlerBottom;
			else if (facing == EnumFacing.UP)
				return (T) handlerTop;
			else
				return (T) handlerSide;
		}
		return super.getCapability(capability, facing);
	}
    
}
