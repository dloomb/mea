package com.meamobile.printicular_sdk.core.models;

public class Territory extends Model
{
    private String mCountryCode, mCountryName;
    private PrintService mDefaultPrintService;


    public String getCountryName()
    {
        return mCountryName;
    }

    public String getCountryCode()
    {
        return mCountryCode;
    }

    public PrintService getmDefaultPrintService()
    {
        return mDefaultPrintService;
    }
}
