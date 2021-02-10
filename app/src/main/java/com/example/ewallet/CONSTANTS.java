package com.example.ewallet;

public class CONSTANTS {
    //api link
    final static String MARKET_UPDATES_URL2 = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?CMC_PRO_API_KEY=11c5c6d8-2f77-4f86-add2-47241f13fb44";
    //api link
    private static String BINANCE_API_URL="https://api.binance.com/api/v3/ticker/price?symbol=crypUSDT";
    //get the price for crypto using binance api
    public static  String getUrlFor(String symbol){
       String url_crypto=new String(BINANCE_API_URL);
       url_crypto=url_crypto.replace("cryp",symbol);
       return url_crypto;
    }
    //get the data of the corresponding crypto usnig crypto watch api
    public static String getHistUrlFor(String symbol){
        String url=new String(HIST_DATA_LINK);
        url=url.replace("cryp_symbol",symbol.toLowerCase());
        return url;
    }
    private final static String HIST_DATA_LINK="https://api.cryptowat.ch/markets/kraken/cryp_symbolusd/trades?limit=30";
    //PHP scripts links
    final static String WALLET_TYPES_URL="http://10.0.2.2/cryptoBank/public/Wallet_TypeController/getSupportedWallets";
    final static String ADD_WALLET_URL="http://10.0.2.2/cryptoBank/public/WalletController/addWallet";
    final static String LOAD_WALLETS_URL="http://10.0.2.2/cryptoBank/public/WalletController/getWallets";
    final static String DELETE_WALLET_URL="http://10.0.2.2/cryptoBank/public/WalletController/DeleteWallet";
    final static String SEND_MONEY_URL="http://10.0.2.2/cryptoBank/public/WalletController/sendMoney";
    final static String GET_WALLET_ADDRESS_URL="http://10.0.2.2/cryptoBank/public/WalletController/getWalletAddress";
    final static String LOG_IN_URL="http://10.0.2.2/cryptoBank/public/UserController/LogIn";
    final static String SIGN_UP_URL="http://10.0.2.2/cryptoBank/public/UserController/createUser";
    final static String LOG_OUT_URL="http://10.0.2.2/cryptoBank/public/UserController/logOut";
    final static String TRANSACTIONS_URL="http://10.0.2.2/cryptoBank/public/TransactionController/getTransactions";
    final static String MESSAGE_URL="http://10.0.2.2/cryptoBank/public/MessageController/sendMessage";
}
