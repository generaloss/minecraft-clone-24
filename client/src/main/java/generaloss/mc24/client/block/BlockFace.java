package generaloss.mc24.client.block;

import generaloss.mc24.server.Directory;
import jpize.util.array.FloatList;
import jpize.util.math.vector.Vec3f;

public class BlockFace {

    public static final int VERTICES_NUMBER = 4;
    public static final int[][] VERTEX_INDEX_PERMUTATIONS = new int[][] {
        {0, 1, 2, 3}, // normal
        {1, 2, 3, 0}, // rotated
    };

    private final Directory occludesTo;
    private final Directory occlude;
    private final float[] vertexArray;
    private final String textureID;
    private final BlockVertex[] vertices;

    public BlockFace(Directory occludesTo, Directory occlude, String textureID, BlockVertex[] vertices) {
        if(vertices.length != VERTICES_NUMBER)
            throw new IllegalArgumentException("Vertices count must be " + VERTICES_NUMBER + " / " + vertices.length);

        this.occludesTo = occludesTo;
        this.occlude = occlude;
        this.textureID = textureID;
        this.vertices = vertices;

        // calculate normals
        calculateNormals(vertices);

        // vertex data array
        final FloatList vertexDataList = new FloatList();
        for(BlockVertex vertex: vertices)
            vertexDataList.add(vertex.array());
        this.vertexArray = vertexDataList.arrayTrimmed();
    }

    public BlockFace(Directory occlude, String textureID, BlockVertex[] vertices) {
        this(calculateOccludesTo(vertices), occlude, textureID, vertices);
    }

    public BlockFace(String textureID, BlockVertex[] vertices) {
        this(calculateOccludesTo(vertices), calculateOcclude(vertices), textureID, vertices);
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

    public BlockVertex[] getVertices() {
        return vertices;
    }

    public float[] getVertexArray() {
        return vertexArray;
    }


    private static void calculateNormals(BlockVertex[] vertices) {
        for(int i = 0; i < vertices.length; i += VERTICES_NUMBER){
            final BlockVertex vertexA = vertices[i + 0];
            final BlockVertex vertexB = vertices[i + 1];
            final BlockVertex vertexC = vertices[i + 2];
            final BlockVertex vertexD = vertices[i + 3];

            final Vec3f AB = new Vec3f()
                .set(vertexB.getX(), vertexB.getY(), vertexB.getZ())
                .sub(vertexA.getX(), vertexA.getY(), vertexA.getZ());

            final Vec3f BC = new Vec3f()
                .set(vertexC.getX(), vertexC.getY(), vertexC.getZ())
                .sub(vertexB.getX(), vertexB.getY(), vertexB.getZ());

            final Vec3f normal = AB.setCrs(AB, BC).nor();
            vertexA.setNormal(normal);
            vertexB.setNormal(normal);
            vertexC.setNormal(normal);
            vertexD.setNormal(normal);
        }
    }


    private static Directory calculateOccludesTo(BlockVertex[] vertices) {
        final float x0 = vertices[0].getX();
        final float y0 = vertices[0].getY();
        final float z0 = vertices[0].getZ();
        if(x0 < 0 || x0 > 1 || y0 < 0 || y0 > 1 || z0 < 0 || z0 > 1)
            return Directory.NONE;

        return evalDirectory(vertices, x0, y0, z0);
    }

    private static Directory calculateOcclude(BlockVertex[] vertices) {
        final float x0 = vertices[0].getX();
        final float y0 = vertices[0].getY();
        final float z0 = vertices[0].getZ();
        if(!(x0 == 0F || x0 == 1F) || !(y0 == 0F || y0 == 1F) || !(z0 == 0F || z0 == 1F))
            return Directory.NONE;

        return evalDirectory(vertices, x0, y0, z0);
    }

    private static Directory evalDirectory(BlockVertex[] vertices, float x0, float y0, float z0) {
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
