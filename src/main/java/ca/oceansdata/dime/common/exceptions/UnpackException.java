package ca.oceansdata.dime.common.exceptions;

import ca.oceansdata.dime.common.nickel.Nickel;

public class UnpackException extends Exception {

    Class c;
    Nickel nickel;

    public UnpackException(Nickel n, Class c){
        this.c = c;
        this.nickel = n;
    }

    public String getMessage(){
        return "Could not unpack " + c.getSimpleName() + " out of nickel!";
    }

    public Nickel getNickel(){
        return nickel;
    }

}
