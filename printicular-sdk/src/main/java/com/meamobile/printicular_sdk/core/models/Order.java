package com.meamobile.printicular_sdk.core.models;

import java.util.List;

public class Order extends Model
{
    protected List<LineItem> mLineItems;
    protected Address mAddress;
    protected Store mStore;
    protected PrintService mPrintService;
    protected String
            mCurrency,
            mClientName,
            mClientVersion;

    public Order()
    {

    }


    ///-----------------------------------------------------------
    /// @name Property Access
    ///-----------------------------------------------------------
    //region Property Access
    public List<LineItem> getLineItems()
    {
        return mLineItems;
    }

    public void setLineItems(List<LineItem> mLineItems)
    {
        this.mLineItems = mLineItems;
    }

    public Address getAddress()
    {
        return mAddress;
    }

    public void setAddress(Address mAddress)
    {
        this.mAddress = mAddress;
    }

    public Store getStore()
    {
        return mStore;
    }

    public void setStore(Store mStore)
    {
        this.mStore = mStore;
    }

    public PrintService getPrintService()
    {
        return mPrintService;
    }

    public void setPrintService(PrintService mPrintService)
    {
        this.mPrintService = mPrintService;
    }

    public String getCurrency()
    {
        return mCurrency;
    }

    public void setCurrency(String mCurrency)
    {
        this.mCurrency = mCurrency;
    }

    public String getClientName()
    {
        return mClientName;
    }

    public void setClientName(String mClientName)
    {
        this.mClientName = mClientName;
    }

    public String getClientVersion()
    {
        return mClientVersion;
    }

    public void setClientVersion(String mClientVersion)
    {
        this.mClientVersion = mClientVersion;
    }
    //endregion
}
