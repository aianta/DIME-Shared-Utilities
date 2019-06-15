package ca.oceansdata.dime.common.event.types;

import ca.oceansdata.dime.common.event.Event;
import ca.oceansdata.dime.common.event.EventType;
import ca.oceansdata.dime.common.event.IllegalEventFormatException;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class LogoutEvent extends Event {

    public LogoutEvent(){
        super(EventType.LOGOUT);
    }

    public LogoutEvent(JsonObject data) throws IllegalEventFormatException {
        super(data);
    }

}
