package generaloss.mc24.server.block;

import generaloss.mc24.server.registry.Identifiable;
import generaloss.mc24.server.registry.Registries;
import org.json.JSONObject;

import java.util.*;

public class Block implements Identifiable<String> {

    private String ID;
    private final BlockPropertiesHolder properties;
    private BlockStateContainer statesHolder;

    public Block() {
        this.properties = new BlockPropertiesHolder();
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
        return statesHolder;
    }

    public BlockState getDefaultState() {
        return statesHolder.getDefaultState();
    }

    public Block buildStates(Map<String, StateProperty<?>> stateProperties, Registries registries) {
        statesHolder = new BlockStateContainer(this, stateProperties, registries);
        return this;
    }


    public void loadFromJSON(String jsonString, Registries registries) {
        final JSONObject jsonObject = new JSONObject(jsonString);
        this.setID(jsonObject.getString("block_ID"));

        if(jsonObject.has("properties")){
            final JSONObject jsonProperties = jsonObject.getJSONObject("properties");
            for(String propertyName: jsonProperties.keySet()){
                final BlockProperty property = BlockProperty.byName(propertyName);
                if(property == null){
                    System.err.println("[WARN]: Block property '" + propertyName + "' does not exist ('" + ID + "' block).");
                    continue;
                }
                final Object object = jsonProperties.get(propertyName);
                properties.set(propertyName, property.loadFromJSON(object));
            }
        }

        this.buildStates(Map.of(), registries);
    }

}
