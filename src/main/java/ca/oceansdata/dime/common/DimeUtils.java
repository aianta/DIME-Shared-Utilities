package ca.oceansdata.dime.common;

import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.internal.JaegerTracer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;

import java.time.Instant;
import java.util.*;

public class DimeUtils {

    public static JaegerTracer getTracer(String operationName){
        SamplerConfiguration samplerConfig = SamplerConfiguration.fromEnv().withType("const").withParam(1);
        ReporterConfiguration reporterConfig = ReporterConfiguration.fromEnv().withLogSpans(true);
        Configuration config = new Configuration(operationName).withSampler(samplerConfig).withReporter(reporterConfig);
        return config.getTracer();
    }

    /**Build event bus message headers for internal communication between services
     * @Author Alexandru Ianta
     * @param orcid orcid to bind to the message
     * @param action action to bind to the message
     * @return Delivery options with event bus message headers
     */
    public static DeliveryOptions buildHeaders(String orcid, String action){
        DeliveryOptions opts = new DeliveryOptions();

        opts.addHeader("action", action);
        opts.addHeader("correlationId", UUID.randomUUID().toString());
        opts.addHeader("timestamp", Date.from(Instant.now()).toString());
        opts.addHeader("orcid", orcid);
        opts.addHeader("queryParams", "{}");

        return opts;
    }

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

    /**
     * @Author Alexandru Ianta
     * Returns all headers of the message as a json object
     * @param msg message to extract meta data from
     * @return a json object containing all headers of the message
     */
    public static JsonObject extractHeaders(Message msg){
        JsonObject result =  new JsonObject();

        for (Map.Entry<String, String> entry: msg.headers().entries()){
            if(entry.getKey().equals("responseHeaders") || entry.getKey().equals("requestHeaders") || entry.getKey().equals("queryParams")){
                result.put(entry.getKey(), new JsonObject(entry.getValue()));
            }else{
                result.put(entry.getKey(), entry.getValue());
            }
        }

        return result;
    }

    /** Returns the query parameters sent with the http request associated with the message.
     * @Author Alexandru Ianta
     * @param msg message to extract query params from.
     * @return a Json Object containing the query parameters if they exist, empty otherwise.
     */
    public static JsonObject extractQueryParams(Message msg){
        if(msg.headers().contains("queryParams")){
            return new JsonObject(msg.headers().get("queryParams"));
        }else{
            return new JsonObject();
        }
    }

    /**Returns the http headers of the http request associated with the message.
     *
     * @Author Alexandru Ianta
     * @param msg message to extract request headers from.
     * @return a Json Object containing the http headers of the http request associated with this message if they exist, empty otherwise.
     */
    public static JsonObject extractRequestHeaders(Message msg){
        if(msg.headers().contains("requestHeaders")){
            return new JsonObject(msg.headers().get("requestHeaders"));
        }else{
            return new JsonObject();
        }
    }

    public static Map<String, String> extractHeaderMap(Message msg){

        final Map<String,String> map = new HashMap<>();

        Iterator<Map.Entry<String,String>> it = msg.headers().entries().iterator();

        while (it.hasNext()){
            Map.Entry<String,String> entry = it.next();
            map.put(entry.getKey(), entry.getValue());
        }

        return map;

    }

}
