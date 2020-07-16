package ca.oceansdata.dime.common.nickel;

import io.vertx.core.eventbus.DeliveryOptions;

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
        });
        return options;
    }
}
