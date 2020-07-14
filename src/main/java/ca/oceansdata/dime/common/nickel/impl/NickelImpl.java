package ca.oceansdata.dime.common.nickel.impl;

import ca.oceansdata.dime.common.nickel.Nickel;
import ca.oceansdata.dime.common.nickel.NickelOrigin;
import ca.oceansdata.dime.common.nickel.NickelType;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.tag.Tags;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

public class NickelImpl implements Nickel {

    private NickelType type;
    private Date timestamp;
    private UUID correlationId;
    private String orcid;
    private NickelOrigin origin = NickelOrigin.UNSPECIFIED;
    private Integer statusCode = 200;
    private JsonObject httpResponseHeaders = new JsonObject();
    private JsonObject requestQueryParams = new JsonObject();
    private byte[] payload = new byte[]{};
    private JsonObject tracing = new JsonObject();
    private JsonObject metadata = new JsonObject();


    @Override
    public Nickel pack(byte[] bytes) {
        putData(bytes);
        return this;
    }

    @Override
    public NickelType type() {
        return this.type;
    }

    @Override
    public long timestamp() {
        return timestamp.getTime();
    }

    @Override
    public Date dateTimestamp() {
        return timestamp;
    }

    @Override
    public UUID correlationId() {
        return correlationId;
    }

    @Override
    public String orcid() {
        return orcid;
    }

    @Override
    public JsonObject getMeta() {
        return metadata;
    }

    @Override
    public Nickel setMeta(JsonObject metadata) {
        this.metadata = metadata;
        return this;
    }


    public void putData(byte[] data) {
        this.payload = data;
    }

    @Override
    public byte[] getData() {
        return this.payload;
    }

    @Override
    public NickelOrigin origin() {
        return origin;
    }

    @Override
    public int statusCode() {
        return statusCode;
    }

    @Override
    public JsonObject httpResponseHeaders() {
        return httpResponseHeaders;
    }

    @Override
    public JsonObject requestQueryParams() {
        return requestQueryParams;
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return getTracingMap().entrySet().iterator();
    }

    @Override
    public void forEach(Consumer<? super Map.Entry<String, String>> action) {
        getTracingMap().entrySet().forEach(action);
    }

    @Override
    public Spliterator<Map.Entry<String, String>> spliterator() {
        return getTracingMap().entrySet().spliterator();
    }

    @Override
    public void put(String key, String value) {
        tracing.put(key,value);
    }

    public Nickel setType(HttpMethod method){
        this.type = NickelType.fromHttpMethod(method);
        return this;
    }
    public Nickel setType(NickelType type) {
        this.type = type;
        return this;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimestamp(long time){
        this.timestamp = Date.from(Instant.ofEpochMilli(time));
    }

    public void setCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
    }

    public Nickel setOrcid(String orcid) {
        this.orcid = orcid;
        return this;
    }


    public Nickel setOrigin(NickelOrigin origin) {
        this.origin = origin;
        return this;
    }

    public Nickel setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public void setHttpResponseHeaders(JsonObject httpResponseHeaders) {
        this.httpResponseHeaders = httpResponseHeaders;
    }


    public Nickel pack(JsonArray array){
        httpResponseHeaders.put("Content-Type", "application/json");
        String encodedJson = array.encode();
        putData(encodedJson.getBytes());
        return this;
    }

    public Nickel pack(JsonObject object){
        httpResponseHeaders.put("Content-Type", "application/json");
        String encodedJson = object.encode();
        putData(encodedJson.getBytes());
        return this;
    }

    @Override
    public Scope extendScope(Tracer tracer, String operationName) {
        Tracer.SpanBuilder spanBuilder;
        try{
            SpanContext parentSpan = tracer.extract(
                    Format.Builtin.TEXT_MAP,
                    new TextMapAdapter(getTracingMap())
            );
            if(parentSpan == null){
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

    @Override
    public Nickel packAndInject(Tracer tracer, JsonObject data) {
        pack(data);
        tracer.inject(tracer.scopeManager().activeSpan().context(), Format.Builtin.TEXT_MAP, this);
        return this;
    }

    @Override
    public Nickel packAndInject(Tracer tracer, JsonArray data) {
        pack(data);
        tracer.inject(tracer.scopeManager().activeSpan().context(), Format.Builtin.TEXT_MAP, this);
        return this;
    }

    @Override
    public Nickel packAndInject(Tracer tracer, byte[] data) {
        pack(data);
        tracer.inject(tracer.scopeManager().activeSpan().context(), Format.Builtin.TEXT_MAP, this);
        return this;
    }

    @Override
    public Nickel packInjectAndFinish(Tracer tracer, JsonObject data) {
        pack(data);
        tracer.inject(tracer.scopeManager().activeSpan().context(), Format.Builtin.TEXT_MAP, this);
        tracer.activeSpan().finish();
        return this;
    }

    @Override
    public Nickel packInjectAndFinish(Tracer tracer, JsonArray data) {
        pack(data);
        tracer.inject(tracer.scopeManager().activeSpan().context(), Format.Builtin.TEXT_MAP, this);
        tracer.activeSpan().finish();
        return this;
    }

    @Override
    public Nickel packInjectAndFinish(Tracer tracer, byte[] data) {
        pack(data);
        tracer.inject(tracer.scopeManager().activeSpan().context(), Format.Builtin.TEXT_MAP, this);
        tracer.activeSpan().finish();
        return this;
    }

    /** Utility method that converts the tracing json object
     *  into a Map<String,String>. Used to encode and decode
     *  tracing text map when sending a nickel over the event
     *  bus.
     *
     *  @return a <String,String> map of the traicing json object
     */
    private Map<String,String> getTracingMap(){
        Map<String,Object> temp = tracing.getMap();
        Map<String,String> map = new HashMap<>();
        temp.forEach((s,o)->map.put(s,(String)o));
        return map;
    }

    public JsonObject tracing() {
        return tracing;
    }

    public Nickel setRequestQueryParams(JsonObject requestQueryParams) {
        this.requestQueryParams = requestQueryParams;
        return this;
    }

    public void setTracing(JsonObject tracing) {
        this.tracing = tracing;
    }

    public JsonObject toJson(){
        JsonObject result = new JsonObject()
                .put("correlationId", correlationId().toString())
                .put("timestamp", dateTimestamp().toString())
                .put("metadata", getMeta())
                .put("orcid", orcid())
                .put("type", type().name())
                .put("origin", origin().name())
                .put("statusCode", statusCode())
                .put("requestQueryParameters", requestQueryParams())
                .put("httpResponseHeaders", httpResponseHeaders())
                .put("payloadSize", payload.length);

        return result;
    }
}
