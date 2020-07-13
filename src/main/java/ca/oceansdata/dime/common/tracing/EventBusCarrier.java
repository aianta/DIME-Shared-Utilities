package ca.oceansdata.dime.common.tracing;

import ca.oceansdata.dime.common.exceptions.MissingActionException;
import io.vertx.core.eventbus.DeliveryOptions;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@Deprecated
public class EventBusCarrier implements io.opentracing.propagation.TextMap {

    private static final Logger log = LoggerFactory.getLogger(EventBusCarrier.class);
    private final DeliveryOptions opts;

    @Deprecated
    public EventBusCarrier(){
        opts = new DeliveryOptions();
        opts.addHeader("correlationId", UUID.randomUUID().toString());
        opts.addHeader("timestamp", Date.from(Instant.now()).toString());
        opts.addHeader("queryParams", "{}");
    }

    @Deprecated
    public EventBusCarrier(String orcid, String action){
        opts = new DeliveryOptions();
        opts.addHeader("action", action);
        opts.addHeader("correlationId", UUID.randomUUID().toString());
        opts.addHeader("timestamp", Date.from(Instant.now()).toString());
        opts.addHeader("orcid", orcid);
        opts.addHeader("queryParams", "{}");
    }

    @Deprecated
    public void setQueryParams(JsonObject params){
        if(opts != null){

            if(opts.getHeaders().get("queryParams") != null){
                opts.getHeaders().remove("queryParams");
            }

            opts.addHeader("queryParams", params.toString());
        }
    }

    @Deprecated
    public String getCorrelationId(){
        return opts.getHeaders().get("correlationId");
    }

    /** Sets the correlation id to that of the passed in carrier.
     * @param c The carrier's whose correlationId we're copying
     */
    @Deprecated
    public void setCorrelationId(EventBusCarrier c){
        opts.getHeaders().remove("correlationId");
        opts.addHeader("correlationId", c.getCorrelationId());
    }

    @Deprecated
    public EventBusCarrier(Message msg, String action){
        opts = new DeliveryOptions();
        //Copy all headers from the message except the action and timestamp header
        for(Map.Entry<String, String> entry: msg.headers().entries()){
            if(!entry.getKey().equals("action")&&!entry.getKey().equals("timestamp")){
                opts.addHeader(entry.getKey(), entry.getValue());
            }
        }

        //Set action
        opts.addHeader("action", action);

        //Generate new timestamp
        opts.addHeader("timestamp", Date.from(Instant.now()).toString());
    }


    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        throw new UnsupportedOperationException("carrier is write-only");
    }

    @Override
    public void put(String key, String value) {

        opts.addHeader(key, value);
    }

    @Deprecated
    public void bindJson(String name, JsonObject obj){

        //Clear any existing value first
        if(opts.getHeaders().contains(name)){
            opts.getHeaders().remove(name);
        }

        opts.addHeader(name, obj.encode());
    }

    @Deprecated
    public DeliveryOptions getOptions(){
        try{
            if(!opts.getHeaders().contains("orcid")){
                log.info("No ORCID in delivery options.");
            }

            if(!opts.getHeaders().contains("action")){
                throw new MissingActionException();
            }

        }catch (MissingActionException mae){
            log.error(mae.toString());
            mae.printStackTrace();
        }

        return opts;

    }

}
