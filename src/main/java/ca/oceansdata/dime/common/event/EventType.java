package ca.oceansdata.dime.common.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum EventType {
    LOGIN("Login"),
    LOGOUT("Logout"),
    DOCUMENT_UPLOAD ("Document Upload"),
    ATTRIBUTE_MAPPING("Attribute Mapping"),
    DOWNLOAD_INTEGRATED_FILE ("Download Integrated File"),
    DOWNLOAD_ORIGINAL_FILE ("Download Original File"),
    COMMUNITY_TASK_CREATED ("Community Task Created"),
    COMMUNITY_TASK_MATCH ("Community Task Match"),
    COMMUNITY_TASK_SKIP ("Community Task Skip"),
    COMMUNITY_TASK_FEEDBACK ("Community Task Feedback"),
    COMMUNITY_MATCH_RESULT ("Community Match Result"),
    PROFILE_UPDATE ("Profile Update"),
    DOWNLOAD_DIME_TOOLS_FOR_WINDOWS ("Download DIME Tools for Windows"),
    UPDATE_METADATA_FIELD("Updated Metadata Field");

    public static Map<String,EventType> typeMap;
    static {
        typeMap = new HashMap<>();
        typeMap.put(LOGIN.getText(), LOGIN);
        typeMap.put(LOGOUT.getText(), LOGOUT);
        typeMap.put(DOCUMENT_UPLOAD.getText(),DOCUMENT_UPLOAD);
        typeMap.put(ATTRIBUTE_MAPPING.getText(),ATTRIBUTE_MAPPING);
        typeMap.put(DOWNLOAD_INTEGRATED_FILE.getText(), DOWNLOAD_INTEGRATED_FILE);
        typeMap.put(DOWNLOAD_ORIGINAL_FILE.getText(), DOWNLOAD_ORIGINAL_FILE);
        typeMap.put(COMMUNITY_TASK_MATCH.getText(), COMMUNITY_TASK_MATCH);
        typeMap.put(COMMUNITY_TASK_SKIP.getText(), COMMUNITY_TASK_SKIP);
        typeMap.put(COMMUNITY_TASK_FEEDBACK.getText(), COMMUNITY_TASK_FEEDBACK);
        typeMap.put(COMMUNITY_MATCH_RESULT.getText(), COMMUNITY_MATCH_RESULT);
        typeMap.put(PROFILE_UPDATE.getText(), PROFILE_UPDATE);
        typeMap.put(DOWNLOAD_DIME_TOOLS_FOR_WINDOWS.getText(), DOWNLOAD_DIME_TOOLS_FOR_WINDOWS);
        typeMap.put(COMMUNITY_TASK_CREATED.getText(),COMMUNITY_TASK_CREATED);
        typeMap.put(UPDATE_METADATA_FIELD.getText(), UPDATE_METADATA_FIELD);
    }

    private String text;

    private EventType(String text){
        this.text = text;
    }

    public String getText(){
        return this.text;
    }

    public static EventType getType(String text){
        if(typeMap.get(text) == null){
            throw new RuntimeException(String.format("There is no event type for (%s)", text));
        }
        return typeMap.get(text);
    }
}
