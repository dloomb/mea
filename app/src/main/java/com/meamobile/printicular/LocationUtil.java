package com.meamobile.printicular;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LocationUtil implements LocationListener
{
    private static String TAG = "MEA.LocationUtil";
    private static LocationUtil mInstance;

    private LocationManager mLocationManager;
    private Geocoder mGeocoder;
    private LocationUtilCallback mCallback;

    public enum Precision
    {
        FAST,
        EXACT
    }

    public interface LocationUtilCallback
    {
        void completion(String error, Address address);
    }

    public static void getLocation(Precision precision, Context context, LocationUtilCallback callback)
    {
        if (mInstance == null)
        {
            mInstance = new LocationUtil();
            mInstance.mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            mInstance.mGeocoder = new Geocoder(context, Locale.getDefault());
            mInstance.mCallback = callback;
        }

        String detail = precision == Precision.EXACT ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER;

        Location location = mInstance.mLocationManager.getLastKnownLocation(detail);
        if(location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000)
        {
            mInstance.getCountryFromLocation(location);
        }
        else {
            mInstance.mLocationManager.requestLocationUpdates(detail, 0, 0, mInstance);
        }
    }

    private void getCountryFromLocation(Location location)
    {
        try
        {
            List<Address> addresses = mGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses == null && addresses.isEmpty())
            {
                finish("No results found", null);
                return;
            }

            Address result = addresses.get(0);
            Log.d(TAG, "Location found: " + addresses.toString());
            finish(null, result);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            finish("Geocoder threw exception", null);
        }
    }

    private void finish(String error, Address address)
    {
        mCallback.completion(error, address);

        if (error != null)
        {
            Log.e(TAG, error);
        }

        mLocationManager.removeUpdates(this);

        mInstance.mCallback = null;
        mInstance.mLocationManager = null;
        mInstance = null;
    }


    ///-----------------------------------------------------------
    /// @name LocationListener
    ///-----------------------------------------------------------


    @Override
    public void onLocationChanged(Location location)
    {
        if (location != null)
        {
            Log.d(TAG, "Location changed: " + location.getLatitude() + " and " + location.getLongitude());
            getCountryFromLocation(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderEnabled(String provider)
    {

    }

    @Override
    public void onProviderDisabled(String provider)
    {

    }




}
