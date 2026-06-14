package pipi.mod.unltd_furnace.network;

import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import pipi.mod.unltd_furnace.inventory.MenuUnltdFurnace;
import pipi.mod.unltd_furnace.tileentity.RecipeDefiner;

public class PacketCookingRecipeDefiner {

	private final RecipeDefiner<AbstractCookingRecipe> data;
	
	public PacketCookingRecipeDefiner(RecipeDefiner<AbstractCookingRecipe> recipeDefiner) {
		this.data = recipeDefiner;
	}
	
	public static void encode(PacketCookingRecipeDefiner packet, FriendlyByteBuf buf) {
		buf.writeMap(
				packet.data.getDefinerMap(),
				(buffer, item) -> buffer.writeRegistryId(ForgeRegistries.ITEMS, item),
				(buffer, recipeType) -> buffer.writeRegistryId(ForgeRegistries.RECIPE_TYPES, recipeType)
		);
	}

	public static PacketCookingRecipeDefiner decode(FriendlyByteBuf buf) {
		Map<Item, RecipeType<? extends AbstractCookingRecipe>> data = buf.readMap(
				FriendlyByteBuf::readRegistryId,
				FriendlyByteBuf::readRegistryId
		);
		return new PacketCookingRecipeDefiner(new RecipeDefiner.Cooking(data));
	}

	public static void handle(PacketCookingRecipeDefiner packet, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			LocalPlayer player = Minecraft.getInstance().player;
			if(player == null) {
				return;
			}
			
			if (player.containerMenu instanceof MenuUnltdFurnace furnaceMenu) {
				furnaceMenu.setRecipeDefiner(packet.data);
			}
		});
		ctx.get().setPacketHandled(true);
	}
	
}
