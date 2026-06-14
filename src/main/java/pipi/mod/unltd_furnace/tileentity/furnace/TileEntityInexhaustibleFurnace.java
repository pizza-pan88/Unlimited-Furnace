package pipi.mod.unltd_furnace.tileentity.furnace;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.level.block.state.BlockState;
import pipi.mod.unltd_furnace.object.UnltdFurnaceBlockEntityTypes;
import pipi.mod.unltd_furnace.tileentity.RecipeDefiner;
import pipi.mod.unltd_furnace.tileentity.TileEntityUnltdFurnaceBase;
import pipi.mod.unltd_furnace.tileentity.UnltdFurnaceInfo;

public class TileEntityInexhaustibleFurnace extends TileEntityUnltdFurnaceBase {
	public static final Component NAME = Component.translatable("block.unltd_furnace.inexhaustible_furnace");

	public TileEntityInexhaustibleFurnace(BlockPos pos, BlockState state) {
		super(UnltdFurnaceBlockEntityTypes.INEXHAUSTIBLE_FURNACE.get(), pos, state);
	}

	@Override
	protected Component getDefaultName() {
		return NAME;
	}

	@Override
	public UnltdFurnaceInfo getFurnaceInfo() {
		return UnltdFurnaceInfo.INEXHAUSTIBLE;
	}

	@Override
	public RecipeDefiner<AbstractCookingRecipe> getRecipeDefiner() {
		return RecipeDefiner.Cooking.UNLTF_FURNACE_DEFINER;
	}

}
