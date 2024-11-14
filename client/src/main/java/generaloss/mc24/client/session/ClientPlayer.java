package generaloss.mc24.client.session;

import jpize.app.Jpize;
import jpize.util.camera.PerspectiveCamera;
import jpize.util.ctrl.MotionInput;
import jpize.util.ctrl.RotationInput;
import jpize.util.math.EulerAngles;

public class ClientPlayer {

    private final PerspectiveCamera camera;
    private final MotionInput motionInput;
    private final RotationInput rotationInput;

    public ClientPlayer() {
        this.camera = new PerspectiveCamera(0.01F, 100F, 85F);
        this.motionInput = new MotionInput();
        this.rotationInput = new RotationInput(new EulerAngles());
    }

    public PerspectiveCamera camera() {
        return camera;
    }

    public void update() {
        camera.rotation().setRotation(rotationInput.getTarget());
        camera.position().add(motionInput.getMotionDirected().mul(Jpize.getDT() * 10));
        camera.update();
    }

}
