package pipi.mod.unltd_furnace.tileentity;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Maps;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public class RecipeDefiner<T extends Recipe<?>> {
	private final Map<Item, RecipeType<? extends T>> definerMap;
	
	public RecipeDefiner() {
		this(Maps.newHashMap());
	}
	
	public RecipeDefiner(Map<Item, RecipeType<? extends T>> definerMap) {
		Objects.requireNonNull(definerMap, "Definer Map must not be null.");
		this.definerMap = definerMap;
	}
	
	public Map<Item, RecipeType<? extends T>> getDefinerMap() {
		return this.definerMap;
	}
	public Set<Item> getDefiners() {
		return this.definerMap.keySet();
	}
	public Collection<RecipeType<? extends T>> getRecipeTypes() {
		return this.definerMap.values();
	}

	public boolean isDefiner(Item item) {
		return this.definerMap.containsKey(item);
	}
	public boolean isDefiner(ItemStack item) {
		return item.isEmpty() ? false : this.isDefiner(item.getItem());
	}
	
	public RecipeType<? extends T> getRecipeType(ItemStack item, RecipeType<? extends T> orElse) {
		return isDefiner(item) ? this.definerMap.get(item.getItem()) : orElse;
	}
	
	public static class Cooking extends RecipeDefiner<AbstractCookingRecipe> {
		public static final Cooking UNLTF_FURNACE_DEFINER;
		
		static {
			Map<Item, RecipeType<? extends AbstractCookingRecipe>> map = Maps.newHashMap();
			
			map.put(Items.BLAST_FURNACE, RecipeType.BLASTING);
			map.put(Items.SMOKER, RecipeType.SMOKING);
			map.put(Items.CAMPFIRE, RecipeType.CAMPFIRE_COOKING);
			map.put(Items.SOUL_CAMPFIRE, RecipeType.CAMPFIRE_COOKING);
			
			UNLTF_FURNACE_DEFINER = new Cooking(map);
		}

		public Cooking() { super(); }
		public Cooking(Map<Item, RecipeType<? extends AbstractCookingRecipe>> definerMap) {
			super(definerMap);
		}
	}
	
}
