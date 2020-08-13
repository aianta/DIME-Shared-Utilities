package ca.oceansdata.dime.common.nickel;

import ca.oceansdata.dime.common.event.Event;
import ca.oceansdata.dime.common.exceptions.ErrorNickel;
import ca.oceansdata.dime.common.exceptions.UnpackException;
import ca.oceansdata.dime.common.nickel.impl.NickelImpl;
import io.opentracing.Scope;
import io.opentracing.Tracer;
import io.opentracing.propagation.TextMap;
import io.vertx.core.eventbus.DeliveryOptions;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.CompositeFuture;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.core.Promise;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.Instant;
import java.util.*;

/** Common eventbus message wrapper interface for DIME services
 *  @Author Alexandru Ianta
 *
 *  Supports distributed tracing.
 *
 */
public interface Nickel extends TextMap {
    Logger log = LoggerFactory.getLogger(Nickel.class);
    String SYSTEM_ORCID = "0000-0000-0000-0000";

    /** Create a nickel with no type, orcid, and an unspecified origin.
     *
     * @return
     */
    static Nickel create(){
        NickelImpl nickel = new NickelImpl();
        nickel.setCorrelationId(UUID.randomUUID());
        nickel.setTimestamp(Date.from(Instant.now()));
        nickel.setOrigin(NickelOrigin.UNSPECIFIED);
        nickel.setStatusCode(200); //Default 200 status code
        return nickel;
    }

    /** Create a nickel with a given type, origin and orcid
     *
     * @param orcid orcid of the nickel
     * @param type type of the nickel
     * @param origin origin of the nickel
     * @return the nickel
     */
    static Nickel create(String orcid, NickelType type, NickelOrigin origin){
        NickelImpl nickel = (NickelImpl)create();
        nickel.setType(type);
        nickel.setOrcid(orcid);
        nickel.setOrigin(origin);
        return nickel;
    }

    /** Create a nickel from another nickel,
     *  inheriting the correlation id, status code,
     *  timestamp, and orcid.
     *
     * @param src nickel to inherit from
     * @return a new nickel with inherited properties from src
     */
    static Nickel from(Nickel src){
        NickelImpl result = new NickelImpl();
        result.setCorrelationId(src.correlationId());
        result.setStatusCode(src.statusCode());
        result.setTimestamp(src.dateTimestamp());
        result.setOrcid(src.orcid());
        return result;
    }

    static Nickel create(Event event){
        log.info("Creating nickel for {} with target: {} ",event.getType().name(), event.getEventTarget());
        Nickel result = Nickel.create()
                .setOrcid(event.getEventTarget())
                .setType(NickelType.POST)
                .pack(event.toJson());
        return result;
    }

    /** Get a response nickel from a nickel
     *
     * @param src the nickel to produce a response from
     * @return a response nickel
     */
    static Nickel nickelForA(Nickel src){
        NickelImpl result = (NickelImpl)from(src);
        result.setType(NickelType.RESPONSE);
        result.setOrigin(NickelOrigin.UNSPECIFIED);
        return result;
    }

    /** Get a response nickel from a nickel, while
     *  specifying an origin
     * @param src
     * @param origin
     * @return
     */
    static Nickel nickelForA(Nickel src, NickelOrigin origin){
        Nickel result = nickelForA(src);
        result.setOrigin(origin);
        return result;
    }

    /** Get an error nickel from a nickel
     *
     * @param src the nickel to produce an error from
     * @return an error nickel
     */
    static Nickel badNickel(Nickel src){
        NickelImpl result = (NickelImpl)from(src);
        result.setType(NickelType.ERROR);
        result.setStatusCode(500); //Default error status code
        return result;
    }

    /** Publish a nickel on a given event bus to a given address.
     *
     * @param eb the event bus to publish the nickel on
     * @param address the address to publish the nickel to
     * @param nickel the nickel to publish
     */
    static void publish(EventBus eb, String address, Nickel nickel){

        //Create message headers required to capture response nickel
        DeliveryOptions options = new DeliveryOptions()
                .addHeader("correlationId", nickel.correlationId().toString())
                .addHeader("type", nickel.type().name());

        options = NickelUtils.appendMetaToHeaders(options, nickel);

        //Send the nickel!
        eb.publish(address, nickel, options);
    }

    /** Send a collection of nickels to various addresses and get a composite future
     *  for when we've received replies for each nickel that was sent.
     * @param eb
     * @param nickels a map of addresses and the nickels to be sent to that address
     * @return
     */
    static CompositeFuture sendAll(EventBus eb, Iterable<Map.Entry<String, List<Nickel>>> nickels){

        List<Future> nickelFutures = new ArrayList<>();

        Iterator<Map.Entry<String,List<Nickel>>> it = nickels.iterator();
        while (it.hasNext()){
            Map.Entry<String,List<Nickel>> entry = it.next();
            for(Nickel n: entry.getValue()){
                nickelFutures.add(send(eb, entry.getKey(), n));
            }
        }

        return CompositeFuture.all(nickelFutures);
    }

    /** Send a nickel and get a future for an associated response nickel.
     *
     * @param eb the event bus to send the nickel on
     * @param address the address to send the nickel to and listen for the the response nickel on
     * @param nickel the nickel to be sent
     * @return a promise that completes successfully upon the capture of a response
     * nickel with the same correlation id, or fail upon the capture of an error
     * nickel with the same correlation id.
     */
    static Future<Nickel> send(EventBus eb, String address, Nickel nickel){
        Promise<Nickel> promise = Promise.promise();

        //Create a promise to complete once the consumer is no longer required.
        Promise<Void> consumerPromise = Promise.promise();

        //Create an event bus consumer that listens for a response nickel
        MessageConsumer<Nickel> consumer = eb.consumer(address, msg->{

            //If a nickel from the address has our correlation id
            if(msg.headers().get("correlationId").equals(nickel.correlationId().toString())){

                //If it's a response nickel
                if(NickelType.valueOf(msg.headers().get("type")).equals(NickelType.RESPONSE)){
                    promise.complete(msg.body());
                    //Either way, once we've gotten something back, unregister the consumer
                    consumerPromise.complete();
                }

                //If it's an error nickel
                if(NickelType.valueOf(msg.headers().get("type")).equals(NickelType.ERROR)){
                    promise.fail(new ErrorNickel(msg.body()));
                    //Either way, once we've gotten something back, unregister the consumer
                    consumerPromise.complete();
                }

                //If it's a timeout nickel
                if(NickelType.valueOf(msg.headers().get("type")).equals(NickelType.TIMEOUT)){
                    promise.complete(msg.body());
                    consumerPromise.complete();
                }

            }
        });

        //Unregister the consumer after a nickel is received.
        consumerPromise.future().onComplete(done->consumer.unregister());

        //Send the nickel!
        publish(eb, address, nickel);

        return promise.future();
    }

    static Future<Nickel> sendWithTimeout(Vertx vertx, String address, Nickel nickel, long timeout){
        //Create a timeout nickel to send if we don't get a response in 5 seconds.
        Nickel timeoutNickel = Nickel.from(nickel)
                .setOrcid(SYSTEM_ORCID)
                .setStatusCode(504)
                .setType(NickelType.TIMEOUT);

        /** Set a timer that sends the timeout nickel if a response is not received by timeout */
        Future<Nickel> response = send(vertx.eventBus(), address, nickel);
        vertx.setTimer(timeout, timeoutId->{
            if(!response.isComplete()){
                log.warn("Request {} - {} - {} time out!",
                        nickel.correlationId().toString(),
                        nickel.type().toString(),
                        address
                );
                Nickel.publish(vertx.eventBus(), address, timeoutNickel);
            }
        });

        return response;
    }


    static <T> T unpack(Nickel nickel, Class<T> tClass) throws UnpackException {
        switch (tClass.getSimpleName()){
            case "JsonObject":
                String jsonStr = new String(nickel.getData());
                JsonObject contentJson = new JsonObject(jsonStr);
                return (T)contentJson;
            case "JsonArray":
                String jsonArrayStr = new String(nickel.getData());
                JsonArray contentJsonArray = new JsonArray(jsonArrayStr);
                return (T)contentJsonArray;
            default:
                throw new UnpackException(nickel, tClass);
        }
    }

    Nickel setType(HttpMethod method);

    Nickel setType(NickelType type);

    Nickel setOrigin(NickelOrigin origin);

    Nickel setStatusCode(Integer statusCode);

    /** The type of the nickel. Nickels have different types, which can change the way they are processed by services
     *  that receive them. Common nickel types are HTTP methods (GET, PUT, POST), but nickels sent internally by
     *  services to other services may use custom types like SCHEMA_CREATED, NEW_DOCUMENT, etc.
     *
     * @return
     */
    NickelType type();

    /** The timestamp at which the nickel was created. If a nickel is created from another nickel,
     *  the produced nickel inherits the source nickel's timestamp.
     * @return
     */
    long timestamp();

    /** The timestamp at which the nickel was created in Date format. If a nickel is created from another nickel,
     *  the produced nickel inherits the source nickel's timestamp.
     *
     * @return
     */
    Date dateTimestamp();

    /** Returns a uuid generated by the origin of the request. Usually this is DIMEGateway.
     *  Allows logs to be matched up when a nickel travels across different services.
     * @return the correlationId of the nickel
     */
    UUID correlationId();

    /** Returns the ORCID associated with the nickel. Because nickels correspond with events in the DIME service
     * ecosystem, they are often related to processing or retrieving data for a logged in user (such as fetching their
     * documents).
     *
     * If an action is internal to the DIME services and not related to a user, the 'system oricd' 0000-0000-0000-0000
     * is used.
     *
     * @return the orcid
     */
    String orcid();

    /** Returns the nickel's metadata object.
     *  <b>Will not be null, but may be empty</b>
     *
     * @return metadata json object.
     */
    JsonObject getMeta();

    /** Set the metadata for this nickel. This is a json object pased around with the nickel,
     *  containing arbitrary key value pairs
     *
     * @param metadata the json object to set as the nickel's metadata
     * @return the nickel, with the metadata set
     */
    Nickel setMeta(JsonObject metadata);

    byte [] getData();

    NickelOrigin origin();

    int statusCode();

    JsonObject httpResponseHeaders();

    Nickel setHttpResponseHeaders(JsonObject json);

    JsonObject requestQueryParams();

    Nickel setRequestQueryParams(JsonObject params);

    Nickel setOrcid(String orcid);

    Nickel pack(byte[] bytes);

    Nickel pack(JsonArray array);

    Nickel pack(JsonObject object);

    /** Get's the scope embedded in the nickel and starts a span
     *  for the given operation name.
     * @param tracer tracer to use
     * @param operationName operation name of the new span
     * @return the scope of the nickel with the new span activated.
     */
    Scope extendScope(Tracer tracer, String operationName);

    /** Packs a JsonObject and injects the distributed tracing context
     *  into the nickel.
     * @param tracer the tracer holding the context
     * @param data the data to pack into the nickel
     * @return the updated nickel
     */
    Nickel packAndInject(Tracer tracer,Scope scope, JsonObject data);

    /** Packs a JsonArray and injects the distributed tracing context
     *  into the nickel.
     * @param tracer the tracer holding the context
     * @param data the data to pack into the nickel
     * @return the updated nickel
     */
    Nickel packAndInject(Tracer tracer,Scope scope, JsonArray data);

    /** Packs a byte [] and injects the distributed tracing context
     *  into the nickel.
     * @param tracer the tracer holding the context
     * @param data the data to pack into the nickel
     * @return the updated nickel
     */
    Nickel packAndInject(Tracer tracer,Scope scope, byte [] data);

    /** Packs a JsonObject, injects the distributed tracing context
     *  into the nickel, and finally finishes the current span.
     *
     * @param tracer the tracer holding the context
     * @param data the data to pack into the nickel
     * @return the updated nickel
     */
    Nickel packInjectAndFinish(Tracer tracer,Scope scope,JsonObject data);

    /** Packs a JsonArray, injects the distributed tracing context
     *  into the nickel, and finally finishes the current span.
     *
     * @param tracer the tracer holding the context
     * @param data the data to pack into the nickel
     * @return the updated nickel
     */
    Nickel packInjectAndFinish(Tracer tracer,Scope scope, JsonArray data);

    /** Packs a byte [], injects the distributed tracing context
     *  into the nickel, and finally finishes the current span.
     *
     * @param tracer the tracer holding the context
     * @param data the data to pack into the nickel
     * @return the updated nickel
     */
    Nickel packInjectAndFinish(Tracer tracer,Scope scope, byte [] data);

    JsonObject toJson();


}
