package generaloss;

import jpize.app.Jpize;
import jpize.app.JpizeApplication;
import jpize.gl.Gl;
import jpize.glfw.Glfw;
import jpize.glfw.init.GlfwPlatform;
import jpize.util.math.Intersector;
import jpize.util.math.Maths;
import jpize.util.math.vector.Vec2f;
import jpize.util.math.vector.Vec3f;
import jpize.util.pixmap.Canvas;
import jpize.util.time.Stopwatch;
import jpize.util.time.Sync;

public class Test extends JpizeApplication {

    private int x1=100, y1=200,  x2=100, y2=600,  x3=600, y3=600,  x4=600, y4=200,  x5=400, y5=400;
    private final Canvas canvas;

    public Test() {
        this.canvas = new Canvas();
    }

    @Override
    public void update() { }

    Stopwatch stopwatch = new Stopwatch().start();

    @Override
    public void render() {
        Gl.clearColorBuffer();
        canvas.clear();

        int color = 0xFFFFFF;

        final float px = Jpize.getX();
        final float py = Jpize.input().getCursorNativeY();

        if(Intersector.isPointOnPolygon(px, py, x1, y1,  x2, y2,  x3, y3,  x4, y4,  x5, y5))
            color = 0x55FF55;

        canvas.drawLineRGB(x1, y1, x2, y2, color);
        canvas.drawLineRGB(x2, y2, x3, y3, color);
        canvas.drawLineRGB(x3, y3, x4, y4, color);
        canvas.drawLineRGB(x4, y4, x5, y5, color);
        canvas.drawLineRGB(x5, y5, x1, y1, color);

        canvas.drawLineRGB(x1, y1, (int) px, (int) py, 0xFFFF88);
        canvas.drawLineRGB(x2, y2, (int) px, (int) py, 0xFFFF88);
        canvas.drawLineRGB(x3, y3, (int) px, (int) py, 0xFFFF88);
        canvas.drawLineRGB(x4, y4, (int) px, (int) py, 0xFFFF88);
        canvas.drawLineRGB(x5, y5, (int) px, (int) py, 0xFFFF88);

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
