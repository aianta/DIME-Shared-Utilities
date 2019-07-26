package ca.oceansdata.dime.common.testing;

import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.Vertx;

public class DimeTestUtils {


    /**Returns a config retriever for the specified json configuration file path.
     * @Author Alexandru Ianta
     * @param vertx the vertx instance with which to create the config retriever
     * @param path the path to the configuration file (in JSON format)
     * @return
     */
    public static ConfigRetriever createConfigRetriever(Vertx vertx, String path){

        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setConfig(new JsonObject().put("path", path));

        ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(fileStore);

        return ConfigRetriever.create(vertx, options);

    }

}
