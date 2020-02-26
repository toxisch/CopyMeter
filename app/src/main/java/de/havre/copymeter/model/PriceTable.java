package de.havre.copymeter.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PriceTable implements GsonSerializable {

    @SerializedName("id")
    public String id;

    @SerializedName("priceRangeList")
    public List<PriceRange> priceRangeList = new ArrayList<PriceRange>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<PriceRange> getPriceRangeList() {
        return priceRangeList;
    }

    public void setPriceRangeList(List<PriceRange> priceRangeList) {
        this.priceRangeList = priceRangeList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PriceTable)) return false;

        PriceTable that = (PriceTable) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (priceRangeList != null ? !priceRangeList.equals(that.priceRangeList) : that.priceRangeList != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (priceRangeList != null ? priceRangeList.hashCode() : 0);
        return result;
    }
}
