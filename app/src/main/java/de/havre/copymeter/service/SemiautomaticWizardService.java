package de.havre.copymeter.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.havre.copymeter.client.SnmpClient;
import de.havre.copymeter.client.SnmpClientException;
import de.havre.copymeter.client.SnmpClientFactory;
import de.havre.copymeter.model.Printer;
import de.havre.copymeter.persitence.ConfigService;
import roboguice.util.Ln;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The wizard provides function for a semiautomatic configuration of a counter.
 */
@Singleton
public class SemiautomaticWizardService {

    // http://www.ietf.org/rfc/rfc1759.txt
    //public static final String PRINTER_MIB = "1.3.6.1.2.1.43";

    public static final String BASE_OID = "1.3.6.1.4.1"; // company mib

    private Map<String, Map<String, Integer>> cache = new HashMap<String, Map<String, Integer>>();

    @Inject
    ConfigService configService;

    public void cleanCache()
    {
        cache.clear();
    }

    public List<String> resolveCounterOid(Integer currentCounterValue, String printerId, List<String> oids) throws NoMatchingOidException, SnmpClientException {
        SnmpClient snmpClient = getClient(printerId);
        Map<String, Integer> dataModel;
        if (oids == null)
        {
            dataModel = getCurrentModelState(snmpClient, printerId);

        }
        else
        {
            dataModel = snmpClient.getOidValues(oids);
        }

        List<String> result = findMatchingOids(currentCounterValue, dataModel);
        if (result.size() == 0) {
            String message = "There is no OID which matches the counter value = '" + currentCounterValue + "'";
            throw new NoMatchingOidException(message);
        }
        return result;
    }

    public Map<String, Integer> findCounters(Map<String, Integer> lookup, String printerId) throws SnmpClientException {
        SnmpClient snmpClient = getClient(printerId);
        if (lookup == null)
        {
            Map<String, Integer> oidModel = getCurrentModelState(snmpClient, printerId);
            return oidModel;
        }
        Map<String, Integer> result = new HashMap<String, Integer>();
        Map<String, Integer> newValues = getCurrentModelState(snmpClient, printerId);
        for (String oid : newValues.keySet())
        {
           Integer newValue = newValues.get(oid);
           Integer oldValue = lookup.get(oid);
           if (newValue != null && newValue == oldValue + 1)
           {
               result.put(oid, newValue);
           }
        }
        return result;
    }

    private  Map<String, Integer> getCurrentModelState(SnmpClient snmpClient, String printerId) throws SnmpClientException
    {
        List<String> PrinterOidModel = configService.getTallyConfig().getModelCache(printerId);
        if (PrinterOidModel == null)
        {
            Map<String, Integer> stringIntegerMap = snmpClient.getOidModel(BASE_OID);
            Ln.d(stringIntegerMap);
            PrinterOidModel = new ArrayList<String>(stringIntegerMap.keySet());
            configService.getTallyConfig().putModelCache(printerId, PrinterOidModel);
            return stringIntegerMap;
        }
        Map<String, Integer> newValues = snmpClient.getOidValues(PrinterOidModel);
        return newValues;
    }

    private boolean valueChanged(Map<String, Integer> initalValues, Map<String, Integer> oidValues) {

        for (String key : initalValues.keySet())
        {
            if (!initalValues.get(key).equals(oidValues.get(key)))
            {
                return true;
            }
        }
        return false;
    }

    private List<String> findMatchingOids(Integer currentCounterValue, Map<String, Integer> oidModel) {
        List<String> result = new ArrayList<String>();
        for (String key : oidModel.keySet()) {
            if (currentCounterValue.equals(oidModel.get(key))) {
                result.add(key);
            }
        }
        return result;
    }

    private SnmpClient getClient(String printerId)
    {
        Printer printerConfig = configService.getTallyConfig().getPrinter(printerId);
        return SnmpClientFactory.createClient(printerConfig.getIp(),printerConfig.getPort());
    }
}
