package at_code;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.util.data.BlockMeta;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;

// https://github.com/GTNewHorizons/Amazing-Trophies LGPL-3.0
public class GTComplexTrophyModelHandler {

    public static final String ID = "complex";
    private static final double TROPHY_PEDESTAL_HEIGHT = 5.0 / 16.0;

    private BaseModelStructure model;

    public GTComplexTrophyModelHandler() {}

    public GTComplexTrophyModelHandler(BaseModelStructure model) {
        this.model = model;
    }

    public void parse(String[][] structure, Char2ObjectMap<BlockMeta> blockInfoMap, boolean transpose,
        @Nullable Set<Character> skipHalfOffset) {
        Set<Character> set = skipHalfOffset == null ? Collections.emptySet() : skipHalfOffset;
        this.model = new GeneratedModelStructure(structure, blockInfoMap, transpose, set);
    }

    public void render(double x, double y, double z, int rotation, @Nullable String name, float size) {

        // Render custom structure.
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glTranslated(x, y - 0.5 + TROPHY_PEDESTAL_HEIGHT, z);
        GL11.glRotatef(-90, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(22.5f * rotation, 0.0f, 1.0f, 0.0f);

        RenderHelper.INSTANCE.renderModel(model, size);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
