package ca.oceansdata.dime.common;

import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.tag.Tags;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;

import java.time.Instant;
import java.util.*;

public class DimeUtils {

    /**Returns a tracing span scope from an event bus message. Allows for distributed tracing across process
     * boundaries via clustered event bus.
     * @Author Alexandru Ianta
     * @param tracer Tracer of the receiving service
     * @param msg Message to extract span from
     * @param operationName Name of the operation for the span scope
     * @return Scope of the extracted span from the message
     */
    public static Scope startSpanFromMessage(Tracer tracer, Message msg, String operationName){
        //Get the header map from the message
        Map<String,String> map = extractHeaderMap(msg);

        Tracer.SpanBuilder spanBuilder;
        try{
            SpanContext parentSpan = tracer.extract(Format.Builtin.TEXT_MAP, new TextMapAdapter(map));
            if(parentSpan == null){
                //If no parent span could be extracted, just build a normal span.
                spanBuilder = tracer.buildSpan(operationName);
            }else{
                spanBuilder = tracer.buildSpan(operationName).asChildOf(parentSpan);
            }
        }catch (IllegalArgumentException e){
            spanBuilder = tracer.buildSpan(operationName);
        }

        Span span = spanBuilder.withTag(Tags.SPAN_KIND, Tags.SPAN_KIND_CONSUMER).start();

        return tracer.activateSpan(span);

    }

    /** Returns a jager tracer for a given operation.
     * @Author Alexandru Ianta
     * @param operationName Name of the operation for which to create a tracer.
     * @return the jager tracer for the specified operation.
     */
    public static JaegerTracer getTracer(String operationName){
        SamplerConfiguration samplerConfig = SamplerConfiguration.fromEnv().withType("ratelimiting").withParam(2.0);
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

    /** Extracts message headers as Map<String,String>, useful for extracting tracing contexts.
     * @Author Alexandru Ianta
     * @param msg message to extract headers from.
     * @return Map of headers
     */
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
