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
            mCity,
            mName,
            mState,
            mPhone,
            mAddress,
            mChecksum,
            mPostCode,
            mStoreCode,
            mRetailerId,
            mRetailerStoreId;

    private double
            mLatitude,
            mLongitude;

    private boolean
            mActive;

    @Override
    public void populate(Map data)
    {
        super.populate(data);

        Map attributes = findMapWithKey(data, "attributes");

        if (attributes != null)
        {
            mName = (String) attributes.get("name");
            mAddress = (String) attributes.get("address");
            mCity = (String) attributes.get("city");
            mState = (String) attributes.get("state");
            mPostCode = (String) attributes.get("postcode");
            mPhone = (String) attributes.get("phone");
            mStoreCode = (String) attributes.get("store_code");
            mRetailerId = (String) attributes.get("retailer_id");
            mRetailerStoreId = (String) attributes.get("retailer_store_id");
            mChecksum = (String) attributes.get("checksum");

            mLatitude =  (Double) safeParse(attributes.get("latitude"), ClassType.DOUBLE);
            mLongitude = (Double) safeParse(attributes.get("longitude"), ClassType.DOUBLE);

            mActive = (boolean) safeParse(attributes.get("active"), ClassType.BOOLEAN); // ((Number) attributes.get("active")).intValue() == 1;
        }
    }

    @Override
    public Map<String, Map> evaporate() {
        Map <String, Map> data = super.evaporate();

        Map<String, Object> attributes = (Map<String, Object>) data.get("data").get("attributes");
        attributes.put("name", mName);
        attributes.put("address", mAddress);
        attributes.put("city", mCity);
        attributes.put("state", mState);
        attributes.put("postcode", mPostCode);
        attributes.put("phone", mPhone);
        attributes.put("store_code", mStoreCode);
        attributes.put("retailer_id", mRetailerId);
        attributes.put("retailer_store_id", mRetailerStoreId);
        attributes.put("checksum", mChecksum);
        attributes.put("latitude", mLatitude);
        attributes.put("longitude", mLongitude);
        attributes.put("active", mActive);

        return data;
    }

    @Override
    public String getType()
    {
        return "stores";
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

    public String getRetailerStoreId()
    {
        return mRetailerStoreId;
    }
}
