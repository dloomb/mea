package com.meamobile.printicular_sdk.user_interface.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

import rx.Observable;
import rx.Subscriber;
import rx.subjects.PublishSubject;

public class ObservableRelativeLayout extends RelativeLayout
{
    private Observable<Integer> mObservable;
    private Subscriber mSubscriber;

    protected void init()
    {
        mObservable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                mSubscriber = subscriber;
            }
        });
    }

    public ObservableRelativeLayout(Context context) {
        super(context);
        init();
    }

    public ObservableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ObservableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ObservableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public Observable<Integer> getObservable()
    {
        return mObservable;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mSubscriber != null)
        {
            mSubscriber.onNext(heightMeasureSpec);
        }
    }
}
