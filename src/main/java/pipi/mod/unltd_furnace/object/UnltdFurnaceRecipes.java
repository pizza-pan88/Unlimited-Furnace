package pipi.mod.unltd_furnace.object;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreIngredient;
import pipi.mod.unltd_furnace.UnlimitedFurnace;

public final class UnltdFurnaceRecipes {
	public static final Ingredient OBSIDIAN = new OreIngredient("obsidian");
	public static final Ingredient QUARTZ = new OreIngredient("gemQuartz");
	public static final Ingredient NETHER_STAR = new OreIngredient("netherStar");
	
	public static void register() {
		// Parallel Furnace
		GameRegistry.addShapedRecipe(
				UnlimitedFurnace.locate("parallel_furnace"), null,
				new ItemStack(UnltdFurnaceBlocks.PARALLEL_FURNACE),
				"OFO",
				"F F",
				"OFO",
				'F', Blocks.FURNACE,
				'O', OBSIDIAN
		);

		// Flash Furnace
		GameRegistry.addShapedRecipe(
				UnlimitedFurnace.locate("flash_furnace"), null,
				new ItemStack(UnltdFurnaceBlocks.FLASH_FURNACE),
				"qsq",
				"rFr",
				"BBB",
				'F', Blocks.FURNACE,
				'q', QUARTZ,
				's', NETHER_STAR,
				'r', Items.BLAZE_ROD,
				'B', Blocks.NETHER_BRICK
		);

		// Inexhaustuible Furnace
		GameRegistry.addShapedRecipe(
				UnlimitedFurnace.locate("inexhaustuible_furnace"), null,
				new ItemStack(UnltdFurnaceBlocks.INEXHAUSTIBLE_FURNACE),
				"bEb",
				"RFR",
				"BBB",
				'F', Blocks.FURNACE,
				'b', Items.DRAGON_BREATH,
				'E', Item.getItemFromBlock(Blocks.DRAGON_EGG),
				'R', Blocks.END_ROD,
				'B', Blocks.END_BRICKS
		);

		// Unlimited Furnace
		GameRegistry.addShapedRecipe(
				UnlimitedFurnace.locate("unlimited_furnace"), null,
				new ItemStack(UnltdFurnaceBlocks.UNLIMITED_FURNACE),
				"sss",
				"PFI",
				"sss",
				's', NETHER_STAR,
				'P', UnltdFurnaceBlocks.PARALLEL_FURNACE,
				'F', UnltdFurnaceBlocks.FLASH_FURNACE,
				'I', UnltdFurnaceBlocks.INEXHAUSTIBLE_FURNACE
		);

	}
	
}
