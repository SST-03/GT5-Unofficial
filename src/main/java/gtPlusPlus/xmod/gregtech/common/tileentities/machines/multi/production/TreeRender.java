package gtPlusPlus.xmod.gregtech.common.tileentities.machines.multi.production;

import java.util.EnumMap;

import net.minecraft.block.Block;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import gregtech.api.util.GTLog;
import gtPlusPlus.xmod.gregtech.common.tileentities.machines.multi.production.MTETreeFarm.Mode;

public class TreeRender extends EntityFX {

    public boolean enableProduction = false;
    public MTETreeFarm treeFarm;

    public TreeRender(World world, int x, int y, int z, int age) {
        super(world, x, y, z);
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.particleMaxAge = age;
        this.particleAge = 0;
    }

    public TreeRender(TreeRender r, int age) {
        super(r.worldObj, r.posX, r.posY, r.posZ);
        this.particleMaxAge = age;
        this.particleAge = 0;
        this.ticksExisted = r.ticksExisted;
        this.treeFarm = r.treeFarm;
    }

    public void setMTE(MTETreeFarm mte) {
        this.treeFarm = mte;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 3;
    }

    @Override
    public int getFXLayer() {
        return 3;
    }

    // 0Air 1Log 2Log(nottree->leave) 3Leave
    public static short[][][] treeA = { { { 0, 3, 0 }, { 3, 2, 3 }, { 0, 3, 0 } },
        { { 0, 0, 0 }, { 0, 3, 0 }, { 0, 0, 0 } }, };

    @Override
    public void onUpdate() {
        if (this.particleAge++ >= this.particleMaxAge) this.setDead();
    }

    private Block _getBlockFromItemStack(ItemStack stack) {
        if (stack == null) return null;
        return Block.getBlockFromItem(stack.getItem());
    }

    private int _getDamageFromItemStack(ItemStack stack) {
        if (stack == null) return 0;
        return stack.getItemDamage();
    }

    private void _setPos2Render(Tessellator tessellator, double x, double y, double z, int a, int b, int c,
        float size) {
        double X = x + (a - 2) * size;
        double Y = y + (0.5f + b) * size;
        double Z = z + (c - 2) * size;
        tessellator.setTranslation(X - interpPosX - x, Y - interpPosY - y, Z - interpPosZ - z);
        // if (this.ticksExisted % 19 == 0) GTLog.err.println("[TreeRender]: set pos to " + X + "," + Y + "," + Z);
    }

    @Override
    public void renderParticle(Tessellator p_70539_1_, float p_70539_2_, float p_70539_3_, float p_70539_4_,
        float p_70539_5_, float p_70539_6_, float p_70539_7_) {
        if (this.treeFarm == null) {
            // GTLog.err.println("TreeRender cannot found te treeFarm");
            return;
        }
        if (this.treeFarm.mMaxProgresstime <= 0) {
            // GTLog.err.println("TreeRender not working");
            return;
        }
        float progress = ((float) this.treeFarm.mProgresstime) / this.treeFarm.mMaxProgresstime;
        EnumMap<Mode, ItemStack> outputPerMode = MTETreeFarm.getOutputsForSapling(this.treeFarm.findSapling());

        // RenderBlocks RenderBlocks_Instance = RenderBlocks.getInstance();
        Tessellator tessellator = Tessellator.instance;
        Block[] displayMode = new Block[4];
        int[] meta = new int[4]; // NOT TO USE [0] because i dont like it, use 1-index

        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);

        displayMode[1] = displayMode[2] = _getBlockFromItemStack(outputPerMode.get(Mode.LOG));
        meta[1] = meta[2] = _getDamageFromItemStack(outputPerMode.get(Mode.LOG));

        displayMode[3] = _getBlockFromItemStack(outputPerMode.get(Mode.LEAVES));
        meta[3] = _getDamageFromItemStack(outputPerMode.get(Mode.LEAVES));
        if (displayMode[2] == null) displayMode[2] = displayMode[3];
        meta[2] = meta[3];

        if (this.ticksExisted % 19 == 0) GTLog.err.println("Loaded TreeRender, just wait");
        for (int y = 0; y < treeA.length; y++) {
            for (int x = 0; x < treeA[y].length; x++) {
                for (int z = 0; z < treeA[y][x].length; z++) {
                    int modeDisplay = treeA[y][x][z];
                    if (displayMode[modeDisplay] != null) {
                        _setPos2Render(tessellator, this.posX, this.posY, this.posZ, x, y, z, progress);
                        // :spotless:off
                        try{
                        RenderBlocks.getInstance()
                            .renderBlockUsingTexture(displayMode[modeDisplay], (int) this.posX, (int) this.posY, (int) this.posZ, displayMode[modeDisplay].getIcon(1,meta[modeDisplay]));
                            //.renderBlockAsItem(displayMode[modeDisplay], meta[modeDisplay], progress);
                        } catch (Exception ex) {}//Ignore it
                        //:spotless:on
                    }
                }
            }
        }

        tessellator.setTranslation(0d, 0d, 0d);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(true);
    }
}
