package org.agilelovers.server.common.errors;

public class NoEmailFound extends RuntimeException{

    public NoEmailFound(String ID) { super("No Email by this ID" + ID);}

}
