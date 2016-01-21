package com.meamobile.printicular_sdk.core.models;

import java.util.List;

public class Order extends DeviceTokenableModel
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

    public void setLineItems(List<LineItem> lineItems)
    {
        mLineItems = lineItems;
    }

    public Address getAddress()
    {
        return mAddress;
    }

    public void setAddress(Address address)
    {
        mAddress = address;
    }

    public Store getStore()
    {
        return mStore;
    }

    public void setStore(Store store)
    {
        mStore = store;
    }

    public PrintService getPrintService()
    {
        return mPrintService;
    }

    public void setPrintService(PrintService printService)
    {
        mPrintService = printService;
    }

    public String getCurrency()
    {
        return mCurrency;
    }

    public void setCurrency(String currency)
    {
        mCurrency = currency;
    }

    public String getClientName()
    {
        return mClientName;
    }

    public void setClientName(String clientName)
    {
        mClientName = clientName;
    }

    public String getClientVersion()
    {
        return mClientVersion;
    }

    public void setClientVersion(String clientVersion)
    {
        mClientVersion = clientVersion;
    }
    //endregion
}
