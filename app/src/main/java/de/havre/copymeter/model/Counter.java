package de.havre.copymeter.model;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class Counter implements GsonSerializable {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("oid")
    private String oid;

    @SerializedName("regex")
    private String regex;

    @SerializedName("lastCounts")
    private int lastCounts;

    @SerializedName("priceTableId")
    private String priceTableId;

    @SerializedName("currentCounts")
    private int currentCounts = 0;

    @SerializedName("currentCountsSnapshot")
    private int currentCountsSnapshot = 0;

    @SerializedName("offsetCounts")
    private int offsetCounts = 0;

    @SerializedName("counterType")
    private CounterType counterType;

    public Counter(String name, String oid, String priceTableId) {
        this();
        this.oid = oid;
        this.name = name;
        this.priceTableId = priceTableId;
    }

    public Counter(String name, String dataSource, String priceTableId, CounterType counterType) {
        this();
        switch (counterType)
        {
            case OID: this.oid = dataSource; break;
            case REGEX: this.regex = dataSource; break;
        }
        this.name = name;
        this.priceTableId = priceTableId;
    }

    public Counter() {
        id = UUID.randomUUID().toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public int getLastCounts() {
        return lastCounts;
    }

    public void setLastCounts(int lastCounts) {
        this.lastCounts = lastCounts;
    }

    public String getPriceTableId() {
        return priceTableId;
    }

    public void setPriceTableId(String priceTableId) {
        this.priceTableId = priceTableId;
    }

    public void setCurrentCounts(int currentCounts) {
        this.currentCounts = currentCounts;
    }

    public int calculateRelativeCountes() {
        return currentCounts - offsetCounts;
    }

    public int getCurrentCountsSnapshot() {
        return currentCountsSnapshot;
    }

    public void setCurrentCountsSnapshot(int currentCountsSnapshot) {
        this.currentCountsSnapshot = currentCountsSnapshot;
    }

    public int getOffsetCounts() {
        return offsetCounts;
    }

    public void setOffsetCounts(int offsetCounts) {
        this.offsetCounts = offsetCounts;
    }

    public void reset() {
        lastCounts = calculateRelativeCountes();
        offsetCounts = currentCounts;
    }

    public void resetToSnapshot() {
        lastCounts = currentCountsSnapshot;
        offsetCounts = currentCounts;
    }

    public void makeSnapshot() {
        currentCountsSnapshot = calculateRelativeCountes();
    }

    public void updateCounter(int counterValue) {
        if (offsetCounts == 0) {
            offsetCounts = counterValue;
        }
        currentCounts = counterValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Counter)) return false;

        Counter counter = (Counter) o;

        if (currentCounts != counter.currentCounts) return false;
        if (currentCountsSnapshot != counter.currentCountsSnapshot) return false;
        if (lastCounts != counter.lastCounts) return false;
        if (offsetCounts != counter.offsetCounts) return false;
        if (id != null ? !id.equals(counter.id) : counter.id != null) return false;
        if (name != null ? !name.equals(counter.name) : counter.name != null) return false;
        if (oid != null ? !oid.equals(counter.oid) : counter.oid != null) return false;
        if (priceTableId != null ? !priceTableId.equals(counter.priceTableId) : counter.priceTableId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (oid != null ? oid.hashCode() : 0);
        result = 31 * result + lastCounts;
        result = 31 * result + (priceTableId != null ? priceTableId.hashCode() : 0);
        result = 31 * result + currentCounts;
        result = 31 * result + currentCountsSnapshot;
        result = 31 * result + offsetCounts;
        return result;
    }
}

