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

    /** Verifies that the fields provided are sufficient to publish a dataset on dataverse. If they're not
     *  throws a missing metadata fields exception.
     *
     * @param fields
     * @return
     * @throws MissingMetadataFields
     */
    public static boolean isDataversePublishReady(JsonArray fields) throws MissingMetadataFields{

        log.info("Checking metadata fields for dataverse publishing.");

        boolean hasTitle = false;
        boolean hasAuthor = false;
        boolean hasContact = false;
        boolean hasSubject = false;
        boolean hasDescription = false;

        for(Object o: fields){
            JsonObject json = (JsonObject)o;
            JsonObject key = json.getJsonObject("key");
            JsonObject keyParent = key.getJsonObject("parent");
            JsonObject value = json.getJsonObject("value");

            if(keyParent != null && keyParent.getString("name").equals("Dataverse Minimum Required")){
                switch (key.getString("name")){
                    case "title":
                        hasTitle = value != null;
                        break;
                    case "dsDescriptionValue":
                        hasDescription = value != null;
                        break;
                    case "subject":
                        hasSubject = value != null;
                        break;
                    case "authorName":
                        hasAuthor = value != null;
                        break;
                    case "datasetContactEmail":
                        hasContact = value != null;
                        break;
                }


            }
        }

        if(hasTitle && hasAuthor && hasContact && hasDescription && hasSubject){
            log.info("Document is ready to publish to dataverse!");
            return true;
        }else{
            log.info("Document not ready to publish to dataverse!");

            //Assemble list of missing metadata fields
            JsonArray missingFields = new JsonArray();

            if(!hasTitle){
                missingFields.add("title");
            }

            if(!hasAuthor){
                missingFields.add("authorName");
            }

            if(!hasSubject){
                missingFields.add("subject");
            }

            if(!hasContact){
                missingFields.add("datasetContactEmail");
            }

            if(!hasDescription){
                missingFields.add("dsDescriptionValue");
            }

            throw new MissingMetadataFields(missingFields);
        }

    }

    /** Verifies that the fields provided are sufficient to publish an article on figshare.
     *  If they are not throws a missing metadata fields exception.
     * @param fields metadata fields to check
     * @return true if the fields are sufficient to publish to figshare
     */
    public static boolean isFigsharePublishReady(JsonArray fields) throws MissingMetadataFields {


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
                JsonObject value = json.getJsonObject("value");

                log.info("inspecting: {}", json.encodePrettily());

                //TODO - change this to check for uuids once we have persistence for metadata fields
                if(keyParent != null && keyParent.getString("name").equals("Figshare Publish")){

                    switch (key.getString("name")){
                        case "Title":
                            hasTitle = value != null;
                            break;
                        case "Authors":
                            hasAuthors = value != null;
                            break;
                        case "Categories":
                            hasCategories = value != null;
                            break;
                        case "Item Type":
                            hasItemType = value != null;
                            break;
                        case "Keywords":
                            hasKeywords = value != null;
                            break;
                        case "Description":
                            hasDescription = value != null;
                            break;
                        case "License":
                            hasLicense = value != null;
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
