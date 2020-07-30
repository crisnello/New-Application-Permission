package com.crisnello.poc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class FloatingActivity extends AppCompatActivity implements ItemListDialogFragment.Listener {

    private static String TAG = "FloatingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating);

        uiChange();

        ItemListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");

    }

    private void uiChange() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int visibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

                final View decorView = getWindow().getDecorView();
                decorView.setSystemUiVisibility(visibility);
            }
        });
    }

    private void showSystemUI() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View decorView = getWindow().getDecorView();
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }

        });

    }

    @Override
    public void onItemClicked(int position) {
        Log.i(TAG, "Position is " + position);
    }

    @Override
    public void onClosed() {
        try{
            showSystemUI();
            finish();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
