package pipi.mod.unltd_furnace.inventory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ForgeEventFactory;
import pipi.mod.unltd_furnace.tileentity.TileEntityUnltdFurnaceBase;

public class SlotUnltdFurnaceResult extends Slot {
	private final Player player;
	private int removeCount;

	public SlotUnltdFurnaceResult(Player player, Container container, int id, int x, int y) {
		super(container, id, x, y);
		this.player = player;
	}
	
	public boolean mayPlace(ItemStack stack) {
		return false;
	}
	   
	public ItemStack remove(int count) {
		if (this.hasItem()) {
			this.removeCount += Math.min(count, this.getItem().getCount());
		}
		
		return super.remove(count);
	}

	public void onTake(Player player, ItemStack stack) {
		this.checkTakeAchievements(stack);
		super.onTake(player, stack);
	}

	protected void onQuickCraft(ItemStack stack, int count) {
		this.removeCount += count;
		this.checkTakeAchievements(stack);
	}

	protected void checkTakeAchievements(ItemStack stack) {
		stack.onCraftedBy(this.player.level(), this.player, this.removeCount);
		Player player = this.player;
		if (player instanceof ServerPlayer serverplayer) {
			Container container = this.container;
			if (container instanceof TileEntityUnltdFurnaceBase unltdFurnace) {
				unltdFurnace.awardUsedRecipesAndPopExperience(serverplayer);
			}
		}
		
		this.removeCount = 0;
		ForgeEventFactory.firePlayerSmeltedEvent(this.player, stack);
	}
}	