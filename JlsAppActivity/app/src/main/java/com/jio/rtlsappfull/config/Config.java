package com.jio.rtlsappfull.config;

import com.jio.rtlsappfull.BuildConfig;

public class Config {

    public static  boolean logEnable;
    public static String SERVER_API_URL = "";
    public static String SERVER_GET_TOKEN_URL="";
    public static String SERVER_SUBMIT_TOKEN_URL="";
    public static String JIOVM_SERVER_API_URL = "";
    public static String JIOVM_SERVER_SUBMIT_TOKEN_URL="";
    public static  boolean isJioVm;
    public static String RADISYS_GEOLOCATE_API_URL_ALL="";
    public static String JIOVM_GEOLOCATE_API_URI_TRI = "";
    public static String JIOVM_WIFI_SERVER_SUBMIT_TOKEN_URL = "";
    public static String SIT_ID1;
    public static String SIT_ID2;
    public static String DEV_ID1;
    public static String DEV_ID2;
    public static String PROD_ID1;
    public static String PROD_ID2;
    public static String PREPROD_ID1;
    public static String PREPROD_ID2;
    public static String SIT_URL;
    public static String DEV_URL;
    public static String PROD_URL;
    public static String PREPROD_URL;
    public static String LOCATION_DEV_URL;
    public static String LOCATION_SIT_URL;
    public static String LOCATION_PREPROD_URL;
    public static String LOCATION_PROD_URL;
    public static String SERVER_PREPROD_API_KEY_URL;
    public static String SERVER_DEV_API_KEY_URL;
    public static String SERVER_SIT_API_KEY_URL;
    public static String SERVER_PROD_API_KEY_URL;
    public static String SUBMIT_API_URL_PRE_PROD;
    public static String SUBMIT_API_URL_PROD;
    public static String SUBMIT_API_URL_SIT;
    public static String SUBMIT_API_URL_DEV;
    public static String SUBMIT_CELL_LOCATION_DEV;
    public static String SUBMIT_CELL_LOCATION_SIT;
    public static String SUBMIT_CELL_LOCATION_PROD;
    public static String SUBMIT_CELL_LOCATION_PREPROD;
    public static String VERSION_NAME;

    static {
        logEnable=BuildConfig.logEnable;
        SERVER_API_URL=BuildConfig.SERVER_API_URL;
        SERVER_GET_TOKEN_URL=BuildConfig.SERVER_GET_TOKEN_URL;
        SERVER_SUBMIT_TOKEN_URL= BuildConfig.SERVER_SUBMIT_TOKEN_URL;
        isJioVm=BuildConfig.isJioVm;
        JIOVM_SERVER_API_URL=BuildConfig.JIOVM_SERVER_API_URL;
        JIOVM_SERVER_SUBMIT_TOKEN_URL=BuildConfig.JIOVM_SERVER_SUBMIT_TOKEN_URL;
        RADISYS_GEOLOCATE_API_URL_ALL=BuildConfig.RADISYS_GEOLOCATE_API_URL_ALL;
        JIOVM_GEOLOCATE_API_URI_TRI = BuildConfig.JIOVM_GEOLOCATE_API_URL_TRI;
        JIOVM_WIFI_SERVER_SUBMIT_TOKEN_URL = BuildConfig.JIOVM_WIFI_SERVER_SUBMIT_TOKEN_URL;
        SIT_ID1 = BuildConfig.SIT_ID1;
        SIT_ID2 = BuildConfig.SIT_ID2;
        DEV_ID1 = BuildConfig.DEV_ID1;
        DEV_ID2 = BuildConfig.DEV_ID2;
        SIT_URL = BuildConfig.SIT_URL;
        DEV_URL = BuildConfig.DEV_URL;
        PROD_URL = BuildConfig.PROD_URL;
        PREPROD_URL = BuildConfig.PRE_PROD_URL;
        LOCATION_DEV_URL = DEV_URL + BuildConfig.LOCATION_APPPENDED;
        LOCATION_SIT_URL = SIT_URL + BuildConfig.LOCATION_APPPENDED;
        LOCATION_PREPROD_URL = PREPROD_URL + BuildConfig.LOCATION_APPPENDED;
        LOCATION_PROD_URL = PROD_URL + BuildConfig.LOCATION_APPPENDED;
        SERVER_PREPROD_API_KEY_URL = BuildConfig.SERVER_PREPROD_API_KEY_URL;
        SERVER_DEV_API_KEY_URL = BuildConfig.SERVER_DEV_API_KEY_URL;
        SERVER_SIT_API_KEY_URL = BuildConfig.SERVER_SIT_API_KEY_URL;
        SERVER_PROD_API_KEY_URL = BuildConfig.SERVER_PROD_API_KEY_URL;
        PROD_ID1 = BuildConfig.PROD_ID1;
        PROD_ID2 = BuildConfig.PROD_ID2;
        PREPROD_ID1 = BuildConfig.PREPROD_ID1;
        PREPROD_ID2 = BuildConfig.PREPROD_ID2;
        SUBMIT_API_URL_PRE_PROD = PREPROD_URL + BuildConfig.SUBMIT_API_APPENDED;
        SUBMIT_API_URL_PROD = PROD_URL + BuildConfig.SUBMIT_API_APPENDED;
        SUBMIT_API_URL_SIT = SIT_URL + BuildConfig.SUBMIT_API_APPENDED;
        SUBMIT_API_URL_DEV = DEV_URL + BuildConfig.SUBMIT_API_APPENDED;
        SUBMIT_CELL_LOCATION_DEV = DEV_URL + BuildConfig.SUBMIT_CELL_LOCATION;
        SUBMIT_CELL_LOCATION_SIT = SIT_URL + BuildConfig.SUBMIT_CELL_LOCATION;
        SUBMIT_CELL_LOCATION_PROD = PROD_URL + BuildConfig.SUBMIT_CELL_LOCATION;
        SUBMIT_CELL_LOCATION_PREPROD = PREPROD_URL + BuildConfig.SUBMIT_CELL_LOCATION;
        VERSION_NAME = BuildConfig.VERSION_NAME;
    }
}
