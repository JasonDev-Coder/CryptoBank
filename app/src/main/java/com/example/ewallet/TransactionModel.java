package com.example.ewallet;

import java.math.BigDecimal;
import java.util.Date;


public class TransactionModel implements Comparable<TransactionModel> {
    enum typeTransaction{
        SEND,RECEIVE;
    }
    private String wallet_addr_sender;
    private String wallet_addr_receiver;
    private String type_symbol;
    private Date date;
    private BigDecimal amount_crypto;
    private double amount_us;
    private typeTransaction type;
    public TransactionModel(){

    }
    public TransactionModel(String wallet_addr_sender, String wallet_addr_receiver, String type_symbol, Date date, BigDecimal amount_crypto, double amount_us,typeTransaction type) {
        this.wallet_addr_sender = wallet_addr_sender;
        this.wallet_addr_receiver = wallet_addr_receiver;
        this.type_symbol = type_symbol;
        this.date = date;
        this.amount_crypto = amount_crypto;
        this.amount_us = amount_us;
        this.type=type;
    }

    public String getWallet_addr_sender() {
        return wallet_addr_sender;
    }

    public void setWallet_addr_sender(String wallet_addr_sender) {
        this.wallet_addr_sender = wallet_addr_sender;
    }

    public String getWallet_addr_receiver() {
        return wallet_addr_receiver;
    }

    public void setWallet_addr_receiver(String wallet_addr_receiver) {
        this.wallet_addr_receiver = wallet_addr_receiver;
    }

    public String getType_symbol() {
        return type_symbol;
    }

    public void setType_symbol(String type_symbol) {
        this.type_symbol = type_symbol;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getAmount_crypto() {
        return amount_crypto;
    }

    public void setAmount_crypto(BigDecimal amount_crypto) {
        this.amount_crypto = amount_crypto;
    }

    public double getAmount_us() {
        return amount_us;
    }

    public void setAmount_us(double amount_us) {
        this.amount_us = amount_us;
    }

    public typeTransaction getType() {
        return type;
    }

    public void setType(typeTransaction type) {
        this.type = type;
    }

    @Override
    public int compareTo(TransactionModel o) {
        if(date.compareTo(o.getDate())==0){
            return o.getAmount_crypto().compareTo(amount_crypto);
        }else
            return o.getDate().compareTo(date);
    }
}
