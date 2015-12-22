package com.meamobile.photokit.user_interface;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meamobile.photokit.R;

import java.util.ArrayList;


public class NavigationFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ArrayList<Fragment> mFragments;

    private OnFragmentInteractionListener mListener;

    public NavigationFragment() {
        mFragments = new ArrayList<Fragment>();
    }

    public static NavigationFragment newInstance(String param1, String param2) {
        NavigationFragment fragment = new NavigationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

//        ExplorerFragment initial = (ExplorerFragment) getChildFragmentManager().findFragmentById(R.id.initialFragment);
//        initial.NavigationFragment = this;
//        mFragments.add(initial);

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    public void pushFragment(Fragment fragment)
    {
        mFragments.add(fragment);
//        ((ExplorerFragment)fragment).NavigationFragment = this;

        android.support.v4.app.FragmentManager fm = getChildFragmentManager();
        fm.beginTransaction()
//                .setCustomAnimations(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom)
                .replace(R.id.containerFrameLayout, fragment)
                .commit();
    }
}
