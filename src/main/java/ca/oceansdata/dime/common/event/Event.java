package ca.oceansdata.dime.common.event;

import ca.oceansdata.dime.common.event.types.*;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.sql.SQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


/**@Author Alexandru Ianta
 * An event is a metadata entity that describes some action that has been performed on the DIME system.
 *
 */
public class Event {

    private static final Logger log = LoggerFactory.getLogger(Event.class);

    private UUID id;
    private Date timestamp;
    private String eventTarget; //ORCID of the user who should see this event
    private EventType type;
    protected JsonObject data = new JsonObject();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", new Locale("us"));

    /**
     * No-Args constructor
     */
    public Event(){
        this.id = UUID.randomUUID();
        this.timestamp = Date.from(Instant.now());
    }

    /**
     * Event type constructor, used by child classes
     */
    public Event(EventType type){
        this.id = UUID.randomUUID();
        this.timestamp = Date.from(Instant.now());
        this.type = type;
    }


    /** DataObject constructor.
     *  This allows us to pass events around the event bus easily.
     *
     * @param data
     */
    public Event (JsonObject data) throws IllegalEventFormatException{

        if(!data.containsKey("id")){
            throw new IllegalEventFormatException(data, "id", "key missing");
        }
        this.id = UUID.fromString(data.getString("id"));

        if(!data.containsKey("timestamp")){
            throw new IllegalEventFormatException(data, "timestamp", "key missing");
        }

        try{
            Date date = dateFormat.parse(data.getString("timestamp"));
            this.timestamp = date;
        }catch (ParseException pe){
            log.error("Error parsing timestamp from JSON Object:\n{}", data.encodePrettily());
            log.error(pe.getMessage());
            pe.printStackTrace();
            throw new IllegalEventFormatException(data, "timestamp", "wrong format");
        }

        if(!data.containsKey("eventTarget")){
            throw new IllegalEventFormatException(data, "eventTarget", "key missing");
        }
        this.eventTarget = data.getString("eventTarget");

        if(!data.containsKey("type")){
            throw new IllegalEventFormatException(data, "type", "key missing");
        }
        this.type = EventType.getType(data.getString("type"));


        this.data = data.getJsonObject("data");

    }


    public JsonObject toJson(){
        JsonObject result = new JsonObject()
                .put("id", getId().toString())
                .put("timestamp", getTimestamp().toString())
                .put("eventTarget", getEventTarget())
                .put("type", getType().getText())
                .put("data", data);

        return result;
    }


    public static Event fromSQLResult(JsonObject data){

        Event event = new Event();
        event.setId(UUID.fromString(data.getString("ID")));

        Date d = new Date();
        d.setTime(data.getLong("TIMESTAMP"));
        event.setTimestamp(d);

        event.setEventTarget(data.getString("EVENT_TARGET"));
        event.setType(EventType.getType(data.getString("EVENT_TYPE")));

        JsonObject json = new JsonObject(data.getString("DATA"));
        event.setData(json);

        try{

            switch (event.getType()){
                case LOGIN:
                    return new LoginEvent(event.toJson());
                case LOGOUT:
                    return new LogoutEvent(event.toJson());
                case DOCUMENT_UPLOAD:
                    return new DocumentUploadEvent(event.toJson());
                case ATTRIBUTE_MAPPING:
                    return new AttributeMappingEvent(event.toJson());
                case DOWNLOAD_INTEGRATED_FILE:
                    return new DownloadIntegratedFileEvent(event.toJson());
                case DOWNLOAD_ORIGINAL_FILE:
                    return new DownloadOriginalFileEvent(event.toJson());
                case COMMUNITY_TASK_MATCH:
                    return new CommunityTaskMatchEvent(event.toJson());
                case COMMUNITY_MATCH_RESULT:
                    return new CommunityMatchResultEvent(event.toJson());
                case PROFILE_UPDATE:
                    return new ProfileUpdateEvent(event.toJson());
                case DOWNLOAD_DIME_TOOLS_FOR_WINDOWS:
                    return new DownloadDimeToolsForWindowsEvent(event.toJson());
                case UPDATE_METADATA_FIELD:
                    return new UpdateMetadataFieldEvent(event.toJson());
                case TASK_CREATED:
                    return new TaskCreatedEvent(event.toJson());
                case TASK_SKIPPED:
                    return new TaskSkippedEvent(event.toJson());
                case TASK_UPDATED:
                    return new TaskUpdatedEvent(event.toJson());
                case TASK_DISMISSED:
                    return new TaskDismissedEvent(event.toJson());
                default:
                    log.error("No such event.");
            }

        }catch (IllegalEventFormatException iefe){
            log.error("Error parsing event from database");
            log.error(iefe.getMessage());
            iefe.printStackTrace();
        }

        return event;
    }


    public static void createTable(SQLConnection conn){

        String sql = "CREATE TABLE IF NOT EXISTS EVENTS (" +
                "ID TEXT PRIMARY KEY NOT NULL," +
                "TIMESTAMP INTEGER NOT NULL," +
                "EVENT_TARGET TEXT NOT NULL," +
                "EVENT_TYPE TEXT NOT NULL," +
                "DATA TEXT" +
                ");";

        conn.rxUpdate(sql).subscribe(
                success->{
                    log.info("Successfully created events table in database!");
                },
                err->{
                    log.error("Error creating events table in database!");
                    log.error(err.getMessage());
                    log.error("Closing connection!");
                    conn.close();
                    err.printStackTrace();
                }
        );

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getEventTarget() {
        return eventTarget;
    }

    public void setEventTarget(String eventTarget) {
        this.eventTarget = eventTarget;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

}
