package generaloss.mc24.client.level.renderer.block;

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

}
