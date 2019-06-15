package ca.oceansdata.dime.common.event;

import ca.oceansdata.dime.common.DimeUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    private EventBus eb;

    public EventService(EventBus eb){
        this.eb = eb;
    }

    public void emit(EventType type, String target, JsonObject data){

        JsonObject emission = new JsonObject()
                .put("type", type.getText())
                .put("target", target)
                .put("data", data);

        eb.publish("dime.events", emission);

    }

    public void emit(Event e){
        try{
            if(e.getEventTarget() == null){
                throw new NullPointerException("eventTarget cannot be null! Cannot emit event!");
            }else{
                if(e.getType() == null){
                    throw new Exception("Event type cannot be null! Cannot emit event!");
                }else{
                    eb.publish("dime.events", e);
                }
            }
        }catch (Exception err){
            log.error("Error emitting event!");
            log.error(err.getMessage());
            err.printStackTrace();
        }


    }

}
