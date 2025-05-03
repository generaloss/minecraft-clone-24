package generaloss;

import jpize.context.Jpize;
import jpize.context.JpizeApplication;
import jpize.lwjgl.context.ContextManager;
import jpize.lwjgl.context.GlfwContextBuilder;
import jpize.opengl.gl.GL;
import jpize.util.pixmap.Canvas;

public class Line extends JpizeApplication {

    Canvas canvas;

    float a = 0.8F;
    float b = 4F;

    int centerX = 300;
    int centerY = 400;

    public Line() {
        canvas = new Canvas();
    }

    @Override
    public void render() {
        a += Jpize.getScroll() * 0.05F;

        GL.clearColorBuffer();
        canvas.clear();
        canvas.drawCircleRGB(centerX, centerY, 5, 0xFFFFFF);

        for(int x = 0; x < Jpize.getWidth(); x++) {
            // line
            float y = (a * x + b);
            canvas.setPixelRGB(x, Jpize.getHeight() - (int) y, 0xFFFFFF);
            // perpendecular
            float y1 = (1 / a * x + b / a);
            canvas.setPixelRGB(x, Jpize.getHeight() - (int) y1, 0x00FFFF);
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
        GlfwContextBuilder.create(1280, 720, "Line").build().setApp(new Line());
        ContextManager.run();
    }

}
