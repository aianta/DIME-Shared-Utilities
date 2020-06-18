package ca.oceansdata.dime.common.nickel;

import ca.oceansdata.dime.common.exceptions.UnpackException;
import ca.oceansdata.dime.common.nickel.impl.NickelImpl;
import io.opentracing.Scope;
import io.opentracing.Tracer;
import io.opentracing.propagation.TextMap;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Promise;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.MessageConsumer;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/** Common eventbus message wrapper interface for DIME services
 *  @Author Alexandru Ianta
 *
 *  Supports distributed tracing.
 *
 */
public interface Nickel extends TextMap {

    /** Create a nickel with no type, orcid, or origin.
     *
     * @return
     */
    static Nickel create(){
        NickelImpl nickel = new NickelImpl();
        nickel.setCorrelationId(UUID.randomUUID());
        nickel.setTimestamp(Date.from(Instant.now()));
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

    /** Get a response nickel from a nickel
     *
     * @param src the nickel to produce a response from
     * @return a response nickel
     */
    static Nickel nickelForA(Nickel src){
        NickelImpl result = (NickelImpl)from(src);
        result.setType(NickelType.RESPONSE);
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

        //Send the nickel!
        eb.publish(address, nickel, options);
    }

    /** Send a nickel and get a promise for an associated response nickel.
     *
     * @param eb the event bus to send the nickel on
     * @param address the address to send the nickel to and listen for the the response nickel on
     * @param nickel the nickel to be sent
     * @return a promise that completes successfully upon the capture of a response
     * nickel with the same correlation id, or fail upon the capture of an error
     * nickel with the same correlation id.
     */
    static Promise<Nickel> send(EventBus eb, String address, Nickel nickel){
        Promise<Nickel> promise = Promise.promise();
        //Create a promise to complete once the consumer is no longer required.
        Promise<Void> consumerPromise = Promise.promise();

        //Create an event bus consumer that listens for a response nickel
        MessageConsumer<Nickel> consumer = eb.consumer(address, msg->{
            //If a nickel from the address has our correlation id
            if(msg.headers().get("correlationId").equals(nickel.correlationId().toString())){

                //If it's a response nickel
                if(msg.headers().get("type").equals(NickelType.RESPONSE.name())){
                    promise.complete(msg.body());
                }

                //If it's an error nickel
                if(msg.headers().get("type").equals(NickelType.ERROR)){
                    promise.fail("Bad Nickel!"); //TODO - probably should pass at least an error message around
                }

                //Either way, once we've gotten something back, unregister the consumer
                consumerPromise.complete();
            }
        });

        //Unregister the consumer after a nickel is received.
        consumerPromise.future().onComplete(done->consumer.unregister());

        //Create message headers required to capture response nickel
        DeliveryOptions options = new DeliveryOptions()
                .addHeader("correlationId", nickel.correlationId().toString())
                .addHeader("type", nickel.type().name());

        //Send the nickel!
        publish(eb, address, nickel);

        return promise;
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

    void setType(HttpMethod method);

    void setType(NickelType type);

    void setOrigin(NickelOrigin origin);

    void setStatusCode(Integer statusCode);

    NickelType type();

    long timestamp();

    Date dateTimestamp();

    UUID correlationId();

    String orcid();


    byte [] getData();

    NickelOrigin origin();

    int statusCode();

    JsonObject httpResponseHeaders();

    JsonObject requestQueryParams();

    boolean sendable();

    void pack(byte[] bytes);

    void pack(JsonArray array);

    void pack(JsonObject object);

    /** Get's the scope embedded in the nickel and starts a span
     *  for the given operation name.
     * @param tracer tracer to use
     * @param operationName operation name of the new span
     * @return the scope of the nickel with the new span activated.
     */
    Scope extendScope(Tracer tracer, String operationName);


}
