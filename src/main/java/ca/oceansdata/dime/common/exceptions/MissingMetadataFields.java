package ca.oceansdata.dime.common.exceptions;

import io.vertx.core.json.JsonArray;

public class MissingMetadataFields extends Exception {

    private JsonArray missingFields;

    public MissingMetadataFields(JsonArray missingFields){
        this.missingFields = missingFields;
    }

    public JsonArray getMissingFields(){
        return missingFields;
    }

    public String getMessage(){
        return "Missing " + missingFields.size() + " metadata fields required to publish to figshare.";
    }

}
