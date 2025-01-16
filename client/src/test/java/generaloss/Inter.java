package generaloss;

import jpize.util.math.vector.Vec2f;
// import org.locationtech.jts.geom.*;

public class Inter {

    public static float[] getPolygonIntersection(Vec2f[] verticesA, Vec2f[] verticesB) {

    }

    public static float[] getPolygonIntersection(float[] verticesA, float[] verticesB) {
         final Vec2f[] a = new Vec2f[verticesA.length / 2];
         final Vec2f[] b = new Vec2f[verticesB.length / 2];
         for(int i = 0; i < a.length; i++) {
             final int index = (i * 2);
             a[i] = new Vec2f(verticesA[index], verticesA[index + 1]);
         }
        for(int i = 0; i < b.length; i++) {
            final int index = (i * 2);
            b[i] = new Vec2f(verticesB[index], verticesB[index + 1]);
        }
        return getPolygonIntersection(a, b);
    }


    // public static float[] getPolygonIntersection(float[] vertices1, float[] vertices2) {
    //     GeometryFactory geometryFactory = new GeometryFactory();

    //     // Создание многоугольников из массива вершин
    //     Polygon polygon1 = createPolygon(vertices1, geometryFactory);
    //     Polygon polygon2 = createPolygon(vertices2, geometryFactory);

    //     // Вычисление пересечения
    //     Geometry intersection = polygon1.intersection(polygon2);

    //     // Преобразование результата в массив float[]
    //     return geometryToFloatArray(intersection);
    // }

    // private static Polygon createPolygon(float[] vertices, GeometryFactory geometryFactory) {
    //     Coordinate[] coordinates = new Coordinate[vertices.length / 2 + 1];
    //     for(int i = 0; i < vertices.length; i += 2){
    //         coordinates[i / 2] = new Coordinate(vertices[i], vertices[i + 1]);
    //     }
    //     // Замыкаем многоугольник
    //     coordinates[coordinates.length - 1] = coordinates[0];
    //     return geometryFactory.createPolygon(coordinates);
    // }

    // private static float[] geometryToFloatArray(Geometry geometry) {
    //     if(geometry instanceof MultiPolygon multiPolygon){
    //         return geometryToFloatArray(multiPolygon.getGeometryN(0));
    //     }

    //     Coordinate[] coordinates = geometry.getCoordinates();
    //     float[] result = new float[coordinates.length * 2];
    //     for(int i = 0; i < coordinates.length; i++){
    //         result[i * 2] = (float) coordinates[i].x;
    //         result[i * 2 + 1] = (float) coordinates[i].y;
    //     }
    //     return result;
    // }

}
