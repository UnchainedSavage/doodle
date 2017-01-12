package com.midea.fridge.fridgedoodle.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.midea.fridge.fridgedoodle.R;

/**
 * Created by Administrator on 2016/4/27.
 */
public class TwoButtonDialogFragment extends Fragment {

    private Context context;
    private TextView mContentText;
    private Button submit;
    private Button cancel;
    private OnClickWatcher mOnClickWatcher;
    private String mContent;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.two_button_dialog,container,false);

        mContentText = (TextView) view.findViewById(R.id.message);
        if(!TextUtils.isEmpty(mContent)) {
            mContentText.setText(mContent);
        }
        submit = (Button) view.findViewById(R.id.submit);
        cancel = (Button) view .findViewById(R.id.cancel);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickWatcher.onClickConfirm();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickWatcher.onClickCancel();
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickWatcher.onClickCancel();
            }
        });
        return view;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public void setOnClickWatcher(OnClickWatcher onClickWatcher) {
        mOnClickWatcher = onClickWatcher;
    }

    public interface OnClickWatcher {
        void onClickConfirm();
        void onClickCancel();
    }
}
