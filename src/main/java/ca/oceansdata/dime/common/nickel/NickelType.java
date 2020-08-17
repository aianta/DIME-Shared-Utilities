package ca.oceansdata.dime.common.nickel;

import io.vertx.core.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* Defines the kinds of service bound nickels
 *
 */
public enum NickelType {
    // Service-Bound Nickels
    //HTTP Methods
    GET,
    POST,
    PUT,
    DELETE,
    //Custom Service Actions
    ALL,
    MAP,
    RETRIEVE,
    NEW_METADATA,
    CREATE,
    UPDATE,
    SCHEMA_CREATED,
    SAVE,
    NOTIFICATIONS,
    ACTIVE,
    NEW_DOCUMENT,
    ANNOUNCE,
    CREATE_TASK,
    CREATE_TASKS,
    // Client-Bound Nickels
    ERROR,
    TIMEOUT,
    RESPONSE;

    private static final Logger log = LoggerFactory.getLogger(NickelType.class);

    public static NickelType fromHttpMethod(HttpMethod method){
        switch (method){
            case GET:
                return NickelType.GET;
            case PUT:
                return NickelType.PUT;
            case POST:
                return NickelType.POST;
            case DELETE:
                return NickelType.DELETE;
            default:
                log.error("Http Method {} has no equivalent NickelType!", method.name());
                return null;
        }
    }
}
