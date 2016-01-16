package com.meamobile.printicular_sdk.core.models;

import com.meamobile.printicular_sdk.R;

import java.util.Comparator;
import java.util.Map;

public class Store extends Model
{
    public static Comparator<Store> DistanceSortComparator()
    {
        return new Comparator<Store>()
        {
            @Override
            public int compare(Store o1, Store o2)
            {
                Double d1 = (Double) o1.getMeta("distance");
                Double d2 = (Double) o2.getMeta("distance");

                return d1.compareTo(d2);
            }
        };
    }

    private String
            mName,
            mAddress,
            mCity,
            mState,
            mPostCode,
            mPhone,
            mStoreCode,
            mRetailerId,
            mRetailerStoreId,
            mChecksum;

    private double
            mLatitude,
            mLongitude;

    private boolean
            mActive;

    @Override
    public void populate(Map data)
    {
        super.populate(data);

        Map attributes = (Map) data.get("attributes");

        if (attributes != null)
        {
            mName = (String) attributes.get("name");
            mAddress = (String) attributes.get("address");
            mCity = (String) attributes.get("city");
            mState = (String) attributes.get("state");
            mPostCode = (String) attributes.get("postcode");
            mPhone = (String) attributes.get("phone");
            mStoreCode = (String) attributes.get("store_code");
            mRetailerId = (String) attributes.get("reatiler_id");
            mRetailerStoreId = (String) attributes.get("retailer_store_id");
            mChecksum = (String) attributes.get("checksum");

            mLatitude = Double.parseDouble((String) attributes.get("latitude"));
            mLongitude = Double.parseDouble((String) attributes.get("longitude"));

            mActive = ((Number) attributes.get("active")).intValue() == 1;
        }
    }


    public int getStoreLogoImageResourceId()
    {
        return R.drawable.warehouse_stationery_inverse_no_bg;
    }

    public int getStoreColorResourceId()
    {
        return R.color.store_color_warehouse_stationery;
    }

    public String getName()
    {
        return mName;
    }

    public String getAddress()
    {
        return mAddress;
    }

    public String getCity()
    {
        return mCity;
    }

    public String getState()
    {
        return mState;
    }

    public String getPostCode()
    {
        return mPostCode;
    }

    public String getPhone()
    {
        return mPhone;
    }

    public String getStoreCode()
    {
        return mStoreCode;
    }

    public String getRetailerId()
    {
        return mRetailerId;
    }
}
