package com.meamobile.photokit.local;


import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;

public class LocalSource extends Source
{

    public LocalSource()
    {
        this.mTitle = "Local Photos";
        this.mImageResourceId = R.drawable.local_badge;
    }

    @Override
    public boolean isActive()
    {
        return true;
    }

    @Override
    public int getBrandColor()
    {
        return 0xFF159b8b;
    }

    ///-----------------------------------------------------------
    /// @name Parcelable
    ///-----------------------------------------------------------

    public static final Parcelable.Creator<LocalSource> CREATOR = new Parcelable.Creator<LocalSource>() {
        public LocalSource createFromParcel(Parcel in) {
            return new LocalSource(in);
        }

        public LocalSource[] newArray(int size) {
            return new LocalSource[size];
        }
    };

    protected LocalSource(Parcel in)
    {
        super(in);
    }
}
