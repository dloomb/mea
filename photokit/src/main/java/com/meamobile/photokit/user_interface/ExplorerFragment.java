package com.meamobile.photokit.user_interface;

import android.app.Activity;
import android.content.Intent;
import android.graphics.NinePatch;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.meamobile.photokit.core.Asset;
import com.meamobile.photokit.core.Collection;

import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ExplorerFragment extends Fragment implements ItemClickSupport.OnItemClickListener{

    public interface ExplorerFragmentDelegate
    {
        void pushExplorerWithCollection(Collection collection);
        void setNavigationTitle(String title);
        void setNavigationColor(int color);
        void setDisplaysBackButton(Boolean shouldDisplay);

        void onAssetSelect(Asset asset, int index);
        boolean isAssetSelected(Asset asset, int index);
    }

    private static final String ARG_COLLECTION = "collection";

    private Collection mCollection;

    private GridView mGridView;
    private RecyclerView mRecyclerView;
    private ExplorerRecyclerViewAdapter mAdapter;
    private OnFragmentInteractionListener mListener;
    private ExplorerFragmentDelegate mDelegate;

    public ExplorerFragment() {}


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

        mCollection.loadContents(getActivity())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(x -> {
                    mAdapter.notifyDataSetChanged();
                });
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
            if (mCollection.getTitle() != null) {
                mDelegate.setDisplaysBackButton(true);
                mDelegate.setNavigationTitle(mCollection.getTitle());
                mDelegate.setNavigationColor(mCollection.getSource().getBrandColor());
            } else {
                mDelegate.setDisplaysBackButton(false);
                mDelegate.setNavigationTitle("Select a Source");
                mDelegate.setNavigationColor(Source.BASE_BRAND_COLOR);
            }
        }
    }


    //--------------------------
    //        GridView
    //--------------------------

    protected void initializeGridView(View view)
    {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        mAdapter = new ExplorerRecyclerViewAdapter(getActivity(), mCollection, mDelegate);
        mRecyclerView.setAdapter(mAdapter);
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(this);

        mCollection.setCollectionObserver(mAdapter);

        StaggeredGridLayoutManager layout = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        //Larger Screens means more columns
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int w = (int)(metrics.widthPixels / metrics.density);
        if (w < 400)
        {
            layout.setSpanCount(3);
        }
        else if (w < 500)
        {
            layout.setSpanCount(4);
        }
        else
        {
            layout.setSpanCount(5);
        }
        mRecyclerView.setLayoutManager(layout);

    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v)
    {
        int collectionsCount = mCollection.numberOfCollections();
        if (position < collectionsCount)
        {
            didSelectCollectionAtIndex(position);
        }
        else
        {
            didSelectAssetAtIndex(position - collectionsCount, v);
        }
    }



    //--------------------------
    //        Selection
    //--------------------------

    protected void didSelectCollectionAtIndex(int index)
    {
        final Collection selected = mCollection.collectionAtIndex(index);

        if (selected.getSource().isActive())
        {
            mDelegate.pushExplorerWithCollection(selected);
        }
        else
        {
            selected.getSource().activateSource(getActivity(), new Source.SourceActivationCallback()
            {
                @Override
                public void success()
                {
                    mDelegate.pushExplorerWithCollection(selected);
                }

                @Override
                public void error(String error)
                {
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
