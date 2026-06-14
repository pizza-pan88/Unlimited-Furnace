package pipi.mod.unltd_furnace.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import pipi.mod.unltd_furnace.tileentity.furnace.TileEntityUnlimitedFurnace;

public class SlotUnltdFurnaceFuel extends Slot {
	private final MenuUnltdFurnace menu;

	public SlotUnltdFurnaceFuel(MenuUnltdFurnace menu, Container container, int id, int x, int y) {
		super(container, id, x, y);
		this.menu = menu;
	}

	public boolean mayPlace(ItemStack stack) {
		return this.menu.isFuel(stack) || isBucketLike(stack);
	}

	public int getMaxStackSize(ItemStack stack) {
		return isBucketLike(stack) ? 1 : super.getMaxStackSize(stack);
	}

	public static boolean isBucketLike(ItemStack stack) {
		return TileEntityUnlimitedFurnace.isBucketLike(stack);
	}
	   
}