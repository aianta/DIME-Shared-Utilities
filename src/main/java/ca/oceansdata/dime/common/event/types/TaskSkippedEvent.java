package ca.oceansdata.dime.common.event.types;

import ca.oceansdata.dime.common.event.EventType;
import ca.oceansdata.dime.common.event.IllegalEventFormatException;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

public class TaskSkippedEvent extends TaskEvent {
    public TaskSkippedEvent(UUID taskId) throws IllegalEventFormatException {
        super(taskId, EventType.TASK_SKIPPED);
    }

    public TaskSkippedEvent(JsonObject data) throws IllegalEventFormatException {
        super(data);
    }
}
