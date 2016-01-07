package com.meamobile.printicular.settings;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;
import android.os.Looper;

import com.cocosw.bottomsheet.BottomSheet;
import com.meamobile.photokit.core.Source;
import com.meamobile.printicular.R;

public class AccountsCell extends RecyclerView.ViewHolder
{
    private Context mContext;
    private SettingsRecyclerViewAdapter mAdapter;
    private ImageView mImageView;
    private TextView mTextView;
    private TextView mAccessoryTextView;
    private Source mSource;


    public AccountsCell(View itemView, Context context, SettingsRecyclerViewAdapter adapter)
    {
        super(itemView);

        mContext = context;
        mAdapter = adapter;
        mImageView = (ImageView) itemView.findViewById(R.id.imageView);
        mTextView = (TextView) itemView.findViewById(R.id.textView);
        mAccessoryTextView = (TextView) itemView.findViewById(R.id.textViewAccessory);

        itemView.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v)
        {
            if (mSource.isActive())
            {
                BottomSheet.Builder builder = new BottomSheet.Builder((Activity) mContext);
                builder.title(mSource.getTitle());
                builder.sheet(R.menu.settings_bottom_sheet);
                builder.listener(new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        switch (which)
                        {
                            case R.id.changeUser:

                                break;

                            case R.id.logout:
                                mSource.invalidateSource();
                                mAdapter.notifyDataSetChanged();
                                break;
                        }
                    }
                }).show();
            }
            else
            {
                mSource.activateSource((Activity) mContext, new Source.SourceActivationCallback()
                {
                    @Override
                    public void success()
                    {
                        //Wait (Let FB run first)
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {@Override public void run()
                        {
                            mAdapter.notifyDataSetChanged();
                        }}, 1000);
                    }

                    @Override
                    public void error(String error)
                    {

                    }
                });
            }


        }});
    }

    public void setSource(Source source)
    {
        mImageView.setImageResource(source.getImageResource());
        mTextView.setText(source.getTitle());

        String text = "Connect";
        int color = mContext.getResources().getColor(R.color.printicular_blue);

        if (source.isActive())
        {
            color = mContext.getResources().getColor(R.color.printicular_primary_green);
            text = source.getUsername();
        }

        mAccessoryTextView.setTextColor(color);
        mAccessoryTextView.setText(text);
        mSource = source;
    }
}
