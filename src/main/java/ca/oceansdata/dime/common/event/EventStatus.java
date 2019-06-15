package ca.oceansdata.dime.common.event;

public enum EventStatus {
    UNREAD("UNREAD"),READ("READ");

    private String text;

    private EventStatus(String text){
        this.text = text;
    }

    public String getText(){
        return this.text;
    }
}