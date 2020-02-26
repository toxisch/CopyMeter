package de.havre.copymeter.client;

/**
 * Exception in case of an SNMP Error
 */
public class SnmpClientException extends Exception {

    public SnmpClientException(String message)
    {
        super(message);
    }

}
