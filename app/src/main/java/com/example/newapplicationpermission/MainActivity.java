package com.example.newapplicationpermission;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import android.view.View;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String strResp = null;
                if (Settings.canDrawOverlays(this)) {
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

}
