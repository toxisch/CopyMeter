package de.havre.copymeter.client;

import roboguice.util.Ln;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alex on 18.11.14.
 */
public class DummySnmpClient implements SnmpClient{

    private static Map<String, Integer> model = null;

    public DummySnmpClient()
    {
        if (model == null)
        {
            model = new HashMap<String, Integer>();
            model.put("1.1.1.1.1", new Integer(10000));
            model.put("1.1.1.1.2", new Integer(10000));
            model.put("1.1.1.1.3", new Integer(10001));
            model.put("1.1.1.1.4", new Integer(666));
            model.put("1.1.1.1.5", new Integer(7777));
            model.put("1.1.1.1.6", new Integer(6666));
            model.put("1.1.1.1.7", new Integer(5555));
            model.put("1.1.1.1.8", new Integer(4444));
            model.put("1.1.1.1.9", new Integer(3333));
            model.put("1.3.6.1.2.1.43.10.2.1.4.1.1", new Integer(1));
        }

    }

    private void increment(String oid)
    {
        Integer v = model.get(oid);
        Integer n = new Integer(v+1);
        Ln.i("NEW COUNTER VALUE " + oid + " : " + n);
        model.put(oid,n);
    }

    @Override
    public Map<String, Integer> getOidValues(List<String> oidList) throws SnmpClientException {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        increment("1.3.6.1.2.1.43.10.2.1.4.1.1");
        return cloneer(model);
    }

    private Map<String, Integer> cloneer(Map<String, Integer> model) {
        Map<String, Integer> result = new HashMap<String, Integer>();
        for (String oid : model.keySet())
        {
            result.put(oid, new Integer(model.get(oid)));
        }
        return result;
    }

    @Override
    public Map<String, Integer> getOidModel(String baseOid) throws SnmpClientException {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return cloneer(model);
    }
}
