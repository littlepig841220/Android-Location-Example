package cbs.example.locationexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import cbs.example.locationexample.fusedlocationproviderclient.FLPCAutoUpdateActivity;
import cbs.example.locationexample.fusedlocationproviderclient.FLPCBasicActivity;
import cbs.example.locationexample.googleapiclient.GACAutoUpdateActivity;
import cbs.example.locationexample.googleapiclient.GACBasicActivity;
import cbs.example.locationexample.locationManager.LMAutoUpdateActivity;
import cbs.example.locationexample.locationManager.LMBasicActivity;
import cbs.example.locationexample.locationManager.LMManualUpdateActivity;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener{
    private int PERMISSION_ALL = 1;
    private String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION};

    public static String TAG = "CBSAndy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Log.i(TAG, "MenuActivity onCreate");

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getTag().toString()) {
            case "GACBasic": {
                startActivity(new Intent(getApplicationContext(), GACBasicActivity.class));
                break;
            }
            case "GACManualUpdate": {
                break;
            }
            case "GACAutoUpdate": {
                startActivity(new Intent(getApplicationContext(), GACAutoUpdateActivity.class));
                break;
            }
            case "FLPCBasic": {
                startActivity(new Intent(getApplicationContext(), FLPCBasicActivity.class));
                break;
            }
            case "FLPCManualUpdate": {
                break;
            }
            case "FLPCAutoUpdate": {
                startActivity(new Intent(getApplicationContext(), FLPCAutoUpdateActivity.class));
                break;
            }
            case "LMBasic": {
                startActivity(new Intent(getApplicationContext(), LMBasicActivity.class));
                break;
            }
            case "LMManualUpdate": {
                startActivity(new Intent(getApplicationContext(), LMManualUpdateActivity.class));
                break;
            }
            case "LMAutoUpdate": {
                startActivity(new Intent(getApplicationContext(), LMAutoUpdateActivity.class));
                break;
            }
            default: {
                Toast.makeText(getApplicationContext(), "Unknown action", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //recreate();
        }else {
            Toast.makeText(getApplicationContext(),"You must permission or you can't use this app",Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}