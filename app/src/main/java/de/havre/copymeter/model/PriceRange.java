package de.havre.copymeter.model;

import com.google.gson.annotations.SerializedName;

public class PriceRange implements GsonSerializable {

    @SerializedName("bottom")
    private int bottom = 0;

    @SerializedName("top")
    private int top = 0;

    @SerializedName("price")
    private int price = 0;

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PriceRange that = (PriceRange) o;

        if (bottom != that.bottom) return false;
        if (price != that.price) return false;
        if (top != that.top) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = bottom;
        result = 31 * result + top;
        result = 31 * result + price;
        return result;
    }
}
