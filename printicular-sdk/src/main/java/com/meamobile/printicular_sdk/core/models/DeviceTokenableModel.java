package com.meamobile.printicular_sdk.core.models;

import java.util.Map;

public class DeviceTokenableModel extends Model
{
    protected String mDeviceToken;

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

    @Override
    public Map<String, Map> evaporate()
    {
        Map <String, Map> data = super.evaporate();

        Map<String, Object> attributes = findMapWithKey(data, "attributes");
        if (attributes != null)
        {
            attributes.put("device_token", mDeviceToken);
        }

        return data;
    }

    @Override
    public void update(Model model)
    {
        super.update(model);

        DeviceTokenableModel dtModel = (DeviceTokenableModel) model;
        mDeviceToken = (String) isSetOr(dtModel.getDeviceToken(), mDeviceToken);
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
