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

    private BlockVertex[] vertices;
    private float[] vertexArray;
    private String textureID;

    private Directory hidesFace;
    private Directory hideOppositeFace;


    public BlockVertex[] getVertices() {
        return vertices;
    }

    public float[] getVertexArray() {
        return vertexArray;
    }

    public BlockFace setVertices(BlockVertex[] vertices) {
        if(vertices.length != VERTICES_NUMBER)
            throw new IllegalArgumentException("Vertices count must be " + VERTICES_NUMBER + " / " + vertices.length);

        this.vertices = vertices;
        this.calculateNormals();

        // create array
        final FloatList vertexDataList = new FloatList();
        for(BlockVertex vertex: vertices)
            vertexDataList.add(vertex.array());
        this.vertexArray = vertexDataList.arrayTrimmed();

        return this;
    }

    private void calculateNormals() {
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


    public String getTextureID() {
        return textureID;
    }

    public BlockFace setTextureID(String textureID) {
        this.textureID = textureID;
        return this;
    }


    public Directory getHidesFace() {
        return hidesFace;
    }

    public BlockFace setHidesFace(Directory hidesFace) {
        this.hidesFace = hidesFace;
        return this;
    }

    public BlockFace calculateHidesFace() {
        final float x0 = vertices[0].getX();
        final float y0 = vertices[0].getY();
        final float z0 = vertices[0].getZ();
        if(x0 < 0 || x0 > 1 || y0 < 0 || y0 > 1 || z0 < 0 || z0 > 1){
            this.setHidesFace(Directory.NONE);
        }else{
            final Directory dir = evalDirectory(vertices, x0, y0, z0);
            return this.setHidesFace(dir);
        }
        return this;
    }


    public Directory getHideOppositeFace() {
        return hideOppositeFace;
    }

    public BlockFace setHideOppositeFace(Directory hideOppositeFace) {
        this.hideOppositeFace = hideOppositeFace;
        return this;
    }

    public BlockFace calculateHideOppositeFace() {
        final float x0 = vertices[0].getX();
        final float y0 = vertices[0].getY();
        final float z0 = vertices[0].getZ();
        if(!(x0 == 0F || x0 == 1F) || !(y0 == 0F || y0 == 1F) || !(z0 == 0F || z0 == 1F)){
            return this.setHideOppositeFace(Directory.NONE);
        }else{
            final Directory dir = evalDirectory(vertices, x0, y0, z0);
            this.setHideOppositeFace(dir);
        }
        return this;
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
