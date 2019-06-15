package ca.oceansdata.dime.common.event.types;

import ca.oceansdata.dime.common.event.Event;
import ca.oceansdata.dime.common.event.EventType;
import ca.oceansdata.dime.common.event.IllegalEventFormatException;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class DownloadIntegratedFileEvent extends Event {

    public DownloadIntegratedFileEvent(){
        super(EventType.DOWNLOAD_INTEGRATED_FILE);
    }

    public DownloadIntegratedFileEvent(JsonObject data) throws IllegalEventFormatException {
        super(data);
    }


}
