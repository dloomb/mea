package com.meamobile.printicular_sdk.core.models;

import java.util.Map;

public class DeviceTokenableModel extends Model
{
    private String mDeviceToken;

    @Override
    public void populate(Map data)
    {
        super.populate(data);

        //Attempt to get a device token from the attributes field
        Map attributes = findMapWithKey(data, "attributes");
        if (attributes != null) {
           mDeviceToken = (String) attributes.get("device_token");
        }


        //Attempt to get a device token from the meta field
        if (mDeviceToken == null && mMeta != null) {
            mDeviceToken = (String) mMeta.get("device_token");
        }
    }


    public String getDeviceToken()
    {
        return mDeviceToken;
    }

    public void setDeviceToken(String deviceToken)
    {
        mDeviceToken = deviceToken;
    }
}
