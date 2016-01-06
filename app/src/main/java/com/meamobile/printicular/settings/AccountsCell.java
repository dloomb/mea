package com.meamobile.printicular.settings;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.meamobile.photokit.core.Source;
import com.meamobile.printicular.R;

public class AccountsCell extends RecyclerView.ViewHolder
{
    private Context mContext;
    private ImageView mImageView;
    private TextView mTextView;
    private TextView mAccessoryTextView;
    private Source mSource;


    public AccountsCell(View itemView, Context context)
    {
        super(itemView);

        mContext = context;
        mImageView = (ImageView) itemView.findViewById(R.id.imageView);
        mTextView = (TextView) itemView.findViewById(R.id.textView);
        mAccessoryTextView = (TextView) itemView.findViewById(R.id.textViewAccessory);

        itemView.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v)
        {
            if (mSource.isActive())
            {
                BottomSheet.Builder builder = new BottomSheet.Builder((Activity) mContext);
                builder.title(mSource.Title);
                builder.sheet(R.menu.settings_bottom_sheet);
                builder.listener(new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

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
        mImageView.setImageResource(source.ImageResourceId);
        mTextView.setText(source.Title);

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
