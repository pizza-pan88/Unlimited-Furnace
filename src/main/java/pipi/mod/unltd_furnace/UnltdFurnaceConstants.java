package pipi.mod.unltd_furnace;

public class UnltdFurnaceConstants {
	
	// Slot
	public static final int SLOT_INPUT = 0;
	public static final int SLOT_FUEL = 1;
	public static final int SLOT_OUTPUT = 2;
	public static final int SLOT_RECIPE_DEFINER = 3;
	public static final int SLOT_COUNT = 4;
	
	// I/O
	public static final int[] SLOTS_TOP = new int[] {SLOT_INPUT};
	public static final int[] SLOTS_BOTTOM = new int[] {SLOT_OUTPUT, SLOT_FUEL};
	public static final int[] SLOTS_SIDES = new int[] {SLOT_FUEL};

	// Data
	public static final int DATA_LIT_TIME = 0;
	public static final int DATA_LIT_DURATION = 1;
	public static final int DATA_COOKING_PROGRESS = 2;
	public static final int DATA_COOKING_TOTAL_TIME = 3;
	public static final int DATA_COUNT = 4;
	
}
