package com.meamobile.printicular_sdk.user_interface.store_search;

import android.support.v7.widget.RecyclerView;
import android.text.style.CharacterStyle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.meamobile.printicular_sdk.R;

import java.util.List;

public class StoreSearchResultsRecyclerViewAdapter extends RecyclerView.Adapter
{
    private List<AutocompletePrediction> mPredictions;

    public StoreSearchResultsRecyclerViewAdapter()
    {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_store_search_google_places_result_view_holder, parent, false);
        return new StoreSearchGoolgePlacesResultViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        AutocompletePrediction prediction = mPredictions.get(position);
        ((StoreSearchGoolgePlacesResultViewHolder) holder).setGooglePlacesPrediction(prediction);
    }

    @Override
    public int getItemCount()
    {
        return mPredictions == null ? 0 : mPredictions.size();
    }

    public void setGooglePlacesPredictions(List<AutocompletePrediction> predictions)
    {
        mPredictions = predictions;
        notifyDataSetChanged();
    }

    class StoreSearchGoolgePlacesResultViewHolder extends RecyclerView.ViewHolder
    {
        private TextView mTextView;

        public StoreSearchGoolgePlacesResultViewHolder(View itemView)
        {
            super(itemView);

            mTextView = (TextView) itemView.findViewById(R.id.textView);
        }

        public void setGooglePlacesPrediction(AutocompletePrediction prediction)
        {
            mTextView.setText(prediction.getFullText(null));
        }
    }
}
