package generaloss.mc24.server.block;

public class Block {

    private final String identifier;
    private BlockModel model;

    public Block(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public BlockModel model() {
        return model;
    }

    public void setModel(BlockModel model) {
        this.model = model;
    }

}
