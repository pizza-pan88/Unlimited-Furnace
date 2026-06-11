package pipi.mod.unltd_furnace.block;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pipi.mod.unltd_furnace.UnlimitedFurnace;
import pipi.mod.unltd_furnace.tileentity.TileEntityUnltdFurnaceBase;

public class BlockUnltdFurnace extends BlockContainer {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	private static final int FACING_LENGTH = EnumFacing.VALUES.length;
	public static final PropertyBool LIT = PropertyBool.create("lit");
	private static boolean keepInventory;

	private final int guiId;
	private final Supplier<TileEntityUnltdFurnaceBase> supplier;
	public BlockUnltdFurnace(String name, int guiId, Supplier<TileEntityUnltdFurnaceBase> supplier) {
		super(Material.IRON);
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(FACING, EnumFacing.NORTH)
				.withProperty(LIT, false)
		);
		this.guiId = guiId;
		this.supplier = supplier;
		
		this.setCreativeTab(CreativeTabs.DECORATIONS);
		this.setRegistryName(UnlimitedFurnace.locate(name));
		this.setUnlocalizedName(name);
		this.setHardness(2.5f);
		this.setSoundType(SoundType.METAL);
	}
	
	public Container getContainer(int ID, EntityPlayer player, World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		return new ContainerFurnace(player.inventory, (IInventory)te);
	}
	
	public Gui getGui(int ID, EntityPlayer player, World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		return new GuiFurnace(player.inventory, (IInventory)te);
	}
	
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return this.supplier.get();
	}

	public Block setLightValue(int value) {
		this.lightValue = value;
		return this;
	}
	
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.getValue(LIT) ? 15 : this.lightValue;
	}

	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		this.setDefaultFacing(worldIn, pos, state);
	}

	private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state) {
		if (!worldIn.isRemote) {
			IBlockState iblockstate = worldIn.getBlockState(pos.north());
			IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
			IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
			IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
			EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);

			if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock()) {
				enumfacing = EnumFacing.SOUTH;
			} else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock()) {
				enumfacing = EnumFacing.NORTH;
			} else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock()) {
				enumfacing = EnumFacing.EAST;
			} else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock()) {
				enumfacing = EnumFacing.WEST;
			}
			
			worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
		}
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (stateIn.getValue(LIT)) {
			EnumFacing facing = (EnumFacing)stateIn.getValue(FACING);
			double x = (double)pos.getX() + 0.5D;
			double y = (double)pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
			double z = (double)pos.getZ() + 0.5D;
			double delta = 0.52D;
			double d4 = rand.nextDouble() * 0.6D - 0.3D;

			if (rand.nextDouble() < 0.1D) {
				worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
			}

			switch (facing) {
			case WEST:
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x - delta, y, z + d4, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.FLAME,         x - delta, y, z + d4, 0.0D, 0.0D, 0.0D);
				break;
			case EAST:
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + delta, y, z + d4, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.FLAME,         x + delta, y, z + d4, 0.0D, 0.0D, 0.0D);
				break;
			case NORTH:
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + d4, y, z - delta, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.FLAME,         x + d4, y, z - delta, 0.0D, 0.0D, 0.0D);
				break;
			case SOUTH:
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + d4, y, z + delta, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.FLAME,         x + d4, y, z + delta, 0.0D, 0.0D, 0.0D);
			default:
			}
		}
	}

	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) {
			return true;
		} else {
			TileEntity tileentity = worldIn.getTileEntity(pos);

			if (tileentity instanceof TileEntityUnltdFurnaceBase) {
				playerIn.openGui(UnlimitedFurnace.instance, this.guiId, worldIn, pos.getX(), pos.getY(), pos.getZ());
			}
			return true;
		}
    }

	public static void setState(boolean active, World worldIn, BlockPos pos) {
		IBlockState blockState = worldIn.getBlockState(pos);
		TileEntity te = worldIn.getTileEntity(pos);
		//keepInventory = true;

		worldIn.setBlockState(pos, blockState.withProperty(LIT, active));
		//worldIn.setBlockState(pos, blockState.withProperty(LIT, active), 3);

		//keepInventory = false;

		if (te != null) {
			te.validate();
			worldIn.setTileEntity(pos, te);
		}
	}

	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);

		if (stack.hasDisplayName()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			
			if (tileentity instanceof TileEntityUnltdFurnaceBase) {
				((TileEntityUnltdFurnaceBase)tileentity).setCustomInventoryName(stack.getDisplayName());
			}
		}
	}

	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!keepInventory) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			
			if (tileentity instanceof TileEntityUnltdFurnaceBase) {
				InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityUnltdFurnaceBase)tileentity);
				worldIn.updateComparatorOutputLevel(pos, this);
			}
		}
		
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		ItemStack stack = super.getPickBlock(state, target, world, pos, player);
		TileEntityUnltdFurnaceBase te = (TileEntityUnltdFurnaceBase) world.getTileEntity(pos);
		
		if (te.hasCustomName()) {
			stack.setStackDisplayName(te.getName());
		}
		
		return stack;
	}
    
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
		return Container.calcRedstone(worldIn.getTileEntity(pos));
	}

	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.getFront(meta);
		// lit=false: 0~5, lit=true: 6~11
		boolean isLit = (meta >= FACING_LENGTH);

		if (facing.getAxis() == EnumFacing.Axis.Y) {
			facing = EnumFacing.NORTH;
		}
		
		return this.getDefaultState().withProperty(FACING, facing).withProperty(LIT, isLit);
	}

	public int getMetaFromState(IBlockState state) {
		// facing: 0~5
		int index = state.getValue(FACING).getIndex();
		// lit=false: 0~5, lit=true: 6~11
		return index + (state.getValue(LIT) ? FACING_LENGTH : 0);
	}

	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
	}

	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {FACING, LIT});
	}
}
