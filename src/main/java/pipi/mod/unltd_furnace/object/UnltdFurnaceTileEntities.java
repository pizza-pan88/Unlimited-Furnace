package pipi.mod.unltd_furnace.object;

import net.minecraftforge.fml.common.registry.GameRegistry;
import pipi.mod.unltd_furnace.UnlimitedFurnace;
import pipi.mod.unltd_furnace.tileentity.TileEntityFlashFurnace;
import pipi.mod.unltd_furnace.tileentity.TileEntityInexhaustibleFurnace;
import pipi.mod.unltd_furnace.tileentity.TileEntityParallelFurnace;
import pipi.mod.unltd_furnace.tileentity.TileEntityUnlimitedFurnace;

public final class UnltdFurnaceTileEntities {

	public static void register() {
		GameRegistry.registerTileEntity(
				TileEntityParallelFurnace.class,
				UnlimitedFurnace.locate("parallel_furnace")
		);
		GameRegistry.registerTileEntity(
				TileEntityFlashFurnace.class,
				UnlimitedFurnace.locate("flash_furnace")
		);
		GameRegistry.registerTileEntity(
				TileEntityInexhaustibleFurnace.class,
				UnlimitedFurnace.locate("inexhaustible_furnace")
		);
		GameRegistry.registerTileEntity(
				TileEntityUnlimitedFurnace.class,
				UnlimitedFurnace.locate("unlimited_furnace")
		);
	}
	
}
