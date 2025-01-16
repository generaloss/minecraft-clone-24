package generaloss.mc24.client.block;

import generaloss.mc24.server.Direction;
import generaloss.mc24.server.block.BlockState;
import jpize.util.math.Intersector;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;


import java.util.*;

public class BlockModel {

    private final BlockState blockstate;
    private final Map<Direction, List<BlockFace>> faceGroups;
    private final List<Direction> hidesOthersFaceList;
    private boolean hasAmbientOcclusion;

    public BlockModel(BlockState blockstate) {
        this.blockstate = blockstate;
        this.faceGroups = new HashMap<>();
        this.hidesOthersFaceList = new ArrayList<>();
        this.setHasAmbientOcclusion(true);
    }

    public BlockState getBlockState() {
        return blockstate;
    }


    public List<BlockFace> getFacesGroup(Direction dir) {
        return faceGroups.getOrDefault(dir, List.of());
    }

    public BlockModel addFace(BlockFace face) {
        // add to group
        final Direction direction = face.getDirection();
        final List<BlockFace> group = faceGroups.getOrDefault(direction, new ArrayList<>());
        group.add(face);
        faceGroups.put(direction, group);
        // hide opposite
        final Direction oppositeDirection = direction.opposite();
        if(!hidesOthersFaceList.contains(oppositeDirection) && (face.isSolid() || this.isMultiFaceSolid(direction)))
            hidesOthersFaceList.add(oppositeDirection);
        return this;
    }


    private boolean isMultiFaceSolid(Direction dir) {
        final List<BlockFace> faces = faceGroups.get(dir);
        if(faces == null)
            return false;

        float totalArea = 0F;
        for(BlockFace face1 : faces) {
            totalArea += getFaceArea(face1);

            for(BlockFace face2 : faces){
                if(face1 == face2)
                    continue;
                totalArea -= getFacesIntersectArea(face1, face2);
            }
        }

        if(blockstate.isBlockID("stone_stairs") && dir == Direction.EAST)
            System.out.println("totalArea " + totalArea);
        return totalArea >= 1F;
    }

    private static float getFaceArea(BlockFace quad1) {
        final BlockVertex[] varr = quad1.getVertices();
        final Direction dir = quad1.getDirection();

        final float[] vertices = new float[] {
                (dir.getZ() != 0) ? varr[0].getX() : (dir.getY() != 0) ? varr[0].getX() : varr[0].getY(),
                (dir.getZ() != 0) ? varr[0].getY() : varr[0].getZ(),
                (dir.getZ() != 0) ? varr[1].getX() : (dir.getY() != 0) ? varr[1].getX() : varr[1].getY(),
                (dir.getZ() != 0) ? varr[1].getY() : varr[1].getZ(),
                (dir.getZ() != 0) ? varr[2].getX() : (dir.getY() != 0) ? varr[2].getX() : varr[2].getY(),
                (dir.getZ() != 0) ? varr[2].getY() : varr[2].getZ(),
                (dir.getZ() != 0) ? varr[3].getX() : (dir.getY() != 0) ? varr[3].getX() : varr[3].getY(),
                (dir.getZ() != 0) ? varr[3].getY() : varr[3].getZ()
        };

        return Intersector.getPolygonArea(vertices);
    }

    private float getFacesIntersectArea(BlockFace quad1, BlockFace quad2) {
        final BlockVertex[] varr1 = quad1.getVertices();
        final BlockVertex[] varr2 = quad2.getVertices();
        final Direction dir1 = quad1.getDirection();
        final Direction dir2 = quad2.getDirection();

        final float[] vertices1 = new float[] {
            (dir1.getZ() != 0) ? varr1[0].getX() : (dir1.getY() != 0) ? varr1[0].getX() : varr1[0].getY(),
            (dir1.getZ() != 0) ? varr1[0].getY() : varr1[0].getZ(),
            (dir1.getZ() != 0) ? varr1[1].getX() : (dir1.getY() != 0) ? varr1[1].getX() : varr1[1].getY(),
            (dir1.getZ() != 0) ? varr1[1].getY() : varr1[1].getZ(),
            (dir1.getZ() != 0) ? varr1[2].getX() : (dir1.getY() != 0) ? varr1[2].getX() : varr1[2].getY(),
            (dir1.getZ() != 0) ? varr1[2].getY() : varr1[2].getZ(),
            (dir1.getZ() != 0) ? varr1[3].getX() : (dir1.getY() != 0) ? varr1[3].getX() : varr1[3].getY(),
            (dir1.getZ() != 0) ? varr1[3].getY() : varr1[3].getZ()
        };
        final float[] vertices2 = new float[] {
            (dir2.getZ() != 0) ? varr2[0].getX() : (dir2.getY() != 0) ? varr2[0].getX() : varr2[0].getY(),
            (dir2.getZ() != 0) ? varr2[0].getY() : varr2[0].getZ(),
            (dir2.getZ() != 0) ? varr2[1].getX() : (dir2.getY() != 0) ? varr2[1].getX() : varr2[1].getY(),
            (dir2.getZ() != 0) ? varr2[1].getY() : varr2[1].getZ(),
            (dir2.getZ() != 0) ? varr2[2].getX() : (dir2.getY() != 0) ? varr2[2].getX() : varr2[2].getY(),
            (dir2.getZ() != 0) ? varr2[2].getY() : varr2[2].getZ(),
            (dir2.getZ() != 0) ? varr2[3].getX() : (dir2.getY() != 0) ? varr2[3].getX() : varr2[3].getY(),
            (dir2.getZ() != 0) ? varr2[3].getY() : varr2[3].getZ()
        };

        final float[] intersectionVertices = getPolygonIntersection(vertices1, vertices2);
        if(blockstate.isBlockID("stone_stairs") && dir1 == Direction.EAST)
            System.out.println("verts: " + Arrays.toString(intersectionVertices));
        return Intersector.getPolygonArea(intersectionVertices);
    }

    public static float[] getPolygonIntersection(float[] vertices1, float[] vertices2) {
        GeometryFactory geometryFactory = new GeometryFactory();

        // Создание многоугольников из массива вершин
        Polygon polygon1 = createPolygon(vertices1, geometryFactory);
        Polygon polygon2 = createPolygon(vertices2, geometryFactory);

        // Вычисление пересечения
        Geometry intersection = polygon1.intersection(polygon2);

        // Преобразование результата в массив float[]
        return geometryToFloatArray(intersection);
    }

    private static Polygon createPolygon(float[] vertices, GeometryFactory geometryFactory) {
        Coordinate[] coordinates = new Coordinate[vertices.length / 2 + 1];
        for (int i = 0; i < vertices.length; i += 2) {
            coordinates[i / 2] = new Coordinate(vertices[i], vertices[i + 1]);
        }
        // Замыкаем многоугольник
        coordinates[coordinates.length - 1] = coordinates[0];
        return geometryFactory.createPolygon(coordinates);
    }

    private static float[] geometryToFloatArray(Geometry geometry) {
        if (!(geometry instanceof Polygon)) {
            System.err.println("Result is not a polygon. (" + geometry + ")");
            return new float[0];
        }

        Coordinate[] coordinates = geometry.getCoordinates();
        float[] result = new float[coordinates.length * 2];
        for (int i = 0; i < coordinates.length; i++) {
            result[i * 2] = (float) coordinates[i].x;
            result[i * 2 + 1] = (float) coordinates[i].y;
        }
        return result;
    }


    public boolean isHidesOthersFace(Direction dir) {
        return hidesOthersFaceList.contains(dir);
    }


    public boolean hasAmbientOcclusion() {
        return hasAmbientOcclusion;
    }

    public BlockModel setHasAmbientOcclusion(boolean hasAmbientOcclusion) {
        this.hasAmbientOcclusion = hasAmbientOcclusion;
        return this;
    }

}
