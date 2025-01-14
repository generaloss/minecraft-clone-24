package generaloss;

import jpize.app.Jpize;
import jpize.app.JpizeApplication;
import jpize.gl.Gl;
import jpize.glfw.Glfw;
import jpize.glfw.init.GlfwPlatform;
import jpize.util.Rect;
import jpize.util.math.Intersector;
import jpize.util.math.vector.Vec2f;
import jpize.util.pixmap.Canvas;
import jpize.util.time.Stopwatch;

public class Test extends JpizeApplication {

    private final int[] poly1i   = {0, 0,  0, 200,  150, 300,  150, 200,  250, 150,  400, 250,  400, 150,  250, 0}; //0,0, 200,0, 200,200, 0,200};//
    private final float[] poly1f = {0, 0,  0, 200,  150, 300,  150, 200,  250, 150,  400, 250,  400, 150,  250, 0}; //0,0, 200,0, 200,200, 0,200};//

    private final int[] poly2i   = {0, 0,  400, 0,  300, 300,  100, 200}; //-40,100, 100,-40, 240,100, 100,240};//
    private final float[] poly2f = {0, 0,  400, 0,  300, 300,  100, 200}; //-40,100, 100,-40, 240,100, 100,240};//

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

        final float[] intersectionf = Inter.getPolygonsIntersection(poly1f, poly2fTranslated);
        final int[] intersectioni = new int[intersectionf.length];
        for(int i = 0; i < intersectionf.length; i++)
            intersectioni[i] = (int) intersectionf[i];


        canvas.enableBlending();
        final Rect poly1Bounds = Inter.getPolygonBounds(new Rect(), poly1f);
        for(int i = (int) poly1Bounds.x - 50; i < poly1Bounds.width + poly1Bounds.x + 50; i++){
            for(int j = (int) poly1Bounds.y - 50; j < poly1Bounds.height + poly1Bounds.y + 50; j++){
                if(Inter.isPointInPolygon(i, j, poly1f))
                    canvas.setPixelRGBA(i, j, 0x333300AA);
            }
        }
        canvas.disableBlending();
        canvas.drawLinePathRGB(0xAAAA00, poly1i);

        canvas.enableBlending();
        final Rect poly2Bounds = Inter.getPolygonBounds(poly1Bounds, poly2fTranslated);
        for(int i = (int) poly2Bounds.x - 50; i < poly2Bounds.width + poly2Bounds.x + 50; i++){
            for(int j = (int) poly2Bounds.y - 50; j < poly2Bounds.height + poly2Bounds.y + 500; j++){
                if(Inter.isPointInPolygon(i, j, poly2fTranslated))
                    canvas.setPixelRGBA(i, j, 0x003300AA);
            }
        }
        canvas.disableBlending();
        canvas.drawLinePathRGB(0x00AA00, poly2iTranslated);

        canvas.enableBlending();
        final Rect intersectionBounds = Inter.getPolygonBounds(poly2Bounds, intersectionf);
        // for(int i = (int) intersectionBounds.x - 50; i < intersectionBounds.width + intersectionBounds.x + 50; i++){
        //     for(int j = (int) intersectionBounds.y - 50; j < intersectionBounds.height + intersectionBounds.y + 50; j++){
        //         if(Inter.isPointInPolygon(i, j, intersectionf))
        //             canvas.setPixelRGBA(i, j, 0x330000AA);
        //     }
        // }
        canvas.disableBlending();
        // canvas.drawDottedLinePathRGB(0xFF0000, 10, intersectioni);
        for(int i = 0; i < intersectioni.length; i += 2)
            canvas.drawDottedLineRGB(intersectioni[i], intersectioni[i + 1], intersectioni[(i + 2) % intersectioni.length], intersectioni[(i + 3) % intersectioni.length], 10, 0xFF0000);

        for(int i = 0; i < intersectioni.length; i += 2){
            final int x = intersectioni[i];
            final int y = intersectioni[i + 1];
            canvas.drawCircleRGB(x, y, 3, 0xFFFFFF);
        }

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
