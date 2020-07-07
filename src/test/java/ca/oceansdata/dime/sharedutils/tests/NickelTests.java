package ca.oceansdata.dime.sharedutils.tests;

import ca.oceansdata.dime.common.event.Event;
import ca.oceansdata.dime.common.exceptions.UnpackException;
import ca.oceansdata.dime.common.nickel.*;
import ca.oceansdata.dime.common.nickel.codec.NickelCodec;
import ca.oceansdata.dime.common.nickel.impl.NickelImpl;
import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.reporters.LoggingReporter;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.jaegertracing.spi.Reporter;
import io.jaegertracing.internal.reporters.NoopReporter;
import io.jaegertracing.spi.Sampler;
import io.opentracing.Scope;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(VertxExtension.class)
public class NickelTests {

    private static final Logger log = LoggerFactory.getLogger(NickelTests.class);
    private static final String EB_ADDRESS = "nickel-test";
    private static MessageConsumer consumer = null;
    private static NickelRouter router = null;

    /** Before all tests configure the eventbus to use the nickel codec
     * @param vertx
     * @param testContext
     */
    @BeforeAll
    static void init(Vertx vertx, VertxTestContext testContext){
        vertx.eventBus().getDelegate().registerDefaultCodec(NickelImpl.class, new NickelCodec());
        testContext.completeNow();
    }

    /** After each test, if an event bus consumer was registered, unregister
     *  it.
     * @param vertx
     * @param testContext
     */
    @AfterEach
    void unregisterConsumer(Vertx vertx, VertxTestContext testContext){
        if(consumer != null){
            consumer.rxUnregister().subscribe(()->testContext.verify(()->{
                testContext.completeNow();
            }));
        }else{
            testContext.completeNow();
        }
    }

    /** After each test, if a nickel router was created, destroy it.
     *
     * @param vertx
     * @param testContext
     */
    @AfterEach
    void destroyRouter(Vertx vertx, VertxTestContext testContext){
        if(router != null){
            router.destroy();
            testContext.completeNow();
        }else{
            testContext.completeNow();
        }
    }

    @Test
    @DisplayName("Error handling with Nickels")
    void errorWithBadNickel(Vertx vertx, VertxTestContext testContext){
        EventBus eb = vertx.eventBus();

        router = new NickelRouter(eb, EB_ADDRESS)
                .function((request, response)->{
                    Nickel errNickel = Nickel.badNickel(request);
                    errNickel.pack(new JsonObject()
                    .put("error","A dummy error"));
                    errNickel.setStatusCode(500);
                    log.info("errNickel: {}", errNickel.toJson().encodePrettily());
                    return errNickel;
                });

        Nickel n = createDefaultTestNickel();
        Nickel.send(eb, EB_ADDRESS, n).onFailure(
                err->testContext.verify(()->{
                    log.info("Error message: {}", err.getMessage());
                    testContext.completeNow();
                })
        );
    }

    @Test
    @DisplayName("Http Method to Nickel Type resolution")
    void httpMethodNickelTypeResolution(Vertx vertx, VertxTestContext testContext){
        assertEquals(NickelType.GET, Nickel.create().setType(HttpMethod.GET).type());
        assertEquals(NickelType.POST, Nickel.create().setType(HttpMethod.POST).type());
        assertEquals(NickelType.PUT, Nickel.create().setType(HttpMethod.PUT).type());
        assertEquals(NickelType.DELETE, Nickel.create().setType(HttpMethod.DELETE).type());
        testContext.completeNow();
    }

    @Test
    @DisplayName("ThrowException on unknown unpack class")
    void unknownUpack(Vertx vertx, VertxTestContext testContext){
        Nickel n = createDefaultTestNickel();
        assertThrows(UnpackException.class, ()->Nickel.unpack(n, Event.class));
        testContext.completeNow();
    }

    @Test
    @DisplayName("Send JsonArray payload")
    void jsonArrayPayload(Vertx vertx, VertxTestContext testContext){
        EventBus eb = vertx.eventBus();

        router = new NickelRouter(eb, EB_ADDRESS)
                .handler(nickel -> testContext.verify(()->{
                    JsonArray body = Nickel.unpack(nickel, JsonArray.class);
                    assertTrue(body.size() > 0);
                    assertEquals("element", body.getString(0));
                    testContext.completeNow();
                }));

        Nickel n = createDefaultTestNickel();
        n.pack(new JsonArray().add("element"));
        Nickel.publish(eb,EB_ADDRESS, n);
    }

    @Test
    @DisplayName("Send document payload")
    void documentPayload(Vertx vertx, VertxTestContext testContext){
        EventBus eb = vertx.eventBus();

        router = new NickelRouter(eb, EB_ADDRESS)
                .handler(nickel -> testContext.verify(()->{

                    byte [] receivedBytes = nickel.getData();
                    log.info("Received bytes: {}", nickel.getMeta().getInteger("fileSize"));
                    assertTrue(nickel.getMeta().getInteger("fileSize") > 0);
                    assertEquals(nickel.getMeta().getInteger("fileSize"), receivedBytes.length );
                    testContext.completeNow();

                }));

        try{
            byte [] bytes = Files.readAllBytes(FileSystems.getDefault().getPath("Halifax_Buoy_trial.csv"));

            Nickel n = createDefaultTestNickel();
            n.setMeta(new JsonObject().put("fileSize", bytes.length));
            log.info("sending {} bytes", bytes.length);
            n.pack(bytes);
            Nickel.publish(eb, EB_ADDRESS, n);

        }catch (IOException ioException){
            log.error("IO exception during test");
            testContext.failNow(ioException);
        }
    }


    @Test
    @DisplayName("Inject distributed tracing into nickel")
    void distributedTracing(Vertx vertx, VertxTestContext testContext){
        EventBus eb = vertx.eventBus();
        Tracer tracer = getDummyTracer("unit-test");
        Scope scope = tracer.activateSpan(tracer.buildSpan("sendingNickel").start());

        router = new NickelRouter(eb, EB_ADDRESS)
                .handler(nickel -> testContext.verify(()->{

                    try(Scope processingScope = nickel.extendScope(tracer, "processing")){

                        processingScope.span().log("done processing");
                        processingScope.span().finish();


                        testContext.completeNow();

                    }


                }));

        Nickel n = createDefaultTestNickel();
        tracer.inject(scope.span().context(), Format.Builtin.TEXT_MAP, n);
        Nickel.publish(eb, EB_ADDRESS, n);
        tracer.activeSpan().finish();

    }

    @Test
    @DisplayName("Send a nickel with http request parameters")
    void requestParamNickel(Vertx vertx, VertxTestContext testContext){
        EventBus eb = vertx.eventBus();

        //Create a router that checks the request parameters
        router = new NickelRouter(eb, EB_ADDRESS)
                .handler(nickel -> testContext.verify(()->{
                    assertTrue(nickel.requestQueryParams().containsKey("query"));
                    assertEquals("time", nickel.requestQueryParams().getString("query"));
                    testContext.completeNow();
                }));

        NickelImpl n = (NickelImpl) createDefaultTestNickel();
        n.setRequestQueryParams(new JsonObject().put("query", "time"));
        Nickel.publish(eb, EB_ADDRESS, n);
    }

    @Test
    @DisplayName("Send a nickel with some metadata")
    void sendNickelMeta(Vertx vertx, VertxTestContext testContext){
        EventBus eb = vertx.eventBus();

        //Create a router that checks the metadata
        router = new NickelRouter(eb, EB_ADDRESS)
                .handler(nickel->testContext.verify(()->{
                    log.info(nickel.toJson().encodePrettily());
                    assertTrue(nickel.getMeta().containsKey("triggerCount"));
                    assertEquals(5, nickel.getMeta().getInteger("triggerCount"));
                    testContext.completeNow();

                }));

        //Create the nickel and attach the metadata
        Nickel n = createDefaultTestNickel();
        n.setMeta(new JsonObject().put("triggerCount", 5));

        log.info(n.toJson().encodePrettily());

        //Send the nickel
        Nickel.send(eb, EB_ADDRESS, n);
    }

    @Test
    @DisplayName("Send and receive a batch of nickels")
    void sendBatch(Vertx vertx, VertxTestContext testContext){
        EventBus eb = vertx.eventBus();
        int batchSize = 5;

        router = new NickelRouter(eb, EB_ADDRESS)
                .function((in,out)->out);

        NickelBatch roll = new NickelBatch();

        for(int i = 0; i < batchSize; i++){
            roll.add(EB_ADDRESS, createDefaultTestNickel());
        }

        Nickel.sendAll(eb,roll).rxOnComplete().subscribe(
                results->testContext.verify(()->{

                    assertEquals(batchSize, results.size());
                    testContext.completeNow();

                })
        );

    }

    @Test
    @DisplayName("Use NickelRouter to apply a type specific function to incomping nickels")
    void typeFunctionsWithRouter(Vertx vertx, VertxTestContext testContext){
        EventBus eb = vertx.eventBus();

        Checkpoint get = testContext.checkpoint();
        Checkpoint post = testContext.checkpoint();
        Checkpoint other = testContext.checkpoint();

        //Create a router with 2 type functions and a global function
        router = new NickelRouter(eb, EB_ADDRESS)
                .typeFunction(NickelType.GET,
                        ((getNickel, respNickel)->respNickel.pack(
                                new JsonObject()
                                .put("I was a", "get nickel!")
                        )))
                .typeFunction(NickelType.POST,
                        ((postNickel, respNickel)->respNickel.pack(
                                new JsonObject()
                                .put("I was a", "post nickel!")
                        )))
                .function((otherNickel, respNickel)->respNickel.pack(
                        new JsonObject()
                        .put("I was a", "different nickel!")
                ));

        //Register a consumer to listen and parse response nickels
        consumer = eb.consumer(EB_ADDRESS, msg->testContext.verify(()->{

            Nickel response = (Nickel)msg.body();
            log.info("Got {}", response.toJson().encodePrettily());
            if(response.type().equals(NickelType.RESPONSE)){
                log.info("Got response nickel");
                JsonObject data = Nickel.unpack(response, JsonObject.class);

                assertTrue(data.containsKey("I was a"));

                if(data.getString("I was a").equals("get nickel!")){
                    get.flag();
                }

                if(data.getString("I was a").equals("post nickel!")){
                    post.flag();
                }

                if(data.getString("I was a").equals("different nickel!")){
                    other.flag();
                }
            }
        }));
        Nickel n1 = createDefaultTestNickel();
        Nickel n2 = createDefaultTestNickel().setType(NickelType.POST);
        Nickel n3 = createDefaultTestNickel().setType(NickelType.ACTIVE);
        Nickel.publish(eb,EB_ADDRESS, n1);
        Nickel.publish(eb,EB_ADDRESS, n2);
        Nickel.publish(eb, EB_ADDRESS, n3);
    }

    @Test
    @DisplayName("Use NickelRouter to apply a function to incoming nickels")
    void simpleNickelFunctionWithRouter(Vertx vertx, VertxTestContext testContext){
        EventBus eb = vertx.eventBus();

        router = new NickelRouter(eb, EB_ADDRESS)
                .function((in,out)-> out) //Simply return the provided output nickel
        ;

        consumer = eb.consumer(EB_ADDRESS,msg->testContext.verify(()->{
            Nickel response = (Nickel)msg.body();
            if(response.type().equals(NickelType.RESPONSE)){
                log.info(response.toJson().encodePrettily());
                testContext.completeNow();
            }
        }));

        Nickel input = createDefaultTestNickel();
        Nickel.publish(eb, EB_ADDRESS, input);
    }

    @Test
    @DisplayName("Use NickelRouter to handle nickels by type")
    void typeHandlingWithNickelRouter(Vertx vertx, VertxTestContext testContext){
        EventBus eb = vertx.eventBus();

        //Create 3 checkpoints to be hit by nickel type handlers
        Checkpoint typeHandlers = testContext.checkpoint(3);

        //Create a nickel router with 3 type handlers
        router = new NickelRouter(eb, EB_ADDRESS)
                .typeHandler(NickelType.GET, getNickel->{
                    assertEquals(NickelType.GET,getNickel.type());
                    typeHandlers.flag();
                } )
                .typeHandler(NickelType.POST, postNickel->{
                    assertEquals( NickelType.POST,postNickel.type());
                    typeHandlers.flag();
                })
                .typeHandler(NickelType.ERROR, errorNickel->{
                    assertEquals(NickelType.ERROR, errorNickel.type());
                    typeHandlers.flag();
                });

        //Send 3 nickels
        Nickel n1 = createDefaultTestNickel().setType(NickelType.GET);
        Nickel n2 = createDefaultTestNickel().setType(NickelType.POST);
        Nickel n3 = createDefaultTestNickel().setType(NickelType.ERROR);

        Nickel.publish(eb, EB_ADDRESS, n1);
        Nickel.publish(eb, EB_ADDRESS, n2);
        Nickel.publish(eb, EB_ADDRESS, n3);

    }

    @Test
    @DisplayName("Use NickelRouter to handle nickels")
    void simpleNickelHandler(Vertx vertx, VertxTestContext testContext){
        EventBus eb = vertx.eventBus();

        router = new NickelRouter(eb, EB_ADDRESS);


        router.handler(n-> testContext.verify(()->{
            log.info("I got a nickel from the router!");
            testContext.completeNow();
        }));


        Nickel n = createDefaultTestNickel();


        Nickel.publish(eb,EB_ADDRESS, n);

    }

    @Test
    @DisplayName("Send and receive data using Nickel interface")
    void sendReceiveNickel(Vertx vertx, VertxTestContext testContext){
        EventBus eb = vertx.eventBus();

        Nickel n = createDefaultTestNickel();

        consumer = eb.consumer(EB_ADDRESS, msg-> testContext.verify(()->{

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

        consumer = eb.consumer(EB_ADDRESS, msg->testContext.verify(()->{
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

        consumer = eb.consumer(EB_ADDRESS, msg->testContext.verify(()->{
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

        consumer = eb.consumer(EB_ADDRESS, msg-> testContext.verify(()->{
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

    public static JaegerTracer getDummyTracer(String operationName){
        Sampler constantSampler = new ConstSampler(true);
        Reporter loggingReporter = new LoggingReporter();

        return new JaegerTracer.Builder(operationName)
                .withReporter(loggingReporter)
                .withSampler(constantSampler)
                .build();
    }


}
