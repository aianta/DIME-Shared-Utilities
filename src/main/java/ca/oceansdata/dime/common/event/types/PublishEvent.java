package ca.oceansdata.dime.common.event.types;

import ca.oceansdata.dime.common.event.Event;
import ca.oceansdata.dime.common.event.EventType;
import ca.oceansdata.dime.common.event.IllegalEventFormatException;
import io.vertx.core.json.JsonObject;


import java.util.UUID;

public class PublishEvent extends Event {

    UUID entityId; //The entity that was published
    String destination; //Where it was published to

    public PublishEvent (
            UUID entityId,
            String destination
    ){
        super(EventType.PUBLISH);
        this.entityId = entityId;
        this.destination = destination;

        JsonObject data = new JsonObject()
                .put("entityId", entityId.toString())
                .put("destination", destination);
        this.setData(data);
    }

    public PublishEvent(){super(EventType.PUBLISH);}

    public PublishEvent(JsonObject data) throws IllegalEventFormatException{
        super(data);

        if(!data.containsKey("data")){
            throw new IllegalEventFormatException(data, "data", "key missing");
        }
        data = data.getJsonObject("data");

        if(!data.containsKey("entityId")){
            throw new IllegalEventFormatException(data, "entityId", "key missing");
        }
        this.entityId = UUID.fromString(data.getString("entityId"));

        if(!data.containsKey("destination")){
            throw new IllegalEventFormatException(data, "destination", "key missing");
        }
        this.destination = data.getString("destination");
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
