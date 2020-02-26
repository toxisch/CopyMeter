package de.havre.copymeter.model;

import com.google.gson.annotations.SerializedName;

import java.util.*;

public class TallyConfig implements GsonSerializable {

    @SerializedName("printerList")
    private List<Printer> printerList = new ArrayList<Printer>();

    @SerializedName("priceTableList")
    private Map<String, PriceTable> priceTableList = new HashMap<String, PriceTable>();

    @SerializedName("modelCache")
    private Map<String, List<String>> modelCache = new LinkedHashMap<String, List<String>>();

    public List<String> getModelCache(String printerId) {
        return modelCache.get(printerId);
    }

    public void putModelCache(String printerId, List<String> model) {
        modelCache.put(printerId, model);
    }

    public void resetModelCache()
    {
        modelCache = new LinkedHashMap<String, List<String>>();
    }

    public void deleteModelCache(String printerId) {
        modelCache.remove(printerId);
    }

    public Map<String, PriceTable> getPriceTableList() {
        return priceTableList;
    }

    public List<Printer> getPrinterList() {
        return printerList;
    }

    public void addPrinter(Printer printer) {
        deletePrinter(printer.getId());
        printerList.add(printer);
    }

    public void deletePrinter(String printerId) {
        try {
            Printer printer = getPrinter(printerId);
            if (printer != null) {
                printerList.remove(getPrinter(printerId));
            }
        } catch (RuntimeException e) {
        }
    }

    public Printer getPrinter(String printerId) {
        for (Printer printer : printerList) {
            if (printer.getId().equals(printerId)) {
                return printer;
            }
        }
        throw new RuntimeException("Printer with id '" + printerId + "' does not exist");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TallyConfig)) return false;

        TallyConfig that = (TallyConfig) o;

        if (modelCache != null ? !modelCache.equals(that.modelCache) : that.modelCache != null) return false;
        if (priceTableList != null ? !priceTableList.equals(that.priceTableList) : that.priceTableList != null)
            return false;
        if (printerList != null ? !printerList.equals(that.printerList) : that.printerList != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = printerList != null ? printerList.hashCode() : 0;
        result = 31 * result + (priceTableList != null ? priceTableList.hashCode() : 0);
        result = 31 * result + (modelCache != null ? modelCache.hashCode() : 0);
        return result;
    }
}
