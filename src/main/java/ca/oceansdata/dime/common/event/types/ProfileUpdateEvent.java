package ca.oceansdata.dime.common.event.types;

import ca.oceansdata.dime.common.event.Event;
import ca.oceansdata.dime.common.event.EventType;
import ca.oceansdata.dime.common.event.IllegalEventFormatException;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class ProfileUpdateEvent extends Event {


    JsonObject oldValue;
    JsonObject newValue;

    /**Convenience constructor
     * @param oldValue the old value of the field
     * @param newValue the updated value of the field
     */
    public ProfileUpdateEvent( JsonObject oldValue, JsonObject newValue){
        super(EventType.PROFILE_FIELD_UPDATE);

        this.oldValue = oldValue;
        this.newValue = newValue;

        JsonObject data = new JsonObject()
                .put("oldValue", oldValue)
                .put("newValue", newValue);
        this.setData(data);
    }

    public ProfileUpdateEvent(){
        super(EventType.PROFILE_FIELD_UPDATE);
    }

    public ProfileUpdateEvent(JsonObject data) throws IllegalEventFormatException {
        super(data);

        if(!data.containsKey("data")){
            throw new IllegalEventFormatException(data, "data", "key missing");
        }

        JsonObject inner = data.getJsonObject("data");

        if(!inner.containsKey("fieldName")){
            throw new IllegalEventFormatException(data, "data->fieldName", "key missing");
        }

        if(!inner.containsKey("oldValue")){
            throw new IllegalEventFormatException(data, "data->oldValue", "key missing");
        }
        this.oldValue = inner.getJsonObject("oldValue");

        if(!inner.containsKey("newValue")){
            throw new IllegalEventFormatException(data, "data->newValue", "key missing");
        }
        this.newValue = inner.getJsonObject("newValue");
    }


    public JsonObject getOldValue() {
        return oldValue;
    }

    public void setOldValue(JsonObject oldValue) {
        this.oldValue = oldValue;
        data.mergeIn(new JsonObject().put("oldValue", oldValue));
    }

    public JsonObject getNewValue() {
        return newValue;
    }

    public void setNewValue(JsonObject newValue) {
        this.newValue = newValue;
        data.mergeIn(new JsonObject().put("newValue", newValue));
    }
}
