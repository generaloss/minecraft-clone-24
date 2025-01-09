package generaloss.mc24.server.block;

import generaloss.mc24.server.Identifiable;
import generaloss.mc24.server.registry.ServerRegistries;
import jpize.util.res.Resource;
import org.json.JSONArray;
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


    public void loadFromJSON(Resource resource) {
        final JSONObject jsonObject = new JSONObject(resource.readString());
        this.setID(resource.simpleName());

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

        final List<StateProperty<?>> stateProperties = new ArrayList<>();
        if(jsonObject.has("variant_properties")){
            final JSONArray jsonStateProperties = jsonObject.getJSONArray("variant_properties");
            for(int i = 0; i < jsonStateProperties.length(); i++){
                final String statePropertyName = jsonStateProperties.getString(i);
                final StateProperty<?> stateProperty = ServerRegistries.STATE_PROPERTY.get(statePropertyName);
                stateProperties.add(stateProperty);
            }
        }

        // System.out.println("Load block '" + this.getID() + "'");

        statesContainer.create(stateProperties.toArray(new StateProperty[0]));
    }

}
