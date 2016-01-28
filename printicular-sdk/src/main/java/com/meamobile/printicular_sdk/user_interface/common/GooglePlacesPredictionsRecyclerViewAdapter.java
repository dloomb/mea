package com.meamobile.printicular_sdk.user_interface.common;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.meamobile.printicular_sdk.R;

import java.util.List;

public class GooglePlacesPredictionsRecyclerViewAdapter extends RecyclerView.Adapter
{
    private List<AutocompletePrediction> mPredictions;
    private RecyclerView mRecyclerView;

    public GooglePlacesPredictionsRecyclerViewAdapter(RecyclerView recyclerView)
    {
        mRecyclerView = recyclerView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_store_search_google_places_result_view_holder, parent, false);
        return new GooglePlacesResultViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        AutocompletePrediction prediction = mPredictions.get(position);
        ((GooglePlacesResultViewHolder) holder).setGooglePlacesPrediction(prediction);
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
        layoutRecyclerView();
    }

    protected void layoutRecyclerView()
    {
        ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();
        if (mPredictions == null || mPredictions.size() == 0)
        {
            params.height = 0;
        }
        else
        {
            params.height = mPredictions.size() * 50 * (int)Resources.getSystem().getDisplayMetrics().density;;// ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }

    class GooglePlacesResultViewHolder extends RecyclerView.ViewHolder
    {
        private TextView mTextView;

        public GooglePlacesResultViewHolder(View itemView)
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
