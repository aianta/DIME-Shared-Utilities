package ca.oceansdata.dime.common.event.types;


import ca.oceansdata.dime.common.event.EventType;
import ca.oceansdata.dime.common.event.IllegalEventFormatException;
import io.vertx.core.json.JsonObject;


import java.util.UUID;

public class TaskCreatedEvent extends TaskEvent {

    public TaskCreatedEvent(UUID taskId){
        super(taskId, EventType.TASK_CREATED);
    }

    public TaskCreatedEvent(JsonObject data) throws IllegalEventFormatException {
        super(data);
    }


}
