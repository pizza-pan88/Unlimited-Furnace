package pipi.mod.unltd_furnace.tileentity;

public class TileEntityParallelFurnace extends TileEntityUnltdFurnaceBase {

	@Override
	public String getContainerName() {
		return "tile.parallel_furnace.name";
	}

	@Override
	public UnltdFurnaceInfo getFurnaceInfo() {
		return UnltdFurnaceInfo.PARALLEL;
	}

}
