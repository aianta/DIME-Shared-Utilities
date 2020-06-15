package ca.oceansdata.dime.common.nickel.codec;

import ca.oceansdata.dime.common.nickel.Nickel;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class NickelCodec implements MessageCodec<Nickel, Nickel> {
    @Override
    public void encodeToWire(Buffer buffer, Nickel nickel) {

    }

    @Override
    public Nickel decodeFromWire(int pos, Buffer buffer) {
        return null;
    }

    @Override
    public Nickel transform(Nickel nickel) {
        return null;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
