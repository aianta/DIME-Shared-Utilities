package ca.oceansdata.dime.common.event.types;

import ca.oceansdata.dime.common.event.Event;
import ca.oceansdata.dime.common.event.EventType;
import ca.oceansdata.dime.common.event.IllegalEventFormatException;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

@DataObject
public class EditSchemaDescriptionEvent extends Event {

    UUID schemaId;
    String oldDescription;
    String newDescription;

    /**Convenience constructor
     * @param schemaId Id of the schema whose description has been edited
     * @param oldDescription old description
     * @param newDescription edited description
     */
    public EditSchemaDescriptionEvent(UUID schemaId, String oldDescription, String newDescription){
        super(EventType.EDIT_SCHEMA_DESCRIPTION);
        this.schemaId = schemaId;
        this.oldDescription = oldDescription;
        this.newDescription = newDescription;

        JsonObject data = new JsonObject()
                .put("schemaId", schemaId.toString())
                .put("oldDescription", oldDescription)
                .put("newDescription", newDescription);
        this.setData(data);
    }

    public EditSchemaDescriptionEvent(){
        super(EventType.EDIT_SCHEMA_DESCRIPTION);
    }

    public EditSchemaDescriptionEvent(JsonObject data) throws IllegalEventFormatException{
        super(data);

        if(!data.containsKey("data")){
            throw new IllegalEventFormatException(data, "data", "key missing");
        }

        JsonObject inner = data.getJsonObject("data");

        if(!inner.containsKey("schemaId")){
            throw new IllegalEventFormatException(data, "data->schemaId", "key missing");
        }
        this.schemaId = UUID.fromString(inner.getString("schemaId"));

        if(!inner.containsKey("oldDescription")){
            throw new IllegalEventFormatException(data, "data->oldDescription", "key missing");
        }
        this.oldDescription = inner.getString("oldDescription");

        if(!inner.containsKey("newDescription")){
            throw new IllegalEventFormatException(data, "data->newDescription",  "key missing");
        }
        this.newDescription = inner.getString("newDescription");
    }

    public UUID getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(UUID schemaId) {
        this.schemaId = schemaId;
        data.mergeIn(new JsonObject().put("schemaId", schemaId.toString()));
    }

    public String getOldDescription() {
        return oldDescription;
    }

    public void setOldDescription(String oldDescription) {
        this.oldDescription = oldDescription;
        data.mergeIn(new JsonObject().put("oldDescription", oldDescription));
    }

    public String getNewDescription() {
        return newDescription;
    }

    public void setNewDescription(String newDescription) {
        this.newDescription = newDescription;
        data.mergeIn(new JsonObject().put("newDescription", newDescription));
    }
}
