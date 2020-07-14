package ca.oceansdata.dime.common.nickel;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.HashMap;
import java.util.Map;

/** A Nickel router allows for easy processing of nickels sent on the
 *  event bus.
 *
 *  Two kinds of processors are supported by the NickelHandler.
 *
 *  <ul>
 *      <li>
 *        <b>Functions</b>
 *          Functions produce a nickel when provided with a nickel. The
 *          produced nickel is automatically published onto the eventbus.
 *
 *          Note that functions cannot be applied to RESPONSE or ERROR nickels,
 *          as that would create an infinite loop. Ex: Function produces RESPONSE
 *           which causes the router to invoke the function again and so on.
 *      </li>
 *      <li>
 *          <b>Handlers</b>
 *           Handlers simply accept a nickel as a parameter and do something.
 *      </li>
 *  </ul>
 *
 *  One can specify a  global nickel function/handler or
 *  type function/handler(s) that get invoked only if the incoming nickel is
 *  of the corresponding type.
 *
 *  Type function/handler(s) have precedence over the global function/handler,
 *  and will be invoked instead of the global function/handler if they exist.
 *
 *  The global function/handler will be called if no type function/handler
 *  for the incoming nickel type has been found.
 *
 *  Functions take precedence over handlers, thus an incoming nickel
 *  will be processed by whichever function/handler is found first in this order:
 *
 * <ol>
 *    <li>Type Functions</li>
 *    <li>Global Function</li>
 *    <li>Type Handlers</li>
 *    <li>Global Handler</li>
 * </ol>
 *
 *
 *  If a type function/handler is invoked, the global function/handler will not
 *  be invoked.
 */
public class NickelRouter implements Handler<Message> {
    private static final Logger log = LoggerFactory.getLogger(NickelRouter.class);

    public static int NICKLES_SWALLOWED = 0;

    //Event Bus to bind handler to
    private EventBus eb;
    //Address on the event bus to bind handler to
    private String address;

    //Event bus message consumer for this router
    private MessageConsumer consumer;

    private NickelHandler globalHandler; //Global handler
    private Map<NickelType, NickelHandler > actionMap = new HashMap<>(); //Type handlers

    private NickelFunction globalFunction; //Global function
    private Map<NickelType, NickelFunction> functionMap = new HashMap<>(); //Nickel functions

    public NickelRouter (EventBus eb, String address){
        //Bind to the eventbus on the given address
        this.eb = eb;
        this.address = address;
        consumer = eb.consumer(address, this::handle);

    }

    public NickelRouter function(NickelFunction function){
        this.globalFunction = function;
        return this;
    }

    public NickelRouter typeFunction(NickelType type, NickelFunction function)
    {
        //Prevent infinite function loops
        if(type.equals(NickelType.ERROR) || type.equals(NickelType.RESPONSE)){
            log.error("Function cannot be applied to {} nickels!", type.name());
            return this;
        }

        functionMap.put(type, function);
        return this;
    }

    public NickelRouter handler(NickelHandler handler){
        this.globalHandler = handler;
        return this;
    }

    public NickelRouter typeHandler(NickelType type, NickelHandler handler){
        actionMap.put(type, handler);
        return this;
    }

    public void handle(Message event) {
        //Extract the nickel from the event bus message
        Nickel nickel = (Nickel)event.body();

        //Prevent functions from being applied on either RESPONSE or ERROR nickels
        if(!(nickel.type().equals(NickelType.RESPONSE) ||
        nickel.type().equals(NickelType.ERROR)))
        {
            /* Get the nickel function for this type of nickel.
             * If no type function exists, use the global function if one exists.
             */
            NickelFunction function =
                    functionMap.get(nickel.type()) != null?
                            functionMap.get(nickel.type()):
                            globalFunction;

            /* If an appropriate function has been found, apply it to the incoming nickel,
             * then publish its result on the eventbus.
             */
            if(function != null){
                function.apply(nickel, Nickel.nickelForA(nickel)).onSuccess(
                        nickelback->Nickel.publish(eb, address, nickelback)
                ).onFailure(
                        err->Nickel.publish(eb, address,
                                Nickel.badNickel(nickel).pack(
                                        new JsonObject()
                                        .put("error", err.getMessage())
                                ))
                );
                return;
            }
        }


        /* At this stage, no functions were to be executed on the nickel
         * attempt to handle the nickel by finding an appropriate action.
         */

        /* Get the handler for this type of nickel.
         * If no type handler exists, use the global handler if one exists.
         */
        NickelHandler handler =
                actionMap.get(nickel.type()) != null?
                actionMap.get(nickel.type()):
                globalHandler;

        // If an appropriate handler has been found, pass it the incoming nickel.
        if(handler != null){
            handler.handle(nickel);
            return;
        }

        // No function or handler for this nickel, increment swallow counter to aide debugging
        NICKLES_SWALLOWED++;
        return;
    }

    /** Clean up all functions and handlers,
     *  then unregister event bus consumer and
     *  remove event bus reference.
     */
    public void destroy(){
        globalFunction = null;
        globalHandler = null;
        functionMap = null;
        actionMap = null;
        if(consumer != null){
            consumer.rxUnregister().subscribe(
                    ()->{
                        consumer = null;
                        eb = null;
                        log.info("Nickel router destroyed");
                    }
            );
        }
    }
}
