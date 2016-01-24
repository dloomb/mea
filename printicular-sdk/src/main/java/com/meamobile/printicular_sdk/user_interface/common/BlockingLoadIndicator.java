package com.meamobile.printicular_sdk.user_interface.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.meamobile.printicular_sdk.R;

import rx.Observable.Operator;
import rx.Subscriber;

public class BlockingLoadIndicator implements Operator<Object, Object>
{
    private Activity mActivity;
    private BlockingDialog mDialog;
    private Subscriber mSubscriber;

    public BlockingLoadIndicator(Activity activity)
    {
        mActivity = activity;
    }

    public void cancelOperation()
    {
        mSubscriber.unsubscribe();
    }

    @Override
    public Subscriber<? super Object> call(Subscriber<? super Object> subscriber) {
        mSubscriber = subscriber;
        return new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                mDialog.dismiss();
                subscriber.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                mDialog.dismiss();
                subscriber.onError(e);
            }

            @Override
            public void onNext(Object o) {
                subscriber.onNext(o);
            }

            @Override
            public void onStart() {

                mDialog = new BlockingDialog(mActivity, BlockingLoadIndicator.this);
                mDialog.show();

                subscriber.onStart();
            }
        };
    }

    protected void show()
    {
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) mActivity
                .findViewById(android.R.id.content)).getRootView();


        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.template_non_blocking_loading_indicator, viewGroup, false);

        View toolbar = viewGroup.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((RelativeLayout.LayoutParams) v.getLayoutParams()).addRule(RelativeLayout.BELOW, R.id.toolbar);
        }

        viewGroup.addView(v);
    }
}


class BlockingDialog extends Dialog implements DialogInterface.OnKeyListener
{
    private BlockingLoadIndicator mOperator;

    public BlockingDialog(Context context, BlockingLoadIndicator operator) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.template_blocking_loading_indicator);
        setCancelable(false);
        setOnKeyListener(this);

        mOperator = operator;

        ImageView iv = (ImageView) findViewById(R.id.imageViewAnimation);
        iv.setBackgroundResource(R.drawable.loading_animation);
        iv.post(new Runnable() {
            @Override
            public void run() {
                AnimationDrawable frameAnimation = (AnimationDrawable) iv.getBackground();
                frameAnimation.start();
            }
        });

        Button b = (Button) findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Not workign yet
//                mOperator.cancelOperation();
//                BlockingDialog.this.dismiss();
            }
        });
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
    {
        return false;
    }
}