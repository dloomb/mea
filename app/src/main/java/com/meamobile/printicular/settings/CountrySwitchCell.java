package com.meamobile.printicular.settings;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.meamobile.printicular.LocationUtil;
import com.meamobile.printicular.R;

import java.util.Locale;

public class CountrySwitchCell extends RecyclerView.ViewHolder
{
    private Context mContext;
    private SettingsRecyclerViewAdapter mAdapter;
    private ImageView mImageView;
    private TextView mTextView;
    private ImageButton mImageButton;

    public CountrySwitchCell(View itemView, Context context, SettingsRecyclerViewAdapter adapter)
    {
        super(itemView);

        mContext = context;
        mAdapter = adapter;
        mImageView = (ImageView) itemView.findViewById(R.id.imageView);
        mTextView = (TextView) itemView.findViewById(R.id.textView);
        mImageButton = (ImageButton) itemView.findViewById(R.id.imageButton);
        itemView.setOnClickListener(getOnClickListener());
    }

    public void setCountry(Locale country)
    {
        String name = "Rest of World";
        int imageId = R.drawable.rest_of_world_country;
        if (country != null)
        {
            switch (country.getISO3Country())
            {
                case "NZL":
                    name = "New Zealand";
                    imageId = R.drawable.nz_country;
                    break;

                case "USA":
                    name = "USA, PR, Canada and Mexico";
                    imageId = R.drawable.usa_can_mex_pr_country;
                    break;

                case "DEU":
                    name = "Germany";
                    imageId = R.drawable.germany_country;
                    break;
            }
            mTextView.setText(name);
            mImageView.setImageResource(imageId);
        }
    }

    public View.OnClickListener getOnClickListener()
    {
        return new View.OnClickListener() {@Override public void onClick(View v)
        {
            LocationPickerDialog dialog = new LocationPickerDialog(mContext);
            dialog.setLocationPickerDialogInterface(getLocationPickerInterface());
            dialog.setCurrentLocale(LocationUtil.getCurrentCountry());
            dialog.show();
        }};
    }

    ///-----------------------------------------------------------
    /// @name Location Picker
    ///-----------------------------------------------------------

    protected LocationPickerDialog.LocationPickerDialogInterface getLocationPickerInterface()
    {
        return new LocationPickerDialog.LocationPickerDialogInterface()
        {
            @Override
            public void OnCountrySelected(LocationPickerDialog dialog, Locale country)
            {

            }

            @Override
            public void OnOkClicked(LocationPickerDialog dialog, Locale country)
            {
                LocationUtil.setUserSavedCountry(country);
                mAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void OnCancelClicked(LocationPickerDialog dialog, Locale country)
            {
                dialog.dismiss();
            }
        };
    }
}
