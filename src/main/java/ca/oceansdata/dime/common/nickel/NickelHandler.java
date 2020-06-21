package ca.oceansdata.dime.common.nickel;


import io.vertx.core.Handler;

public interface NickelHandler extends Handler<Nickel> {

    void handle(Nickel nickel);
}
