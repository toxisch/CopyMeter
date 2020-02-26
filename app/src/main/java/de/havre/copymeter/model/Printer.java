package de.havre.copymeter.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Printer implements GsonSerializable {

    @SerializedName("id")
    private String id;

    @SerializedName("number")
    private String number;

    @SerializedName("name")
    private String name;

    @SerializedName("ip")
    private String ip;

    @SerializedName("port")
    private String port;

    @SerializedName("regExUrl")
    private String regExUrl;

    @SerializedName("printerState")
    private PrinterState printerState;

    @SerializedName("counterList")
    private List<Counter> counterList;

    public Printer() {
        id = UUID.randomUUID().toString();
        printerState = PrinterState.OFFLINE;
        counterList = new ArrayList<Counter>();
        port = "161";
        regExUrl = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public String getRegExUrl() {
        return regExUrl;
    }

    public void setRegExUrl(String regExUrl) {
        this.regExUrl = regExUrl;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public PrinterState getPrinterState() {
        return printerState;
    }

    public void setPrinterState(PrinterState printerState) {
        this.printerState = printerState;
    }

    public List<Counter> getCounterList() {
        return counterList;
    }

    public Counter getCounter(String counterId) {
        for (Counter counter : counterList) {
            if (counter.getId().equals(counterId)) {
                return counter;
            }
        }
        throw new RuntimeException("Counter with id '" + counterId + "' does not exist");
    }

    public void deleteCounter(String counterId) {
        try {
            Counter counter = this.getCounter(counterId);
            if (counter != null) {
                counterList.remove(counter);
            }
        } catch (RuntimeException e) {
        }
    }

    public void addCounter(Counter counter) {
        if (counter != null) {
            deleteCounter(counter.getId());
            counterList.add(counter);
        }
    }

    public void reset() {
        for (Counter counterModel : counterList) {
            counterModel.reset();
        }
    }

    public void resetToSnapshot() {
        for (Counter counterModel : counterList) {
            counterModel.resetToSnapshot();
        }
    }

    public void makeSnapshot() {
        for (Counter counterModel : counterList) {
            counterModel.makeSnapshot();
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Printer)) return false;

        Printer printer = (Printer) o;

        if (counterList != null ? !counterList.equals(printer.counterList) : printer.counterList != null) return false;
        if (id != null ? !id.equals(printer.id) : printer.id != null) return false;
        if (ip != null ? !ip.equals(printer.ip) : printer.ip != null) return false;
        if (name != null ? !name.equals(printer.name) : printer.name != null) return false;
        if (number != null ? !number.equals(printer.number) : printer.number != null) return false;
        if (port != null ? !port.equals(printer.port) : printer.port != null) return false;
        if (printerState != printer.printerState) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (ip != null ? ip.hashCode() : 0);
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (printerState != null ? printerState.hashCode() : 0);
        result = 31 * result + (counterList != null ? counterList.hashCode() : 0);
        return result;
    }
}
