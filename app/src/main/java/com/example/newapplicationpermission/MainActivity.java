package com.example.newapplicationpermission;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    /*  Permission request code to draw over other apps  */
    private static final int DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE = 1222;

    private TextView tvMsg;

    private Button btnOpen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvMsg = findViewById(R.id.tvMsg);

        btnOpen = findViewById(R.id.btnOpen);
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDrawOverOtherApp();
            }
        });

        getDrawOverOtherApp();

    }

    public void getDrawOverOtherApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !canDrawOverlaysAfterUserWasAskedToEnableIt(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE);
        }else{
            tvMsg.setText(getResources().getString(R.string.text_permission_ok));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String strResp = null;
                if (canDrawOverlaysAfterUserWasAskedToEnableIt(this)) {
                    Toast.makeText(this, getResources().getString(R.string.text_permission_ok), Toast.LENGTH_LONG).show();
                    strResp = getResources().getString(R.string.text_permission_ok);
                    btnOpen.setVisibility(View.GONE);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.text_permission_nok), Toast.LENGTH_LONG).show();
                    strResp = getResources().getString(R.string.text_permission_nok);
                    btnOpen.setVisibility(View.VISIBLE);
                }
                tvMsg.setText(strResp);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static boolean canDrawOverlaysAfterUserWasAskedToEnableIt(Context context) {
        if(context == null )
            return false;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        } else {
            if (Settings.canDrawOverlays(context)) {
                return true;
            }
            try {
                final WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                if (mgr == null) {
                    return false;
                }
                final View viewToAdd = new View(context);


                WindowManager.LayoutParams params = new WindowManager.LayoutParams(0, 0, android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
                viewToAdd.setLayoutParams(params);


                mgr.addView(viewToAdd, params);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (viewToAdd != null && mgr != null) {
                            mgr.removeView(viewToAdd);
                        }
                    }
                }, 500);

                return true;

            } catch (Exception e) {
                return false;
            }
        }
    }

}
