package ca.oceansdata.dime.common.event;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.EventBus;

public class EventService {

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

}
