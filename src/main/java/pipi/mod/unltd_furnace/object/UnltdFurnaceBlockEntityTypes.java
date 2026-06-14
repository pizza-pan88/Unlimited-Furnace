package pipi.mod.unltd_furnace.object;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import pipi.mod.unltd_furnace.ModInfo;
import pipi.mod.unltd_furnace.tileentity.furnace.TileEntityFlashFurnace;
import pipi.mod.unltd_furnace.tileentity.furnace.TileEntityInexhaustibleFurnace;
import pipi.mod.unltd_furnace.tileentity.furnace.TileEntityParallelFurnace;
import pipi.mod.unltd_furnace.tileentity.furnace.TileEntityUnlimitedFurnace;

public final class UnltdFurnaceBlockEntityTypes {
	
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES;
	
	public static final RegistryObject<BlockEntityType<TileEntityParallelFurnace>> PARALLEL_FURNACE;
	public static final RegistryObject<BlockEntityType<TileEntityFlashFurnace>> FLASH_FURNACE;
	public static final RegistryObject<BlockEntityType<TileEntityInexhaustibleFurnace>> INEXHAUSTIBLE_FURNACE;
	public static final RegistryObject<BlockEntityType<TileEntityUnlimitedFurnace>> UNLIMITED_FURNACE;
	
	static {
		BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ModInfo.MODID);
	
		PARALLEL_FURNACE = BLOCK_ENTITY_TYPES.register(
				"parallel_furnace",
				() -> of(TileEntityParallelFurnace::new, UnltdFurnaceBlocks.PARALLEL_FURNACE.get())
		);
		FLASH_FURNACE = BLOCK_ENTITY_TYPES.register(
				"flash_furnace",
				() -> of(TileEntityFlashFurnace::new, UnltdFurnaceBlocks.FLASH_FURNACE.get())
		);
		INEXHAUSTIBLE_FURNACE = BLOCK_ENTITY_TYPES.register(
				"inexhaustible_furnace",
				() -> of(TileEntityInexhaustibleFurnace::new, UnltdFurnaceBlocks.INEXHAUSTIBLE_FURNACE.get())
		);
		UNLIMITED_FURNACE = BLOCK_ENTITY_TYPES.register(
				"unlimited_furnace",
				() -> of(TileEntityUnlimitedFurnace::new, UnltdFurnaceBlocks.UNLIMITED_FURNACE.get())
		);
	}
	
	private static <T extends BlockEntity>
	BlockEntityType<T> of(BlockEntityType.BlockEntitySupplier<T> supplier, Block... blocks) {
		return BlockEntityType.Builder.of(supplier, blocks).build(null);
	}
	
}
