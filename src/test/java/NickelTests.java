import ca.oceansdata.dime.common.nickel.Nickel;
import ca.oceansdata.dime.common.nickel.NickelOrigin;
import ca.oceansdata.dime.common.nickel.NickelType;
import ca.oceansdata.dime.common.nickel.codec.NickelCodec;
import ca.oceansdata.dime.common.nickel.impl.NickelImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.EventBus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(VertxExtension.class)
public class NickelTests {

    private static final Logger log = LoggerFactory.getLogger(NickelTests.class);
    private static final String EB_ADDRESS = "nickel-test";

    @BeforeAll
    static void init(Vertx vertx, VertxTestContext testContext){
        vertx.eventBus().getDelegate().registerDefaultCodec(NickelImpl.class, new NickelCodec());
        testContext.completeNow();
    }

    @Test
    @DisplayName("Send and receive data using Nickel interface")
    void sendReceiveNickel(Vertx vertx, VertxTestContext testContext){
        EventBus eb = vertx.eventBus();

        Nickel n = createDefaultTestNickel();

        eb.consumer(EB_ADDRESS, msg-> testContext.verify(()->{

            Nickel nickel = (Nickel)msg.body();

            if(msg.headers().get("type").equals("GET")){
                log.info("got nickel!");
                Nickel respNickel = Nickel.nickelForA(nickel);
                log.info("response: {}",respNickel.toJson().encodePrettily());
                Nickel.publish(eb, EB_ADDRESS, respNickel);
            }
        }));

        Nickel.send(eb, EB_ADDRESS, n).onSuccess(
                response->testContext.verify(()->{
                    assertEquals(response.type(),NickelType.RESPONSE);
                    assertEquals(response.correlationId(), n.correlationId());
                    testContext.completeNow();
                })
        );


    }

    @Test
    @DisplayName("Send using Nickel interface")
    void sendNickelThruInterface(Vertx vertx, VertxTestContext testContext){
        EventBus eb = vertx.eventBus();

        Nickel n = createDefaultTestNickel();

        eb.consumer(EB_ADDRESS, msg->testContext.verify(()->{
            Nickel nickel = (Nickel)msg.body();
            assertTrue(match(n, nickel));
            assertEquals(msg.headers().get("correlationId"), nickel.correlationId().toString());
            assertEquals(msg.headers().get("type"), nickel.type().name());
            testContext.completeNow();
        }));

        Nickel.publish(eb, EB_ADDRESS, n);

    }

    @Test
    @DisplayName("Send an empty nickel")
    void sendEmptyNickel(Vertx vertx, VertxTestContext testContext){
        EventBus eb = vertx.eventBus();

        log.info("Creating empty nickel");
        Nickel n = createDefaultTestNickel();

        eb.consumer(EB_ADDRESS, msg->testContext.verify(()->{
           try{
               Nickel nickel = (Nickel)msg.body();
               assertEquals(nickel.getData().length, 0);
               assertTrue(match(n, nickel));
               testContext.completeNow();
           }catch (Exception e){
               e.printStackTrace();
               testContext.failNow(e);
           }
        }));

        eb.publish(EB_ADDRESS, n);


    }

    @Test
    @DisplayName("Send a nickel packed with a JsonObject")
    void sendNickel(Vertx vertx, VertxTestContext testContext){

        EventBus eb = vertx.eventBus();

        Nickel n = createDefaultTestNickel();

        JsonObject someData = new JsonObject()
                .put("data", "value");

        n.pack(someData);

        eb.consumer(EB_ADDRESS, msg-> testContext.verify(()->{
            Nickel receivedNickel = (Nickel)msg.body();

            JsonObject payload = Nickel.unpack(receivedNickel, JsonObject.class);

            assertTrue(match(n, receivedNickel));
            assertEquals(someData.encode(), payload.encode());

            testContext.completeNow();
        }));

        eb.publish(EB_ADDRESS, n);


    }

    private static boolean match(Nickel n1, Nickel n2){
        NickelImpl first = (NickelImpl)n1;
        NickelImpl second = (NickelImpl)n2;

        //Check payload length
        if(first.getData().length != second.getData().length){
            return false;
        }

        //Check sendable status
        if(first.sendable() != second.sendable()){
            return false;
        }

        //Check origin
        if(!first.origin().equals(second.origin())){
            return false;
        }

        //Check type
        if(!first.type().equals(second.type())){
            return false;
        }

        //Check status code
        if(first.statusCode() != second.statusCode()){
            return false;
        }

        //Check requestQueryParameters
        if(!first.requestQueryParams().encode().equals(second.requestQueryParams().encode())){
            return false;
        }

        //Check httpResponseHeaders
        if(!first.httpResponseHeaders().encode().equals(second.httpResponseHeaders().encode())){
            return false;
        }

        //Check tracing
        if(!first.tracing().encode().equals(second.tracing().encode())){
            return false;
        }

        //Check timestamp
        if(first.timestamp() != second.timestamp()){
            return false;
        }

        //Check correlation id
        if(!first.correlationId().equals(second.correlationId())){
            return false;
        }

        //Check oricd
        if(!first.orcid().equals(second.orcid())){
            return false;
        }


        return true;
    }

    private static Nickel createDefaultTestNickel(){
        return Nickel.create(
                "0000-0000-0000-0000",
                NickelType.GET,
                NickelOrigin.DIME_GATEWAY
        );
    }

}
