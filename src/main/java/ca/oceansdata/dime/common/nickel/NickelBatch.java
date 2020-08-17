package ca.oceansdata.dime.common.nickel;

import java.util.*;
import java.util.function.Consumer;

/** A utility class for sending a batch of nickels.
 *  Wraps a map of addresses and the nickels to be sent to them.
 */
public class NickelBatch implements Iterable<Map.Entry<String,List<Nickel>>> {

    private Map<String, List<Nickel>> batch = new LinkedHashMap<>();

    /** Add a nickel to the batch
     *
     * @param address the destination address
     * @param nickel the nickel
     * @return the updated nickel batch
     */
    public NickelBatch add(String address, Nickel nickel){
        //Get the nickel list or create it if it doesn't exist
        List<Nickel> nickels = batch.get(address) == null?new ArrayList<>():batch.get(address);

        //Add the new nickle to the list
        nickels.add(nickel);

        //Update the batch
        batch.put(address, nickels);
        return this;
    }


    @Override
    public Iterator<Map.Entry<String, List<Nickel>>> iterator() {
        return batch.entrySet().iterator();
    }

    @Override
    public void forEach(Consumer<? super Map.Entry<String, List<Nickel>>> action) {
        batch.entrySet().forEach(action);
    }

    @Override
    public Spliterator<Map.Entry<String, List<Nickel>>> spliterator() {
        return batch.entrySet().spliterator();
    }
}
