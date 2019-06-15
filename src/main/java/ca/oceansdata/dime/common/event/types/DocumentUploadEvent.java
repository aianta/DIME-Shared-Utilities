package ca.oceansdata.dime.common.event.types;

import ca.oceansdata.dime.common.event.Event;
import ca.oceansdata.dime.common.event.EventType;
import ca.oceansdata.dime.common.event.IllegalEventFormatException;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class DocumentUploadEvent extends Event {

    String documentName;
    String documentId;
    String documentSize;

    /** Convenience constructor
     * @param documentId Id of the uploaded document
     * @param documentName Name of the uploaded document
     * @param documentSize Size of the uploaded document
     */
    public DocumentUploadEvent(String documentId, String documentName, String documentSize){
        super(EventType.DOCUMENT_UPLOAD);

        this.documentName = documentName;
        this.documentId = documentId;
        this.documentSize = documentSize;

        JsonObject data = new JsonObject()
                .put("documentId", documentId)
                .put("documentName", documentName)
                .put("documentSize", documentSize);

        this.setData(data);

    }

    public DocumentUploadEvent(){
        super(EventType.DOCUMENT_UPLOAD);
    }

    public DocumentUploadEvent(JsonObject data) throws IllegalEventFormatException {
        super(data);

        if(!data.containsKey("data")){
            throw new IllegalEventFormatException(data, "data", "missing key");
        }

        JsonObject inner = data.getJsonObject("data");

        if(!inner.containsKey("documentName")){
            throw new IllegalEventFormatException(data, "data->documentName", "missing key");
        }

        this.documentName = inner.getString("documentName");

        if(!inner.containsKey("documentId")){
            throw new IllegalEventFormatException(data, "data->documentId", "missing key");
        }

        this.documentId = inner.getString("documentId");

        if(!inner.containsKey("documentSize")){
            throw new IllegalEventFormatException(data, "data->documentSize", "missing key");
        }

        this.documentSize = inner.getString("documentSize");
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
        data.mergeIn(new JsonObject().put("documentName", documentName));
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
        data.mergeIn(new JsonObject().put("documentId", documentId));
    }

    public String getDocumentSize() {
        return documentSize;
    }

    public void setDocumentSize(String documentSize) {
        this.documentSize = documentSize;
        data.mergeIn(new JsonObject().put("documentSize", documentSize));
    }
}
