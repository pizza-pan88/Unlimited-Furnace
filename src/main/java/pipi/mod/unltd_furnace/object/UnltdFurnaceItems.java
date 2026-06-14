package pipi.mod.unltd_furnace.object;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import pipi.mod.unltd_furnace.ModInfo;
import pipi.mod.unltd_furnace.item.ItemBlockUnltdFurnace;

public final class UnltdFurnaceItems {

	public static final DeferredRegister<Item> ITEMS;

	static {
		ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModInfo.MODID);
		
		UnltdFurnaceBlocks.BLOCKS.getEntries().forEach(
				block -> ITEMS.register(block.getId().getPath(), ItemBlockUnltdFurnace.of(block))
		);
	}
	
}
