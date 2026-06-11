package pipi.mod.unltd_furnace.tileentity;

public class TileEntityFlashFurnace extends TileEntityUnltdFurnaceBase {

	@Override
	public String getContainerName() {
		return "tile.flash_furnace.name";
	}

	@Override
	public UnltdFurnaceInfo getFurnaceInfo() {
		return UnltdFurnaceInfo.FLASH;
	}

}
