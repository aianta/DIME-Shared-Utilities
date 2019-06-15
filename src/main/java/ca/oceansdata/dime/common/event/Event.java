package ca.oceansdata.dime.common.event;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.sql.SQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;


@DataObject
public class Event {

    private static final Logger log = LoggerFactory.getLogger(Event.class);

    private UUID id;
    private Date timestamp;
    private String eventTarget; //ORCID of the user who should see this event
    private EventType type;
    private EventStatus status = EventStatus.UNREAD;
    private Date readTimestamp;
    protected JsonObject data = new JsonObject();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz YYYY");

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

        if(!data.containsKey("status")){
            throw new IllegalEventFormatException(data, "status", "key missing");
        }
        this.status = EventStatus.valueOf(data.getString("status"));

        //Parse read timestamp if it exists
        if(data.containsKey("readTimestamp")){
            try{
                Date readDate = dateFormat.parse(data.getString("readTimestamp"));
                this.readTimestamp = readDate;
            }catch (ParseException pe){
                log.error("Error parsing read timestamp from JSON object:\n{}", data.encodePrettily());
                log.error(pe.getMessage());
                pe.printStackTrace();
                throw new IllegalEventFormatException(data, "readTimestamp", "wrong format");
            }
        }

        this.data = data.getJsonObject("data");

    }


    public JsonObject toJson(){
        JsonObject result = new JsonObject()
                .put("id", getId().toString())
                .put("timestamp", getTimestamp().toString())
                .put("eventTarget", getEventTarget())
                .put("type", getType().getText())
                .put("status", getStatus().getText())
                .put("data", data);

        if(getReadTimestamp() != null){
            result.put("readTimestamp", getReadTimestamp().toString());
        }

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

        //If this event has been read
        if(data.getLong("READ_TIMESTAMP") != null)
        {
            Date readDate = new Date();
            readDate.setTime(data.getLong("READ_TIMESTAMP"));
            event.setReadTimestamp(readDate);
        }


        event.setStatus(EventStatus.valueOf(data.getString("EVENT_STATUS")));

        JsonObject json = new JsonObject(data.getString("DATA"));
        event.setData(json);

        return event;
    }


    public static void createTable(SQLConnection conn){

        String sql = "CREATE TABLE IF NOT EXISTS EVENTS (" +
                "ID TEXT PRIMARY KEY NOT NULL," +
                "TIMESTAMP INTEGER NOT NULL," +
                "EVENT_TARGET TEXT NOT NULL," +
                "EVENT_TYPE TEXT NOT NULL," +
                "READ_TIMESTAMP INTEGER," +
                "EVENT_STATUS TEXT NOT NULL," +
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

    public Date getReadTimestamp() {
        return readTimestamp;
    }

    public void setReadTimestamp(Date readTimestamp) {
        this.readTimestamp = readTimestamp;
    }

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

}