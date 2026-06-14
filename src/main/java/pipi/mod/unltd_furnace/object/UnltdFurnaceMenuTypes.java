package pipi.mod.unltd_furnace.object;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import pipi.mod.unltd_furnace.ModInfo;
import pipi.mod.unltd_furnace.inventory.MenuUnltdFurnace;

public class UnltdFurnaceMenuTypes {
	
	public static final DeferredRegister<MenuType<?>> MENU_TYPES;
	
	public static final RegistryObject<MenuType<MenuUnltdFurnace>> UNLTD_FURNACE;
	
	static {
		MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ModInfo.MODID);
	
		UNLTD_FURNACE = MENU_TYPES.register(
				"unltd_furnace", () -> IForgeMenuType.create(MenuUnltdFurnace::new)
		);
	}

}
