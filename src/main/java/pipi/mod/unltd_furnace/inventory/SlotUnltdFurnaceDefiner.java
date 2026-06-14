package pipi.mod.unltd_furnace.inventory;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import pipi.mod.unltd_furnace.UnlimitedFurnace;

public class SlotUnltdFurnaceDefiner extends Slot {
	public static final ResourceLocation TEXTURE_EMPTY_SLOT = UnlimitedFurnace.locate("slot/furnace");
	
	private final MenuUnltdFurnace menu;
	public SlotUnltdFurnaceDefiner(MenuUnltdFurnace menu, Container container, int id, int x, int y) {
		super(container, id, x, y);
		this.menu = menu;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return this.menu.isRecipeDefiner(stack);
	}
	
	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return 1;
	}
	
	@Nullable
	public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
		return Pair.of(InventoryMenu.BLOCK_ATLAS, TEXTURE_EMPTY_SLOT);
	}
}