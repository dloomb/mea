package com.meamobile.printicular_sdk.user_interface.store_search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meamobile.printicular_sdk.R;
import com.meamobile.printicular_sdk.core.models.Store;

import java.util.Collection;
import java.util.List;

public class StoreResultsRecyclerViewAdapter extends RecyclerView.Adapter
{
    private Collection<Store> mStores;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_store_search_store_result_view_holder, parent, false);
        return new StoreRecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
int i = 0;
    }

    @Override
    public int getItemCount()
    {
        return mStores == null ? 0 : mStores.size();
    }

    public void setStores(Collection<Store> stores)
    {
        mStores = stores;
        notifyDataSetChanged();
    }

    class StoreRecyclerViewHolder extends RecyclerView.ViewHolder
    {

        public StoreRecyclerViewHolder(View itemView)
        {
            super(itemView);

        }
    }
}
