package ca.oceansdata.dime.common.event.types;

import ca.oceansdata.dime.common.event.Event;
import ca.oceansdata.dime.common.event.EventType;
import ca.oceansdata.dime.common.event.IllegalEventFormatException;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

/** An event triggered when a CAMG is created.
 *
 */
@DataObject
public class CommunityTaskCreatedEvent extends Event {

    UUID taskId;
    UUID schemaId;
    UUID attributeId;
    String attribute;
    String originDocument;

    /**Convenience constructor
     * @param taskId Id of the created community task
     * @param schemaId Id of the schema containing the attribute for this task
     * @param attributeId Id of the attribute for this task
     * @param attribute attribute to be matched by the community
     * @param originDocument the document from which this attribute originates
     */
    public CommunityTaskCreatedEvent(UUID taskId, UUID schemaId, UUID attributeId, String attribute, String originDocument){
        super(EventType.COMMUNITY_TASK_CREATED);
        this.taskId = taskId;
        this.schemaId = schemaId;
        this.attributeId = attributeId;
        this.attribute = attribute;
        this.originDocument = originDocument;

        JsonObject data = new JsonObject()
                .put("taskId", taskId.toString())
                .put("schemaId", schemaId.toString())
                .put("attributeId", attributeId.toString())
                .put("attribute", attribute)
                .put("originDocument", originDocument);
        this.setData(data);
    }

    public CommunityTaskCreatedEvent(){
        super(EventType.COMMUNITY_TASK_CREATED);
    }

    public CommunityTaskCreatedEvent(JsonObject data) throws IllegalEventFormatException {
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

        if(!inner.containsKey("originDocument")){
            throw new IllegalEventFormatException(data, "data->originDocument", "key missing");
        }
        this.originDocument = inner.getString("originDocument");

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
        data.mergeIn(new JsonObject().put("schemaId", schemaId));
    }

    public UUID getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(UUID attributeId) {
        this.attributeId = attributeId;
        data.mergeIn(new JsonObject().put("attributeId",  attributeId.toString()));
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
        data.mergeIn(new JsonObject().put("attribute", attribute));
    }

    public String getOriginDocument() {
        return originDocument;
    }

    public void setOriginDocument(String originDocument) {
        this.originDocument = originDocument;
        data.mergeIn(new JsonObject().put("originDocument", originDocument));
    }
}
