package com.meamobile.printicular_sdk.user_interface;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class UserInterfaceUtil
{

    public static void hideKeyboard(Activity activity)
    {
        View v = activity.getCurrentFocus();
        hideKeyboard(v);
    }

    public static void hideKeyboard(View v)
    {
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            v.clearFocus();
        }
    }

}
