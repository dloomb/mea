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
import android.widget.Toast;

import com.meamobile.photokit.core.Collection;

import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Source;


public class ExplorerFragment extends Fragment {

    private static final String ARG_COLLECTION = "collection";

    private Collection mCollection;

    private GridView mGridView;
    private OnFragmentInteractionListener mListener;

    public NavigationFragment NavigationFragment;
    public int ContainerId;

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
    //        GridView
    //--------------------------

    protected void initializeGridView(View view)
    {
        mGridView = (GridView) view.findViewById(R.id.gridview);
        final ExplorerGridViewAdapter adapter = new ExplorerGridViewAdapter(getActivity(), mCollection);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(getOnItemClickListener());

        new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    while (true)
                    {
                        Thread.sleep(1000, 0);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

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
                    didSelectAssetAtIndex(position - collectionsCount);
                }
            }
        };
    }



    //--------------------------
    //        Selection
    //--------------------------

    protected void pushNewExplorerFragementWithCollection(Collection collection)
    {
        ExplorerFragment fragment = ExplorerFragment.newInstance(collection);
        fragment.ContainerId = this.ContainerId;
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left)
                .replace(this.ContainerId, fragment)
                .commit();
    }

    protected void didSelectCollectionAtIndex(int index)
    {
        final Collection selected = mCollection.collectionAtIndex(index);

        if (selected.Source.isActive())
        {
            pushNewExplorerFragementWithCollection(selected);
        }
        else
        {
            selected.Source.activateSource(getActivity(), new Source.SourceActivationCallback() {
                @Override
                public void success() {
                    pushNewExplorerFragementWithCollection(selected);
                }

                @Override
                public void error(String error) {
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT);
                }
            });
        }

    }

    protected void didSelectAssetAtIndex(int index)
    {

    }

}
