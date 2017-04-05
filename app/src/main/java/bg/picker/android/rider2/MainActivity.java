package bg.picker.android.rider2;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String ENDPOINT = "http://35.187.18.151";

    private GoogleMap mGoogleMap;
    private SupportMapFragment mFragment;
    private Marker riderMarker;
    private Marker driverMarker;
    private MarkerOptions markerOptions;
    private LatLng mPosition;

    private static final int range = 1;
    private static float mZoom = 18f;

    private UserClient userClient;
    private String android_id;

    private HashMap<String, Marker> hashMapMarkers = new HashMap<>();

    private float yellowColor = BitmapDescriptorFactory.HUE_YELLOW;
    private float azureColor = BitmapDescriptorFactory.HUE_AZURE;

    private int mInterval = 10000; // 10 seconds by default, can be changed later
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start location listener
        startLocationListener();

        // Setup Map
        setupMapIfNeeded();


        // Setup Retrofit
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(ENDPOINT).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        userClient = retrofit.create(UserClient.class);

        // Setup ANDROID_ID
        android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);

        // Setup HANDLER
        mHandler = new Handler();


    }

    private void setupMapIfNeeded() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (mGoogleMap == null) {
            mFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //startLocationListener();
        showCurrentLocation();


        //startRepeatingTask();

//        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
//            @Override
//            public void onCameraMoveStarted(int i) {
//                //Toast.makeText(MainActivity.this,"ON CAMERA MOVE STARTED",Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
//            @Override
//            public void onCameraIdle() {
//                //riderMarker.setPosition(mGoogleMap.getCameraPosition().target);
////                mPosition = mGoogleMap.getCameraPosition().target;
////                mZoom = mGoogleMap.getCameraPosition().zoom;
////                if(mPosition.latitude > 0) {
////                   // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mPosition.latitude, mPosition.longitude)));
////                    //mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(mZoom));
////                }
//                Toast.makeText(getApplicationContext(),"ON CAMERA IDLE: " + mPosition,Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        //Toast.makeText(this,"PAUSE",Toast.LENGTH_SHORT).show();
        stopLocationListener();

        stopRepeatingTask();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(this,"RESUME",Toast.LENGTH_SHORT).show();
        startLocationListener();

        //startRepeatingTask();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this,"DESTROY",Toast.LENGTH_SHORT).show();
        stopLocationListener();

        stopRepeatingTask();
    }

    private void startLocationListener() {
        long mLocTrackingInterval = 1000 * 5; // 5 sec
        float trackingDistance = 0;
        LocationAccuracy trackingAccuracy = LocationAccuracy.HIGH;

        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(trackingAccuracy)
                .setDistance(trackingDistance)
                .setInterval(mLocTrackingInterval);

        SmartLocation.with(this)
                .location()
                .continuous()
                .config(builder.build())
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        Toast.makeText(MainActivity.this,"RIDER LOCATION UPDATE",Toast.LENGTH_SHORT).show();
                        //showCurrentLocation();
                    }
                });
    }

    private void stopLocationListener() {
        SmartLocation.with(this).location().stop();
    }

    private void showCurrentLocation() {
        Location lastLocation = SmartLocation.with(this).location().getLastLocation();
        if (lastLocation != null) {

            if (riderMarker != null) {
                riderMarker.remove();
            }
            LatLng currentLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            MarkerOptions markerOptions = createMarkerOptions(currentLocation, azureColor);
            //riderMarker = mGoogleMap.addMarker(markerOptions);
            //hashmapMarkers.put(android_id, currLocationMarker);
            //mGoogleMap.addMarker(new MarkerOptions().position(currentLocation).title("currentLocation"));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(mZoom));

        } else {
            Toast.makeText(this,"LastLocation is NULL !!!",Toast.LENGTH_SHORT).show();
        }
    }

    private MarkerOptions createMarkerOptions(LatLng latLng, float color){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(color));
        return markerOptions;
    }

    private void sync(LatLng currentLocation, int range, String deviceId) {

        Rider rider = new Rider();
        rider.setDeviceId(deviceId);
        rider.setLatitude(currentLocation.latitude);
        rider.setLongitude(currentLocation.longitude);
        rider.setRange(range);

        Call<List<Driver>> call = userClient.syncUsers(rider);
        call.enqueue(new Callback<List<Driver>>() {
            @Override
            public void onResponse(Call<List<Driver>> call, Response<List<Driver>> response) {
                List<Driver> drivers = response.body();
                drawUsers(drivers);
            }

            @Override
            public void onFailure(Call<List<Driver>> call, Throwable t) {
                //Toast.makeText(MainActivity.this,"FAIL !!!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void drawUsers(List<Driver> drivers) {

        for (Driver u : drivers) {
            if(!u.getDeviceId().equals(android_id)) {
                if(hashMapMarkers.get(u.getDeviceId()) != null) {
                    driverMarker = hashMapMarkers.get(u.getDeviceId());
                    driverMarker.setPosition(new LatLng(u.getLatitude(),u.getLongitude()));
                } else {
                    markerOptions = createMarkerOptions(new LatLng(u.getLatitude(), u.getLongitude()), yellowColor);
                    driverMarker = mGoogleMap.addMarker(markerOptions);
                    hashMapMarkers.put(u.getDeviceId(),driverMarker);
                }
            }
        }
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                Location lastLocation = SmartLocation.with(MainActivity.this).location().getLastLocation();
                LatLng currentLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                //Toast.makeText(MainActivity.this,"SYNC DRIVERS",Toast.LENGTH_SHORT).show();
                sync(currentLocation,range,android_id);
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }


}
