package com.midea.fridge.fridgedoodle;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public abstract class BaseActivity extends FragmentActivity {
    private Toast mToast;
    public ProgressDialog loadDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

    @Override
    protected void onResume() {
        super.onResume();
        toggleHideyBar();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void setTitle(String title){
        TextView titleTv = (TextView) findViewById(R.id.navigation_title);
        if(titleTv != null){
            titleTv.setText(title);
        }
        View back = findViewById(R.id.btn_back);
        if(back != null){
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    public void showHomeBtn(){
        View home = findViewById(R.id.btn_home);
        if(home != null){
            home.setVisibility(View.VISIBLE);
            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoHome();
                }
            });
        }
    }

    protected void gotoHome() {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }
	
	@SuppressLint("NewApi")
	public void toggleHideyBar() {
        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i("sstang", "Turning immersive mode mode off. ");
        } else {
            Log.i("sstang", "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }

    public void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast == null) {
                    mToast = Toast.makeText(BaseActivity.this, text, Toast.LENGTH_SHORT);
                } else {
                    mToast.setText(text);
                    mToast.setDuration(Toast.LENGTH_SHORT);
                }
                mToast.show();
            }
        });
    }

    public void showProgressDialog(String message,DialogInterface.OnDismissListener listener) {
        if(loadDialog == null || !loadDialog.isShowing()){
            loadDialog = new ProgressDialog(this);
            if(listener != null){
                loadDialog.setOnDismissListener(listener);
            }
            if(TextUtils.isEmpty(message)){
                message = "请等待...";
            }
            loadDialog.setMessage(message);
            loadDialog.setCancelable(true);
            loadDialog.getWindow().setGravity(Gravity.CENTER);
            loadDialog.show();
        }
    }

    public void showProgressDialog2(String message,DialogInterface.OnCancelListener listener) {
        showProgressDialog2(message, listener, true);
    }

    public void showProgressDialog2(String message,DialogInterface.OnCancelListener listener, boolean cancelable) {
        if(loadDialog == null || !loadDialog.isShowing()){
            loadDialog = new ProgressDialog(this);
            if(listener != null){
                loadDialog.setOnCancelListener(listener);
            }
            if(TextUtils.isEmpty(message)){
                message = "请等待...";
            }
            loadDialog.setMessage(message);
            loadDialog.setCancelable(cancelable);
            loadDialog.getWindow().setGravity(Gravity.CENTER);
            loadDialog.show();
        }
    }

    public void dismissProgressDialog(){
        if (loadDialog != null && loadDialog.isShowing()){
            loadDialog.dismiss();
        }
    }
}
