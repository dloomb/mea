package com.meamobile.photokit.user_interface;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.meamobile.photokit.core.Asset;
import com.meamobile.photokit.core.Collection;

import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;


public class ExplorerFragment extends Fragment {

    public interface ExplorerFragmentDelegate
    {
        void pushExplorerWithCollection(Collection collection);
        void setNavigationTitle(String title);
        void setDisplaysBackButton(Boolean shouldDisplay);

        void onAssetSelect(Asset asset, int index);
        boolean isAssetSelected(Asset asset, int index);
    }

    private static final String ARG_COLLECTION = "collection";

    private Collection mCollection;

    private GridView mGridView;
    private OnFragmentInteractionListener mListener;
    private ExplorerFragmentDelegate mDelegate;

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

        if (activity instanceof ExplorerFragmentDelegate) {
            mDelegate = (ExplorerFragmentDelegate)activity;
        }
        else {
            throw new RuntimeException("Cannot attach a ExplorerFragment to a Activity that doesn't implement ExplorerFragmentNavigator");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void onFragmentWillAppear()
    {
        if (mCollection != null && mDelegate != null) {
            if (mCollection.Title != null) {
                mDelegate.setDisplaysBackButton(true);
                mDelegate.setNavigationTitle(mCollection.Title);
            } else {
                mDelegate.setDisplaysBackButton(false);
                mDelegate.setNavigationTitle("Select a Source");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onAuthenticatorResult()
    {

    }

    //--------------------------
    //        GridView
    //--------------------------

    protected void initializeGridView(View view)
    {
        ExplorerGridViewAdapter adapter = new ExplorerGridViewAdapter(getActivity(), mCollection, mDelegate);
        mCollection.setCollectionObserver(adapter);

        mGridView = (GridView) view.findViewById(R.id.gridview);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(getOnItemClickListener());

        //Larger Screens means more columns
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int w = (int)(metrics.widthPixels / metrics.density);
        if (w < 400)
        {
            mGridView.setNumColumns(3);
        }
        else if (w < 500)
        {
            mGridView.setNumColumns(4);
        }
        else
        {
            mGridView.setNumColumns(5);
        }

    }

    protected AdapterView.OnItemClickListener getOnItemClickListener()
    {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                int collectionsCount = mCollection.numberOfCollections();
                if (position < collectionsCount)
                {
                    didSelectCollectionAtIndex(position);
                }
                else
                {
                    didSelectAssetAtIndex(position - collectionsCount, view);
                }
            }
        };
    }



    //--------------------------
    //        Selection
    //--------------------------

    protected void didSelectCollectionAtIndex(int index)
    {
        final Collection selected = mCollection.collectionAtIndex(index);

        if (selected.Source.isActive())
        {
            mDelegate.pushExplorerWithCollection(selected);
        }
        else
        {
            selected.Source.activateSource(getActivity(), new Source.SourceActivationCallback() {
                @Override
                public void success() {
                    mDelegate.pushExplorerWithCollection(selected);
                }

                @Override
                public void error(String error) {
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT);
                }
            });
        }

    }

    protected void didSelectAssetAtIndex(int index, View v)
    {
        Asset selected = mCollection.assetAtIndex(index);
        mDelegate.onAssetSelect(selected, index);

        boolean isSelected = mDelegate.isAssetSelected(selected, index);
        View selectionView = v.findViewById(R.id.selectionIndicatorImageView);
        if (selectionView != null)
        {
            selectionView.setVisibility(isSelected ? View.VISIBLE : View.INVISIBLE);
        }
    }




}
