package ca.oceansdata.dime.common.tracing;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class EventBusCarrier implements io.opentracing.propagation.TextMap {

    private final DeliveryOptions opts;

    public EventBusCarrier(String orcid, String action){
        opts = new DeliveryOptions();
        opts.addHeader("action", action);
        opts.addHeader("correlationId", UUID.randomUUID().toString());
        opts.addHeader("timestamp", Date.from(Instant.now()).toString());
        opts.addHeader("orcid", orcid);
        opts.addHeader("queryParams", "{}");
    }

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

    public void bindJson(String name, JsonObject obj){

        //Clear any existing value first
        if(opts.getHeaders().contains(name)){
            opts.getHeaders().remove(name);
        }

        opts.addHeader(name, obj.encode());
    }
}
