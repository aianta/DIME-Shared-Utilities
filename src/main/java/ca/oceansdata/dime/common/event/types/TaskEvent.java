package ca.oceansdata.dime.common.event.types;

import ca.oceansdata.dime.common.event.Event;
import ca.oceansdata.dime.common.event.EventType;
import ca.oceansdata.dime.common.event.IllegalEventFormatException;
import io.vertx.core.json.JsonObject;

import java.util.Optional;
import java.util.UUID;

public abstract class TaskEvent extends Event {

    UUID taskId;

    public TaskEvent(UUID taskId, EventType type) {
        super(type);
        this.taskId = taskId;

        JsonObject data = new JsonObject()
                .put("taskId", taskId.toString());

        this.setData(data);
    }

    public TaskEvent(JsonObject data) throws IllegalEventFormatException {
        super(data);

        Optional<JsonObject> eventData = Optional.of(data.getJsonObject("data"));
        eventData.flatMap(
                inner->{
                    Optional<UUID> taskId = Optional.of(UUID.fromString(data.getString("taskId")));

                    taskId.flatMap(
                            tId->{
                                this.taskId = tId;
                                return Optional.empty();
                            }
                    );

                    return Optional.empty();
                }
        );
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
        data.mergeIn(new JsonObject().put("taskId", taskId.toString()));
    }
}
