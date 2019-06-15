package ca.oceansdata.dime.common.event.types;

import ca.oceansdata.dime.common.event.Event;
import ca.oceansdata.dime.common.event.EventType;
import ca.oceansdata.dime.common.event.IllegalEventFormatException;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class DownloadDimeToolsForWindowsEvent extends Event {

    public DownloadDimeToolsForWindowsEvent(){
        super(EventType.DOWNLOAD_DIME_TOOLS_FOR_WINDOWS);
    }

    public DownloadDimeToolsForWindowsEvent(JsonObject data) throws IllegalEventFormatException{
        super(data);
    }
    

}
