package at_code;

import java.util.EnumMap;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.util.data.BlockMeta;

import gtPlusPlus.xmod.gregtech.common.tileentities.machines.multi.production.MTETreeFarm;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;

// https://github.com/GTNewHorizons/Amazing-Trophies LGPL-3.0
public class TreeRender {

    public boolean enableProduction = false;
    public MTETreeFarm treeFarm;

    int posX;
    int posY;
    int posZ;

    public TreeRender(int x, int y, int z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    public void setMTE(MTETreeFarm mte) {
        this.treeFarm = mte;
    }

    private static String[][] deepCopy(String[][] input) {
        String[][] output = new String[input.length][];
        for (int i = 0; i < input.length; i++) {
            output[i] = new String[input[i].length];
            System.arraycopy(input[i], 0, output[i], 0, input[i].length);
        }
        return output;
    }

    private Block _getBlockFromItemStack(ItemStack stack) {
        if (stack == null) return null;
        return Block.getBlockFromItem(stack.getItem());
    }

    private int _getDamageFromItemStack(ItemStack stack) {
        if (stack == null) return 0;
        return stack.getItemDamage();
    }

    private void putBlockInfoMapIfPossible(Char2ObjectMap<BlockMeta> blockInfoMap, char c, Block block, int meta) {
        if (block == null || blockInfoMap.containsKey(c)) return;
        blockInfoMap.put(c, new BlockMeta(block, meta));
    }

    private void putBlockInfoMapIfPossible(Char2ObjectMap<BlockMeta> blockInfoMap, char c, MTETreeFarm.Mode mode) {
        ItemStack stack = this.outputPerMode.get(mode);
        putBlockInfoMapIfPossible(blockInfoMap, c, _getBlockFromItemStack(stack), _getDamageFromItemStack(stack));
    }

    private EnumMap<MTETreeFarm.Mode, ItemStack> outputPerMode;

    private GTComplexTrophyModelHandler treeAHandler;
    private GTComplexTrophyModelHandler treeBHandler;

    // :spotless:off

    // A:Log B:Log(nottree->leave) C:Leave
    /*
    public static String[][] treeA = {
        {"   "," C ","   "},
        {" C ","CBC","   "},
        {"   "," C ","   "}
    };
    */
    public static String[][] treeA = {{"D"}};
    public static String[][] treeB = {
        {"     ","     ","CCCCC","CCCCC","     ","     "},
        {"  C  ","  C  ","CCCCC","CCCCC","     ","     "},
        {" CCC "," CAC ","CCACC","CCACC","  A  ","  A  "},
        {"  C  ","  C  ","CCCCC","CCCCC","     ","     "},
        {"     ","     ","CCCCC","CCCCC","     ","     "}
    };
    // :spotless:on

    public void init(ItemStack sapling) {
        // Todo: check if sapling has no production
        this.outputPerMode = MTETreeFarm.getOutputsForSapling(sapling);

        Char2ObjectMap<BlockMeta> blockInfoMap = new Char2ObjectOpenHashMap<>();
        putBlockInfoMapIfPossible(blockInfoMap, 'A', MTETreeFarm.Mode.LOG);
        putBlockInfoMapIfPossible(blockInfoMap, 'B', MTETreeFarm.Mode.LOG);
        putBlockInfoMapIfPossible(blockInfoMap, 'B', MTETreeFarm.Mode.LEAVES);
        putBlockInfoMapIfPossible(blockInfoMap, 'C', MTETreeFarm.Mode.LEAVES);
        putBlockInfoMapIfPossible(blockInfoMap, 'D', MTETreeFarm.Mode.SAPLING);

        treeAHandler = new GTComplexTrophyModelHandler();
        treeAHandler.parse(treeA, blockInfoMap, false, null);

        treeBHandler = new GTComplexTrophyModelHandler();
        treeBHandler.parse(treeB, blockInfoMap, false, null);
    }

    public void render(float xPos, float yPos, float zPos, float size) {
        /*
         * if (this.treeFarm == null) {
         * // GTLog.err.println("TreeRender cannot found te treeFarm");
         * // return;
         * this.outputPerMode = MTETreeFarm
         * .getOutputsForSapling(GameRegistry.makeItemStack("minecraft:sapling", 0, 1, null));
         * GTLog.err.println("Loaded TreeRender in another way " + this.posX + " " + this.posY + " " + this.posZ);
         * } else if (this.treeFarm.mMaxProgresstime <= 0) {
         * // GTLog.err.println("TreeRender not working");
         * return;
         * } else {
         * float progress = ((float) this.treeFarm.mProgresstime) / this.treeFarm.mMaxProgresstime;
         * this.outputPerMode = MTETreeFarm.getOutputsForSapling(this.treeFarm.findSapling());
         * return; // disable regular display, use block display instead
         * }
         */
        GL11.glPushMatrix();
        if (size < 0.04f) treeAHandler.render(xPos, yPos + 4f, zPos, 0, "Tree", 0.2f);
        else treeBHandler.render(xPos, yPos + 4f, zPos, 0, "Tree", Math.min(size,0.8f));
        // treeBHandler.render(xPos, yPos + 4f, zPos, 0, "Tree", Math.min(size*1.3f,1f)*0.8f);
        GL11.glPopMatrix();
    }
}
