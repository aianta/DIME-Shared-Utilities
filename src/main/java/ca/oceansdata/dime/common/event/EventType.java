package ca.oceansdata.dime.common.event;


import java.util.HashMap;
import java.util.Map;

public enum EventType {
    LOGIN("Login", false),
    LOGOUT("Logout", false),
    DOCUMENT_UPLOAD ("Document Upload", true),
    ATTRIBUTE_MAPPING("Attribute Mapping", true),
    DOWNLOAD_INTEGRATED_FILE ("Download Integrated File", true),
    DOWNLOAD_ORIGINAL_FILE ("Download Original File",false),
    COMMUNITY_TASK_MATCH ("Community Task Match",false),
    COMMUNITY_MATCH_RESULT ("Community Match Result", true),
    PROFILE_UPDATE ("Profile Update", true),
    DOWNLOAD_DIME_TOOLS_FOR_WINDOWS ("Download DIME Tools for Windows", false),
    UPDATE_METADATA_FIELD("Updated Metadata Field", true),
    TASK_DISMISSED("Task Dismissed",false),
    TASK_SKIPPED("Task Skipped", false),
    TASK_UPDATED("Task Updated", true),
    TASK_CREATED("Task Created", true);

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
        typeMap.put(COMMUNITY_MATCH_RESULT.getText(), COMMUNITY_MATCH_RESULT);
        typeMap.put(PROFILE_UPDATE.getText(), PROFILE_UPDATE);
        typeMap.put(DOWNLOAD_DIME_TOOLS_FOR_WINDOWS.getText(), DOWNLOAD_DIME_TOOLS_FOR_WINDOWS);
        typeMap.put(UPDATE_METADATA_FIELD.getText(), UPDATE_METADATA_FIELD);
        typeMap.put(TASK_DISMISSED.getText(), TASK_DISMISSED);
        typeMap.put(TASK_CREATED.getText(), TASK_CREATED);
        typeMap.put(TASK_UPDATED.getText(), TASK_UPDATED);
        typeMap.put(TASK_SKIPPED.getText(), TASK_SKIPPED);
    }

    private String text;
    private boolean userVisible;

    EventType(String text, boolean userVisible){
        this.text = text;
        this.userVisible = userVisible;
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

    public boolean isUserVisible(){
        return userVisible;
    }
}
