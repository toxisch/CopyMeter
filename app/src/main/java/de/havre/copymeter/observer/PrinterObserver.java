package de.havre.copymeter.observer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.havre.copymeter.model.Printer;
import de.havre.copymeter.model.TallyConfig;
import de.havre.copymeter.persitence.ConfigService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

@Singleton
public class PrinterObserver {

    @Inject
    private ConfigService configService;

    private Map<Printer, ScheduledFuture> scheduledFutureMap = new HashMap<Printer, ScheduledFuture>();

    private ScheduledExecutorService getService()
    {
        int size = configService.getTallyConfig().getPrinterList().size();
        return Executors.newScheduledThreadPool(size + 1);
    }

    public void removeAll() {
        for (ScheduledFuture scheduledFuture : scheduledFutureMap.values()) {
            scheduledFuture.cancel(true);
        }
        scheduledFutureMap.clear();
    }

    public void removePrinter(Printer printer) {
        ScheduledFuture scheduledFuture = scheduledFutureMap.get(printer);
        scheduledFuture.cancel(true);
        scheduledFutureMap.remove(printer);
    }

    public void registerPrinter(PrinterObserverListener printerObserverListener) {

        TallyConfig tallyConfig = configService.getTallyConfig();
        List<Printer> printerList = tallyConfig.getPrinterList();

        for (final Printer printer : printerList) {
            registerPrinter(printer,  printerObserverListener);
        }
    }

    private void registerPrinter(Printer printer, PrinterObserverListener printerObserverListener) {

        Runnable observer = null;
        if (printer.getRegExUrl() != null && printer.getRegExUrl() != "") {
            observer = new RegexScheduledTask(printer, printerObserverListener);
        } else {
            observer = new OidScheduledTask(printer, printerObserverListener);
        }

        final ScheduledFuture handler =
                getService().scheduleAtFixedRate(observer, 2, 2, SECONDS);
        scheduledFutureMap.put(printer, handler);
    }
}