package pipi.mod.unltd_furnace.tileentity;

public class TileEntityInexhaustibleFurnace extends TileEntityUnltdFurnaceBase {

	@Override
	public String getContainerName() {
		return "tile.inexhaustible_furnace.name";
	}

	@Override
	public UnltdFurnaceInfo getFurnaceInfo() {
		return UnltdFurnaceInfo.INEXHAUSTIBLE;
	}

}
