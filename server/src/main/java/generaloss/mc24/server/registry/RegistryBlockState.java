package generaloss.mc24.server.registry;

import generaloss.mc24.server.IntRegistry;
import generaloss.mc24.server.block.BlockState;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RegistryBlockState extends IntRegistry<BlockState> {

    private final List<Consumer<BlockState>> registerCallbacks;

    public RegistryBlockState() {
        this.registerCallbacks = new ArrayList<>();
    }

    @Override
    public BlockState register(BlockState blockstate) {
        super.register(blockstate);

        // System.out.println("Load blockstate '" + blockstate.getBlockID() + "' variant " + new StringList(blockstate.getStatePropertiesValues()));
        for(Consumer<BlockState> registerCallback: registerCallbacks)
            registerCallback.accept(blockstate);

        return blockstate;
    }

    public void addRegisterCallback(Consumer<BlockState> callback) {
        registerCallbacks.add(callback);
    }

}
