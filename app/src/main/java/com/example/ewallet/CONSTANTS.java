package com.example.ewallet;

public class CONSTANTS {
    final static int BITCOIN_INDEX_JSON = 0;
    final static int ETHERUM_INDEX_JSON = 1;
    final static int TETHER_INDEX_JSON = 2;
    final static int XRP_INDEX_JSON = 4;
    final static int LITECOIN_INDEX_JSON = 3;
    final static String MARKET_UPDATES_URL = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?CMC_PRO_API_KEY=3ea268be-397d-4d62-8127-644e8c4f84d3";
    final static String MARKET_UPDATES_URL2 = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?CMC_PRO_API_KEY=11c5c6d8-2f77-4f86-add2-47241f13fb44";
    private static String BinanceUrl="https://api.binance.com/api/v3/ticker/price?symbol=crypUSDT";
    public static  String getUrlFor(String symbol){
       String url_crypto=new String(BinanceUrl);
       url_crypto=url_crypto.replace("cryp",symbol);
       return url_crypto;
    }
    final static String HIST_BPI_LINK = "https://api.coindesk.com/v1/bpi/historical/close.json";
    final static String WALLET_TYPES_URL="http://10.0.2.2/cryptoBank/public/Wallet_TypeController/getSupportedWallets";
    final static String ADD_WALLET_URL="http://10.0.2.2/cryptoBank/public/WalletController/addWallet";
    final static String LOAD_WALLETS_URL="http://10.0.2.2/cryptoBank/public/WalletController/getWallets";
    final static String DELETE_WALLET_URL="http://10.0.2.2/cryptoBank/public/WalletController/DeleteWallet";
    final static String SEND_MONEY_URL="http://10.0.2.2/cryptoBank/public/WalletController/sendMoney";
    final static String GET_WALLET_ADDRESS_URL="http://10.0.2.2/cryptoBank/public/WalletController/getWalletAddress";
    final static String LOG_IN_URL="http://10.0.2.2/cryptoBank/public/UserController/LogIn";
    final static String SIGN_UP_URL="http://10.0.2.2/cryptoBank/public/UserController/createUser";
}
