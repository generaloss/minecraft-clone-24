package generaloss.mc24.client.level.renderer.block;

import generaloss.mc24.server.common.Directory;

import java.util.HashMap;
import java.util.Map;

public class BlockModelRegistry {

    private static final Map<Integer, BlockModel> MODELS = new HashMap<>();

    public static BlockModel getModel(int ID) {
        return MODELS.get(ID);
    }

    public static void registerModel(int ID, BlockModel model) {
        MODELS.put(ID, model);
    }


    static {
        registerModel(1, new BlockModel(false)
            // east
            .addFace(new BlockFace("grass_block_side", new BlockVertex[] {
                new BlockVertex(1F, 1, 0,  0, 0,  1, 1, 1, 1F),
                new BlockVertex(1F, 0, 0,  0, 1,  1, 1, 1, 1F),
                new BlockVertex(1F, 0, 1,  1, 1,  1, 1, 1, 1F),
                new BlockVertex(1F, 1, 1,  1, 0,  1, 1, 1, 1F)
            }))
            // west
            .addFace(new BlockFace("grass_block_side", new BlockVertex[] {
                new BlockVertex(0F, 1, 1,  0, 0,  1, 1, 1, 1F),
                new BlockVertex(0F, 0, 1,  0, 1,  1, 1, 1, 1F),
                new BlockVertex(0F, 0, 0,  1, 1,  1, 1, 1, 1F),
                new BlockVertex(0F, 1, 0,  1, 0,  1, 1, 1, 1F)
            }))
            // east overlay
            .addFace(new BlockFace("grass_block_side_overlay", new BlockVertex[] {
                new BlockVertex(1F, 1, 0,  0, 0,  0.55F, 0.75F, 0.3F, 1F),
                new BlockVertex(1F, 0, 0,  0, 1,  0.55F, 0.75F, 0.3F, 1F),
                new BlockVertex(1F, 0, 1,  1, 1,  0.55F, 0.75F, 0.3F, 1F),
                new BlockVertex(1F, 1, 1,  1, 0,  0.55F, 0.75F, 0.3F, 1F)
            }))
            // west overlay
            .addFace(new BlockFace("grass_block_side_overlay", new BlockVertex[] {
                new BlockVertex(0F, 1, 1,  0, 0,  0.55F, 0.75F, 0.3F, 1F),
                new BlockVertex(0F, 0, 1,  0, 1,  0.55F, 0.75F, 0.3F, 1F),
                new BlockVertex(0F, 0, 0,  1, 1,  0.55F, 0.75F, 0.3F, 1F),
                new BlockVertex(0F, 1, 0,  1, 0,  0.55F, 0.75F, 0.3F, 1F)
            }))
            // up
            .addFace(new BlockFace("grass_block_top", new BlockVertex[] {
                new BlockVertex(0, 1F, 1,  0, 0,  0.55F, 0.75F, 0.3F, 1F),
                new BlockVertex(0, 1F, 0,  0, 1,  0.55F, 0.75F, 0.3F, 1F),
                new BlockVertex(1, 1F, 0,  1, 1,  0.55F, 0.75F, 0.3F, 1F),
                new BlockVertex(1, 1F, 1,  1, 0,  0.55F, 0.75F, 0.3F, 1F)
            }))
            // down
            .addFace(new BlockFace("dirt", new BlockVertex[] {
                new BlockVertex(0, 0F, 0,  0, 0,  1, 1, 1, 1F),
                new BlockVertex(0, 0F, 1,  0, 1,  1, 1, 1, 1F),
                new BlockVertex(1, 0F, 1,  1, 1,  1, 1, 1, 1F),
                new BlockVertex(1, 0F, 0,  1, 0,  1, 1, 1, 1F)
            }))
            // north
            .addFace(new BlockFace("grass_block_side", new BlockVertex[] {
                new BlockVertex(1, 1, 1F,  0, 0,  1, 1, 1, 1F),
                new BlockVertex(1, 0, 1F,  0, 1,  1, 1, 1, 1F),
                new BlockVertex(0, 0, 1F,  1, 1,  1, 1, 1, 1F),
                new BlockVertex(0, 1, 1F,  1, 0,  1, 1, 1, 1F)
            }))
            // south
            .addFace(new BlockFace("grass_block_side", new BlockVertex[] {
                new BlockVertex(0, 1, 0F,  0, 0,  1, 1, 1, 1F),
                new BlockVertex(0, 0, 0F,  0, 1,  1, 1, 1, 1F),
                new BlockVertex(1, 0, 0F,  1, 1,  1, 1, 1, 1F),
                new BlockVertex(1, 1, 0F,  1, 0,  1, 1, 1, 1F)
            }))
            // north overlay
            .addFace(new BlockFace("grass_block_side_overlay", new BlockVertex[] {
                new BlockVertex(1, 1, 1F,  0, 0,  0.55F, 0.75F, 0.3F, 1F),
                new BlockVertex(1, 0, 1F,  0, 1,  0.55F, 0.75F, 0.3F, 1F),
                new BlockVertex(0, 0, 1F,  1, 1,  0.55F, 0.75F, 0.3F, 1F),
                new BlockVertex(0, 1, 1F,  1, 0,  0.55F, 0.75F, 0.3F, 1F)
            }))
            // south overlay
            .addFace(new BlockFace("grass_block_side_overlay", new BlockVertex[] {
                new BlockVertex(0, 1, 0F,  0, 0,  0.55F, 0.75F, 0.3F, 1F),
                new BlockVertex(0, 0, 0F,  0, 1,  0.55F, 0.75F, 0.3F, 1F),
                new BlockVertex(1, 0, 0F,  1, 1,  0.55F, 0.75F, 0.3F, 1F),
                new BlockVertex(1, 1, 0F,  1, 0,  0.55F, 0.75F, 0.3F, 1F)
            }))
        );

        registerModel(2, new BlockModel(true)
            // east
            .addFace(new BlockFace(Directory.NONE, Directory.NONE, "oak_leaves", new BlockVertex[] {
                new BlockVertex(1F, 1, 0,  0, 0,  0.4F, 0.7F, 0.3F, 1F),
                new BlockVertex(1F, 0, 0,  0, 1,  0.4F, 0.7F, 0.3F, 1F),
                new BlockVertex(1F, 0, 1,  1, 1,  0.4F, 0.7F, 0.3F, 1F),
                new BlockVertex(1F, 1, 1,  1, 0,  0.4F, 0.7F, 0.3F, 1F)
            }))
            // west
            .addFace(new BlockFace(Directory.NONE, Directory.NONE, "oak_leaves", new BlockVertex[] {
                new BlockVertex(0F, 1, 1,  0, 0,  0.4F, 0.7F, 0.3F, 1F),
                new BlockVertex(0F, 0, 1,  0, 1,  0.4F, 0.7F, 0.3F, 1F),
                new BlockVertex(0F, 0, 0,  1, 1,  0.4F, 0.7F, 0.3F, 1F),
                new BlockVertex(0F, 1, 0,  1, 0,  0.4F, 0.7F, 0.3F, 1F)
            }))
            // up
            .addFace(new BlockFace(Directory.NONE, Directory.NONE, "oak_leaves", new BlockVertex[] {
                new BlockVertex(0, 1F, 1,  0, 0,  0.4F, 0.7F, 0.3F, 1F),
                new BlockVertex(0, 1F, 0,  0, 1,  0.4F, 0.7F, 0.3F, 1F),
                new BlockVertex(1, 1F, 0,  1, 1,  0.4F, 0.7F, 0.3F, 1F),
                new BlockVertex(1, 1F, 1,  1, 0,  0.4F, 0.7F, 0.3F, 1F)
            }))
            // down
            .addFace(new BlockFace(Directory.NONE, Directory.NONE, "oak_leaves", new BlockVertex[] {
                new BlockVertex(0, 0F, 0,  0, 0,  0.4F, 0.7F, 0.3F, 1F),
                new BlockVertex(0, 0F, 1,  0, 1,  0.4F, 0.7F, 0.3F, 1F),
                new BlockVertex(1, 0F, 1,  1, 1,  0.4F, 0.7F, 0.3F, 1F),
                new BlockVertex(1, 0F, 0,  1, 0,  0.4F, 0.7F, 0.3F, 1F)
            }))
            // north
            .addFace(new BlockFace(Directory.NONE, Directory.NONE, "oak_leaves", new BlockVertex[] {
                new BlockVertex(1, 1, 1F,  0, 0,  0.4F, 0.7F, 0.3F, 1F),
                new BlockVertex(1, 0, 1F,  0, 1,  0.4F, 0.7F, 0.3F, 1F),
                new BlockVertex(0, 0, 1F,  1, 1,  0.4F, 0.7F, 0.3F, 1F),
                new BlockVertex(0, 1, 1F,  1, 0,  0.4F, 0.7F, 0.3F, 1F)
            }))
            // south
            .addFace(new BlockFace(Directory.NONE, Directory.NONE, "oak_leaves", new BlockVertex[] {
                new BlockVertex(0, 1, 0F,  0, 0,  0.4F, 0.7F, 0.3F, 1F),
                new BlockVertex(0, 0, 0F,  0, 1,  0.4F, 0.7F, 0.3F, 1F),
                new BlockVertex(1, 0, 0F,  1, 1,  0.4F, 0.7F, 0.3F, 1F),
                new BlockVertex(1, 1, 0F,  1, 0,  0.4F, 0.7F, 0.3F, 1F)
            }))
        );

        registerModel(3, new BlockModel(false)
            // east
            .addFace(new BlockFace("stone", new BlockVertex[] {
                new BlockVertex(1F, 1, 0,  0, 0,  1, 1, 1, 1F),
                new BlockVertex(1F, 0, 0,  0, 1,  1, 1, 1, 1F),
                new BlockVertex(1F, 0, 1,  1, 1,  1, 1, 1, 1F),
                new BlockVertex(1F, 1, 1,  1, 0,  1, 1, 1, 1F)
            }))
            // west
            .addFace(new BlockFace("stone", new BlockVertex[] {
                new BlockVertex(0F, 1, 1,  0, 0,  1, 1, 1, 1F),
                new BlockVertex(0F, 0, 1,  0, 1,  1, 1, 1, 1F),
                new BlockVertex(0F, 0, 0,  1, 1,  1, 1, 1, 1F),
                new BlockVertex(0F, 1, 0,  1, 0,  1, 1, 1, 1F)
            }))
            // up
            .addFace(new BlockFace("stone", new BlockVertex[] {
                new BlockVertex(0, 1F, 1,  0, 0,  1, 1, 1, 1F),
                new BlockVertex(0, 1F, 0,  0, 1,  1, 1, 1, 1F),
                new BlockVertex(1, 1F, 0,  1, 1,  1, 1, 1, 1F),
                new BlockVertex(1, 1F, 1,  1, 0,  1, 1, 1, 1F)
            }))
            // down
            .addFace(new BlockFace("stone", new BlockVertex[] {
                new BlockVertex(0, 0F, 0,  0, 0,  1, 1, 1, 1F),
                new BlockVertex(0, 0F, 1,  0, 1,  1, 1, 1, 1F),
                new BlockVertex(1, 0F, 1,  1, 1,  1, 1, 1, 1F),
                new BlockVertex(1, 0F, 0,  1, 0,  1, 1, 1, 1F)
            }))
            // north
            .addFace(new BlockFace("stone", new BlockVertex[] {
                new BlockVertex(1, 1, 1F,  0, 0,  1, 1, 1, 1F),
                new BlockVertex(1, 0, 1F,  0, 1,  1, 1, 1, 1F),
                new BlockVertex(0, 0, 1F,  1, 1,  1, 1, 1, 1F),
                new BlockVertex(0, 1, 1F,  1, 0,  1, 1, 1, 1F)
            }))
            // south
            .addFace(new BlockFace("stone", new BlockVertex[] {
                new BlockVertex(0, 1, 0F,  0, 0,  1, 1, 1, 1F),
                new BlockVertex(0, 0, 0F,  0, 1,  1, 1, 1, 1F),
                new BlockVertex(1, 0, 0F,  1, 1,  1, 1, 1, 1F),
                new BlockVertex(1, 1, 0F,  1, 0,  1, 1, 1, 1F)
            }))
        );

    }

}
