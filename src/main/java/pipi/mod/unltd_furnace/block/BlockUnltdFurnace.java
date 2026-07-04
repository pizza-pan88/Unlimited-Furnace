package pipi.mod.unltd_furnace.block;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import pipi.mod.unltd_furnace.network.PacketCookingRecipeDefiner;
import pipi.mod.unltd_furnace.network.UnltdFurnacePacketHandler;
import pipi.mod.unltd_furnace.tileentity.TileEntityUnltdFurnaceBase;

public class BlockUnltdFurnace<T extends TileEntityUnltdFurnaceBase> extends BaseEntityBlock {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	
	private final Supplier<BlockEntityType<T>> supplier;
	public BlockUnltdFurnace(UnaryOperator<Properties> operator, Supplier<BlockEntityType<T>> supplier) {
		super(operator.apply(Properties.of()
				.strength(2.5f)
				.sound(SoundType.METAL)
				.mapColor(MapColor.METAL)
				.requiresCorrectToolForDrops()
		));
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(LIT, Boolean.valueOf(false))
		);
		this.supplier = supplier;
	}
	/** Use an {@link UnaryOperator} instead of the {@link Properties}. */
	@Deprecated
	public BlockUnltdFurnace(Properties properties, Supplier<BlockEntityType<T>> supplier) {
		this(
				t -> properties.strength(2.5f).sound(SoundType.METAL).mapColor(MapColor.METAL).requiresCorrectToolForDrops(),
				supplier
		);
	}

	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return this.supplier.get().create(pos, state);
	}

	@Nullable
	public <U extends BlockEntity> BlockEntityTicker<U> getTicker(Level level, BlockState state, BlockEntityType<U> type) {
		return createUnltdFurnaceTicker(level, type, this.supplier.get());
	}
	
	protected void openContainer(Level level, BlockPos pos, Player player) {
		if(level.isClientSide) {
			return;
		}
			
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof TileEntityUnltdFurnaceBase unltdFurnace) {
			
			NetworkHooks.openScreen((ServerPlayer)player, unltdFurnace);
			UnltdFurnacePacketHandler.INSTANCE.send(
					PacketDistributor.PLAYER.with(() -> (ServerPlayer)player),
					new PacketCookingRecipeDefiner(unltdFurnace.getRecipeDefiner())
			);
		}
	}
	
	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
		return state.getValue(LIT) ? 15 : super.getLightEmission(state, level, pos);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
		if (state.getValue(LIT)) {
			double x = (double)pos.getX() + 0.5D;
			double y = (double)pos.getY();
			double z = (double)pos.getZ() + 0.5D;
			if (rand.nextDouble() < 0.1D) {
				level.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
			}

			Direction direction = state.getValue(FACING);
			Direction.Axis axis = direction.getAxis();
			double delta = 0.52D;
			double d4 = rand.nextDouble() * 0.6D - 0.3D;
			double dx = (axis == Direction.Axis.X) ? (double)direction.getStepX() * delta : d4;
			double dy = rand.nextDouble() * 6.0D / 16.0D;
			double dz = (axis == Direction.Axis.Z) ? (double)direction.getStepZ() * delta : d4;
			level.addParticle(ParticleTypes.SMOKE, x + dx, y + dy, z + dz, 0.0D, 0.0D, 0.0D);
			level.addParticle(ParticleTypes.FLAME, x + dx, y + dy, z + dz, 0.0D, 0.0D, 0.0D);
		}
	}

	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			this.openContainer(level, pos, player);
			return InteractionResult.CONSUME;
		}
	}
	
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		if (stack.hasCustomHoverName()) {
			BlockEntity blockentity = level.getBlockEntity(pos);
			if (blockentity instanceof TileEntityUnltdFurnaceBase unltdFurnace) {
				unltdFurnace.setCustomName(stack.getHoverName());
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean p_48717_) {
		if (!oldState.is(newState.getBlock())) {
			BlockEntity blockentity = level.getBlockEntity(pos);
			
			if (blockentity instanceof TileEntityUnltdFurnaceBase unltdFurnace) {
				if (level instanceof ServerLevel serverLevel) {
					Containers.dropContents(level, pos, unltdFurnace);
					unltdFurnace.getRecipesToAwardAndPopExperience(serverLevel, Vec3.atCenterOf(pos));
				}
				
				level.updateNeighbourForOutputSignal(pos, this);
			}
			
			super.onRemove(oldState, level, pos, newState, p_48717_);
		}
	}

	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
	}
	
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@SuppressWarnings("deprecation")
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, LIT);
	}
		   
	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createUnltdFurnaceTicker(Level level, BlockEntityType<T> type, BlockEntityType<? extends TileEntityUnltdFurnaceBase> furnaceType) {
		return level.isClientSide ? null : createTickerHelper(type, furnaceType, TileEntityUnltdFurnaceBase::serverTick);
	}
	
}
