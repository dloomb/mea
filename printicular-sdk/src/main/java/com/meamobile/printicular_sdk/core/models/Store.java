package com.meamobile.printicular_sdk.core.models;

import com.meamobile.printicular_sdk.R;

import java.util.Map;

public class Store extends Model
{
    private String
            mName,
            mAddress,
            mCity,
            mState,
            mPostCode,
            mPhone,
            mStoreCode,
            mRetailerId;


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
        }
    }


    public int getStoreLogoImageResourceId()
    {
        return R.drawable.walgreens_store_title;
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
