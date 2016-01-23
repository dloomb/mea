package com.meamobile.printicular_sdk.core.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order extends DeviceTokenableModel
{
    protected List<LineItem> mLineItems;

    protected Address mAddress;

    protected Store mStore;

    protected PrintService mPrintService;

    protected String
            mCurrency,
            mVendorId,
            mStatus,
            mClientName,
            mClientVersion;

    protected double
            mTaxTotal,
            mTotal,
            mSubTotal,
            mFreightTotal;



    public Order()
    {

    }

    @Override
    public String getType()
    {
        return "orders";
    }

    @Override
    public void populate(Map data)
    {
        super.populate(data);

        Map attributes = findMapWithKey(data, "attributes");
        if (attributes != null)
        {
            mStatus = (String) attributes.get("status");
            mCurrency = (String) attributes.get("currency");
            mClientName = (String) attributes.get("client_name");
            mClientVersion = (String) safeParse(attributes.get("client_version"), ClassType.STRING);
            mTaxTotal = (double) safeParse(attributes.get("tax_total"), ClassType.DOUBLE);
            mSubTotal = (double) safeParse(attributes.get("subtotal"), ClassType.DOUBLE);
            mTotal = (double) safeParse(attributes.get("total"), ClassType.DOUBLE);
            mFreightTotal = (double) safeParse(attributes.get("freight_total"), ClassType.DOUBLE);
        }
    }

    @Override
    public Map<String, Map> evaporate()
    {
        Map <String, Map> data = super.evaporate();

        Map<String, Object> attributes = findMapWithKey(data, "attributes");
        if (attributes != null)
        {
            attributes.put("currency", mCurrency);
            attributes.put("total", mTotal);
            attributes.put("subtotal", mSubTotal);
            attributes.put("tax_total", mTaxTotal);
            attributes.put("client_name", mClientName);
            attributes.put("client_version", mClientVersion);
        }

        Map relationships = findMapWithKey(data, "relationships");
        if (relationships != null)
        {
            if (mPrintService != null) {
                relationships.put("print_service", mPrintService.getDataWrappedResourceIdentifierObject());
            }

            if (mStore != null) {
                relationships.put("store", mStore.getDataWrappedResourceIdentifierObject());
            }

            if (mLineItems != null) {
                List<Map> items = new ArrayList<>();

                for (LineItem i : mLineItems)
                {
                    if (i.getId() == -1) {
                        items.add(i.evaporate().get("data"));
                    } else {
                        items.add(i.getResourceIdentifierObject());
                    }
                }

                HashMap dataWrappedItems = new HashMap();
                dataWrappedItems.put("data", items);

                relationships.put("line_items", dataWrappedItems);
            }

            if (mAddress != null) {
                relationships.put("address", mAddress.getDataWrappedResourceIdentifierObject());
            }
        }

        return data;
    }

    @Override
    public void update(Model model)
    {
        super.update(model);

        Order orderModel = (Order) model;

        mCurrency = (String) isSetOr(orderModel.getCurrency(), mCurrency);
        mVendorId = (String) isSetOr(orderModel.getVendorId(), mVendorId);
        mStatus = (String) isSetOr(orderModel.getStatus(), mStatus);
        mClientName = (String) isSetOr(orderModel.getClientName(), mClientName);
        mClientVersion = (String) isSetOr(orderModel.getClientVersion(), mClientVersion);

        mTotal =  isSetOr(orderModel.getTotal(), mTotal);
        mSubTotal = isSetOr(orderModel.getSubTotal(), mSubTotal);
        mTaxTotal =  isSetOr(orderModel.getTaxTotal(), mTaxTotal);
        mFreightTotal = isSetOr(orderModel.getFreightTotal(), mFreightTotal);
    }


    ///-----------------------------------------------------------
    /// @name Property Access
    ///-----------------------------------------------------------

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

    public String getVendorId()
    {
        return mVendorId;
    }

    public void setVendorId(String vendorId)
    {
        mVendorId = vendorId;
    }

    public String getStatus()
    {
        return mStatus;
    }

    public void setStatus(String status)
    {
        mStatus = status;
    }

    public double getTaxTotal()
    {
        return mTaxTotal;
    }

    public void setTaxTotal(double taxTotal)
    {
        this.mTaxTotal = taxTotal;
    }

    public double getTotal()
    {
        return mTotal;
    }

    public void setTotal(double total)
    {
        this.mTotal = total;
    }

    public double getSubTotal()
    {
        return mSubTotal;
    }

    public void setSubTotal(double subTotal)
    {
        this.mSubTotal = subTotal;
    }

    public double getFreightTotal()
    {
        return mFreightTotal;
    }

    public void setFreightTotal(double freightTotal)
    {
        mFreightTotal = freightTotal;
    }
}
