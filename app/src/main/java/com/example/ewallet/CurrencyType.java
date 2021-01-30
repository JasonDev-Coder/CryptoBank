package com.example.ewallet;

import android.graphics.Bitmap;
import android.media.Image;
import android.widget.ImageView;

public class CurrencyType {
    private String CurrencyName;
    private String CurrencySymbol;
    private int id;
    private Bitmap image;
    public CurrencyType(String currencyName, String currencySymbol,int id) {
        CurrencyName = currencyName;
        CurrencySymbol = currencySymbol;
        this.id=id;
    }

    public CurrencyType(String currencyName, String currencySymbol, int id, Bitmap image) {
        CurrencyName = currencyName;
        CurrencySymbol = currencySymbol;
        this.id=id;
        this.image=image;
    }


    public String getCurrencyName() {
        return CurrencyName;
    }

    public void setCurrencyName(String currencyName) {
        CurrencyName = currencyName;
    }

    public String getCurrencySymbol() {
        return CurrencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        CurrencySymbol = currencySymbol;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getImage() {
        return image;
    }
    public void setImage(Bitmap image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "CurrencyType{" +
                "CurrencyName='" + CurrencyName + '\'' +
                ", CurrencySymbol='" + CurrencySymbol + '\'' +
                ", id=" + id +
                '}';
    }
}
