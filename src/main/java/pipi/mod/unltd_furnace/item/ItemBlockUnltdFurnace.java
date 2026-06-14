package pipi.mod.unltd_furnace.item;

import java.util.function.Supplier;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class ItemBlockUnltdFurnace extends BlockItem {
	
	public static Supplier<ItemBlockUnltdFurnace> of(Supplier<Block> block) {
		return () -> new ItemBlockUnltdFurnace(block);
	}
	
	public ItemBlockUnltdFurnace(Supplier<Block> block) {
		super(block.get(), new Properties());
	}
	
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entityItem) {
		entityItem.setInvulnerable(true);;
		return super.onEntityItemUpdate(stack, entityItem);
	}
}
