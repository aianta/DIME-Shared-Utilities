package ca.oceansdata.dime.common.event.types;

import ca.oceansdata.dime.common.event.Event;
import ca.oceansdata.dime.common.event.EventType;
import ca.oceansdata.dime.common.event.IllegalEventFormatException;
import io.vertx.core.json.JsonObject;

import java.util.Optional;
import java.util.UUID;

public class UpdateMetadataFieldEvent extends Event {

    UUID entityId;
    UUID fieldId;
    UUID keyId;
    UUID oldValueId;
    UUID newValueId;

    public UpdateMetadataFieldEvent(
            UUID entityId,
            UUID fieldId,
            UUID keyId,
            UUID oldValueId,
            UUID newValueId
    ){
        super(EventType.UPDATE_METADATA_FIELD);
        this.entityId = entityId;
        this.fieldId = fieldId;
        this.keyId = keyId;
        this.oldValueId = oldValueId;
        this.newValueId = newValueId;

        JsonObject data = new JsonObject()
                .put("entityId", entityId.toString())
                .put("fieldId", fieldId.toString())
                .put("keyId", keyId.toString())
                .put("newValueId", newValueId.toString());
        if(oldValueId != null){
            data.put("oldValueId", oldValueId.toString());
        }
        this.setData(data);
    }

    public UpdateMetadataFieldEvent(){
        super(EventType.UPDATE_METADATA_FIELD);
    }

    public UpdateMetadataFieldEvent(JsonObject data) throws IllegalEventFormatException {
        super(data);

        Optional<JsonObject> eventData = Optional.of(data.getJsonObject("data"));
        eventData.flatMap(
                inner->{

                    //TODO - this was silly in retrospect, if a useful learning exercise on Optionals. Should refactor as it Null Pointers in UUID.fromString() if inner is missing any of the keys.

                    Optional<UUID> entityId = Optional.of(UUID.fromString(inner.getString("entityId")));
                    Optional<UUID> fieldId = Optional.of(UUID.fromString(inner.getString("fieldId")));
                    Optional<UUID> keyId = Optional.of(UUID.fromString(inner.getString("keyId")));
                    Optional<UUID> newValueId = Optional.of(UUID.fromString(inner.getString("newValueId")));


                    this.oldValueId = inner.containsKey("oldValueId")?UUID.fromString(inner.getString("oldValueId")):null;


                    entityId.flatMap(eId->fieldId.flatMap(fId->keyId.flatMap(kId->newValueId.flatMap(nvId->{
                        this.entityId = eId;
                        this.keyId = kId;
                        this.fieldId = fId;
                        this.newValueId = nvId;
                        return Optional.empty();
                    }))));
                    return Optional.empty();
                }
        );
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
        data.mergeIn(new JsonObject().put("entityId", entityId.toString()));
    }

    public UUID getFieldId() {
        return fieldId;
    }

    public void setFieldId(UUID fieldId) {
        this.fieldId = fieldId;
        data.mergeIn(new JsonObject().put("fieldId", fieldId.toString()));
    }

    public UUID getKeyId() {
        return keyId;
    }

    public void setKeyId(UUID keyId) {
        this.keyId = keyId;
        data.mergeIn(new JsonObject().put("keyId", keyId.toString()));
    }

    public UUID getOldValueId() {
        return oldValueId;
    }

    public void setOldValueId(UUID oldValueId) {
        this.oldValueId = oldValueId;
        if(oldValueId != null){
            data.mergeIn(new JsonObject().put("oldValueId", oldValueId.toString()));
        }
    }

    public UUID getNewValueId() {
        return newValueId;
    }

    public void setNewValueId(UUID newValueId) {
        this.newValueId = newValueId;
        data.mergeIn(new JsonObject().put("newValueId", newValueId.toString()));
    }
}
