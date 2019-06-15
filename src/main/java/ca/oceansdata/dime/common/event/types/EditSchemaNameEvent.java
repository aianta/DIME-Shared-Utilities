package ca.oceansdata.dime.common.event.types;

import ca.oceansdata.dime.common.event.Event;
import ca.oceansdata.dime.common.event.EventType;
import ca.oceansdata.dime.common.event.IllegalEventFormatException;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

@DataObject
public class EditSchemaNameEvent extends Event {

    UUID schemaId;
    String oldName;
    String newName;

    /**Convenience constructor
     * @param schemaId Id of the renamed schema
     * @param oldName old schema name
     * @param newName edited schema name
     */
    public EditSchemaNameEvent(UUID schemaId, String oldName, String newName){
        super(EventType.EDIT_SCHEMA_NAME);
        this.schemaId = schemaId;
        this.oldName = oldName;
        this.newName = newName;

        JsonObject data = new JsonObject()
                .put("schemaId", schemaId.toString())
                .put("oldName", oldName)
                .put("newName", newName);
        this.setData(data);
    }

    public EditSchemaNameEvent(){
        super(EventType.EDIT_SCHEMA_NAME);
    }

    public EditSchemaNameEvent(JsonObject data) throws IllegalEventFormatException{
        super(data);

        if(!data.containsKey("data")){
            throw new IllegalEventFormatException(data, "data", "key missing");
        }

        JsonObject inner = data.getJsonObject("data");

        if(!inner.containsKey("schemaId")){
            throw new IllegalEventFormatException(data, "data->schemaId", "key missing");
        }
        this.schemaId = UUID.fromString(inner.getString("schemaId"));

        if(!inner.containsKey("oldName")){
            throw new IllegalEventFormatException(data, "data->oldName");
        }
        this.oldName = inner.getString("oldName");

        if(!inner.containsKey("newName")){
            throw new IllegalEventFormatException(data, "data->newName");
        }
        this.newName = inner.getString("newName");
    }

    public UUID getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(UUID schemaId) {
        this.schemaId = schemaId;
        data.mergeIn(new JsonObject().put("schemaId", schemaId.toString()));
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
        data.mergeIn(new JsonObject().put("oldName", oldName));
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
        data.mergeIn(new JsonObject().put("newName", newName));
    }
}
