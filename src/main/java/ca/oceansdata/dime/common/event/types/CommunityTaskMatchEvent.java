package ca.oceansdata.dime.common.event.types;

import ca.oceansdata.dime.common.event.Event;
import ca.oceansdata.dime.common.event.EventType;
import ca.oceansdata.dime.common.event.IllegalEventFormatException;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

/** An event triggered when an AMT is completed for the user completing the AMT.
 *
 *  "You matched an attribute for a community member!"
 *
 */
@DataObject
public class CommunityTaskMatchEvent extends Event {

    UUID taskId;
    UUID schemaId;
    UUID attributeId;
    String attribute;
    String definition;
    String source;
    UUID definitionId;

    /**Convenience constructor
     * @param taskId Id of the community task that was matched
     * @param schemaId Id of the schema containing the attribute for this task
     * @param attributeId Id of the attribute for this task
     * @param attribute attribute that was matched
     * @param definitionId Id of the definition that the attribute was matched to
     * @param definition definition that the attribute was matched to
     * @param source source of the definition the attribute was matched to
     */
    public CommunityTaskMatchEvent(UUID taskId, UUID schemaId, UUID attributeId, String attribute, UUID definitionId, String definition, String source){
        super(EventType.COMMUNITY_TASK_MATCH);
        this.taskId = taskId;
        this.schemaId = schemaId;
        this.attributeId = attributeId;
        this.attribute = attribute;
        this.definition = definition;
        this.definitionId = definitionId;
        this.source = source;

        JsonObject data = new JsonObject()
                .put("taskId", taskId.toString())
                .put("schemaId", schemaId.toString())
                .put("attributeId", attributeId.toString())
                .put("attribute", attribute)
                .put("definitionId", definitionId.toString())
                .put("definition", definition)
                .put("source", source);
        this.setData(data);
    }

    public CommunityTaskMatchEvent(){
        super(EventType.COMMUNITY_TASK_MATCH);
    }

    public CommunityTaskMatchEvent(JsonObject data) throws IllegalEventFormatException{
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

        if(!inner.containsKey("definitionId")){
            throw new IllegalEventFormatException(data, "data->definitionId", "key missing");
        }
        this.definitionId = UUID.fromString(inner.getString("definitionId"));

        if(!inner.containsKey("definition")){
            throw new IllegalEventFormatException(data, "data->definition", "key missing");
        }
        this.definition = inner.getString("definition");

        if(!inner.containsKey("source")){
            throw new IllegalEventFormatException(data, "data->source", "key missing");
        }
        this.source = inner.getString("source");
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

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
        data.mergeIn(new JsonObject().put("definition", definition));
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
        data.mergeIn(new JsonObject().put("source", source));
    }

    public UUID getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(UUID definitionId) {
        this.definitionId = definitionId;
        data.mergeIn(new JsonObject().put("definitionId", definitionId.toString()));
    }
}
