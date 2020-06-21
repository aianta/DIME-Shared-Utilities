package ca.oceansdata.dime.common.nickel;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface NickelFunction extends BiFunction<Nickel,Nickel,Nickel> {

    /** A method that produces a nickel upon being applied on a nickel.
     *
     *  For convenience, a response nickel is passed as a second parameter
     *  which can be easily customized and returned.
     *
     * @param in input nickel (received off the eventbus for example)
     * @param out a response nickel pre-generated from the nickel input for convenience
     * @return the resulting nickel
     */
    @Override
    Nickel apply(Nickel in, Nickel out);
}
