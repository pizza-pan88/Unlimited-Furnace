package pipi.mod.unltd_furnace.plugin;

import java.util.Collection;
import java.util.List;

import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.crafting.RecipeType;
import pipi.mod.unltd_furnace.inventory.ScreenUnltdFurnace;

public class GuiUnltdFurnaceHandler implements IGuiContainerHandler<ScreenUnltdFurnace> {
	
	@Override
	public Collection<IGuiClickableArea> getGuiClickableAreas(ScreenUnltdFurnace furnaceScreen, double mouseX, double mouseY) {
		Rect2i area = new Rect2i(78, 32, 28, 23);
		IGuiClickableArea clickableArea = new IGuiClickableArea() {
			
			@Override
			public Rect2i getArea() {
				return area;
			}
			
			@Override
			public void onClick(IFocusFactory focusFactory, IRecipesGui recipesGui) {
				RecipeType<?> recipeType = furnaceScreen.getMenu().getRecipeType();
				mezz.jei.api.recipe.RecipeType<?> jeiRecipeType = UnltdFurnaceJEIPlugin.TO_JEI_RECIPE_TYPE.getOrDefault(recipeType, RecipeTypes.SMELTING);
				recipesGui.showTypes(List.of(jeiRecipeType, RecipeTypes.FUELING));
			}
			
		};
		return List.of(clickableArea);
	}
}
