package ca.oceansdata.dime.common.exceptions;

import ca.oceansdata.dime.common.nickel.Nickel;
import ca.oceansdata.dime.common.nickel.NickelOrigin;
import ca.oceansdata.dime.common.nickel.NickelType;
import io.opentracing.Scope;
import io.opentracing.Tracer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class ErrorNickel extends Exception implements Nickel {
    private static final Logger log = LoggerFactory.getLogger(ErrorNickel.class);

    private Nickel source;

    public ErrorNickel(Nickel n){
        source = n;
    }


    @Override
    public Nickel setType(HttpMethod method) {
        log.error("Cannot set type on Error Nickel!");
        return null;
    }

    @Override
    public Nickel setType(NickelType type) {
        log.error("Cannot set type on Error Nickel!");
        return null;
    }

    @Override
    public Nickel setOrigin(NickelOrigin origin) {
        log.error("Cannot set origin on Error Nickel!");
        return null;
    }

    @Override
    public Nickel setStatusCode(Integer statusCode) {
        log.error("Cannot set status code on Error Nickel!");
        return null;
    }

    @Override
    public NickelType type() {
        return source.type();
    }

    @Override
    public long timestamp() {
        return source.timestamp();
    }

    @Override
    public Date dateTimestamp() {
        return source.dateTimestamp();
    }

    @Override
    public UUID correlationId() {
        return source.correlationId();
    }

    @Override
    public String orcid() {
        return source.orcid();
    }

    @Override
    public JsonObject getMeta() {
        return source.getMeta();
    }

    @Override
    public Nickel setMeta(JsonObject metadata) {
        log.error("Cannot set metadata on Error Nickel!");
        return null;
    }

    @Override
    public byte[] getData() {
        return source.getData();
    }

    @Override
    public NickelOrigin origin() {
        return source.origin();
    }

    @Override
    public int statusCode() {
        return source.statusCode();
    }

    @Override
    public JsonObject httpResponseHeaders() {
        return source.httpResponseHeaders();
    }

    @Override
    public JsonObject requestQueryParams() {
        return source.requestQueryParams();
    }

    @Override
    public Nickel setRequestQueryParams(JsonObject params) {
        log.error("Cannot set request query parameters on Error Nickel!");
        return null;
    }

    @Override
    public Nickel setOrcid(String orcid) {
        log.error("Cannot set orcid on Error Nickel!");
        return null;
    }

    @Override
    public Nickel pack(byte[] bytes) {
        log.error("Cannot pack into Error Nickel!");
        return null;
    }

    @Override
    public Nickel pack(JsonArray array) {
        log.error("Cannot pack into Error Nickel!");
        return null;
    }

    @Override
    public Nickel pack(JsonObject object) {
        log.error("Cannot pack into Error Nickel!");
        return null;
    }

    @Override
    public Scope extendScope(Tracer tracer, String operationName) {
        return source.extendScope(tracer, operationName);
    }

    @Override
    public JsonObject toJson() {
        return source.toJson();
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return source.iterator();
    }

    @Override
    public void put(String key, String value) {
        source.put(key,value);
    }
}
