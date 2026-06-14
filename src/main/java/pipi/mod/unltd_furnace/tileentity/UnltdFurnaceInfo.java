package pipi.mod.unltd_furnace.tileentity;

import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;

public class UnltdFurnaceInfo {
	public static final UnltdFurnaceInfo VANILLA = new UnltdFurnaceInfo(false, false, false, false, false);
	public static final UnltdFurnaceInfo PARALLEL = new UnltdFurnaceInfo(false, false, true, true, false);
	public static final UnltdFurnaceInfo FLASH = new UnltdFurnaceInfo(false, false, false, false, true);
	public static final UnltdFurnaceInfo INEXHAUSTIBLE = new UnltdFurnaceInfo(true, true, false, false, false);
	public static final UnltdFurnaceInfo UNLIMITED = new UnltdFurnaceInfo(true, true, true, true, true);

	public final boolean saveInput, saveFuel, unltdStack, smeltingAll, instantSmelting;
	public UnltdFurnaceInfo(boolean saveInput, boolean saveFuel, boolean unltdStack,
			boolean smeltingAll, boolean instantSmelting) {
		this.saveInput = saveInput;
		this.saveFuel = saveFuel;
		this.unltdStack = unltdStack;
		this.smeltingAll = smeltingAll;
		this.instantSmelting = instantSmelting;
	}

	/** アイテムの精錬にかかる時間 */
	public int getCookTime(@Nullable AbstractCookingRecipe recipe) {
		return this.instantSmelting ? 1 : (recipe == null ? 200 : recipe.getCookingTime());
	}
	
	/** 燃料の燃焼が持続する時間 */
	public int getBurnDuration(ItemStack stack, RecipeType<?> recipeType) {
		return ForgeHooks.getBurnTime(stack, recipeType);
	}

}
