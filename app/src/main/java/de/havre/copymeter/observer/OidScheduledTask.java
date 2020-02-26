package de.havre.copymeter.observer;

import de.havre.copymeter.client.SnmpClient;
import de.havre.copymeter.client.SnmpClientFactory;
import de.havre.copymeter.model.Counter;
import de.havre.copymeter.model.Printer;
import de.havre.copymeter.model.PrinterState;
import roboguice.util.Ln;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by alex on 16.06.14.
 */
public class OidScheduledTask implements Runnable {

    private SnmpClient snmpClient;

    private Printer printer;

    private PrinterObserverListener printerObserverListener;

    public OidScheduledTask(Printer printer, PrinterObserverListener printerObserverListener) {
        this.snmpClient = SnmpClientFactory.createClient(printer.getIp(), printer.getPort());
        this.printer = printer;
        this.printerObserverListener = printerObserverListener;
    }

    @Override
    public void run() {
        try {
            List<String> oidList = getOidList();
            Map<String, Integer> oidValues = snmpClient.getOidValues(oidList);
            updateModel(oidValues);
            printer.setPrinterState(PrinterState.ONLINE);

        } catch (Throwable e) {
            Ln.d("IO SNMP Exception: ", e);
            printer.setPrinterState(PrinterState.OFFLINE);
        }
        printerObserverListener.copyCounterNotification(printer);
    }

    private void updateModel(Map<String, Integer> oidValues) {
        for (Counter counter : printer.getCounterList()) {
            Integer value = oidValues.get(counter.getOid());
            Counter counterModel = printer.getCounter(counter.getId());
            if (value != null) {
                counterModel.updateCounter(value);
            }
            else
            {
                Ln.e("The oid '"+ counter.getOid()+"' response was null");
                printer.setPrinterState(PrinterState.OFFLINE);

            }
        }
    }

    private List<String> getOidList() {
        ArrayList<String> oidList = new ArrayList<String>();
        for (Counter counter : printer.getCounterList()) {
            if (counter.getOid() != null) {
                oidList.add(counter.getOid());
            }
        }
        return oidList;
    }


}
