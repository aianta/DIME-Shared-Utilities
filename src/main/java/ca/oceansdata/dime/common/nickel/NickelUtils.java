package ca.oceansdata.dime.common.nickel;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class NickelUtils {

    /** Append a nickel's metadata fields to the event bus message headers.
     *  WHY: The eventbus consumers on the front end use this metadata to process
     *  events like a new metadata key being created.
     * @param options
     * @param nickel
     * @return
     */
    public static DeliveryOptions appendMetaToHeaders(DeliveryOptions options, Nickel nickel){
        nickel.getMeta().forEach(e->{
            if(e.getValue() instanceof String) options.addHeader(e.getKey(), (String)e.getValue());
            if(e.getValue() instanceof Integer) options.addHeader(e.getKey(), Integer.toString((Integer)e.getValue()));
            if(e.getValue() instanceof Double) options.addHeader(e.getKey(), Double.toString((Double)e.getValue()));
            /** Ignore json entities.
             *  <p>
             *      <b>WHY:</b> Duplicating nickel metadata like this is a questionable design choice as is.
             *      We want to keep it to simple fields, rather than support its expansion into deep copying
             *      json in event bus msg headers.
             *  </p>
             */
            if(e.getValue() instanceof JsonObject) return;
            if(e.getValue() instanceof JsonArray) return;

        });
        return options;
    }
}
