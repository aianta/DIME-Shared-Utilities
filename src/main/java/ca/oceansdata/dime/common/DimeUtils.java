package ca.oceansdata.dime.common;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.reactivex.core.eventbus.Message;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class DimeUtils {

    /** Builds event bus message headers with specified action value
     *  from a given event bus message.
     *
     * @Author Alexandru Ianta
     * @param msg message to copy headers from
     * @param action action to associate with new headers
     * @return Delivery options with event bus message headers
     */
    public static DeliveryOptions buildHeaders(Message msg, String action){

        DeliveryOptions opt = new DeliveryOptions();

        //Copy all headers from the message except the action and timestamp header
        for(Map.Entry<String, String> entry: msg.headers().entries()){
            if(!entry.getKey().equals("action")&&!entry.getKey().equals("timestamp")){
                opt.addHeader(entry.getKey(), entry.getValue());
            }
        }

        //Set action
        opt.addHeader("action", action);

        //Generate new timestamp
        opt.addHeader("timestamp", Date.from(Instant.now()).toString());

        return opt;
    }

}
