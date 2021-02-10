package com.example.ewallet;

import android.graphics.Bitmap;

// a plain java class that will be used to load cryptocurrencies data from the db into objects of this type
public class CurrencyType {
    private String CurrencyName;//nmae
    private String CurrencySymbol;//symbol
    private int id;
    private Bitmap image;//image of the crypto
    private String description;//description of the crypto
    public CurrencyType(String currencyName, String currencySymbol,int id) {
        CurrencyName = currencyName;
        CurrencySymbol = currencySymbol;
        this.id=id;
    }

    public CurrencyType(String currencyName, String currencySymbol, int id, Bitmap image,String description) {
        CurrencyName = currencyName;
        CurrencySymbol = currencySymbol;
        this.id=id;
        this.image=image;
        this.description=description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
