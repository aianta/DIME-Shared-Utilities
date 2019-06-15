package ca.oceansdata.dime.common.event.types;

import ca.oceansdata.dime.common.event.Event;
import ca.oceansdata.dime.common.event.EventType;
import ca.oceansdata.dime.common.event.IllegalEventFormatException;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class DownloadOriginalFileEvent extends Event {

    public DownloadOriginalFileEvent(){
        super(EventType.DOWNLOAD_ORIGINAL_FILE);
    }

    public DownloadOriginalFileEvent(JsonObject data) throws IllegalEventFormatException{
        super(data);
    }

}
