package org.agilelovers.server.common.errors;

public class NoEmailConfigured extends RuntimeException{

    public NoEmailConfigured(){ super("Email configuration needs to be setup first"); }
}
