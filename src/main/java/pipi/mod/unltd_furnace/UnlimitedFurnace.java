package pipi.mod.unltd_furnace;

import static pipi.mod.unltd_furnace.ModInfo.*;

import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pipi.mod.unltd_furnace.object.UnltdFurnaceBlocks;
import pipi.mod.unltd_furnace.object.UnltdFurnaceRecipes;

@Mod(modid = MODID, name = NAME, version = VERSION, updateJSON = UPDATE_JSON)
public class UnlimitedFurnace {
	private static Logger logger;
	
	@Instance(MODID)
	public static UnlimitedFurnace instance;
	
	public static final CreativeTabs TAB_FURNACES = new CreativeTabs("furnaces") {

		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(Blocks.FURNACE);
		}
		
		@SideOnly(Side.CLIENT)
		public void displayAllRelevantItems(NonNullList<ItemStack> items) {
			items.add(new ItemStack(Blocks.FURNACE));
			UnltdFurnaceBlocks.register(block -> items.add(new ItemStack(block)));
		}
	    
	};

	@EventHandler
	public void construct(FMLConstructionEvent event) {
		MinecraftForge.EVENT_BUS.register(new UnltdFurnaceEventHandler());
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		UnltdFurnaceRecipes.register();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new UnltdFurnaceGuiHandler());
	}
    
	@EventHandler
	public void postInit(FMLPreInitializationEvent event) {
		ModInfo.loadInfo(event.getModMetadata());
	}

	public static Logger getLogger() {
		return logger;
	}
	
	public static ResourceLocation locate(String path) {
		return new ResourceLocation(MODID, path);
	}
	
	public static String locateStr(String path) {
		return MODID + ":" + path;
	}
}
