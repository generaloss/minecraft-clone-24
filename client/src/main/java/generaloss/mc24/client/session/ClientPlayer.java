package generaloss.mc24.client.session;

import jpize.app.Jpize;
import jpize.util.camera.PerspectiveCamera;
import jpize.util.input.MotionInput;
import jpize.util.input.RotationInput;
import jpize.util.math.EulerAngles;
import jpize.util.math.vector.Vec3f;

public class ClientPlayer {

    private final PerspectiveCamera camera;
    private final MotionInput motionInput;
    private final RotationInput rotationInput;
    private final Vec3f velocity;

    public ClientPlayer() {
        this.camera = new PerspectiveCamera(0.01F, 1000F, 85F);
        this.motionInput = new MotionInput();
        this.rotationInput = new RotationInput(new EulerAngles(), false);
        this.rotationInput.setSpeed(0.3F);
        this.velocity = new Vec3f();
    }

    public PerspectiveCamera camera() {
        return camera;
    }

    public RotationInput input() {
        return rotationInput;
    }

    public void update() {
        camera.rotation().setRotation(rotationInput.getTarget());
        motionInput.update(rotationInput.getTarget().getYaw());

        velocity.add(motionInput.getMotionDirected().mul(Jpize.getDeltaTime()));
        velocity.mul(0.97F);

        camera.position().add(velocity);
        camera.update();
    }

}
