package cbs.example.locationexample;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;

public class LocationService extends Service {
    private LocationBinder locationBinder;
    //public FusedLocationProviderClient fusedLocationClient;
    //public LocationRequest locationRequest;
    private Handler handler = new Handler();
    private ShowTimeRunnable showTimeRunnable = new ShowTimeRunnable();
    private int CountTime = 1;
    private Bundle message;
    private Intent intent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        locationBinder = new LocationBinder();
        return locationBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("test","onCreate()");

        message = new Bundle();
        intent = new Intent("LocationBackgroundService");
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.i("test","onRebind()");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("test","onUnbind()");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(showTimeRunnable);
        Log.i("test","onDestroy()");
    }

    public void startLocationUpdate(FusedLocationProviderClient fromActivityFLPC, LocationRequest fromActivityLR){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fromActivityFLPC.requestLocationUpdates(fromActivityLR,
                locationCallback,
                Looper.getMainLooper());
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location currentLocation = locationResult.getLastLocation();
            if (currentLocation != null){
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.location_updated_message), Toast.LENGTH_SHORT).show();
                String returnString = MessageFormat.format("Provider(提供者):{0}\nAccuracy(精準度):{1}\n" +
                                "Altitude(高度):{2}\nAccuracy altitude(精準的高度):{3}\n" +
                                "Longitude(經度):{4}\nLatitude:(緯度){5}\n" +
                                "Bearing(方位):{6}\nAccuracy bearing(精確的方位):{7}\n" +
                                "Speed(速度):{8}\nAccuracy speed(精確的速度){9}\n" +
                                "Time(時間):{10}",
                        currentLocation.getProvider(), currentLocation.getAccuracy(),
                        currentLocation.getAltitude(), currentLocation.getVerticalAccuracyMeters(),
                        currentLocation.getLongitude(), currentLocation.getLatitude(),
                        currentLocation.getBearing(), currentLocation.getBearingAccuracyDegrees(),
                        currentLocation.getSpeed(), currentLocation.getSpeedAccuracyMetersPerSecond(),
                        DateFormat.getTimeInstance().format(new Date(currentLocation.getTime())));
                message.putString("returnString", returnString);
                intent.putExtras(message);
                sendBroadcast(intent);
                //updateLocationUI();
                //System.out.println("test");
            }
            /*for (Location location : locationResult.getLocations()) {
                currentLocation = location;
                if (currentLocation != null){
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.location_updated_message), Toast.LENGTH_SHORT).show();
                    updateLocationUI();
                }
            }*/
        }
    };

    public class LocationBinder extends Binder {
        public LocationService getService(){
            handler.postDelayed(showTimeRunnable,1000);
            return LocationService.this;
        }
    }

    class ShowTimeRunnable implements Runnable{

        @Override
        public void run() {
            //Log.i("test",CountTime + "sec.");
            CountTime++;
            handler.postDelayed(showTimeRunnable,1000);
        }
    }
}
