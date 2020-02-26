package de.havre.copymeter.client;

/**
 * Created by alex on 18.11.14.
 */
public class SnmpClientFactory {

    public static final boolean TEST_MODE = false;

    public static SnmpClient createClient(String ip, String port)
    {
        if (TEST_MODE)
        {
            return new DummySnmpClient();
        }
        RealSnmpClient client = new RealSnmpClient();
        client.ip = ip;
        client.port = port;
        return client;
    }
}
