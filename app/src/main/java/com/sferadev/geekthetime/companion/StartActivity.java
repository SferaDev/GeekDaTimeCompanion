package com.sferadev.geekthetime.companion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;
import com.sferadev.geekthetime.companion.util.SystemUiHider;

import static com.sferadev.geekthetime.companion.App.getContext;
import static com.sferadev.geekthetime.companion.Utils.isServiceRunning;
import static com.sferadev.geekthetime.companion.Utils.mStartShown;

public class StartActivity extends Activity {

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final boolean TOGGLE_ON_CLICK = true;
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
    public TextView mText;
    public ProgressBar mProgress;
    View.OnTouchListener mTouchIgnore = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            cancelSearch();
            return true;
        }
    };
    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };
    private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mStartShown) {
            cancelSearch();
        }
        setContentView(R.layout.activity_start);
        mText = (TextView) findViewById(R.id.main_text);
        mProgress = (ProgressBar) findViewById(R.id.progress);

        if (!isServiceRunning(UpdateService.class)) {
            Intent i = new Intent(this, UpdateService.class);
            this.startService(i);
        }

        connectToPebble();

        final View contentView = findViewById(R.id.main_content);

        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {

                    @Override
                    public void onVisibilityChange(boolean visible) {
                        if (visible && AUTO_HIDE) {
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        findViewById(R.id.ignore_button).setOnTouchListener(mTouchIgnore);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void connectToPebble() {
        Handler mQuitHandler = new Handler();
        if (PebbleKit.isWatchConnected(getApplicationContext())) {
            Runnable mShowTextRunnable = new Runnable() {
                @Override
                public void run() {
                    mText.setText(getResources().getText(R.string.start_success));
                    mProgress.setVisibility(View.GONE);
                }
            };
            Runnable mQuitRunnable = new Runnable() {
                @Override
                public void run() {
                    cancelSearch();
                }
            };
            mQuitHandler.postDelayed(mShowTextRunnable, 2500);
            mQuitHandler.postDelayed(mQuitRunnable, 3500);
        } else {
            Runnable mShowTextRunnable = new Runnable() {
                @Override
                public void run() {
                    mText.setText(getResources().getText(R.string.start_error));
                    mProgress.setVisibility(View.GONE);
                }
            };
            mQuitHandler.postDelayed(mShowTextRunnable, 3000);

        }
    }

    private void cancelSearch() {
        finish();
        mStartShown = true;
        Intent intent = new Intent(getContext(), GeekActivity.class);
        startActivity(intent);
    }
}
