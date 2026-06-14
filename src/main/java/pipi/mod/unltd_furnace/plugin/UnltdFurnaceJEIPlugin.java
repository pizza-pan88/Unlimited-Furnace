package pipi.mod.unltd_furnace.plugin;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import pipi.mod.unltd_furnace.UnlimitedFurnace;
import pipi.mod.unltd_furnace.inventory.MenuUnltdFurnace;
import pipi.mod.unltd_furnace.inventory.ScreenUnltdFurnace;
import pipi.mod.unltd_furnace.object.UnltdFurnaceBlocks;

@JeiPlugin
public class UnltdFurnaceJEIPlugin implements IModPlugin {
	public static final ResourceLocation PLUGIN_UID = UnlimitedFurnace.locate("jei_plugin");
	
	public static final Map<RecipeType<? extends AbstractCookingRecipe>, mezz.jei.api.recipe.RecipeType<?>>
	TO_JEI_RECIPE_TYPE = Map.of(
			RecipeType.SMELTING, RecipeTypes.SMELTING,
			RecipeType.SMOKING, RecipeTypes.SMOKING,
			RecipeType.BLASTING, RecipeTypes.BLASTING,
			RecipeType.CAMPFIRE_COOKING, RecipeTypes.CAMPFIRE_COOKING
	);
	
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
		registration.addGuiContainerHandler(ScreenUnltdFurnace.class, new IGuiContainerHandler<>() {
			@Override
			public Collection<IGuiClickableArea> getGuiClickableAreas(ScreenUnltdFurnace containerScreen, double mouseX, double mouseY) {
				Rect2i area = new Rect2i(78, 32, 28, 23);
				IGuiClickableArea clickableArea = new IGuiClickableArea() {
					@Override
					public Rect2i getArea() {
						return area;
					}
					@Override
					public void onClick(IFocusFactory focusFactory, IRecipesGui recipesGui) {
						RecipeType<?> recipeType = containerScreen.getMenu().getRecipeType();
						mezz.jei.api.recipe.RecipeType<?> jeiRecipeType = TO_JEI_RECIPE_TYPE.getOrDefault(recipeType, RecipeTypes.SMELTING);
						recipesGui.showTypes(List.of(jeiRecipeType, RecipeTypes.FUELING));
					}
				};
				return List.of(clickableArea);
			}
		});
	}
	
}
