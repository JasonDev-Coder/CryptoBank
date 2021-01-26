package com.example.ewallet;

public class CurrencyType {
    private String CurrencyName;
    private String CurrencySymbol;
    private int id;
    public CurrencyType(String currencyName, String currencySymbol,int id) {
        CurrencyName = currencyName;
        CurrencySymbol = currencySymbol;
        this.id=id;
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

    @Override
    public String toString() {
        return "CurrencyType{" +
                "CurrencyName='" + CurrencyName + '\'' +
                ", CurrencySymbol='" + CurrencySymbol + '\'' +
                ", id=" + id +
                '}';
    }
}
