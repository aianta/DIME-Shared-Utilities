package ca.oceansdata.dime.common.validation;

import ca.oceansdata.dime.common.exceptions.MissingMetadataFields;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishingValidationUtility {
    private static final Logger log = LoggerFactory.getLogger(PublishingValidationUtility.class);


    /** Verifies that the fields provided are sufficient to publish an article on figshare.
     *  If they are not throws a missing metadata fields exception.
     * @param fields metadata fields to check
     * @return true if the fields are sufficient to publish to figshare
     */
    private boolean isFigsharePublishReady(JsonArray fields) throws MissingMetadataFields {
        

            log.info("Checking fields!");

            boolean hasTitle = false;
            boolean hasAuthors = false;
            boolean hasCategories = false;
            boolean hasItemType = false;
            boolean hasKeywords = false;
            boolean hasDescription = false;
            boolean hasLicense = false;

            for(Object o : fields) {
                JsonObject json = (JsonObject) o;
                JsonObject key = json.getJsonObject("key");
                JsonObject keyParent = key.getJsonObject("parent");

                log.info("inspecting: {}", json.encodePrettily());

                //TODO - change this to check for uuids once we have persistence for metadata fields
                if(keyParent != null && keyParent.getString("name").equals("Figshare Publish")){

                    switch (key.getString("name")){
                        case "Title":
                            hasTitle = true;
                            break;
                        case "Authors":
                            hasAuthors = true;
                            break;
                        case "Categories":
                            hasCategories = true;
                            break;
                        case "Item Type":
                            hasItemType = true;
                            break;
                        case "Keywords":
                            hasKeywords = true;
                            break;
                        case "Description":
                            hasDescription = true;
                            break;
                        case "License":
                            hasLicense = true;
                            break;
                    }
                }
            }

            if(hasTitle &&
                    hasAuthors &&
                    hasCategories &&
                    hasItemType &&
                    hasKeywords &&
                    hasDescription &&
                    hasLicense
            ){
                log.info("Document ready to publish on figshare!");
                return true;
            }else{

                log.info("Document not ready to publish on figshare!");

                JsonArray missingFields = new JsonArray();

                if(!hasTitle){
                    missingFields.add("Title");
                }

                if(!hasAuthors){
                    missingFields.add("Authors");
                }

                if(!hasCategories){
                    missingFields.add("Categories");
                }

                if(!hasItemType){
                    missingFields.add("Item Type");
                }

                if(!hasKeywords){
                    missingFields.add("Key Words");
                }

                if(!hasDescription){
                    missingFields.add("Description");
                }

                if(!hasLicense){
                    missingFields.add("License");
                }

                throw new MissingMetadataFields(missingFields);

            }

    }
}
