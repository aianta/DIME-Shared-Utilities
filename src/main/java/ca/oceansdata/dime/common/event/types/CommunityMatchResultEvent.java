package ca.oceansdata.dime.common.event.types;

import ca.oceansdata.dime.common.event.Event;
import ca.oceansdata.dime.common.event.EventType;
import ca.oceansdata.dime.common.event.IllegalEventFormatException;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/** An event that is triggered when another user completes an AMT for this target's CAMG.
 *
 *  "Hey you have a community matching result!"
 *
 */
@DataObject
public class CommunityMatchResultEvent extends Event{

    private static final Logger log = LoggerFactory.getLogger(CommunityMatchResultEvent.class);

    UUID amtId;
    UUID camgId;

    /**Convenience constructor
     * @param amtId Id of the Attribute Match Task the was completed to trigger this event
     * @param camgId Id of the Community Attribute Matching Goal that as a new match
     */
    public CommunityMatchResultEvent(UUID amtId, UUID camgId){
        super(EventType.COMMUNITY_MATCH_RESULT);
        this.amtId = amtId;
        this.camgId = camgId;

        JsonObject data = new JsonObject()
                .put("amtId", amtId.toString())
                .put("camgId", camgId.toString());

        this.setData(data);
    }

    public CommunityMatchResultEvent(){
        super(EventType.COMMUNITY_MATCH_RESULT);
    }

    public CommunityMatchResultEvent(JsonObject data) throws IllegalEventFormatException{
        super(data);

        if(!data.containsKey("data")){
            throw new IllegalEventFormatException(data, "data", "key missing");
        }

        JsonObject inner = data.getJsonObject("data");


        if(!inner.containsKey("amtId")){
            throw new IllegalEventFormatException(data, "data->amtId", "key missing");
        }
        this.amtId = UUID.fromString(inner.getString("amtId"));

        if(!inner.containsKey("camgId")){
            throw new IllegalEventFormatException(data, "data->camgId", "key missing");
        }
        this.camgId = UUID.fromString(inner.getString("camgId"));

    }

    public UUID getAmtId() {
        return amtId;
    }

    public void setAmtId(UUID amtId) {
        this.amtId = amtId;
        data.mergeIn(new JsonObject().put("amtId", amtId.toString()));
    }

    public UUID getCamgId() {
        return camgId;
    }

    public void setCamgId(UUID camgId) {
        this.camgId = camgId;
        data.mergeIn(new JsonObject().put("camgId", camgId.toString()));
    }
}
