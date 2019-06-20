package ca.oceansdata.dime.common.exceptions;

public class MissingActionException extends Exception {

    public String toString(){
        return "Event bus delivery options missing 'action' header!";
    }
}
