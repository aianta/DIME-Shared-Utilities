package ca.oceansdata.dime.common.event;

import ca.oceansdata.dime.common.event.types.*;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    private EventBus eb;

    public EventService(EventBus eb){
        this.eb = eb;
    }

    public void emit(Event e){
        try{
            if(e.getEventTarget() == null){
                throw new NullPointerException("eventTarget cannot be null! Cannot emit event!");
            }else{
                if(e.getType() == null){
                    throw new Exception("Event type cannot be null! Cannot emit event!");
                }else{
                    eb.publish("dime.events", e.toJson());
                }
            }
        }catch (Exception err){
            log.error("Error emitting event!");
            log.error(err.getMessage());
            err.printStackTrace();
        }


    }

    public static Event parse(Message msg){

        JsonObject json = (JsonObject)msg.body();

        try{
            switch (json.getString("type")){
                case "Attribute Mapping":
                    return new AttributeMappingEvent(json);

                case "Document Upload":
                    return new DocumentUploadEvent(json);

                case "Login":
                    return new LoginEvent(json);

                case "Logout":
                    return new LogoutEvent(json);

                case "Download Integrated File":
                    return new DownloadIntegratedFileEvent(json);

                case "Download Original File":
                    return new DownloadOriginalFileEvent(json);

                case "Community Task Created":
                    return new CommunityTaskCreatedEvent(json);

                case "Community Task Match":
                    return new CommunityTaskMatchEvent(json);

                case "Community Task Skip":
                    return new CommunityTaskSkipEvent(json);

                case "Community Task Feedback":
                    return new CommunityTaskFeedbackEvent(json);

                case "Community Match Result":
                    return new CommunityMatchResultEvent(json);

                case "Profile Update":
                    return new ProfileUpdateEvent(json);

                case "Download DIME Tools for Windows":
                    return new DownloadDimeToolsForWindowsEvent(json);

                case "Edit Dataset Name":
                    return new EditSchemaNameEvent(json);

                case "Edit Dataset Description":
                    return new EditSchemaDescriptionEvent(json);

                case "Updated Metadata Field":
                    return new UpdateMetadataField(json);

                default:
                    log.error("No such event type!");
            }
        }catch (IllegalEventFormatException e){
            log.error("Error parsing event object off the wire");
            log.error(e.getMessage());
            e.printStackTrace();
        }

        return null;

    }

}
