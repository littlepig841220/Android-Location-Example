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
import android.view.View;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener{
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button:{
                startActivity(new Intent(getApplicationContext(),BasicActivity.class));
                break;
            }
            case R.id.button2:{
                startActivity(new Intent(getApplicationContext(),BasicActivity2.class));
                break;
            }
            case R.id.button3:{
                startActivity(new Intent(getApplicationContext(), LocationSettingActivity.class));
                break;
            }
            case R.id.button4:{
                startActivity(new Intent(getApplicationContext(), LocationSettingActivity2.class));
                break;
            }
            case R.id.button7:{
                startActivity(new Intent(getApplicationContext(), BackgroundActivity.class));
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            recreate();
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