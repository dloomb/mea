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



    ///-----------------------------------------------------------
    /// @name Proprety Access
    ///-----------------------------------------------------------


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

    public String getDeviceToken() {
        return mDeviceToken;
    }

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
