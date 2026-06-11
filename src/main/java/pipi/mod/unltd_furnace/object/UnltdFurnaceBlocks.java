package pipi.mod.unltd_furnace.object;

import java.lang.reflect.Field;
import java.util.function.Consumer;

import net.minecraft.block.Block;
import pipi.mod.unltd_furnace.UnlimitedFurnace;
import pipi.mod.unltd_furnace.block.BlockUnltdFurnace;
import pipi.mod.unltd_furnace.tileentity.TileEntityFlashFurnace;
import pipi.mod.unltd_furnace.tileentity.TileEntityInexhaustibleFurnace;
import pipi.mod.unltd_furnace.tileentity.TileEntityParallelFurnace;
import pipi.mod.unltd_furnace.tileentity.TileEntityUnlimitedFurnace;

public final class UnltdFurnaceBlocks {

	public static final Block PARALLEL_FURNACE =
			new BlockUnltdFurnace("parallel_furnace", 0, TileEntityParallelFurnace::new)
			.setResistance(2000.0f);
	public static final Block FLASH_FURNACE =
			new BlockUnltdFurnace("flash_furnace", 0, TileEntityFlashFurnace::new)
			.setLightValue(10).setResistance(500000.0f);
	public static final Block INEXHAUSTIBLE_FURNACE =
			new BlockUnltdFurnace("inexhaustible_furnace", 0,  TileEntityInexhaustibleFurnace::new)
			.setLightValue(5).setResistance(1000.0f);
	public static final Block UNLIMITED_FURNACE =
			new BlockUnltdFurnace("unlimited_furnace", 0,  TileEntityUnlimitedFurnace::new)
			.setLightValue(15).setResistance(Integer.MAX_VALUE);
	
	public static void register(Consumer<Block> consumer) {
		if(consumer == null) return;
		
		Field[] fields = UnltdFurnaceBlocks.class.getFields();
		for(Field field : fields) {
			try {
				Object obj = field.get((UnltdFurnaceBlocks)null);
				if(obj instanceof Block) {
					consumer.accept((Block)obj);
				}
			} catch(IllegalAccessException e) {
				UnlimitedFurnace.getLogger().warn("Failed to access : {}", field.getName());
			} catch(NullPointerException e) {
				UnlimitedFurnace.getLogger().error("Non static field : {}", field.getName());
				e.printStackTrace();
			}
		}
	}

}
