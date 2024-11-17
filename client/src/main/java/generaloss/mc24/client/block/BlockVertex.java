package generaloss.mc24.client.block;

public class BlockVertex {

    public static final int POSITION_SIZE = 3;
    public static final int TEXCOORD_SIZE = 2;
    public static final int COLOR_SIZE = 4;
    public static final int SIZE = (POSITION_SIZE + TEXCOORD_SIZE + COLOR_SIZE);

    public static final int POSITION_OFFSET = 0;
    public static final int TEXCOORD_OFFSET = (POSITION_OFFSET + POSITION_SIZE);
    public static final int COLOR_OFFSET = (TEXCOORD_OFFSET + TEXCOORD_SIZE);

    private final float[] array;

    public BlockVertex(float x, float y, float z, float u, float v, float r, float g, float b, float a) {
        this.array = new float[] {
            x, y, z,
            u, v,
            r, g, b, a,
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


    public float getU() {
        return array[TEXCOORD_OFFSET + 0];
    }

    public float getV() {
        return array[TEXCOORD_OFFSET + 1];
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

}
