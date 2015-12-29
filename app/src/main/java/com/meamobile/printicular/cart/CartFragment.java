package com.meamobile.printicular.cart;

import android.app.ActionBar;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.meamobile.printicular.R;



public class CartFragment extends Fragment {

    private static String LOG = "CartFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private GridView mGridView;
    private CartGridViewAdapter mGridAdapter;

    private OnFragmentInteractionListener mListener;

    public CartFragment() {
    }


    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
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
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        initializeGridView(view);
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



    //--------------------------
    //         GridView
    //--------------------------

    public void onDataChanged(int index)
    {
        mGridAdapter.notifyDataSetChanged();
        mGridView.smoothScrollToPosition(index);
    }

    protected void initializeGridView(View view)
    {
        mGridAdapter = new CartGridViewAdapter(getActivity());

        mGridView = (GridView) view.findViewById(R.id.gridview);
        mGridView.setAdapter(mGridAdapter);
        mGridView.setOnItemClickListener(getOnItemClickListener());

        view.post(new Runnable() {
            @Override
            public void run() {
                int width = getView().getWidth();
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, width);
                params.gravity = Gravity.CENTER;
                mGridView.setLayoutParams(params);
            }
        });
    }

    protected AdapterView.OnItemClickListener getOnItemClickListener()
    {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//
                Log.d(LOG, "Width " + mGridView.getWidth() + ", Height " + mGridView.getHeight());
            }
        };
    }

}
