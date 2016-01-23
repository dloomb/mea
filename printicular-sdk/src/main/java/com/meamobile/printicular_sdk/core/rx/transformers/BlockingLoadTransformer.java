package com.meamobile.printicular_sdk.core.rx.transformers;

import android.app.AlertDialog;
import android.content.Context;

import rx.Observable.Operator;
import rx.Subscriber;
import rx.observers.SafeSubscriber;

public class BlockingLoadTransformer<T> implements Operator
{
    Context mContext;
    AlertDialog mDialog;

    public BlockingLoadTransformer(Context context) {
        mContext = context;
    }


    @Override
    public Object call(Object o) {
        SafeSubscriber s = (SafeSubscriber) o;
        return new Subscriber<T>(s) {
            @Override
            public void onCompleted() {
        /* add your own onCompleted behavior here, or just pass the completed notification through: */
                mDialog.dismiss();

                if(!s.isUnsubscribed()) {
                    s.onCompleted();
                }
            }

            @Override
            public void onError(Throwable t) {
        /* add your own onError behavior here, or just pass the error notification through: */
                if(!s.isUnsubscribed()) {
                    s.onError(t);
                }
            }

            @Override
            public void onNext(T item) {
        /* this example performs some sort of operation on each incoming item and emits the results */
                if(!s.isUnsubscribed()) {
                    s.onNext(item);
                }
            }

            @Override
            public void onStart() {

                mDialog = new AlertDialog.Builder(mContext)
                        .setTitle("Loading")
                        .setMessage("Loading..")
                        .show();

                s.onStart();
            }
        };
    }
}
