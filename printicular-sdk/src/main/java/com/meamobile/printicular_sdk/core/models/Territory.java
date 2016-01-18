package com.meamobile.printicular_sdk.core.models;

import java.util.Locale;

public class Territory extends Model
{
    private String mCountryCode, mCountryName;
    private PrintService mDefaultPrintService;

    public Territory(){}

    public Territory(Locale locale)
    {
        mCountryCode = locale.getISO3Country();
        mCountryName = locale.getDisplayCountry();
    }

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
