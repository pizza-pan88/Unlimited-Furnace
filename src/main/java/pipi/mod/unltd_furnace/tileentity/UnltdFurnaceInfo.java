package pipi.mod.unltd_furnace.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

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
	public int getCookTime(ItemStack stack) {
		return this.instantSmelting ? 1 : 200;
	}
	
	/** 燃料の燃焼が持続する時間 */
	public int getFuelBurnTime(ItemStack stack) {
		return TileEntityFurnace.getItemBurnTime(stack);
	}

}
