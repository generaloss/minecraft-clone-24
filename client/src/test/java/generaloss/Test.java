package generaloss;

import jpize.app.Jpize;
import jpize.app.JpizeApplication;
import jpize.gl.Gl;
import jpize.glfw.Glfw;
import jpize.glfw.init.GlfwPlatform;
import jpize.glfw.input.Key;
import jpize.util.math.Mathc;
import jpize.util.math.Maths;
import jpize.util.pixmap.Canvas;

public class Test extends JpizeApplication {

    private final float[][] map;
    private final Canvas canvas;

    public Test() {
        this.map = new float[3][3];
        this.generateMap();
        this.canvas = new Canvas();
        this.drawTemplate();
    }

    @Override
    public void update() {
        if(Key.G.down()){
            this.generateMap();
        }

        canvas.clear();
        this.drawTemplate();

        if(Key.A.pressed()){

            final float[][] smooth = new float[2][2];
            for(int i1 = 0; i1 < 2; i1++) {
                final int i0 = (i1 - 1);
                for(int j1 = 0; j1 < 2; j1++) {
                    final int j0 = (j1 - 1);
                    smooth[i1][j1] += (
                        map[i0 + 1][j0 + 1] +
                        map[i1 + 1][j0 + 1] +
                        map[i0 + 1][j1 + 1] +
                        map[i1 + 1][j1 + 1]
                    ) / 4F;
                }
            }

            final int size = Maths.round(Jpize.getHeight() / 3F);
            for(int i = size; i < size * 2; i++) {
                for(int j = size; j < size * 2; j++) {

                    final float x = ((float) i - size) / size;
                    final float y = ((float) j - size) / size;
                    final float invX = (1F - x);
                    final float invY = (1F - y);

                    final float x_y1 = (smooth[0][0] * invX + smooth[1][0] * x);
                    final float x_y2 = (smooth[0][1] * invX + smooth[1][1] * x);

                    final float xy = (x_y1 * invY + x_y2 * y);
                    canvas.setPixel(i, j, xy, xy, xy);
                }
            }

            for(int i = 0; i < 2; i++) {
                for(int j = 0; j < 2; j++) {
                    final float value = smooth[i][j];
                    canvas.fillCircle(i * size + size, j * size + size, 20, value, value, value);
                }
            }
        }
    }

    private void drawTemplate() {
        final int size = Maths.round(Jpize.getHeight() / 3F);
        for(int i = 0; i < map.length; i++) {
            final float[] column = map[i];
            for(int j = 0; j < column.length; j++) {
                final float value = map[i][j];
                canvas.fill(i * size, j * size, (i + 1) * size, (j + 1) * size, value, value, value);
            }
        }
    }

    private void generateMap() {
        for(int i = 0; i < map.length; i++){
            final float[] column = map[i];
            for(int j = 0; j < column.length; j++){
                map[i][j] = Mathc.random();
            }
        }
    }

    @Override
    public void render() {
        Gl.clearColorBuffer();
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
