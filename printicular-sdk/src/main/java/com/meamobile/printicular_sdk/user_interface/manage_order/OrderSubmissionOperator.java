package com.meamobile.printicular_sdk.user_interface.manage_order;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.meamobile.printicular_sdk.core.models.Order;

import rx.Observable;
import rx.Subscriber;

public class OrderSubmissionOperator implements Observable.Operator<Object, Order>
{
    private Context mContext;
    private OrderSubmissionDialog mDialog;

    public OrderSubmissionOperator(Context context)
    {
        mContext = context;
    }

    @Override
    public Subscriber<? super Order> call(Subscriber<? super Object> subscriber)
    {
        return new Subscriber<Order>()
        {
            @Override
            public void onCompleted()
            {
                subscriber.onCompleted();
            }

            @Override
            public void onError(Throwable e)
            {
                subscriber.onError(e);
            }

            @Override
            public void onNext(Order order)
            {

                subscriber.onNext(order);
            }

            @Override
            public void onStart()
            {
                mDialog = new OrderSubmissionDialog(mContext);
                mDialog.show();

                subscriber.onStart();
            }
        };
    }
}

class OrderSubmissionDialog extends Dialog
{

    public OrderSubmissionDialog(Context context)
    {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

    }
}
