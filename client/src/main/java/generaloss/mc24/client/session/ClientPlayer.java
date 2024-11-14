package generaloss.mc24.client.session;

import jpize.app.Jpize;
import jpize.util.camera.PerspectiveCamera;
import jpize.util.input.MotionInput;
import jpize.util.input.RotationInput;
import jpize.util.math.EulerAngles;

public class ClientPlayer {

    private final PerspectiveCamera camera;
    private final MotionInput motionInput;
    private final RotationInput rotationInput;

    public ClientPlayer() {
        this.camera = new PerspectiveCamera(0.01F, 100F, 85F);
        this.motionInput = new MotionInput();
        this.rotationInput = new RotationInput(new EulerAngles(), false);
    }

    public PerspectiveCamera camera() {
        return camera;
    }

    public RotationInput input() {
        return rotationInput;
    }

    public void update() {
        camera.rotation().setRotation(rotationInput.getTarget());
        camera.position().add(motionInput.getMotionDirected().mul(Jpize.getDeltaTime() * 10));
        camera.update();
    }

}
