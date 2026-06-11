package pipi.mod.unltd_furnace.tileentity;

public class TileEntityUnlimitedFurnace extends TileEntityUnltdFurnaceBase {

	@Override
	public String getContainerName() {
		return "tile.unlimited_furnace.name";
	}

	@Override
	public UnltdFurnaceInfo getFurnaceInfo() {
		return UnltdFurnaceInfo.UNLIMITED;
	}

}
