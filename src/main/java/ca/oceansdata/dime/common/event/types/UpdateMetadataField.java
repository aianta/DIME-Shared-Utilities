package ca.oceansdata.dime.common.event.types;

import java.util.UUID;

public class UpdateMetadataField {

    UUID entityId;
    UUID fieldId;
    UUID keyId;
    UUID oldValue;
    UUID newValue;

    public UpdateMetadataField(
            UUID entityId,
            UUID fieldId,
            UUID keyId,
            UUID oldValue,
            UUID newValue
    ){
        this.entityId = entityId;
        this.fieldId = fieldId;
        this.keyId = keyId;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

}
