package de.havre.copymeter.service;

/**
 * Exception in case of an SNMP Error
 */
public class NoMatchingOidException extends Exception {

    public NoMatchingOidException(String message)
    {
        super(message);
    }

}
