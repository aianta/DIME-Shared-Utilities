package ca.oceansdata.dime.common.event.types;

import ca.oceansdata.dime.common.event.Event;
import ca.oceansdata.dime.common.event.EventType;
import ca.oceansdata.dime.common.event.IllegalEventFormatException;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class ProfileFieldUpdateEvent extends Event {

    String fieldName;
    String oldValue;
    String newValue;

    /**Convenience constructor
     * @param fieldName Name of the updated field
     * @param oldValue the old value of the field
     * @param newValue the updated value of the field
     */
    public ProfileFieldUpdateEvent(String fieldName, String oldValue, String newValue){
        super(EventType.PROFILE_FIELD_UPDATE);
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;

        JsonObject data = new JsonObject()
                .put("fieldName", fieldName)
                .put("oldValue", oldValue)
                .put("newValue", newValue);
        this.setData(data);
    }

    public ProfileFieldUpdateEvent(){
        super(EventType.PROFILE_FIELD_UPDATE);
    }

    public ProfileFieldUpdateEvent(JsonObject data) throws IllegalEventFormatException {
        super(data);

        if(!data.containsKey("data")){
            throw new IllegalEventFormatException(data, "data", "key missing");
        }

        JsonObject inner = data.getJsonObject("data");

        if(!inner.containsKey("fieldName")){
            throw new IllegalEventFormatException(data, "data->fieldName", "key missing");
        }
        this.fieldName = inner.getString("fieldName");

        if(!inner.containsKey("oldValue")){
            throw new IllegalEventFormatException(data, "data->oldValue", "key missing");
        }
        this.oldValue = inner.getString("oldValue");

        if(!inner.containsKey("newValue")){
            throw new IllegalEventFormatException(data, "data->newValue", "key missing");
        }
        this.newValue = inner.getString("newValue");
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
        data.mergeIn(new JsonObject().put("fieldName", fieldName));
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
        data.mergeIn(new JsonObject().put("oldValue", oldValue));
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
        data.mergeIn(new JsonObject().put("newValue", newValue));
    }
}
