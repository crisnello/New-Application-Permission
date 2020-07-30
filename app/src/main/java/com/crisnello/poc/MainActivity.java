package com.crisnello.poc;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private static String TAG = "MainActivity";

    private static final int DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE = 1222;

    private TextView tvMsg;

    private Button btnOpen,  btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        tvMsg = findViewById(R.id.tvMsg);

        btnOpen = findViewById(R.id.btnOpen);
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDrawOverOtherApp();
            }
        });

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        mAuth.signInWithEmailAndPassword("usuario@login.com", "123456")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            LogCurrentUser(user);
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void LogCurrentUser(FirebaseUser user){
        if (user != null) {

            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            boolean emailVerified = user.isEmailVerified();

            String uid = user.getUid();

            Log.i(TAG,"Name : " + name + " Email : " + email);
            tvMsg.setText("Hello " + email);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        LogCurrentUser(currentUser);
    }


    public void getDrawOverOtherApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !canDrawOverlaysStart(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE);

        }else{
            tvMsg.setText(getResources().getString(R.string.text_permission_ok));
            btnOpen.setVisibility(View.GONE);

            openDialog();

        }
    }

    public void openDialog(){
        Intent intent = new Intent(this, FloatingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String strResp = null;
        if (requestCode == DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !canDrawOverlays(this)) {
                //Toast.makeText(this, getResources().getString(R.string.text_permission_nok), Toast.LENGTH_LONG).show();
                strResp = getResources().getString(R.string.text_permission_nok);
                btnOpen.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    btnOpen.setText(getResources().getString(R.string.text_btn));

            } else {
                Toast.makeText(this, getResources().getString(R.string.text_permission_ok), Toast.LENGTH_LONG).show();
                strResp = getResources().getString(R.string.text_permission_ok);
                btnOpen.setVisibility(View.GONE);
            }
            tvMsg.setText(strResp);
            openDialog();
        } else { super.onActivityResult(requestCode, resultCode, data); }
    }


    public static boolean canDrawOverlaysStart(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            return Settings.canDrawOverlays(context);
        } else {
            if (Settings.canDrawOverlays(context)) return true;
            Log.e(TAG,"Android Oreo");

            return validateOreo(context);
        }
    }

    public static boolean canDrawOverlays(Context context) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {//USING APP OPS MANAGER
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            if (manager != null) {
                try {
                    int result = manager.checkOp(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, Binder.getCallingUid(), context.getPackageName());
                    if(result == AppOpsManager.MODE_ALLOWED && validateOreo(context))
                        return true;
                    else
                        return false;
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }
            }
        }

        return false;

    }

    public static boolean validateOreo(Context context){
           try {
                WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                if (mgr == null) {
                    return false; //getSystemService might return null
                }
                View viewToAdd = new View(context);
                WindowManager.LayoutParams params = new WindowManager.LayoutParams(0, 0, android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
                viewToAdd.setLayoutParams(params);
                mgr.addView(viewToAdd, params);
                mgr.removeView(viewToAdd);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
    }


}
