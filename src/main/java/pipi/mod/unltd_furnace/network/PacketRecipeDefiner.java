package pipi.mod.unltd_furnace.network;

import java.util.Map;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ForgeRegistries;
import pipi.mod.unltd_furnace.tileentity.RecipeDefiner;

public interface PacketRecipeDefiner<T extends Recipe<?>> {
	public RecipeDefiner<T> getDate();
	
	public static <T extends Recipe<?>> void encodeDefiner(RecipeDefiner<T> definer, FriendlyByteBuf buf) {
		buf.writeMap(
				definer.getDefinerMap(),
				(buffer, item) -> buffer.writeRegistryId(ForgeRegistries.ITEMS, item),
				(buffer, recipeType) -> buffer.writeRegistryId(ForgeRegistries.RECIPE_TYPES, recipeType)
		);
	}

	public static <T extends Recipe<?>> RecipeDefiner<T> decodeDefiner(FriendlyByteBuf buf) {
		Map<Item, RecipeType<? extends T>> data = buf.readMap(
				FriendlyByteBuf::readRegistryId,
				FriendlyByteBuf::readRegistryId
		);
		return new RecipeDefiner<>(data);
	}
	
}
