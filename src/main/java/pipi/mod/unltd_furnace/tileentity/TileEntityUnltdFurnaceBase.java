package pipi.mod.unltd_furnace.tileentity;

import static pipi.mod.unltd_furnace.UnltdFurnaceConstants.*;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import pipi.mod.unltd_furnace.inventory.MenuUnltdFurnace;

public abstract class TileEntityUnltdFurnaceBase extends BaseContainerBlockEntity implements WorldlyContainer, RecipeHolder, StackedContentsCompatible {
	
	protected NonNullList<ItemStack> stacks = NonNullList.<ItemStack>withSize(SLOT_COUNT, ItemStack.EMPTY);
	private int litTime;
	private int litDuration;
	private int cookingProgress;
	private int cookingTotalTime;
	public final ContainerData dataAccess = new ContainerData() {
		public int get(int id) {
			return switch (id) {
			case DATA_LIT_TIME -> TileEntityUnltdFurnaceBase.this.litTime;
			case DATA_LIT_DURATION -> TileEntityUnltdFurnaceBase.this.litDuration;
			case DATA_COOKING_PROGRESS -> TileEntityUnltdFurnaceBase.this.cookingProgress;
			case DATA_COOKING_TOTAL_TIME -> TileEntityUnltdFurnaceBase.this.cookingTotalTime;
			default -> 0;
			};
		}
		public void set(int id, int value) {
			switch (id) {
			case DATA_LIT_TIME -> TileEntityUnltdFurnaceBase.this.litTime = value;
			case DATA_LIT_DURATION -> TileEntityUnltdFurnaceBase.this.litDuration = value;
			case DATA_COOKING_PROGRESS -> TileEntityUnltdFurnaceBase.this.cookingProgress = value;
			case DATA_COOKING_TOTAL_TIME -> TileEntityUnltdFurnaceBase.this.cookingTotalTime = value;
			}
		}
		public int getCount() {
			return DATA_COUNT;
		}
	};
	private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();

	private final Map<RecipeType<? extends AbstractCookingRecipe>, RecipeManager.CachedCheck<Container, ? extends AbstractCookingRecipe>> quickCheck;
	protected TileEntityUnltdFurnaceBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.quickCheck = Maps.newHashMap();
	}
	
	/** かまどの名前 */
	protected abstract Component getDefaultName();

	/** かまどの情報 */
	public abstract UnltdFurnaceInfo getFurnaceInfo();

	/** アイテムとレシピの対応表 */
	public abstract RecipeDefiner<AbstractCookingRecipe> getRecipeDefiner();

	/** かまどのメニュー */
	protected AbstractContainerMenu createMenu(int windowId, Inventory inventory) {
		return new MenuUnltdFurnace(windowId, inventory, this, this.dataAccess);
	}
	
	@Nonnull
	public static RecipeType<? extends AbstractCookingRecipe> getRecipeType(TileEntityUnltdFurnaceBase furnace) {
		RecipeDefiner<AbstractCookingRecipe> toType = furnace.getRecipeDefiner();
		ItemStack definer = furnace.stacks.get(SLOT_RECIPE_DEFINER);
		if(definer.isEmpty() || toType == null) {
			return RecipeType.SMELTING;
		}
		
		return toType.getRecipeType(definer, RecipeType.SMELTING);
	}
	
	public static RecipeManager.CachedCheck<Container, ? extends AbstractCookingRecipe> getQuickCheck(TileEntityUnltdFurnaceBase furnace) {
		return furnace.quickCheck.computeIfAbsent(getRecipeType(furnace), type -> RecipeManager.createCheck(type));
	}

	@Nonnull
	public static AbstractCookingRecipe getRecipe(TileEntityUnltdFurnaceBase furnace, Level level) {
		return getQuickCheck(furnace).getRecipeFor(furnace, level).orElse(null);
	}
	
	public static boolean isBucketLike(ItemStack stack) {
		if(stack.is(Items.BUCKET)) {
			return true;
		} else {
			IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(stack).orElse(null);
			if(fluidHandler == null)
				return false;
				
			// 空の場合のみ
			FluidStack fluidStack = fluidHandler.drain(FluidType.BUCKET_VOLUME, FluidAction.SIMULATE);
			return fluidStack.isEmpty();
		}
	}
	
	public static boolean isWaterBucketLike(ItemStack stack) {
		if(stack.is(Items.WATER_BUCKET)) {
			return true;
		} else {
			IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(stack).orElse(null);
			if(fluidHandler == null)
				return false;
			
			// 水が入っている場合のみ
			FluidStack fluidStack = fluidHandler.drain(FluidType.BUCKET_VOLUME, FluidAction.SIMULATE);
			return !fluidStack.isEmpty() && (fluidStack.getFluid() == Fluids.WATER);
		}
	}

	@Override
	public int getMaxStackSize() {
		return this.getFurnaceInfo().unltdStack ? Integer.MAX_VALUE : 64;
	}
	
	public boolean isLit() {
		return this.litTime > 0;
	}
	
	public void load(CompoundTag compoundtag) {
		super.load(compoundtag);
		this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(compoundtag, this.stacks);
		this.litTime = compoundtag.getInt("BurnTime");
		this.cookingProgress = compoundtag.getInt("CookTime");
		this.cookingTotalTime = compoundtag.getInt("CookTimeTotal");
		this.litDuration = getBurnDuration(this);
		CompoundTag recipesUsedTag = compoundtag.getCompound("RecipesUsed");
		
		for(String s : recipesUsedTag.getAllKeys()) {
			this.recipesUsed.put(ResourceLocation.parse(s), recipesUsedTag.getInt(s));
		}
	}

	protected void saveAdditional(CompoundTag compoundtag) {
		super.saveAdditional(compoundtag);
		compoundtag.putInt("BurnTime", this.litTime);
		compoundtag.putInt("CookTime", this.cookingProgress);
		compoundtag.putInt("CookTimeTotal", this.cookingTotalTime);
		ContainerHelper.saveAllItems(compoundtag, this.stacks);
		CompoundTag recipesUsedTag = new CompoundTag();
		this.recipesUsed.forEach((recipe, number) -> {
			recipesUsedTag.putInt(recipe.toString(), number);
		});
		compoundtag.put("RecipesUsed", recipesUsedTag);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityUnltdFurnaceBase furnace) {
		boolean defaultLitState = furnace.isLit();
		boolean updated = false;
		
		if (furnace.isLit()) {
			--furnace.litTime;
		}

		ItemStack input = furnace.stacks.get(SLOT_INPUT);
		ItemStack fuel = furnace.stacks.get(SLOT_FUEL);
		boolean hasInput = !input.isEmpty();
		boolean hasfuel = !fuel.isEmpty();
		if ((furnace.isLit() || hasfuel) && hasInput) {
			Recipe<?> recipe;
			if(hasInput) {
				recipe = getRecipe(furnace, level);
			} else {
				recipe = null;
			}
			
			int stackLim = furnace.getMaxStackSize();
			boolean canBurn = furnace.canBurn(level.registryAccess(), recipe, furnace.stacks, stackLim);
			if (!furnace.isLit() && canBurn) {
				furnace.litTime = getBurnDuration(furnace);
				furnace.litDuration = furnace.litTime;

				if (furnace.isLit()) {
					updated = true;
					
					if(fuel.hasCraftingRemainingItem()) {
						furnace.stacks.set(SLOT_FUEL, fuel.getCraftingRemainingItem());
					} else if (hasfuel) {
						if(!furnace.getFurnaceInfo().saveFuel) {
							fuel.shrink(1);
						}
						
						if (fuel.isEmpty()) {
							furnace.stacks.set(SLOT_FUEL, fuel.getCraftingRemainingItem());
						}
					}
				}
			}

			if(furnace.isLit() && canBurn) {
				++furnace.cookingProgress;
					
				if(furnace.cookingProgress >= furnace.cookingTotalTime) {
					furnace.cookingProgress = 0;
					furnace.cookingTotalTime = getTotalCookTime((AbstractCookingRecipe)recipe, furnace);
					
					// まとめて精錬も考慮
					int burned = furnace.burn(level.registryAccess(), recipe, furnace.stacks, stackLim);
					furnace.setRecipeUsed(recipe, burned);
					
					updated = true;
				}
			} else {
				furnace.cookingProgress = 0;
			}
		} else if (!furnace.isLit() && furnace.cookingProgress > 0) {
			furnace.cookingProgress = Mth.clamp(furnace.cookingProgress - 2, 0, furnace.cookingTotalTime);
		}

		// 燃えてる状態が切り替わってたら更新
		boolean currentLitState = furnace.isLit();
		if (Boolean.logicalXor(defaultLitState, currentLitState)) {
			updated = true;
			state = state.setValue(AbstractFurnaceBlock.LIT, Boolean.valueOf(currentLitState));
			level.setBlock(pos, state, 3);
		}
		
		if(updated) {
			setChanged(level, pos, state);
		}
	}
    
	private boolean canBurn(RegistryAccess registry, @Nullable Recipe<?> recipe, NonNullList<ItemStack> stacks, int stackLimit) {
		ItemStack input = stacks.get(SLOT_INPUT);
		if (!input.isEmpty() && recipe != null) {
			@SuppressWarnings("unchecked")
			ItemStack result = ((Recipe<WorldlyContainer>)recipe).assemble(this, registry);
			if (result.isEmpty()) {
				return false;
			} else {
				ItemStack output = stacks.get(SLOT_OUTPUT);
				if (output.isEmpty()) {
					return true;
				} else if (!ItemStack.isSameItem(output, result)) {
					return false;
				} else  {
					int smelting = this.getFurnaceInfo().smeltingAll ? input.getCount() : 1;
					int smelted = result.getCount() * smelting;
					int limit = this.getFurnaceInfo().unltdStack ? Integer.MAX_VALUE : stackLimit;
					// オーバーフロー対策
					int capacity = limit - output.getCount();
					return capacity >= smelted;
				}
			}
		} else {
			return false;
		}
	}
	
	public int burn(RegistryAccess registry, @Nullable Recipe<?> recipe, NonNullList<ItemStack> stacks, int stackLimit) {
		if (recipe != null && this.canBurn(registry, recipe, stacks, stackLimit)) {
			ItemStack input = stacks.get(SLOT_INPUT);
			@SuppressWarnings("unchecked")
			ItemStack result = ((Recipe<WorldlyContainer>)recipe).assemble(this, registry);
			ItemStack output = stacks.get(SLOT_OUTPUT);

			// 1回で精錬する個数
			int burning = this.getFurnaceInfo().smeltingAll ? input.getCount() : 1;
			int burned = result.getCount() * burning;
			if(output.isEmpty()) {
				ItemStack resultCopy = result.copy();
				resultCopy.setCount(burned);
				stacks.set(SLOT_OUTPUT, resultCopy);
			} else if (output.getItem() == result.getItem()) {
				output.grow(burned);
			}

			// 濡れたスポンジから水を取り出す
			ItemStack fuel = stacks.get(SLOT_FUEL);
			boolean isWetSponge = input.is(Blocks.WET_SPONGE.asItem());
			if(isWetSponge && !fuel.isEmpty()) {
				
				if(fuel.is(Items.BUCKET)) {
					this.setItem(SLOT_FUEL, new ItemStack(Items.WATER_BUCKET));
				} else {
					IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(fuel).orElse(null);
					if(fluidHandler != null) {
						FluidStack water = new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME);
						fluidHandler.fill(water, FluidAction.EXECUTE);
					}
				}
			}
			
			if(!this.getFurnaceInfo().saveInput) {
				input.shrink(burning);
			}
			return burning;
		} else {
			return 0;
		}
	}
	
	protected static int getBurnDuration(TileEntityUnltdFurnaceBase furnace) {
		ItemStack fuel = furnace.stacks.get(SLOT_FUEL);
		RecipeType<?> recipeType = getRecipeType(furnace);
		return furnace.getFurnaceInfo().getBurnDuration(fuel, recipeType);
	}

	protected static int getTotalCookTime(AbstractCookingRecipe recipe, TileEntityUnltdFurnaceBase furnace) {
		return furnace.getFurnaceInfo().getCookTime(recipe);
	}
	
	protected static int getTotalCookTime(Level level, TileEntityUnltdFurnaceBase furnace) {
		AbstractCookingRecipe recipe = getRecipe(furnace, level);
		return getTotalCookTime(recipe, furnace);
	}
	
	public static boolean isFuel(ItemStack stack, RecipeType<?> recipeType) {
		return ForgeHooks.getBurnTime(stack, recipeType) > 0;
	}
	
	public int[] getSlotsForFace(Direction direction) {
		return switch (direction) {
		case UP -> SLOTS_TOP;
		case DOWN -> SLOTS_BOTTOM;
		default -> SLOTS_SIDES;
		};
	}

	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
		return this.canPlaceItem(index, itemStackIn);
	}

	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		if (direction == Direction.DOWN && index == SLOT_FUEL) {
			return isWaterBucketLike(stack);
		} else { 
			return true;
		}
	}

	public int getContainerSize() {
		return this.stacks.size();
	}

	public boolean isEmpty() {
		for(ItemStack itemstack : this.stacks) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public ItemStack getItem(int index) {
		return this.stacks.get(index);
	}

	public ItemStack removeItem(int index, int count) {
		return ContainerHelper.removeItem(this.stacks, index, count);
	}

	public ItemStack removeItemNoUpdate(int index) {
		return ContainerHelper.takeItem(this.stacks, index);
	}

	public void setItem(int index, ItemStack stack) {
		ItemStack currentStack = this.stacks.get(index);
		boolean isSameStack = !stack.isEmpty() && ItemStack.isSameItemSameTags(currentStack, stack);
		boolean countChanged = (this.getFurnaceInfo().smeltingAll && currentStack.getCount() != stack.getCount());
		this.stacks.set(index, stack);
		if (stack.getCount() > this.getMaxStackSize()) {
			stack.setCount(this.getMaxStackSize());
		}

		if (index == SLOT_INPUT && (!isSameStack || countChanged)) {
			this.cookingTotalTime = getTotalCookTime(this.level, this);
			this.cookingProgress = 0;
			this.setChanged();
		}

	}

	public boolean stillValid(Player player) {
		return Container.stillValidBlockEntity(this, player);
	}

	public boolean canPlaceItem(int index, ItemStack stack) {
		if (index == SLOT_OUTPUT) {
			return false;
		} else if(index == SLOT_RECIPE_DEFINER) {
			return this.getRecipeDefiner().isDefiner(stack);
		} else if(index != SLOT_FUEL) {
			return true;
		} else {
			ItemStack fuel = this.stacks.get(SLOT_FUEL);
			return isFuel(fuel, getRecipeType(this)) || (isBucketLike(stack) && !ItemStack.isSameItem(stack, fuel));
		}
	}

	public void clearContent() {
		this.stacks.clear();
	}

	/** 1処理で複数回精錬するとき用 */
	public void setRecipeUsed(@Nullable Recipe<?> recipe, int count) {
		if (recipe != null && count > 0) {
			ResourceLocation recipeId = recipe.getId();
			this.recipesUsed.addTo(recipeId, count);
		}
	}
	
	public void setRecipeUsed(@Nullable Recipe<?> recipe) {
		this.setRecipeUsed(recipe, 1);
	}

	@Nullable
	public Recipe<?> getRecipeUsed() {
		return null;
	}
	
	public void awardUsedRecipes(Player player, List<ItemStack> stacks) {
	}

	public void awardUsedRecipesAndPopExperience(ServerPlayer player) {
		List<Recipe<?>> recipes = this.getRecipesToAwardAndPopExperience(player.serverLevel(), player.position());
		player.awardRecipes(recipes);

		for(Recipe<?> recipe : recipes) {
			if (recipe != null) {
				player.triggerRecipeCrafted(recipe, this.stacks);
			}
		}
		
		this.recipesUsed.clear();
	}

	public List<Recipe<?>> getRecipesToAwardAndPopExperience(ServerLevel level, Vec3 pos) {
		List<Recipe<?>> recipes = Lists.newArrayList();
		
		for(Object2IntMap.Entry<ResourceLocation> entry : this.recipesUsed.object2IntEntrySet()) {
			level.getRecipeManager().byKey(entry.getKey()).ifPresent((recipe) -> {
				recipes.add(recipe);
				createExperience(level, pos, entry.getIntValue(), ((AbstractCookingRecipe)recipe).getExperience());
			});
		}
		return recipes;
	}

	private static void createExperience(ServerLevel level, Vec3 pos, int count, float exp) {
		float total = (float)count * exp;
		int value = Mth.floor(total);
		
		float f = Mth.frac(total);
		if (f != 0.0F && Math.random() < (double)f) {
			++value;
		}
		
		ExperienceOrb.award(level, pos, value);
	}

	public void fillStackedContents(StackedContents contents) {
		for(ItemStack itemstack : this.stacks) {
			contents.accountStack(itemstack);
		}
	}

	LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (capability == ForgeCapabilities.ITEM_HANDLER && facing != null && !this.remove) {
			return switch (facing) {
			case UP -> handlers[0].cast();
			case DOWN -> handlers[1].cast();
			default -> handlers[2].cast();
			};
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		for (int x = 0; x < handlers.length; ++x)
			handlers[x].invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		this.handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
	}
}
