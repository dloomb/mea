package com.meamobile.photokit.user_interface;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.meamobile.photokit.core.Collection;

import com.meamobile.photokit.R;


public class ExplorerFragment extends Fragment {

    private static final String ARG_COLLECTION = "collection";

    private Collection mCollection;

    private GridView mGridView;
    private OnFragmentInteractionListener mListener;

    public NavigationFragment NavigationFragment;

    public ExplorerFragment() {
        // Required empty public constructor
    }


    public static ExplorerFragment newInstance(Collection collection)
    {
        ExplorerFragment fragment = new ExplorerFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_COLLECTION, collection);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCollection = (Collection) getArguments().getParcelable(ARG_COLLECTION);
        }

        if (mCollection == null)
        {
            mCollection = Collection.RootCollection();
        }

        mCollection.loadContents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explorer, container, false);

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
    // GridView
    //--------------------------

    protected void initializeGridView(View view)
    {
        mGridView = (GridView) view.findViewById(R.id.gridview);
        mGridView.setAdapter(new ExplorerGridViewAdapter(getActivity(), mCollection));
        mGridView.setOnItemClickListener(getOnItemClickListener());
    }

    protected AdapterView.OnItemClickListener getOnItemClickListener()
    {
        final ExplorerFragment self = this;
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Collection selected = mCollection.collectionAtIndex(position);

                ExplorerFragment fragment = ExplorerFragment.newInstance(selected);
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
                        .show(fragment)
                        .commit();


//                self.NavigationFragment.pushFragment(fragment);
            }
        };
    }
}
