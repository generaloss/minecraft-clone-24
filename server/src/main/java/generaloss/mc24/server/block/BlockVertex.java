package generaloss.mc24.server.block;

import jpize.util.math.vector.Vec3f;

public class BlockVertex {

    public static final int POSITION_SIZE = 3;
    public static final int TEXCOORD_SIZE = 2;
    public static final int COLOR_SIZE = 4;
    public static final int NORMAL_SIZE = 3;
    public static final int SIZE = (POSITION_SIZE + TEXCOORD_SIZE + COLOR_SIZE + NORMAL_SIZE);

    public static final int[] ATTRIBUTE_SIZE_ARRAY = {
        POSITION_SIZE, TEXCOORD_SIZE, COLOR_SIZE, NORMAL_SIZE,
    };

    public static final int POSITION_OFFSET = 0;
    public static final int TEXCOORD_OFFSET = (POSITION_OFFSET + POSITION_SIZE);
    public static final int COLOR_OFFSET = (TEXCOORD_OFFSET + TEXCOORD_SIZE);
    public static final int NORMAL_OFFSET = (COLOR_OFFSET + COLOR_SIZE);

    private final float[] array;

    public BlockVertex(float x, float y, float z, float u, float v, float r, float g, float b, float a) {
        this.array = new float[] {
            x, y, z,    // position
            u, v,       // texcoords
            r, g, b, a, // color
            0, 0, 0     // normal
        };
    }

    public float[] array() {
        return array;
    }


    public float getX() {
        return array[POSITION_OFFSET + 0];
    }

    public float getY() {
        return array[POSITION_OFFSET + 1];
    }

    public float getZ() {
        return array[POSITION_OFFSET + 2];
    }

    public void setPosition(float x, float y, float z) {
        array[POSITION_OFFSET + 0] = x;
        array[POSITION_OFFSET + 1] = y;
        array[POSITION_OFFSET + 2] = z;
    }


    public float getU() {
        return array[TEXCOORD_OFFSET + 0];
    }

    public float getV() {
        return array[TEXCOORD_OFFSET + 1];
    }

    public void setTexcoords(float u, float v) {
        array[TEXCOORD_OFFSET + 0] = u;
        array[TEXCOORD_OFFSET + 1] = v;
    }


    public float getR() {
        return array[COLOR_OFFSET + 0];
    }

    public float getG() {
        return array[COLOR_OFFSET + 1];
    }

    public float getB() {
        return array[COLOR_OFFSET + 2];
    }

    public float getA() {
        return array[COLOR_OFFSET + 3];
    }

    public void setColor(float r, float g, float b, float a) {
        array[COLOR_OFFSET + 0] = r;
        array[COLOR_OFFSET + 1] = g;
        array[COLOR_OFFSET + 2] = b;
        array[COLOR_OFFSET + 3] = a;
    }


    public float getNormalX() {
        return array[NORMAL_OFFSET + 0];
    }

    public float getNormalY() {
        return array[NORMAL_OFFSET + 1];
    }

    public float getNormalZ() {
        return array[NORMAL_OFFSET + 2];
    }

    public void setNormal(float x, float y, float z) {
        array[NORMAL_OFFSET + 0] = x;
        array[NORMAL_OFFSET + 1] = y;
        array[NORMAL_OFFSET + 2] = z;
    }

    public void setNormal(Vec3f normal) {
        this.setNormal(normal.x, normal.y, normal.z);
    }

}
