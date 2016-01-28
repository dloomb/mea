package com.meamobile.printicular_sdk.core.models;

import java.util.Map;

public class Address extends DeviceTokenableModel
{

    private User mUser;

    private Territory mTerritory;

    private String
            mCity,
            mName,
            mLine1,
            mLine2,
            mEmail,
            mPhone,
            mState,
            mPostcode,
            mDeviceToken;


    public Address(){}

    @Override
    public void populate(Map data) {
        super.populate(data);

        Map attributes = findMapWithKey(data, "attributes");

        if (attributes != null)
        {
            mLine1 = (String) safeParse(attributes.get("line_1"), ClassType.STRING);
            mLine2 = (String) safeParse(attributes.get("line_2"), ClassType.STRING);
            mCity = (String) safeParse(attributes.get("city"), ClassType.STRING);
            mState = (String) safeParse(attributes.get("state"), ClassType.STRING);
            mPostcode = (String) safeParse(attributes.get("postcode"), ClassType.STRING);
            mPhone = (String) safeParse(attributes.get("phone"), ClassType.STRING);
            mEmail = (String) safeParse(attributes.get("email"), ClassType.STRING);
            mName = (String) safeParse(attributes.get("name"), ClassType.STRING);
            mDeviceToken = (String) safeParse(attributes.get("device_token"), ClassType.STRING);
        }

    }
    @Override
    public Map<String, Map> evaporate()
    {
        Map <String, Map> data = super.evaporate();

        Map<String, Object> attributes = findMapWithKey(data, "attributes");
        if (attributes != null)
        {
            safePut(attributes, "line_1", mLine1);
            safePut(attributes, "line_2", mLine2);
            safePut(attributes, "city", mCity);
            safePut(attributes, "state", mState);
            safePut(attributes, "postcode", mPostcode);
            safePut(attributes, "phone", mPhone);
            safePut(attributes, "email", mEmail);
            safePut(attributes, "name", mName);
            safePut(attributes, "device_token", mDeviceToken);
        }


        return data;
    }


    ///-----------------------------------------------------------
    /// @name Proprety Access
    ///-----------------------------------------------------------


    @Override
    public String getType() {
        return "addresses";
    }

    public String getLine1() {
        return mLine1;
    }

    public void setLine1(String mLine1) {
        this.mLine1 = mLine1;
    }

    public String getLine2() {
        return mLine2;
    }

    public void setLine2(String mLine2) {
        this.mLine2 = mLine2;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String mCity) {
        this.mCity = mCity;
    }

    public String getState() {
        return mState;
    }

    public void setState(String mState) {
        this.mState = mState;
    }

    public String getPostcode() {
        return mPostcode;
    }

    public void setPostcode(String mPostcode) {
        this.mPostcode = mPostcode;
    }

    public Territory getTerritory()
    {
        return mTerritory;
    }

    public void setTerritory(Territory mTerritory)
    {
        this.mTerritory = mTerritory;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }
}
