package generaloss;

import generaloss.mc24.server.common.IntSortedList;
import jpize.app.Jpize;
import jpize.app.JpizeApplication;
import jpize.gl.Gl;
import jpize.glfw.input.Key;
import jpize.util.color.Color;
import jpize.util.font.Font;
import jpize.util.mesh.TextureBatch;

import java.util.ArrayList;

public class BinarySearchColumn extends JpizeApplication {

    private final TextureBatch batch;
    private final Font font;
    private final float[] offsetY, offsetTargetY;
    private final int secondOffsetX = (90 + 10);

    private final IntSortedList<Integer> list;

    public BinarySearchColumn() {
        this.batch = new TextureBatch();
        this.font = new Font().loadDefault();
        this.font.getOptions().color().setRGB(0x121215);
        this.offsetY = new float[2];
        this.offsetTargetY = new float[2];
        Gl.clearColor(new Color().setRGB(0x121215));

        this.list = new IntSortedList<>(new ArrayList<>(), object -> object);
    }

    @Override
    public void update() {
        this.handleHeightInput();
        this.handleScrollInput();
    }

    @Override
    public void render() {
        Gl.clearColorBuffer();
        batch.setup();

        // first
        batch.position().set(Jpize.getHalfWidth(), 0);
        batch.drawRectRGB(-45, 0, 90, Jpize.getHeight(), 0x202020);

        batch.position().y += (offsetY[0] + Jpize.getHalfHeight());
        int numOffsetY = 0;
        for(Integer height: list){
            numOffsetY += (80 + 5);
            batch.drawRectRGB(-40, -40 + numOffsetY, 80, 80, 0x454545);

            final String text = String.valueOf(height);
            final float textX = ((80 - font.getTextWidth(text)) * 0.5F - 40);
            final float textY = ((80 - font.getHeightScaled()) * 0.5F + (numOffsetY - 40));
            font.drawText(batch, text, textX, textY);
        }

        // second
        batch.position().set(Jpize.getHalfWidth() + secondOffsetX, 0);
        batch.drawRectRGB(-45, 0, 90, Jpize.getHeight(), 0x402020);

        batch.position().y += (offsetY[1] + Jpize.getHalfHeight());
        numOffsetY = 0;
        for(Integer height: list.sublistTo(-2)){
            numOffsetY += (80 + 5);
            batch.drawRectRGB(-40, -40 + numOffsetY, 80, 80, 0x654545);

            final String text = String.valueOf(height);
            final float textX = ((80 - font.getTextWidth(text)) * 0.5F - 40);
            final float textY = ((80 - font.getHeightScaled()) * 0.5F + (numOffsetY - 40));
            font.drawText(batch, text, textX, textY);
        }

        batch.position().zero();
        batch.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }


    private void centerContent() {
        offsetTargetY[0] = -(list.size() * 80 + (list.size() - 1) * 5) * 0.5F - 40;
    }

    private void handleScrollInput() {
        final float cursorX = (Jpize.getX() - Jpize.getHalfWidth() + 40);

        final int index = ((cursorX >= 0 && cursorX < 90) ? 0 :
                (cursorX >= secondOffsetX && cursorX < secondOffsetX + 90) ? 1 : -1);

        if(index != -1)
            offsetTargetY[index] -= (Jpize.getScroll() * 80F);

        // update scroll offsets
        for(int i = 0; i < 2; i++)
            offsetY[i] += (offsetTargetY[i] - offsetY[i]) * 0.1F;
    }

    private void handleHeightInput() {
        if(Key.P.pressed()) {
            final int num = this.getPressedNum();
            if(num != Integer.MIN_VALUE){
                list.put(num);
                this.centerContent();
            }
        }else if(Key.R.pressed()) {
            final int num = this.getPressedNum();
            if(num != Integer.MIN_VALUE){
                list.remove(num);
                this.centerContent();
            }
        }
    }

    private int getPressedNum() {
        if(Key.NUM_0.down()) return 0 - 5;
        if(Key.NUM_1.down()) return 1 - 5;
        if(Key.NUM_2.down()) return 2 - 5;
        if(Key.NUM_3.down()) return 3 - 5;
        if(Key.NUM_4.down()) return 4 - 5;
        if(Key.NUM_5.down()) return 5 - 5;
        if(Key.NUM_6.down()) return 6 - 5;
        if(Key.NUM_7.down()) return 7 - 5;
        if(Key.NUM_8.down()) return 8 - 5;
        if(Key.NUM_9.down()) return 9 - 5;
        return Integer.MIN_VALUE;
    }



    public static void main(String[] args) {
        Jpize.create(1280, 720, "Column Binary Search Test")
                .build()
                .setApp(new BinarySearchColumn());
        Jpize.run();
    }

}
