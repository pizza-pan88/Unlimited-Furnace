package pipi.mod.unltd_furnace.object;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import pipi.mod.unltd_furnace.ModInfo;
import pipi.mod.unltd_furnace.block.BlockUnltdFurnace;
import pipi.mod.unltd_furnace.tileentity.TileEntityUnltdFurnaceBase;

public final class UnltdFurnaceBlocks {
	
	public static final DeferredRegister<Block> BLOCKS;

	public static final RegistryObject<Block> PARALLEL_FURNACE;
	public static final RegistryObject<Block> FLASH_FURNACE;
	public static final RegistryObject<Block> INEXHAUSTIBLE_FURNACE;
	public static final RegistryObject<Block> UNLIMITED_FURNACE;
	
	static {
		BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModInfo.MODID);
		
		PARALLEL_FURNACE = BLOCKS.register("parallel_furnace", furnace(
				Properties.of().explosionResistance(400.0f),
				UnltdFurnaceBlockEntityTypes.PARALLEL_FURNACE
		));
		FLASH_FURNACE = BLOCKS.register("flash_furnace", furnace(
				Properties.of().lightLevel(emission(10)).explosionResistance(100000.0f),
				UnltdFurnaceBlockEntityTypes.FLASH_FURNACE
		));
		INEXHAUSTIBLE_FURNACE = BLOCKS.register("inexhaustible_furnace", furnace(
				Properties.of().lightLevel(emission(5)).explosionResistance(200.0f),
				UnltdFurnaceBlockEntityTypes.INEXHAUSTIBLE_FURNACE
		));
		UNLIMITED_FURNACE = BLOCKS.register("unlimited_furnace", furnace(
				Properties.of().lightLevel(emission(15)).explosionResistance(Integer.MAX_VALUE),
				UnltdFurnaceBlockEntityTypes.UNLIMITED_FURNACE
		));
	}
	
	private static <T extends TileEntityUnltdFurnaceBase>
	Supplier<Block> furnace(Properties properties, Supplier<BlockEntityType<T>> blockEntityType) {
		return () -> new BlockUnltdFurnace<T>(Properties.of(), blockEntityType);
	}
	
	private static ToIntFunction<BlockState> emission(int value) {
		return state -> value;
	}

}
