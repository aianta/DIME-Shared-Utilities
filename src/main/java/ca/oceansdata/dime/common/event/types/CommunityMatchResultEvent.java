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

    /**Convenience constructor
     * @param amtId Id if the Attribute Match Task the was completed to trigger this event
     */
    public CommunityMatchResultEvent(UUID amtId){
        super(EventType.COMMUNITY_MATCH_RESULT);
        this.amtId = amtId;

        JsonObject data = new JsonObject()
                .put("amtId", amtId.toString());

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

    }

    public UUID getAmtId() {
        return amtId;
    }

    public void setAmtId(UUID taskId) {
        this.amtId = taskId;
        data.mergeIn(new JsonObject().put("amtId", taskId.toString()));
    }
}
