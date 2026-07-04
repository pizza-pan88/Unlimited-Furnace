package pipi.mod.unltd_furnace.inventory;

import static pipi.mod.unltd_furnace.UnltdFurnaceConstants.*;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.Lists;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.RecipeType;
import pipi.mod.unltd_furnace.UnlimitedFurnace;

public class ScreenUnltdFurnace extends AbstractContainerScreen<MenuUnltdFurnace> {
	public static final ResourceLocation TEXTURE = UnlimitedFurnace.locate("textures/gui/unltd_furnace.png");
	public static final Component TIP_DEFINER = Component.translatable("tooltip.slot.definer").withStyle(ChatFormatting.LIGHT_PURPLE);
	public static final Function<RecipeType<?>, Component> TIP_RECIPE_TYPE =
			rt -> Component.translatable("tooltip.slot.recipe_type", rt).withStyle(ChatFormatting.GRAY);
	
	public ScreenUnltdFurnace(MenuUnltdFurnace menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	public void init() {
		super.init();
		this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
	}
	
	protected void gatherDefinerTips(List<FormattedCharSequence> tips) {
		if(this.hoveredSlot.hasItem()) {
			tips.add(this.hoveredSlot.getItem().getHoverName().getVisualOrderText());
		} else {
			tips.add(TIP_DEFINER.getVisualOrderText());
		}
		tips.add(TIP_RECIPE_TYPE.apply(this.menu.getRecipeType()).getVisualOrderText());
	}

	@Override
	protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		if(isDefinerSlot(this.hoveredSlot)) {
			List<FormattedCharSequence> tips = Lists.newArrayList();
			this.gatherDefinerTips(tips);
			guiGraphics.renderTooltip(this.font, tips, mouseX, mouseY);
		} else {
			super.renderTooltip(guiGraphics, mouseX, mouseY);
		}
	}
	
	private static boolean isDefinerSlot(Slot slot) {
		return slot != null && slot.index == SLOT_RECIPE_DEFINER;
	}
	
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float particleTick) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, particleTick);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}
	
	protected void renderBg(GuiGraphics guiGraphics, float particleTick, int mouseX, int mouseY_) {
		int i = this.leftPos;
		int j = this.topPos;
		guiGraphics.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
		if (this.menu.isLit()) {
			int k = this.menu.getLitProgress();
			guiGraphics.blit(TEXTURE, i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
		}
		
		int l = this.menu.getBurnProgress();
		guiGraphics.blit(TEXTURE, i + 79, j + 34, 176, 14, l + 1, 16);
	}
	
}
