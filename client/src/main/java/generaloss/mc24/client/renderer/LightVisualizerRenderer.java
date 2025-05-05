package generaloss.mc24.client.renderer;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.level.LevelChunk;
import generaloss.mc24.client.level.WorldLevel;
import generaloss.mc24.client.meshing.block.BlockCache;
import generaloss.mc24.client.meshing.block.BlockFace;
import generaloss.mc24.client.meshing.block.BlockModel;
import generaloss.mc24.client.player.ClientPlayer;
import generaloss.mc24.client.resources.ClientResources;
import generaloss.mc24.client.resources.handle.BlockModelHandle;
import generaloss.mc24.server.block.BlockState;
import generaloss.mc24.server.chunk.ChunkCache;
import generaloss.mc24.server.chunk.ChunkPos;
import generaloss.mc24.server.common.Direction;
import jpize.util.camera.PerspectiveCamera;
import jpize.util.font.Font;
import jpize.util.font.FontRenderOptions;
import jpize.util.math.matrix.Matrix4f;
import jpize.util.math.vector.Vec2f;
import jpize.util.math.vector.Vec3i;

public class LightVisualizerRenderer {

    private final WorldLevel level;
    private final ChunkCache<LevelChunk> chunkCache;
    private final BlockCache blockCache;
    private LevelChunk chunk;

    public LightVisualizerRenderer(WorldLevel level) {
        this.level = level;
        this.chunkCache = new ChunkCache<>(level);
        this.blockCache = new BlockCache();
    }

    public void render() {
        final ClientPlayer player = Main.getInstance().player();

        final int chunkX = ChunkPos.byBlock(player.camera().position().xFloor());
        final int chunkY = ChunkPos.byBlock(player.camera().position().yFloor());
        final int chunkZ = ChunkPos.byBlock(player.camera().position().zFloor());

        System.out.println(chunkX + " " + chunkY + " " + chunkZ);

        chunk = level.getChunk(chunkX, chunkY, chunkZ);
        if(chunk == null)
            return;

        this.tesselate(chunk);
        System.out.println(chunk.position());
    }

    private void addFaces(int x, int y, int z, BlockModel model, Direction direction, boolean hide) {
        for(BlockFace face: model.getFacesGroup(direction)) {
            if(!face.isSolid() || hide && face.isMightBeCulled())
                continue;

            final Direction dir = face.getDirection();
            final PerspectiveCamera camera = Main.getInstance().player().camera();
            final int lightLevel = chunkCache.getSkyLightLevel(x + dir.getX(), y + dir.getY(), z + dir.getZ());
            final String text = String.valueOf(lightLevel);

            float X = chunk.position().getBlockX() + x;
            float Y = chunk.position().getBlockY() + y;
            float Z = chunk.position().getBlockZ() + z;

            final Font font = ClientResources.FONTS.get("default").resource();
            final FontRenderOptions options = font.getOptions();
            final Matrix4f textMatrix = options.matrix();

            final Vec2f scaleCopy = options.scale().copy();
            options.scale().set(1f / 16f);

            final Vec2f textBounds = font.getTextBounds(text);
            // X += 0.5F;
            // Y += 0.5F;
            // Z += 0.5F;
            // textMatrix.setTranslate(-textBounds.x * 0.25F, textBounds.y * 0.5F, 0F);

            // final Vec3i normal = dir.getNormal();
            // if(normal.x != 0) {
            //     textMatrix.rotateY(90 * normal.x);
            //     X += 0.51F * normal.x;
            // }else if(normal.y != 0) {
            //     textMatrix.rotateX(-90 * normal.y);
            //     Y += 0.51F * normal.y;
            // }else if(normal.z != 0) {
            //     if(normal.z == 1)
            //         textMatrix.rotateY(180);
            //     Z += 0.51F * normal.z;
            // }

            // font.drawText(camera, text, X, Y, Z);

            options.scale().set(scaleCopy);
            font.getOptions().matrix().identity();
        }
    }

    private void tesselate(LevelChunk chunk) {
        chunkCache.initFor(chunk);
        chunk.forEach((x, y, z) -> {
            // blockstate
            final BlockState blockstate = chunk.getBlockState(x, y, z);
            if(blockstate.isBlockID("air"))
                return;

            // model
            final BlockModel model = getBlockModel(blockstate);
            if(model == null)
                return;

            blockCache.initFor(x, y, z, blockstate, chunkCache);

            // add none faces
            this.addFaces(x, y, z, model, Direction.NONE, false);

            // add east faces
            final BlockState blockstateEast = blockCache.getBlockState(Direction.EAST);
            if(blockstateEast.isBlockID("void", "air")){
                this.addFaces(x, y, z, model, Direction.EAST, false);

            }else{
                final BlockModel modelEast = getBlockModel(blockstateEast);
                this.addFaces(x, y, z, model, Direction.EAST, modelEast.isHidesOthersFace(Direction.EAST));
            }

            // add west faces
            final BlockState blockstateWest = blockCache.getBlockState(Direction.WEST);
            if(blockstateWest.isBlockID("void", "air")){
                this.addFaces(x, y, z, model, Direction.WEST, false);

            }else{
                final BlockModel modelWest = getBlockModel(blockstateWest);
                this.addFaces(x, y, z, model, Direction.WEST, modelWest.isHidesOthersFace(Direction.WEST));
            }

            // add up faces
            final BlockState blockstateUp = blockCache.getBlockState(Direction.UP);
            if(blockstateUp.isBlockID("void", "air")){
                this.addFaces(x, y, z, model, Direction.UP, false);

            }else{
                final BlockModel modelUp = getBlockModel(blockstateUp);
                this.addFaces(x, y, z, model, Direction.UP, modelUp.isHidesOthersFace(Direction.UP));
            }

            // add down faces
            final BlockState blockstateDown = blockCache.getBlockState(Direction.DOWN);
            if(blockstateDown.isBlockID("void", "air")){
                this.addFaces(x, y, z, model, Direction.DOWN, false);

            }else{
                final BlockModel modelDown = getBlockModel(blockstateDown);
                this.addFaces(x, y, z, model, Direction.DOWN, modelDown.isHidesOthersFace(Direction.DOWN));
            }

            // add north faces
            final BlockState blockstateNorth = blockCache.getBlockState(Direction.NORTH);
            if(blockstateNorth.isBlockID("void", "air")){
                this.addFaces(x, y, z, model, Direction.NORTH, false);

            }else{
                final BlockModel modelNorth = getBlockModel(blockstateNorth);
                this.addFaces(x, y, z, model, Direction.NORTH, modelNorth.isHidesOthersFace(Direction.NORTH));
            }

            // add south faces
            final BlockState blockstateSouth = blockCache.getBlockState(Direction.SOUTH);
            if(blockstateSouth.isBlockID("void", "air")){
                this.addFaces(x, y, z, model, Direction.SOUTH, false);

            }else{
                final BlockModel modelSouth = getBlockModel(blockstateSouth);
                this.addFaces(x, y, z, model, Direction.SOUTH, modelSouth.isHidesOthersFace(Direction.SOUTH));
            }
        });
    }

    private static BlockModel getBlockModel(BlockState blockstate) {
        final BlockModelHandle handle = ClientResources.BLOCK_STATE_MODELS.get(blockstate);
        if(handle == null)
            return null;
        return handle.resource();
    }

}
