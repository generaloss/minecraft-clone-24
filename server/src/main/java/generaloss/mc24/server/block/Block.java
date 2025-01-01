package generaloss.mc24.server.block;

import generaloss.mc24.server.Identifiable;
import generaloss.mc24.server.registry.Registries;
import org.json.JSONObject;

import java.util.*;

public class Block implements Identifiable<String> {

    public static final Block AIR = new Block().setID("air");
    public static final Block VOID = new Block().setID("void");
    static{
        AIR.properties().set("opacity", 0);
    }

    private String ID;
    private final BlockPropertiesHolder properties;
    private final BlockStateContainer statesContainer;

    public Block() {
        this.properties = new BlockPropertiesHolder();
        this.statesContainer = new BlockStateContainer(this);
    }

    @Override
    public String getID() {
        return ID;
    }

    public Block setID(String ID) {
        this.ID = ID;
        return this;
    }


    public BlockPropertiesHolder properties() {
        return properties;
    }


    public BlockStateContainer states() {
        return statesContainer;
    }

    public BlockState getDefaultState() {
        return statesContainer.getDefaultState();
    }

    public Block createBlockstates(Map<String, StateProperty<?>> stateProperties, Registries registries) {
        statesContainer.create(stateProperties, registries);
        return this;
    }


    public void loadFromJSON(String jsonString) {
        final JSONObject jsonObject = new JSONObject(jsonString);
        this.setID(jsonObject.getString("block_ID"));

        if(jsonObject.has("properties")){
            final JSONObject jsonProperties = jsonObject.getJSONObject("properties");
            for(String propertyName: jsonProperties.keySet()){
                final BlockProperty property = BlockProperty.get(propertyName);
                if(property == null){
                    System.err.println("[WARN]: Block property '" + propertyName + "' does not exist ('" + ID + "' block).");
                    continue;
                }
                final Object object = jsonProperties.get(propertyName);
                properties.set(propertyName, property.loadFromJSON(object));
            }
        }
    }

}
