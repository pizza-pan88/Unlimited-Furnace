package pipi.mod.unltd_furnace.provider;

import java.util.function.Consumer;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import pipi.mod.unltd_furnace.object.UnltdFurnaceBlocks;

public final class UnltdFurnaceRecipeProvider extends RecipeProvider {
	
	public UnltdFurnaceRecipeProvider(PackOutput output) {
		super(output);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> writer) {
		// Parallel Furnace
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, UnltdFurnaceBlocks.PARALLEL_FURNACE.get())
		.pattern("OFO")
		.pattern("F F")
		.pattern("OFO")
		.define('F', Blocks.FURNACE)
		.define('O', Tags.Items.OBSIDIAN)
		.unlockedBy("has_furnace", has(Blocks.FURNACE))
		.save(writer);

		// Flash Furnace
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, UnltdFurnaceBlocks.FLASH_FURNACE.get())
		.pattern("qsq")
		.pattern("rFr")
		.pattern("BBB")
		.define('F', Blocks.FURNACE)
		.define('q', Tags.Items.GEMS_QUARTZ)
		.define('s', Tags.Items.NETHER_STARS)
		.define('r', Tags.Items.RODS_BLAZE)
		.define('B', Blocks.NETHER_BRICKS)
		.unlockedBy("has_furnace", has(Blocks.FURNACE))
		.save(writer);

		// Inexhaustuible Furnace
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, UnltdFurnaceBlocks.INEXHAUSTIBLE_FURNACE.get())
		.pattern("bEb")
		.pattern("RFR")
		.pattern("BBB")
		.define('F', Blocks.FURNACE)
		.define('b', Items.DRAGON_BREATH)
		.define('E', Blocks.DRAGON_EGG)
		.define('R', Blocks.END_ROD)
		.define('B', Blocks.NETHER_BRICKS)
		.unlockedBy("has_furnace", has(Blocks.FURNACE))
		.save(writer);

		// Unlimited Furnace
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, UnltdFurnaceBlocks.UNLIMITED_FURNACE.get())
		.pattern("sss")
		.pattern("PFI")
		.pattern("sss")
		.define('s', Tags.Items.NETHER_STARS)
		.define('P', UnltdFurnaceBlocks.PARALLEL_FURNACE.get())
		.define('F', UnltdFurnaceBlocks.FLASH_FURNACE.get())
		.define('I', UnltdFurnaceBlocks.INEXHAUSTIBLE_FURNACE.get())
		.unlockedBy("has_furnaces", inventoryTrigger(
				ItemPredicate.Builder.item().of(UnltdFurnaceBlocks.PARALLEL_FURNACE.get()).build(),
				ItemPredicate.Builder.item().of(UnltdFurnaceBlocks.FLASH_FURNACE.get()).build(),
				ItemPredicate.Builder.item().of(UnltdFurnaceBlocks.INEXHAUSTIBLE_FURNACE.get()).build()
		)).save(writer);
	}
	
}
