package pipi.mod.unltd_furnace;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pipi.mod.unltd_furnace.item.ItemBlockUnltdFurnace;
import pipi.mod.unltd_furnace.object.UnltdFurnaceBlocks;
import pipi.mod.unltd_furnace.object.UnltdFurnaceTileEntities;

public class UnltdFurnaceEventHandler {

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
		UnltdFurnaceBlocks.register(block -> event.getRegistry().register(
				new ItemBlockUnltdFurnace(block)
		));
		Blocks.DRAGON_EGG.setCreativeTab(CreativeTabs.DECORATIONS);
	}

	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		UnltdFurnaceBlocks.register(event.getRegistry()::register);
		UnltdFurnaceTileEntities.register();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		UnltdFurnaceBlocks.register(block -> {
			ModelLoader.setCustomModelResourceLocation(
					Item.getItemFromBlock(block), 0,
					new ModelResourceLocation(block.getRegistryName(), "inventory")
			);
		});
	}
	
}
