package com.meamobile.printicular_sdk.core.models;

import java.util.Map;

public class Address extends Model
{

    private User mUser;

    private Territory mTerritory;

    private String
            mLine1,
            mLine2,
            mCity,
            mState,
            mPostcode,
            mPhone,
            mEmail,
            mName,
            mDeviceToken;

    @Override
    public void populate(Map data) {
        super.populate(data);


        Map attributes = (Map) data.get("attributes");

        if (attributes != null)
        {
            mLine1 = (String) data.get("line_1");
            mLine2 = (String) data.get("line_2");
            mCity = (String) data.get("city");
            mState = (String) data.get("state");
            mPostcode = (String) data.get("postcode");
            mPhone = (String) data.get("phone");
            mEmail = (String) data.get("email");
            mName = (String) data.get("name");
            mDeviceToken = (String) data.get("device_token");
        }

    }
}
