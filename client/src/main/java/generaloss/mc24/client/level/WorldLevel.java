package generaloss.mc24.client.level;

import generaloss.mc24.client.Main;
import generaloss.mc24.client.chunk.ChunkTesselator;
import generaloss.mc24.server.chunk.Chunk;
import generaloss.mc24.server.chunk.ChunkPos;
import generaloss.mc24.server.registry.Registries;
import generaloss.mc24.server.world.BlockLightEngine;
import generaloss.mc24.server.world.World;
import jpize.util.Disposable;
import jpize.util.camera.PerspectiveCamera;
import jpize.util.math.FastNoise;
import jpize.util.math.Maths;

public class WorldLevel extends World<LevelChunk> implements Disposable {

    private final Main context;
    private final ChunkTesselator tesselator;
    private final LevelRenderer renderer;
    private final BlockLightEngine<LevelChunk> blockLightEngine;

    public WorldLevel(Main context) {
        this.context = context;
        this.tesselator = new ChunkTesselator(context, this);
        this.renderer = new LevelRenderer(context, this);
        this.blockLightEngine = new BlockLightEngine<>(this);
    }

    public ChunkTesselator tesselator() {
        return tesselator;
    }


    FastNoise noise = new FastNoise(345).setFrequency(1 / 64F);

    private void load(int chunkX, int chunkY, int chunkZ) {
        final ChunkPos position = new ChunkPos(chunkX, chunkY, chunkZ);
        final LevelChunk chunk = new LevelChunk(this, position, context.registries());

        final Registries registries = context.registries();
        chunk.forEach((x, y, z) -> {
            final int X = chunkX * Chunk.SIZE + x;
            final int Y = chunkY * Chunk.SIZE + y;
            final int Z = chunkZ * Chunk.SIZE + z;
            if(Y > Chunk.SIZE * 2 - 10){
                if(Maths.randomBoolean(0.002F)){
                    chunk.setBlockState(x, y, z, registries.getBlock("torch").getDefaultState());
                    chunk.setBlockLightLevel(x, y, z, BlockLightEngine.MAX_LEVEL);
                    blockLightEngine.increase(chunk, x, y, z, BlockLightEngine.MAX_LEVEL);
                }
                return;
            }

            if(noise.get(X, Y, Z) > 0F){
                chunk.setBlockState(x, y, z, registries.getBlock("stone").getDefaultState());
            }else{
                // chunk.setBlockLightLevel(x, y, z, BlockLightEngine.MAX_LEVEL);
                if(Maths.randomBoolean(0.002F)){
                    chunk.setBlockState(x, y, z, registries.getBlock("torch").getDefaultState());
                    chunk.setBlockLightLevel(x, y, z, BlockLightEngine.MAX_LEVEL);
                    blockLightEngine.increase(chunk, x, y, z, BlockLightEngine.MAX_LEVEL);
                }
            }
        });
        chunk.forEach((x, y, z) -> {
            if(chunk.getBlockState(x, y, z).isBlockID("stone") && (chunk.getBlockState(x, y + 1, z) == null || chunk.getBlockState(x, y + 1, z).isBlockID("air")))
                chunk.setBlockState(x, y, z, registries.getBlock("grass_block").getDefaultState());
        });

        super.putChunk(chunk);
        tesselator.tesselate(chunk);
    }

    public void loadChunks() {
        System.out.println("Loading chunks...");
        for(int y = -4; y < 4; y++){
            this.load(0, y, 0);
            for(int r = 0; r < 6; r++){
                for(int i = -r; i <= r; i++)
                    this.load(i, y, r);
                for(int i = -r + 1; i <= r; i++)
                    this.load(r, y, i);
                for(int i = -r + 1; i <= r; i++)
                    this.load(i, y, -r);
                for(int i = -r; i <= r - 1; i++)
                    this.load(-r, y, i);
            }
        }
        System.out.println("Chunks loaded.");
    }


    public void update() {
        blockLightEngine.update();
        tesselator.update();
    }

    public void render(PerspectiveCamera camera) {
        renderer.render(camera);
    }


    @Override
    public void dispose() {
        for(LevelChunk chunk: this.getChunks())
            chunk.freeMesh();
        tesselator.dispose();
    }

}
