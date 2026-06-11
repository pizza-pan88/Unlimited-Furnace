package pipi.mod.unltd_furnace;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import pipi.mod.unltd_furnace.block.BlockUnltdFurnace;

public class UnltdFurnaceGuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		Block block = world.getBlockState(pos).getBlock();
		if(block instanceof BlockUnltdFurnace) {
			return ((BlockUnltdFurnace)block).getContainer(ID, player, world, pos);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		Block block = world.getBlockState(pos).getBlock();
		if(block instanceof BlockUnltdFurnace) {
			return ((BlockUnltdFurnace)block).getGui(ID, player, world, pos);
		}
		return null;
	}

}
