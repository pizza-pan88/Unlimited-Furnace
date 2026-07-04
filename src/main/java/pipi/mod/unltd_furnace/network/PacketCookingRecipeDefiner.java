package pipi.mod.unltd_furnace.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraftforge.network.NetworkEvent.Context;
import pipi.mod.unltd_furnace.inventory.MenuUnltdFurnace;
import pipi.mod.unltd_furnace.tileentity.RecipeDefiner;

public record PacketCookingRecipeDefiner(RecipeDefiner<AbstractCookingRecipe> data) implements PacketRecipeDefiner<AbstractCookingRecipe> {
	
	@Override
	public RecipeDefiner<AbstractCookingRecipe> getDate() {
		return this.data;
	}
	
	public static class Handler implements PacketHandler<PacketCookingRecipeDefiner> {
		
		@Override
		public Class<PacketCookingRecipeDefiner> getPacketType() {
			return PacketCookingRecipeDefiner.class;
		}

		@Override
		public void encode(PacketCookingRecipeDefiner packet, FriendlyByteBuf buf) {
			PacketRecipeDefiner.encodeDefiner(packet.getDate(), buf);
		}

		@Override
		public PacketCookingRecipeDefiner decode(FriendlyByteBuf buf) {
			RecipeDefiner<AbstractCookingRecipe> definer = PacketRecipeDefiner.decodeDefiner(buf);
			return new PacketCookingRecipeDefiner(definer);
		}

		@Override
		public void handle(PacketCookingRecipeDefiner packet, Supplier<Context> ctx) {
			Context context = ctx.get();
			context.enqueueWork(() -> {
				LocalPlayer player = Minecraft.getInstance().player;
				if(player == null) {
					return;
				}
				
				if (player.containerMenu instanceof MenuUnltdFurnace furnaceMenu) {
					furnaceMenu.setRecipeDefiner(packet.getDate());
				}
			});
			context.setPacketHandled(true);
		}

	}

}
