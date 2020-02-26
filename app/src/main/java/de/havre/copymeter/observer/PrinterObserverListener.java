package de.havre.copymeter.observer;


import de.havre.copymeter.model.Printer;

public interface PrinterObserverListener {
    public void copyCounterNotification(Printer copyCounter);
}
