package com.meamobile.photokit.user_interface;


import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.meamobile.photokit.R;
import com.meamobile.photokit.core.Asset;
import com.meamobile.photokit.core.Collection;


public class ExplorerGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private Collection mCollection;

    public ExplorerGridViewAdapter(Context c, Collection collection) {
        mContext = c;
        mCollection = collection;
    }

    public int getCount() {
        return mCollection.numberOfAll();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.template_explorer_asset_item, null);
        } else {
            itemView = (View) convertView;
        }

        ImageView mainImageView = (ImageView)itemView.findViewById(R.id.imageView);
        ImageView selectionImageView = (ImageView)itemView.findViewById(R.id.selectionIndicatorImageView);

        if (position < mCollection.numberOfCollections())
        {
            Collection collection = mCollection.collectionAtIndex(position);

            mainImageView.setImageResource(collection.Source.ImageResourceId);
            selectionImageView.setVisibility(View.INVISIBLE);
        }
        else
        {
            Asset asset = mCollection.assetAtIndex(position - mCollection.numberOfCollections());

            mainImageView.setBackgroundColor(0xFF00FF00);
        }


        return itemView;
    }


}