package ca.oceansdata.dime.common.event.types;

import ca.oceansdata.dime.common.event.Event;
import ca.oceansdata.dime.common.event.EventType;
import ca.oceansdata.dime.common.event.IllegalEventFormatException;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

@DataObject
public class CommunityMatchResultEvent extends Event{

    UUID taskId;
    UUID schemaId;
    UUID attributeId;
    String attribute;
    UUID definitionId;
    String definition;
    String source;
    String url;

    /**Convenience constructor
     * @param taskId Id of the task for which we have a community match result
     * @param schemaId Id of the schema containing the attribute for this task
     * @param attributeId Id of the attribute for this task
     * @param attribute Attribute that was matched
     * @param definitionId Id of the definition that the attribute was matched to
     * @param definition Definition that the attribute was matched to
     * @param source Source of the definition the attribute was matched to
     * @param url URL at which the user can inspect the community result
     */
    public CommunityMatchResultEvent(UUID taskId, UUID schemaId, UUID attributeId, String attribute, UUID definitionId, String definition, String source, String url){
        super(EventType.COMMUNITY_MATCH_RESULT);
        this.url = url;
        this.taskId = taskId;
        this.schemaId = schemaId;
        this.attributeId = attributeId;
        this.attribute = attribute;
        this.definitionId = definitionId;
        this.definition = definition;
        this.source = source;

        JsonObject data = new JsonObject()
                .put("taskId", taskId.toString())
                .put("schemaId", schemaId.toString())
                .put("attributeId", attributeId.toString())
                .put("attribute", attribute)
                .put("definitionId", definitionId.toString())
                .put("definition", definition)
                .put("source", definition)
                .put("url", url);
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

        if(!inner.containsKey("url")){
            throw new IllegalEventFormatException(data, "data->url", "key missing");
        }
        this.url = data.getJsonObject("data").getString("url");

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

    public void setUrl(String url){
        this.url = url;
        data.mergeIn(new JsonObject().put("url", url));
    }

    public String getUrl(){
        return this.url;
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

    public UUID getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(UUID definitionId) {
        this.definitionId = definitionId;
        data.mergeIn(new JsonObject().put("definitionId", definitionId.toString()));
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
}
