package generaloss;

import jpize.app.Jpize;
import jpize.app.JpizeApplication;
import jpize.gl.Gl;
import jpize.glfw.Glfw;
import jpize.glfw.init.GlfwPlatform;
import jpize.util.array.FloatList;
import jpize.util.math.Intersector;
import jpize.util.math.vector.Vec2f;
import jpize.util.pixmap.Canvas;
import jpize.util.time.Stopwatch;

import java.util.*;
import java.util.stream.Collectors;

public class Test extends JpizeApplication {

    private final int[] poly1i = {0, 0,  0, 200,  150, 300,  150, 200,  250, 150,  400, 250,  400, 150,  250, 0};
    private final float[] poly1f = {0, 0,  0, 200,  150, 300,  150, 200,  250, 150,  400, 250,  400, 150,  250, 0};

    private final int[] poly2i = {0, 0,  300, 0,  400, 300,  100, 200};
    private final float[] poly2f = {0, 0,  300, 0,  400, 300,  100, 200};

    private final Canvas canvas;

    public Test() {
        this.canvas = new Canvas();
        for(int i = 0; i < poly1i.length; i += 2){
            poly1i[i] += 400;
            poly1i[i + 1] += 200;
            poly1f[i] += 400;
            poly1f[i + 1] += 200;
        }
    }

    Stopwatch stopwatch = new Stopwatch().start();

    public static float[] getPolygonsIntersection(float[] vertices1, float[] vertices2) {
        final Vec2f dst_point = new Vec2f();
        List<Vec2f> verticesList = new ArrayList<>();

        for(int i = 0; i < vertices1.length; i += 2){
            final float p1x1 = vertices1[i];
            final float p1y1 = vertices1[i + 1];

            final int k1 = (i + 2) % vertices1.length;
            final float p1x2 = vertices1[k1];
            final float p1y2 = vertices1[k1 + 1];

            // check points 1
            if(Intersector.isPointOnPolygon(p1x1, p1y1, vertices2))
                verticesList.add(new Vec2f(p1x1, p1y1));

            // check segments
            for(int j = 0; j < vertices2.length; j += 2){
                final float p2x1 = vertices2[j];
                final float p2y1 = vertices2[j + 1];

                final int k2 = (j + 2) % vertices2.length;
                final float p2x2 = vertices2[k2];
                final float p2y2 = vertices2[k2 + 1];

                if(Intersector.getSegmentIntersectSegment(dst_point, p1x1, p1y1, p1x2, p1y2,  p2x1, p2y1, p2x2, p2y2) && !dst_point.isZero()) {
                    verticesList.add(new Vec2f(dst_point.x, dst_point.y));
                    dst_point.zero();
                }
            }
        }

        for(int j = 0; j < vertices2.length; j += 2){
            final float p2x1 = vertices2[j];
            final float p2y1 = vertices2[j + 1];

            // check points 2
            if(Intersector.isPointInPolygon(p2x1, p2y1, vertices1))
                verticesList.add(new Vec2f(p2x1, p2y1));
        }

        if(verticesList.isEmpty())
            return new float[0];

        for(int i = 0; i < verticesList.size(); i++){
            final Vec2f value = verticesList.get(i);
            if(value.x == -0) value.x = 0;
            if(value.y == -0) value.y = 0;
            verticesList.set(i, value);
        }

        verticesList = verticesList.stream().distinct().collect(Collectors.toList());

        final Vec2f center1 = Intersector.getPolygonCenterOfGravity(new Vec2f(), vertices1);
        final Vec2f center2 = Intersector.getPolygonCenterOfGravity(new Vec2f(), vertices2);
        final Vec2f center = center1.add(center2).mul(0.5F);

        verticesList.sort(Comparator.comparingDouble((p) -> Vec2f.angle(p.x - center.x, p.y - center.y)));

        final FloatList vertices = new FloatList();
        for(Vec2f point: verticesList)
            vertices.add(point.x, point.y);
        return vertices.arrayTrimmed();
    }

    @Override
    public void render() {
        Gl.clearColorBuffer();
        canvas.clear();

        final float px = Jpize.getX();
        final float py = Jpize.input().getCursorNativeY();

        final Vec2f poly2center = Intersector.getPolygonCenterOfGravity(new Vec2f(), poly2f);

        final int[] poly2iTranslated = new int[poly2i.length];
        for(int i = 0; i < poly2i.length; i += 2) {
            poly2iTranslated[i] = poly2i[i] + (int) (px - poly2center.x);
            poly2iTranslated[i + 1] = poly2i[i + 1] + (int) (py - poly2center.y);
        }
        final float[] poly2fTranslated = new float[poly2iTranslated.length];
        for(int i = 0; i < poly2iTranslated.length; i++)
            poly2fTranslated[i] = poly2iTranslated[i];

        final float[] intersectionf = getPolygonsIntersection(poly1f, poly2fTranslated);
        final int[] intersectioni = new int[intersectionf.length];
        for(int i = 0; i < intersectionf.length; i++)
            intersectioni[i] = (int) intersectionf[i];

        canvas.drawLinePathRGB(0xFF0000, poly1i);
        canvas.drawLinePathRGB(0x00FF00, poly2iTranslated);
        canvas.drawDottedLinePathRGB(0xFFFF00, 10, intersectioni);

        canvas.render();
    }

    @Override
    public void resize(int width, int height) {
        canvas.resize(width, height);
    }

    @Override
    public void dispose() {
        canvas.dispose();
    }


    public static void main(String[] args) {
        Glfw.glfwInitHintPlatform(GlfwPlatform.X11);
        Jpize.create(1280, 720, "Test").build().setApp(new Test());
        Jpize.run();
    }

}
