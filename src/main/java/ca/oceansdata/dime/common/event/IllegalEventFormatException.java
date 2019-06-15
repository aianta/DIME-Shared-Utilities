package ca.oceansdata.dime.common.event;

import io.vertx.core.json.JsonObject;

public class IllegalEventFormatException extends Exception {

    JsonObject data = null;
    String key = null;
    String hint = null;

    public IllegalEventFormatException(JsonObject data, String key, String hint){
        this.data = data;
        this.key = key;
        this.hint = hint;
    }

    public IllegalEventFormatException(JsonObject data, String key){
        this.data = data;
        this.key = key;
    }

    public IllegalEventFormatException(JsonObject data){
        this.data = data;
    }

    @Override
    public String getMessage() {

        if(key != null && hint != null){
            return "Illegal format in event JSON @key='"+ key + "' " +
                    "("+hint+")" + data.encodePrettily();
        }

        if(key != null){
            return "Illegal format in event JSON @key='"+ key + "' " + data.encodePrettily();
        }

        return "Illegal format in event JSON! " + data.encodePrettily();
    }

}
