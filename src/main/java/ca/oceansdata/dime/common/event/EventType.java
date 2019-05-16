package ca.oceansdata.dime.common.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum EventType {
    LOGIN("Login"),
    LOGOUT("Logout"),
    DOCUMENT_UPLOAD ("Document Upload"),
    MANUAL_ATTRIBUTE_MATCH ("Manual Attribute Match"),
    MANUAL_ATTRIBUTE_MATCH_EDIT ("Manual Attribute Match Edit"),
    DOWNLOAD_INTEGRATED_FILE ("Download Integrated File"),
    DOWNLOAD_ORIGINAL_FILE ("Download Original File"),
    COMMUNITY_TASK_MATCH ("Community Task Match"),
    COMMUNITY_TASK_SKIP ("Community Task Skip"),
    COMMUNITY_TASK_FEEDBACK ("Community Task Feedback"),
    COMMUNITY_MATCH_RESULT ("Community Match Result"),
    PROFILE_FIELD_UPDATE ("Profile Field Update"),
    DOWNLOAD_DIME_TOOLS_FOR_WINDOWS ("Download DIME Tools for Windows"),
    EDIT_SCHEMA_NAME("Edit Dataset Name"),
    EDIT_SCHEMA_DESCRIPTION("Edit Dataset Description");

    public static Map<String,EventType> typeMap;
    static {
        typeMap = new HashMap<>();
        typeMap.put(LOGIN.getText(), LOGIN);
        typeMap.put(LOGOUT.getText(), LOGOUT);
        typeMap.put(DOCUMENT_UPLOAD.getText(),DOCUMENT_UPLOAD);
        typeMap.put(MANUAL_ATTRIBUTE_MATCH.getText(),MANUAL_ATTRIBUTE_MATCH);
        typeMap.put(MANUAL_ATTRIBUTE_MATCH_EDIT.getText(), MANUAL_ATTRIBUTE_MATCH_EDIT);
        typeMap.put(DOWNLOAD_INTEGRATED_FILE.getText(), DOWNLOAD_INTEGRATED_FILE);
        typeMap.put(DOWNLOAD_ORIGINAL_FILE.getText(), DOWNLOAD_ORIGINAL_FILE);
        typeMap.put(COMMUNITY_TASK_MATCH.getText(), COMMUNITY_TASK_MATCH);
        typeMap.put(COMMUNITY_TASK_SKIP.getText(), COMMUNITY_TASK_SKIP);
        typeMap.put(COMMUNITY_TASK_FEEDBACK.getText(), COMMUNITY_TASK_FEEDBACK);
        typeMap.put(COMMUNITY_MATCH_RESULT.getText(), COMMUNITY_MATCH_RESULT);
        typeMap.put(PROFILE_FIELD_UPDATE.getText(), PROFILE_FIELD_UPDATE);
        typeMap.put(DOWNLOAD_DIME_TOOLS_FOR_WINDOWS.getText(), DOWNLOAD_DIME_TOOLS_FOR_WINDOWS);
        typeMap.put(EDIT_SCHEMA_NAME.getText(), EDIT_SCHEMA_NAME);
        typeMap.put(EDIT_SCHEMA_DESCRIPTION.getText(), EDIT_SCHEMA_DESCRIPTION);
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
