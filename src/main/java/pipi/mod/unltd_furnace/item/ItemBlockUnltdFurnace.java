package pipi.mod.unltd_furnace.item;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;

public class ItemBlockUnltdFurnace extends ItemBlock {

	public ItemBlockUnltdFurnace(Block block) {
		super(block);
		this.setRegistryName(block.getRegistryName());
	}
	
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		entityItem.setEntityInvulnerable(true);
		return super.onEntityItemUpdate(entityItem);
	}
}
