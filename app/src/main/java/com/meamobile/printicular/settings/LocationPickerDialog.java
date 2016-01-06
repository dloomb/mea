package com.meamobile.printicular.settings;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Locale;

import com.meamobile.printicular.R;

public class LocationPickerDialog extends Dialog
{
    public interface LocationPickerDialogInterface
    {
        void OnCountrySelected(LocationPickerDialog dialog, Locale country);
        void OnOkClicked(LocationPickerDialog dialog, Locale country);
        void OnCancelClicked(LocationPickerDialog dialog, Locale country);
    }

    private LocationPickerDialogInterface mLocationPickerDialogInterface;

    private Button mOkButton, mCancelButton;
    private RadioGroup mRadioGroup;
    private Locale mCurrentLocale;

    public LocationPickerDialog(Context context)
    {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.template_settings_country_selection_dialog);

        mOkButton = (Button) findViewById(R.id.okButton);
        mOkButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mLocationPickerDialogInterface.OnOkClicked(LocationPickerDialog.this, mCurrentLocale);
            }
        });

        mCancelButton = (Button) findViewById(R.id.cancelButton);
        mCancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mLocationPickerDialogInterface.OnCancelClicked(LocationPickerDialog.this, mCurrentLocale);
            }
        });

        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId)
                {
                    case R.id.radioNZ:
                        mCurrentLocale = new Locale(Locale.getDefault().getLanguage(), "NZL");
                        break;

                    case R.id.radioUSA:
                        mCurrentLocale = new Locale(Locale.getDefault().getLanguage(), "USA");
                        break;

                    case R.id.radioGerman:
                        mCurrentLocale = new Locale(Locale.getDefault().getLanguage(), "DEU");
                        break;

                    case R.id.radioRest:
                        mCurrentLocale = new Locale(Locale.getDefault().getLanguage(), "ABW"); //Just the first ISO3 Country Code (We don't actually support ABW)
                        break;

                }

                mLocationPickerDialogInterface.OnCountrySelected(LocationPickerDialog.this, mCurrentLocale);
            }
        });
    }

    public void setLocationPickerDialogInterface(LocationPickerDialogInterface dialogInterface)
    {
        mLocationPickerDialogInterface = dialogInterface;
    }

    public void setCurrentLocale(Locale locale)
    {
        mCurrentLocale = locale;
        mRadioGroup.check(resourceIdFromLocale(locale));
    }

    protected int resourceIdFromLocale(Locale locale)
    {
        switch (locale.getISO3Country())
        {
            case "NZL":
                return R.id.radioNZ;

            case "USA":
                return R.id.radioUSA;

            case "DEU":
                return R.id.radioGerman;

            default:
                return R.id.radioRest;
        }
    }


}
