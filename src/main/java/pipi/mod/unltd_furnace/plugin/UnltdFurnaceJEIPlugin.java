package pipi.mod.unltd_furnace.plugin;

import java.util.Map;

import com.google.common.collect.Maps;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import pipi.mod.unltd_furnace.UnlimitedFurnace;
import pipi.mod.unltd_furnace.inventory.MenuUnltdFurnace;
import pipi.mod.unltd_furnace.inventory.ScreenUnltdFurnace;
import pipi.mod.unltd_furnace.object.UnltdFurnaceBlocks;

@JeiPlugin
public class UnltdFurnaceJEIPlugin implements IModPlugin {
	public static final ResourceLocation PLUGIN_UID = UnlimitedFurnace.locate("jei_plugin");

	public static final Map<RecipeType<?>, mezz.jei.api.recipe.RecipeType<?>> TO_JEI_RECIPE_TYPE = Maps.newHashMap();
	static {
		TO_JEI_RECIPE_TYPE.put(RecipeType.SMELTING, RecipeTypes.SMELTING);
		TO_JEI_RECIPE_TYPE.put(RecipeType.SMOKING, RecipeTypes.SMOKING);
		TO_JEI_RECIPE_TYPE.put(RecipeType.BLASTING, RecipeTypes.BLASTING);
		TO_JEI_RECIPE_TYPE.put(RecipeType.CAMPFIRE_COOKING, RecipeTypes.CAMPFIRE_COOKING);
	}

	@Override
	public ResourceLocation getPluginUid() {
		return PLUGIN_UID;
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registration.addRecipeTransferHandler(
				MenuUnltdFurnace.class, null,
				RecipeTypes.SMELTING, 0, 1, 3, 36
		);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		UnltdFurnaceBlocks.BLOCKS.getEntries().forEach(
				block -> registration.addRecipeCatalyst(
						block.get(),
						RecipeTypes.SMELTING,
						RecipeTypes.SMOKING,
						RecipeTypes.BLASTING,
						RecipeTypes.CAMPFIRE_COOKING,
						RecipeTypes.FUELING
				)
		);
	}	

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addGuiContainerHandler(ScreenUnltdFurnace.class, new GuiUnltdFurnaceHandler());
	}

}
