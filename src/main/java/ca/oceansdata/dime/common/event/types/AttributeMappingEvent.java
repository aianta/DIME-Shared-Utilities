package ca.oceansdata.dime.common.event.types;

import ca.oceansdata.dime.common.event.Event;
import ca.oceansdata.dime.common.event.EventType;
import ca.oceansdata.dime.common.event.IllegalEventFormatException;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

@DataObject
public class AttributeMappingEvent extends Event {

    UUID attributeId;
    UUID definitionId;
    UUID schemaId;
    String attribute;
    String definition;
    String source;

    /**Convenience constructor
     * @param attributeId Id of the attribute that has been mapped
     * @param definitionId Id of the definition that was mapped to this attribute
     * @param schemaId Id of the schema to which the attribute belongs
     * @param attribute Attribute that was mapped
     * @param definition Definition that is was mapped to
     * @param source Source of the definition that the attribute was mapped to
     */
    public AttributeMappingEvent(UUID attributeId, UUID definitionId, UUID schemaId, String attribute, String definition, String source){
        super(EventType.ATTRIBUTE_MAPPING);
        this.attributeId = attributeId;
        this.definitionId = definitionId;
        this.definition = definition;
        this.schemaId = schemaId;
        this.source = source;

        JsonObject data = new JsonObject()
                .put("attributeId", attributeId.toString())
                .put("definitionId", definitionId.toString())
                .put("schemaId", schemaId.toString())
                .put("attribute", attribute)
                .put("definition", definition)
                .put("source", source);
        this.setData(data);

    }

    public AttributeMappingEvent(){
        super(EventType.ATTRIBUTE_MAPPING);
    }

    public AttributeMappingEvent(JsonObject data) throws IllegalEventFormatException{
        super(data);

        if(!data.containsKey("data")){
            throw new IllegalEventFormatException(data, "data", "missing key");
        }

        JsonObject inner = data.getJsonObject("data");

        if(!inner.containsKey("attributeId")){
            throw new IllegalEventFormatException(data, "data->attributeId", "key missing");
        }
        this.attributeId = UUID.fromString(inner.getString("attributeId"));

        if(!inner.containsKey("definitionId")){
            throw new IllegalEventFormatException(data, "data->definitionId", "key missing");
        }
        this.definitionId = UUID.fromString(inner.getString("definitionId"));

        if(!inner.containsKey("schemaId")){
            throw new IllegalEventFormatException(data, "data->schemaId", "key missing");
        }
        this.schemaId = UUID.fromString(inner.getString("schemaId"));

        if(!inner.containsKey("attribute")){
            throw new IllegalEventFormatException(data, "data->attribute", "key missing");
        }
        this.attribute = inner.getString("attribute");

        if(!inner.containsKey("definition")){
            throw new IllegalEventFormatException(data, "data->definition", "key missing");
        }
        this.definition = inner.getString("definition");

        if(!inner.containsKey("source")){
            throw new IllegalEventFormatException(data, "data->source", "key missing");
        }
        this.source = inner.getString("source");
    }

    public UUID getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(UUID attributeId) {
        this.attributeId = attributeId;
        data.mergeIn(new JsonObject().put("attributeId",attributeId.toString()));
    }

    public UUID getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(UUID definitionId) {
        this.definitionId = definitionId;
        data.mergeIn(new JsonObject().put("definitionId",definitionId.toString()));
    }

    public UUID getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(UUID schemaId) {
        this.schemaId = schemaId;
        data.mergeIn(new JsonObject().put("schemaId",schemaId.toString()));
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
        data.mergeIn(new JsonObject().put("attribute",attribute));
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
        data.mergeIn(new JsonObject().put("definition",definition));
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
        data.mergeIn(new JsonObject().put("source",source));
    }
}
