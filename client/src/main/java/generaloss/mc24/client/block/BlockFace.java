package generaloss.mc24.client.block;

import generaloss.mc24.server.common.Directory;
import jpize.util.array.FloatList;

public class BlockFace {

    private final Directory occludesTo;
    private final Directory occlude;
    private final float[] vertexDataArray;
    private final String textureID;
    private final int[] indices;

    public BlockFace(Directory occludesTo, Directory occlude, String textureID, BlockVertex[] vertices) {
        this.occludesTo = occludesTo;
        this.occlude = occlude;
        this.textureID = textureID;

        // vertex data array
        final FloatList vertexDataList = new FloatList();
        for(BlockVertex vertex: vertices)
            vertexDataList.add(vertex.array());
        this.vertexDataArray = vertexDataList.arrayTrimmed();

        // position indices
        this.indices = new int[vertices.length];
        for(int i = 0; i < indices.length; i++)
            indices[i] = i * BlockVertex.SIZE;
    }

    public BlockFace(String textureID, BlockVertex[] vertices) {
        this(generateOccludesTo(vertices), generateOcclude(vertices), textureID, vertices);
    }


    public Directory getOccludesTo() {
        return occludesTo;
    }

    public Directory getOcclude() {
        return occlude;
    }

    public String getTextureID() {
        return textureID;
    }

    public float[] getVertexDataArray() {
        return vertexDataArray;
    }

    public int[] getIndices() {
        return indices;
    }


    private static Directory generateOccludesTo(BlockVertex[] vertices) {
        final float x0 = vertices[0].getX();
        final float y0 = vertices[0].getY();
        final float z0 = vertices[0].getZ();
        if(x0 < 0 || x0 > 1 || y0 < 0 || y0 > 1 || z0 < 0 || z0 > 1)
            return Directory.NONE;

        return solveDirectory(vertices, x0, y0, z0);
    }

    private static Directory generateOcclude(BlockVertex[] vertices) {
        final float x0 = vertices[0].getX();
        final float y0 = vertices[0].getY();
        final float z0 = vertices[0].getZ();
        if(!(x0 == 0F || x0 == 1F) || !(y0 == 0F || y0 == 1F) || !(z0 == 0F || z0 == 1F))
            return Directory.NONE;

        return solveDirectory(vertices, x0, y0, z0);
    }

    private static Directory solveDirectory(BlockVertex[] vertices, float x0, float y0, float z0) {
        boolean hasX = true;
        boolean hasY = true;
        boolean hasZ = true;
        for(int i = 1; i < vertices.length; i++){
            final float x = vertices[i].getX();
            if(x != x0)
                hasX = false;

            final float y = vertices[i].getY();
            if(y != y0)
                hasY = false;

            final float z = vertices[i].getZ();
            if(z != z0)
                hasZ = false;
        }

        return Directory.byVector(
            (hasX ? Math.signum((-x0 + 0.5) * 2) : 0),
            (hasY ? Math.signum((-y0 + 0.5) * 2) : 0),
            (hasZ ? Math.signum((-z0 + 0.5) * 2) : 0)
        );
    }

}
