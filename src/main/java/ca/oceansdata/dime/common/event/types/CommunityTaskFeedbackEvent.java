package ca.oceansdata.dime.common.event.types;

import ca.oceansdata.dime.common.event.Event;
import ca.oceansdata.dime.common.event.EventType;
import ca.oceansdata.dime.common.event.IllegalEventFormatException;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

@DataObject
public class CommunityTaskFeedbackEvent extends Event {

    UUID taskId;
    UUID schemaId;
    UUID attributeId;
    String attribute;
    String feedback;


    /**Convenience constructor
     * @param taskId Id of the task for which feedback was given
     * @param schemaId Id of the schema containing the attribute for this task
     * @param attributeId Id of the attribute for this task
     * @param attribute attribute for which feedback was given
     * @param feedback the given feedback
     */
    public CommunityTaskFeedbackEvent(UUID taskId, UUID schemaId, UUID attributeId, String attribute, String feedback){
        super(EventType.COMMUNITY_TASK_FEEDBACK);
        this.taskId = taskId;
        this.schemaId = schemaId;
        this.attributeId = attributeId;
        this.attribute = attribute;
        this.feedback = feedback;

        JsonObject data = new JsonObject()
                .put("taskId", taskId.toString())
                .put("schemaId", schemaId.toString())
                .put("attributeId", attributeId.toString())
                .put("attribute", attribute)
                .put("feedback", feedback);
        this.setData(data);
    }

    public CommunityTaskFeedbackEvent(){
        super(EventType.COMMUNITY_TASK_FEEDBACK);
    }

    public CommunityTaskFeedbackEvent(JsonObject data) throws IllegalEventFormatException{
        super(data);

        if(!data.containsKey("data")){
            throw new IllegalEventFormatException(data, "data", "key missing");
        }

        JsonObject inner = data.getJsonObject("data");

        if(!inner.containsKey("taskId")){
            throw new IllegalEventFormatException(data, "data->taskId", "key missing");
        }
        this.taskId = UUID.fromString(inner.getString("taskId"));

        if(!inner.containsKey("schemaId")){
            throw new IllegalEventFormatException(data, "data->schemaId", "key missing");
        }
        this.schemaId = UUID.fromString(inner.getString("schemaId"));

        if(!inner.containsKey("attributeId")){
            throw new IllegalEventFormatException(data, "data->attributeId", "key missing");
        }
        this.attributeId = UUID.fromString(inner.getString("attributeId"));

        if(!inner.containsKey("attribute")){
            throw new IllegalEventFormatException(data, "data->attribute", "key missing");
        }
        this.attribute = inner.getString("attribute");

        if(!inner.containsKey("feedback")){
            throw new IllegalEventFormatException(data, "data->feedback", "key missing");
        }
        this.feedback = inner.getString("feedback");
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
        data.mergeIn(new JsonObject().put("taskId", taskId.toString()));
    }

    public UUID getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(UUID schemaId) {
        this.schemaId = schemaId;
        data.mergeIn(new JsonObject().put("schemaId", schemaId.toString()));
    }

    public UUID getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(UUID attributeId) {
        this.attributeId = attributeId;
        data.mergeIn(new JsonObject().put("attributeId", attributeId.toString()));
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
        data.mergeIn(new JsonObject().put("attribute", attribute));
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
        data.mergeIn(new JsonObject().put("feedback", feedback));
    }
}
