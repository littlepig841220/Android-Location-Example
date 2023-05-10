package cbs.example.locationexample.locationManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GnssAntennaInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.provider.ProviderProperties;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cbs.example.locationexample.MenuActivity;
import cbs.example.locationexample.R;

public class LMAutoUpdateActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {
    private Button buttonPassive, buttonNetwork, buttonFused, buttonGPS;
    private TextView textViewDetail, textViewUpdate;
    private LocationManager locationManager;
    private final List<String> providers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmbasic);

        buttonPassive = findViewById(R.id.button14);
        buttonNetwork = findViewById(R.id.button15);
        buttonFused = findViewById(R.id.button16);
        buttonGPS = findViewById(R.id.button17);
        textViewDetail = findViewById(R.id.textView9);
        textViewUpdate = findViewById(R.id.textView10);
    }

    @Override
    protected void onStart() {
        super.onStart();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        providers.addAll(locationManager.getAllProviders());

        for (String provider : providers) {
            switch (provider) {
                case LocationManager.PASSIVE_PROVIDER: {
                    buttonPassive.setEnabled(true);
                    break;
                }
                case LocationManager.NETWORK_PROVIDER: {
                    buttonNetwork.setEnabled(true);
                    break;
                }
                case LocationManager.FUSED_PROVIDER: {
                    buttonFused.setEnabled(true);
                    break;
                }
                case LocationManager.GPS_PROVIDER: {
                    buttonGPS.setEnabled(true);
                    break;
                }
                default: {
                    print("Unknown provider");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        locationManager.removeUpdates(this);
    }

    @Override
    public void onClick(View v) {
        locationManager.removeUpdates(this);
        textViewUpdate.setText("");

        switch (v.getTag().toString()) {
            case "Passive": {
                getLocation(LocationManager.PASSIVE_PROVIDER);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    print("Permission denied");
                } else {
                    locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this, Looper.getMainLooper());
                }
                break;
            }
            case "Network": {
                getLocation(LocationManager.NETWORK_PROVIDER);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    print("Permission denied");
                } else {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this, Looper.getMainLooper());
                }
                break;
            }
            case "Fused": {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    getLocation(LocationManager.FUSED_PROVIDER);

                    locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 0, 0, this, Looper.getMainLooper());
                } else {
                    print("Need Android 12");
                }
                break;
            }
            case "GPS": {
                getLocation(LocationManager.GPS_PROVIDER);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    print("Permission denied");
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this, Looper.getMainLooper());
                }
                break;
            }
            default: {
                print("Unknown action");
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        textViewUpdate.setText(MessageFormat.format("\nLast Location: {0},{1}\nLast Altitude: {2}" +
                        "\nLast Accuracy: {3}\nLast Time: {4}" +
                        "\nLast Speed: {5}\nLast Bearing: {6}" +
                        "\nLast Bearing Accuracy Degrees: {7}",
                String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), String.valueOf(location.getAltitude()),
                location.getAccuracy(), DateFormat.getTimeInstance().format(new Date(location.getTime())),
                location.getSpeed(), location.getBearing(),
                location.getBearingAccuracyDegrees()));

        Log.i(MenuActivity.TAG, MessageFormat.format("Last Location: {0},{1}", String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude())));
    }

    private void getLocation(String provider) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Permission Define", Toast.LENGTH_SHORT).show();
        } else {
            String GNSSAntennaInformation;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                List<GnssAntennaInfo> information = locationManager.getGnssAntennaInfos();

                if (information != null) {
                    GNSSAntennaInformation = information.toString();
                } else {
                    GNSSAntennaInformation = " null";
                }
            } else {
                GNSSAntennaInformation = "Need Android 12(API 31)";
            }

            String GNSSCapabilities;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                GNSSCapabilities = locationManager.getGnssCapabilities().toString();
            } else {
                GNSSCapabilities = "Need Android 11(API 30)";
            }

            String GNSSHardwareModelName = locationManager.getGnssHardwareModelName();
            String GNSSYearOfHardware = String.valueOf(locationManager.getGnssYearOfHardware());

            String accuracy;
            String powerUsage;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ProviderProperties providerProperties = locationManager.getProviderProperties(provider);
                accuracy = String.valueOf(providerProperties.getAccuracy());
                powerUsage = String.valueOf(providerProperties.getPowerUsage());
            } else {
                accuracy = "Need Android 12(API 31)";
                powerUsage = "Need Android 12(API 31)";
            }
            String enableProvider = locationManager.getProviders(true).toString();
            String hasProvider;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                hasProvider = String.valueOf(locationManager.hasProvider(provider));
            } else {
                hasProvider = "Need Android 12(API 31)";
            }
            String locationEnabled = String.valueOf(locationManager.isLocationEnabled());
            String providerEnabled = String.valueOf(locationManager.isProviderEnabled(provider));

            textViewDetail.setText(MessageFormat.format("Providers: {0}\nEnable Provider: {1}" +
                            "\nHas Provider: {2}\nLocation Enabled: {3}" +
                            "\nProvider Enabled: {4}\nCurrent Provider: {5}" +
                            "\nAccuracy: {6}\nPower Usage: {7}" +
                            "\n" +
                            "\nGNSS Antenna Information: {8}\nGNSS Capabilities: {9}" +
                            "\nGNSS Hardware Model Name: {10}\nGNSS Year Of Hardware: {11}",
                    providers.toString(), enableProvider,
                    hasProvider, locationEnabled,
                    providerEnabled, provider,
                    accuracy, powerUsage,
                    GNSSAntennaInformation, GNSSCapabilities,
                    GNSSHardwareModelName, GNSSYearOfHardware));
        }
    }

    private void print(String message) {
        Log.w(MenuActivity.TAG, message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}