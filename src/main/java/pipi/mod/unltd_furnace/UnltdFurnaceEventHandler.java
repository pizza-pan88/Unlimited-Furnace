package pipi.mod.unltd_furnace;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import pipi.mod.unltd_furnace.inventory.ScreenUnltdFurnace;
import pipi.mod.unltd_furnace.object.UnltdFurnaceMenuTypes;
import pipi.mod.unltd_furnace.provider.UnltdFurnaceBlockLootProvider;
import pipi.mod.unltd_furnace.provider.UnltdFurnaceRecipeProvider;
import pipi.mod.unltd_furnace.provider.UnltdFurnaceTagProvider;

public class UnltdFurnaceEventHandler {
	
	protected static void register(IEventBus modEventBus) {
		modEventBus.register(new UnltdFurnaceEventHandler());
	}

	@SubscribeEvent
	public void onGatherData(GatherDataEvent event) {
		PackOutput output = event.getGenerator().getPackOutput();
		CompletableFuture<Provider> lookupProvider = event.getLookupProvider();
	    ExistingFileHelper fileHelper = event.getExistingFileHelper();

		event.getGenerator().addProvider(
				event.includeServer(),
				(DataProvider.Factory<?>)UnltdFurnaceRecipeProvider::new
		);
		event.getGenerator().addProvider(
				event.includeServer(),
				new LootTableProvider(output, Set.of(), List.of(
						new SubProviderEntry(
								UnltdFurnaceBlockLootProvider::new,
								LootContextParamSets.BLOCK
						)
				))
		);
		event.getGenerator().addProvider(
				event.includeServer(),
				new UnltdFurnaceTagProvider(output, lookupProvider, fileHelper)
		);
	}

	@SubscribeEvent
	public void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			MenuScreens.register(UnltdFurnaceMenuTypes.UNLTD_FURNACE.get(), ScreenUnltdFurnace::new);
		});
	}
	
}
