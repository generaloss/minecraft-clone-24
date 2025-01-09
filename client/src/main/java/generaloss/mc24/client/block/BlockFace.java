package generaloss.mc24.client.block;

import generaloss.mc24.server.Direction;
import jpize.util.array.FloatList;
import jpize.util.math.vector.Vec3f;

public class BlockFace {

    public static final int VERTICES_NUMBER = 4;
    // public static final int[][] VERTEX_INDEX_PERMUTATIONS = new int[][] {
    //     {0, 1, 2, 3}, // normal
    //     {1, 2, 3, 0}, // rotated
    // };

    private BlockVertex[] vertices;
    private float[] vertexArray;
    private String textureID;
    private int tintIndex;

    private Direction direction;
    private Direction culling;
    private boolean solid;


    public BlockVertex[] getVertices() {
        return vertices;
    }

    public float[] getVertexArray() {
        return vertexArray;
    }

    public BlockFace setVertices(BlockVertex... vertices) {
        if(vertices.length != VERTICES_NUMBER)
            throw new IllegalArgumentException("Vertices count must be " + VERTICES_NUMBER + " / " + vertices.length);

        this.vertices = vertices;
        this.calculateNormals();
        this.calculateIsSolidFace();
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

    private void calculateIsSolidFace() {
        float numX = 0;
        float numY = 0;
        float numZ = 0;
        boolean smallX = false;
        boolean smallY = false;
        boolean smallZ = false;
        for(BlockVertex vertex : vertices) {
            final float x = vertex.getX();
            final float y = vertex.getY();
            final float z = vertex.getZ();
            numX += (x - 0.5F);
            numY += (y - 0.5F);
            numZ += (z - 0.5F);
            smallX = smallX || (x > 0F && x < 1F);
            smallY = smallY || (y > 0F && y < 1F);
            smallZ = smallZ || (z > 0F && z < 1F);
        }
        final int num =
                (Math.abs(numX) == 2F && !smallY && !smallZ ? 1 : 0) +
                (Math.abs(numY) == 2F && !smallX && !smallZ ? 1 : 0) +
                (Math.abs(numZ) == 2F && !smallX && !smallY ? 1 : 0);
        this.setSolid(num == 1);
    }


    public String getTextureID() {
        return textureID;
    }

    public BlockFace setTextureID(String textureID) {
        this.textureID = textureID;
        return this;
    }

    
    public Direction getDirection() {
        return direction;
    }
    
    public BlockFace setDirection(Direction direction) {
        this.direction = direction;
        return this;
    }
    

    public Direction getCulling() {
        return culling;
    }

    public boolean isMightBeCulled() {
        return culling != Direction.NONE;
    }

    public BlockFace setCulling(Direction culling) {
        this.culling = culling;
        return this;
    }


    public boolean isSolid() {
        return solid;
    }

    public BlockFace setSolid(boolean solid) {
        this.solid = solid;
        return this;
    }


    public int getTintIndex() {
        return tintIndex;
    }

    public BlockFace setTintIndex(int tintIndex) {
        this.tintIndex = tintIndex;
        return this;
    }

}
