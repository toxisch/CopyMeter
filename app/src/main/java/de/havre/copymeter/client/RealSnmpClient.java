package de.havre.copymeter.client;

import android.util.Log;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;
import roboguice.util.Ln;

import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 27.05.14
 * Time: 19:26
 * To change this template use File | Settings | File Templates.
 */
public class RealSnmpClient implements SnmpClient{

    public static int MAX_PDU = 1000;

    public String ip;

    public String port = "161";

    public Map<String, Integer> getOidValues(List<String> oidList) throws SnmpClientException {

        Map<String, Integer> result = new HashMap<String, Integer>();
        Iterator<String> iterator = oidList.iterator();
        int counter = 0;
        while (iterator.hasNext() )
        {
            List<String> tempList = new ArrayList<String>();
            counter = 0;
            while (iterator.hasNext() && counter < MAX_PDU)
            {
                tempList.add(iterator.next());
                counter++;
            }
            Map<String, Integer> res = getOidValuesInternal(tempList);
            result.putAll(res);
        }
        return result;
    }

    private Map<String, Integer> getOidValuesInternal(List<String> oidList) throws SnmpClientException {

        Map<String, Integer> result = new HashMap<String, Integer>();
        if (oidList.size() == 0)
        {
            return result;
        }
        Snmp snmp = null;
        try {
            Address targetAddress = GenericAddress.parse("udp:" + ip + "/" + port);
            TransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            transport.listen();

            // setting up target
            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString("public"));
            target.setAddress(targetAddress);
            target.setRetries(3);
            target.setTimeout(1000 * 3);
            target.setVersion(SnmpConstants.version2c);

            //OID oid = new OID(oidStr);

            PDU pdu = new PDU();
            for (String oidString : oidList) {
                OID oid = new OID(oidString);
                pdu.add(new VariableBinding(oid));
            }
            pdu.setType(PDU.GET);
            ResponseEvent event = snmp.send(pdu, target, null);

            if (event.getResponse() != null && event.getResponse().getVariableBindings() != null) {
                Vector<? extends VariableBinding> variableBindings = event.getResponse().getVariableBindings();
                for (VariableBinding binding : variableBindings) {
                    if (isNumber(binding)) {
                        result.put(binding.getOid().toString(), new Integer(binding.getVariable().toString()));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            String message = "Unable to resolve OidModel. Reason is: " + e.getMessage();
            Ln.e(e, message);
            throw new SnmpClientException(message);
        } finally {
            if (snmp != null) {
                try {
                    snmp.close();
                } catch (IOException e) {
                    String message = "Unable to close SnmpClient. Reason is: " + e.getMessage();
                    Ln.e(e, message);
                    throw new SnmpClientException(message);
                }
            }
        }
        return result;
    }

    public Map<String, Integer> getOidModel(String baseOid) throws SnmpClientException {

        Snmp snmp = null;
        Map<String, Integer> result = new LinkedHashMap<String, Integer>();
        try {
            //172.100.100.40
            Address targetAddress = GenericAddress.parse("udp:" + ip + "/" + port);
            TransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            transport.listen();

            // setting up target
            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString("public"));
            target.setAddress(targetAddress);
            target.setRetries(3);
            target.setTimeout(1000 * 3);
            target.setVersion(SnmpConstants.version2c);

            OID oid = null;
            try {
                oid = new OID(baseOid);
            } catch (RuntimeException ex) {
                String message = "OID is not specified correctly.";
                Ln.e(message);
                throw new SnmpClientException(message);
            }

            TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
            List<TreeEvent> events = treeUtils.getSubtree(target, oid);
            if (events == null || events.size() == 0) {
                String message = "No result returned.";
                Ln.e(message);
                throw new SnmpClientException(message);
            }

            // Get snmpwalk result.
            for (TreeEvent event : events) {
                if (event != null) {
                    if (event.isError()) {
                        Ln.d("Error: %s = %s", oid, event.getErrorMessage());
                    }

                    VariableBinding[] varBindings = event.getVariableBindings();
                    if (varBindings == null || varBindings.length == 0) {
                        Ln.d("No result returned.");
                    }
                    for (VariableBinding val : varBindings) {
                        System.out.println("String: " + val.getOid() + " " + val.getVariable().toString());


                        if (isString(val)) {
                            Log.v("TAG", "index=");
                            Ln.d("String: %s = %s", val.getOid(), val.getVariable().toString());
                        }
                        if (isObject(val)) {
                            Log.v("TAG", "index=");
                            Ln.d("Object: %s = %s", val.getOid(), val.getVariable().toString());
                        }
                        if (isNumber(val)) {
                            Ln.d("Number: %s = %s", val.getOid(), val.getVariable().toString());
                            Integer oidValue = Integer.valueOf(val.getVariable().toString());
                            if (oidValue > 1)
                            {
                                result.put(val.getOid().toString(), oidValue);
                            }
                        }
                    }
                }
            }
            snmp.close();
        } catch (Exception e) {
            e.printStackTrace();
            String message = "Unable to resolve OidModel. Reason is: " + e.getMessage();
            Ln.e(e, message);
            throw new SnmpClientException(message);
        } finally {
            if (snmp != null) {
                try {
                    snmp.close();
                } catch (IOException e) {
                    String message = "Unable to close SnmpClient. Reason is: " + e.getMessage();
                    Ln.e(e, message);
                    throw new SnmpClientException(message);
                }
            }
        }
        return result;

    }

    private static boolean isNumber(VariableBinding varBinding) {
        if (varBinding != null) {
            if (varBinding.getVariable() instanceof Integer32) return true;
            if (varBinding.getVariable() instanceof Counter32) return true;
            if (varBinding.getVariable() instanceof Counter64) return true;
        }
        return false;
    }

    private static boolean isObject(VariableBinding varBinding) {
        if (varBinding != null) {
            if (varBinding.getVariable() instanceof OID) return true;
        }
        return false;
    }

    private static boolean isString(VariableBinding varBinding) {
        if (varBinding != null) {
            if (varBinding.getVariable() instanceof OctetString) return true;
            if (varBinding.getVariable() instanceof BitString) return true;
        }
        return false;
    }

}
