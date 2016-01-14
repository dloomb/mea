package com.meamobile.printicular_sdk.user_interface.store_search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meamobile.printicular_sdk.R;
import com.meamobile.printicular_sdk.core.models.Store;
import com.meamobile.printicular_sdk.user_interface.common.StoreDetailsViewHolder;

import java.util.Collection;
import java.util.List;

public class StoreResultsRecyclerViewAdapter extends RecyclerView.Adapter
{
    private List<Store> mStores;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_store_view_holder, parent, false);
        StoreDetailsViewHolder holder = new StoreDetailsViewHolder(v);

        holder.setStoreDetailsHeadingVisibility(View.GONE);
        holder.setAccessoryVisibility(View.GONE);
        holder.setMargins(4,0,4,0);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        Store store = mStores.get(position);
        StoreDetailsViewHolder viewHolder = (StoreDetailsViewHolder) holder;
        viewHolder.setStore(store);
    }

    @Override
    public int getItemCount()
    {
        return mStores == null ? 0 : mStores.size();
    }

    public void setStores(List<Store> stores)
    {
        mStores = stores;
        notifyDataSetChanged();
    }

}
