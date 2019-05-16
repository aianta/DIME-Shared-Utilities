package ca.oceansdata.dime.common.event;

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

    private String text;

    private EventType(String text){
        this.text = text;
    }

    public String getText(){
        return this.text;
    }
}
