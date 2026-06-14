package pipi.mod.unltd_furnace;

import static pipi.mod.unltd_furnace.ModInfo.*;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import pipi.mod.unltd_furnace.network.UnltdFurnacePacketHandler;
import pipi.mod.unltd_furnace.object.UnltdFurnaceBlockEntityTypes;
import pipi.mod.unltd_furnace.object.UnltdFurnaceBlocks;
import pipi.mod.unltd_furnace.object.UnltdFurnaceItems;
import pipi.mod.unltd_furnace.object.UnltdFurnaceMenuTypes;

@Mod(MODID)
public class UnlimitedFurnace {
	public static final Logger logger = LogUtils.getLogger();;
	
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
	public static final RegistryObject<CreativeModeTab> TAB_FURNACES = TABS.register("furnaces", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.furnaces"))
			.icon(() -> new ItemStack(Blocks.FURNACE))
			.displayItems((parameters, output) -> {
				output.accept(Blocks.FURNACE);
				output.accept(Blocks.SMOKER);
				output.accept(Blocks.BLAST_FURNACE);
				output.accept(Blocks.CAMPFIRE);
				output.accept(Blocks.SOUL_CAMPFIRE);
				output.accept(UnltdFurnaceBlocks.PARALLEL_FURNACE.get());
				output.accept(UnltdFurnaceBlocks.FLASH_FURNACE.get());
				output.accept(UnltdFurnaceBlocks.INEXHAUSTIBLE_FURNACE.get());
				output.accept(UnltdFurnaceBlocks.UNLIMITED_FURNACE.get());
			})
			.build()
	);
	
	public UnlimitedFurnace(FMLJavaModLoadingContext context) {
		IEventBus modEventBus = context.getModEventBus();
		UnltdFurnaceBlocks.BLOCKS.register(modEventBus);
		UnltdFurnaceItems.ITEMS.register(modEventBus);
		TABS.register(modEventBus);
		
		UnltdFurnaceBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
		UnltdFurnaceMenuTypes.MENU_TYPES.register(modEventBus);
		
		UnltdFurnacePacketHandler.register();
		
		UnltdFurnaceEventHandler.register(modEventBus);
	}
	
	public static ResourceLocation locate(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}
	
	public static String locateStr(String path) {
		return MODID + ":" + path;
	}
}
