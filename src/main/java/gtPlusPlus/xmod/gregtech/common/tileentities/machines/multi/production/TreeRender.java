package gtPlusPlus.xmod.gregtech.common.tileentities.machines.multi.production;

import java.util.EnumMap;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
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

    public void setMTE(MTETreeFarm mte){
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



    //0Air 1Log 2Log(nottree->leave) 3Leave
    public static short[][][] treeA = [
    [
        [0,3,0],
        [3,2,3],
        [0,3,0]
    ],
    [
        [0,0,0],
        [0,3,0],
        [0,0,0]
    ],
    ]

    @Override
    public void onUpdate() {
        if (this.particleAge++ >= this.particleMaxAge) this.setDead();
    }

    private Block _getBlockFromItemStack(ItemStack stack) {
        if (stack == null) return false;
        return Block.getBlockFromItem(stack.getItem());
    }

    @Override
    public void renderParticle(Tessellator p_70539_1_, float p_70539_2_, float p_70539_3_, float p_70539_4_,
        float p_70539_5_, float p_70539_6_, float p_70539_7_) {
        if (this.treeFarm == null) return;
        if (this.treeFarm.mMaxProgresstime <= 0) return;
        double progress = ((double) this.treeFarm.mProgresstime) / ((double) this.treeFarm.mMaxProgresstime);
        EnumMap<Mode, ItemStack> outputPerMode = MTETreeFarm.getOutputsForSapling(this.treeFarm.findSapling());

        Block displayMode1;
        Block displayMode2;
        Block displayMode3;
        displayMode1 = displayMode2 = _getBlockFromItemStack(outputPerMode.get(Mode.LOG));
        displayMode3 = _getBlockFromItemStack(outputPerMode.get(Mode.LEAVES));
        if (displayMode2 == null) displayMode2 = displayMode3;

        
    }
}