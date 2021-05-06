package ca.oceansdata.dime.common.event.types;

import ca.oceansdata.dime.common.event.Event;
import ca.oceansdata.dime.common.event.EventType;
import ca.oceansdata.dime.common.event.IllegalEventFormatException;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class LinkExternalAccountEvent extends Event {

    String accountType;

    public LinkExternalAccountEvent(String accountType){
        super(EventType.LINK_EXTERNAL_ACCOUNT);
        this.accountType = accountType;

        this.setData(new JsonObject().put("accountType", accountType));
    }

    public LinkExternalAccountEvent(){super(EventType.LINK_EXTERNAL_ACCOUNT);}

    public LinkExternalAccountEvent(JsonObject data) throws IllegalEventFormatException{
        super(data);

        if(!data.containsKey("data")){
            throw new IllegalEventFormatException(data, "data", "key missing");
        }
        data = data.getJsonObject("data");

        if(!data.containsKey("accountType")){
            throw new IllegalEventFormatException(data, "data->accountType", "key missing");
        }
        this.accountType = data.getString("accountType");
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
