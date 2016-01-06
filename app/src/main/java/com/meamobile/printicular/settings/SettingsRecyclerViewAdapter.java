package com.meamobile.printicular.settings;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import com.cocosw.bottomsheet.BottomSheet;
import com.meamobile.photokit.core.Source;
import com.meamobile.photokit.facebook.FacebookSource;
import com.meamobile.photokit.instagram.InstagramSource;
import com.meamobile.photokit.photobucket.PhotobucketSource;
import com.meamobile.printicular.LocationUtil;
import com.meamobile.printicular.R;

public class SettingsRecyclerViewAdapter extends RecyclerView.Adapter
{
    public enum HolderViewType
    {
        HEADER,
        COUNTRY_SWITCHER,
        SOURCE_ACCOUNTS,
        FOOTER
    }


    private ArrayList<SettingsRecyclerContent> mContent;
    private Context mContext;

    public SettingsRecyclerViewAdapter(Context context)
    {
        mContext = context;
        setupStaticContent();
    }

    protected void setupStaticContent()
    {
        mContent = new ArrayList<>();

        mContent.add(new SettingsRecyclerContent(HolderViewType.HEADER, "Country"));
        mContent.add(new SettingsRecyclerContent(HolderViewType.COUNTRY_SWITCHER, null));

        mContent.add(new SettingsRecyclerContent(HolderViewType.HEADER, "Accounts"));
        mContent.add(new SettingsRecyclerContent(HolderViewType.SOURCE_ACCOUNTS, new InstagramSource()));
        mContent.add(new SettingsRecyclerContent(HolderViewType.SOURCE_ACCOUNTS, new FacebookSource()));
        mContent.add(new SettingsRecyclerContent(HolderViewType.SOURCE_ACCOUNTS, new PhotobucketSource()));

        mContent.add(new SettingsRecyclerContent(HolderViewType.HEADER, ""));
        mContent.add(new SettingsRecyclerContent(HolderViewType.FOOTER, null));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflator = LayoutInflater.from(parent.getContext());

        switch (HolderViewType.values()[viewType])
        {
            case HEADER:
                View header = inflator.inflate(R.layout.template_settings_recycler_header, parent, false);
                return new SettingsViewHolderHeader(header);

            case COUNTRY_SWITCHER:
                View switcher = inflator.inflate(R.layout.template_settings_recycler_country_switcher, parent, false);
                return new CountrySwitchCell(switcher, mContext, this);

            case SOURCE_ACCOUNTS:
                View accounts = inflator.inflate(R.layout.template_settings_recycler_accounts, parent, false);
                return new AccountsCell(accounts, mContext);

            case FOOTER:
                View footer = inflator.inflate(R.layout.template_settings_recycler_footer, parent, false);
                return new SettingsViewHolderFooter(footer);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        SettingsRecyclerContent content = mContent.get(position);

        switch (content.getViewType())
        {
            case HEADER:
                layoutHeaderAtIndex((SettingsViewHolderHeader) holder, content);
                break;

            case COUNTRY_SWITCHER:
                layoutCountryPickerAtIndex((CountrySwitchCell) holder, position);
                break;

            case SOURCE_ACCOUNTS:
                layoutAccountsAtIndex((AccountsCell) holder, content);
                break;

            case FOOTER:
                layoutFooterAtIndex((SettingsViewHolderFooter) holder, content);
                break;
        }
    }



    @Override
    public int getItemCount()
    {
        return mContent.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        SettingsRecyclerContent content = mContent.get(position);
        return content.getViewTypeAsInt();
    }






    ///-----------------------------------------------------------
    /// @name Content
    ///-----------------------------------------------------------

    protected class SettingsRecyclerContent
    {
        private HolderViewType mViewType;
        private Object mData;

        public SettingsRecyclerContent(HolderViewType type, Object data)
        {
            mViewType = type;
            mData = data;
        }

        public HolderViewType getViewType()
        {
            return mViewType;
        }

        public int getViewTypeAsInt()
        {
            return mViewType.ordinal();
        }

        public Object getData()
        {
            return mData;
        }
    }


    ///-----------------------------------------------------------
    /// @name Holder Layout
    ///-----------------------------------------------------------

    public void layoutHeaderAtIndex(SettingsViewHolderHeader header, SettingsRecyclerContent content)
    {
        header.setText((String) content.getData());
    }

    public void layoutCountryPickerAtIndex(CountrySwitchCell switcher, int index)
    {
        Locale country = LocationUtil.getCurrentCountry();
        switcher.setCountry(country);
    }

    public void layoutAccountsAtIndex(AccountsCell accountsView, SettingsRecyclerContent content)
    {
        Source source = (Source) content.getData();
        accountsView.setSource(source);
    }

    public void layoutFooterAtIndex(SettingsViewHolderFooter footer, SettingsRecyclerContent content)
    {
        footer.mTextViewSecondary.setText("v1.0 (111)\nABCDEF-123456-XYZ123");
    }


    ///-----------------------------------------------------------
    /// @name View Holders Header and Footer
    ///-----------------------------------------------------------

    protected class SettingsViewHolderHeader extends RecyclerView.ViewHolder
    {
        private TextView mTextView;

        public SettingsViewHolderHeader(View itemView)
        {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.textView);
        }

        public void setText(String text)
        {
            mTextView.setText(text);
        }
    }

    protected class SettingsViewHolderFooter extends RecyclerView.ViewHolder
    {
        private TextView mTextViewMain;
        private TextView mTextViewSecondary;

        public SettingsViewHolderFooter(View itemView)
        {
            super(itemView);

            mTextViewMain = (TextView) itemView.findViewById(R.id.textViewMain);
            mTextViewSecondary = (TextView) itemView.findViewById(R.id.textViewSecondary);

            mTextViewMain.setText(R.string.textview_text_small_print);
        }
    }


}
