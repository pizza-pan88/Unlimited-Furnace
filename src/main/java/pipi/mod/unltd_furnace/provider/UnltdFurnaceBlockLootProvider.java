package pipi.mod.unltd_furnace.provider;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import pipi.mod.unltd_furnace.object.UnltdFurnaceBlocks;

public class UnltdFurnaceBlockLootProvider extends BlockLootSubProvider {

	private final Set<Block> knownBloks = Sets.newHashSet();
	public UnltdFurnaceBlockLootProvider() {
		super(Set.of(), FeatureFlags.VANILLA_SET);
	}

	@Override
	protected void generate() {
		UnltdFurnaceBlocks.BLOCKS.getEntries().forEach(
				block -> this.dropSelf(block.get())
		);
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		return this.knownBloks;
	}
	   
	@Override
	protected void add(Block block, LootTable.Builder builder) {
		super.add(block, builder);
		this.knownBloks.add(block);
	}
}
