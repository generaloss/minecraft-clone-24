package generaloss.mc24.client.player;

import generaloss.mc24.client.Main;
import generaloss.mc24.server.network.packet2s.PlayerMovePacket2S;
import generaloss.mc24.server.player.AbstractPlayer;
import jpize.app.Jpize;
import jpize.glfw.input.Key;
import jpize.util.camera.PerspectiveCamera;
import jpize.util.input.MotionInput;
import jpize.util.input.RotationInput;
import jpize.util.math.vector.Vec3f;

import java.util.UUID;

public class ClientPlayer extends AbstractPlayer {

    private final Main context;
    private final PerspectiveCamera camera;
    private final MotionInput motionInput;
    private final RotationInput rotationInput;
    private final Vec3f velocity;
    private final BlockSelectRay blockSelectRay;
    private int frame;

    public ClientPlayer(Main context) {
        super(UUID.randomUUID());
        this.context = context;
        this.camera = new PerspectiveCamera(0.01F, 1000F, 85F);
        this.motionInput = new MotionInput();
        this.rotationInput = new RotationInput(super.rotation(), false);
        this.rotationInput.setSpeed(0.05F);
        this.rotationInput.setSmoothness(0.1F);
        this.velocity = new Vec3f();
        this.blockSelectRay = new BlockSelectRay();
    }

    public PerspectiveCamera camera() {
        return camera;
    }

    public RotationInput input() {
        return rotationInput;
    }

    public BlockSelectRay blockSelectRay() {
        return blockSelectRay;
    }

    public void update() {
        frame++;
        // control
        motionInput.update(rotationInput.getTarget().getYaw());

        final Vec3f acceleration = motionInput.getMotionDirected().mul(Jpize.getDeltaTime() * 0.2);
        if(Key.LCTRL.pressed())
            acceleration.mul(3);

        velocity.add(acceleration);
        velocity.mul(0.97F);
        super.position().add(velocity);

        if(velocity.len() > 0.01F && frame % 3 == 0)
            context.network().sendPacket(new PlayerMovePacket2S(super.position()));

        // camera
        super.rotation().constrain();
        camera.rotation().set(super.rotation());
        camera.position().set(super.position());
        camera.update();

        // ray
        blockSelectRay.update(camera, context.level(), 128);
    }

}
