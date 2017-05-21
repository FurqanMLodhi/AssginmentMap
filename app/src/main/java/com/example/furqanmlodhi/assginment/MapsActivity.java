package com.example.furqanmlodhi.assginment;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    LocationManager locationManager;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap;
    ImageView image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        if (googleServiceAvailable()) {
            Toast.makeText(this, "Perfect", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_maps);


            initMap();
        } else {
            //no show map
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        //     .findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
    }

    public void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

    }

    public boolean googleServiceAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
        } else {
            Toast.makeText(this, "Can't Connect to play service", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        if (mGoogleMap != null) {

            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){

                @Override
                public void onMapClick(LatLng latLng) {
                    MapsActivity.this.setMarker("Local", latLng.latitude,latLng.longitude);
                }
            });


            mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {

                    Geocoder gc = new Geocoder(MapsActivity.this);
                    //for moving check the window
                    LatLng ll = marker.getPosition();
                    double lat = ll.latitude;
                    double lng = ll.longitude;
                    List<Address> list = null;
                    try {
                        list = gc.getFromLocation(lat, lng, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address add = list.get(0);
                    marker.setTitle(add.getLocality());
                    marker.showInfoWindow();

                }
            });

            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                //window value
                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.info_window, null);

                    TextView tvLocaltiy = (TextView) v.findViewById(R.id.tv_locality);
                    TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
                    TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
                    TextView tvSnippat = (TextView) v.findViewById(R.id.tv_snippet);

                    LatLng ll = marker.getPosition();
                    tvLocaltiy.setText(marker.getTitle());
                    tvLat.setText("Latitue: " + ll.latitude);
                    tvLng.setText("Longitude : " + ll.longitude);
                    tvSnippat.setText(marker.getTitle());
                    image.setImageBitmap(imageBitmap);

                    return v;
                }
            });
        }

        //goToLocationZoom(39.008224, -76.8984527, 15);
         if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
         return;
    }
     mGoogleMap.setMyLocationEnabled(true);
    // if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
    //  locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
    //   @Override
    //    public void onLocationChanged(Location location) {

    //    double latitude= location.getLatitude();

    //    double longitude= location.getLongitude();

    //    LatLng latLng=new LatLng(latitude , longitude);

    //    Geocoder geocoder=new Geocoder(getApplicationContext());
    //    try {
    //        List<Address> addressList=geocoder.getFromLocation(latitude, longitude , 1);
    //        String str=addressList.get(0).getLocality()+",";
    //        str+=addressList.get(0).getCountryName();
    //        mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(str));
    //        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng , 10.2f));
    //   } catch (IOException e) {
    //       e.printStackTrace();
    //   }


    // }

    //   @Override
    //   public void onStatusChanged(String provider, int status, Bundle extras) {

    //  }

    //  @Override
    //  public void onProviderEnabled(String provider) {

    //   }

    //  @Override
    //   public void onProviderDisabled(String provider) {

    //   }
    // });

    // }

     }


    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.moveCamera(update);
    }

        Marker marker;
    //search place

    public void geoLocate(View view) throws IOException {
        EditText et = (EditText) findViewById(R.id.editText);
        String location = et.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location, 1);
        Address address = list.get(0);
        String locality = address.getLocality();

        Toast.makeText(this, locality, Toast.LENGTH_LONG).show();
        double lat = address.getLatitude();
        double lng = address.getLongitude();
        goToLocationZoom(lat, lng, 15);

        setMarker(locality, lat, lng);

    }

    //Circle circle;

//    Marker marker1;
//    Marker marker2;
//    Polyline line;

    ArrayList<Marker> markers = new ArrayList<Marker>();
    static final int POLYGON_POINTS= 5;
    Polygon shape;

    private void setMarker(String locality, double lat, double lng) {
        //    if (marker != null) {
        //privous remove
        //       removeEverything();
        //   }

        if(markers.size()==POLYGON_POINTS ){
            removeEverything();

        }


        //marker color on touch etc
        MarkerOptions options = new MarkerOptions()
                .title(locality)
                .draggable(true)
                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                 .icon(BitmapDescriptorFactory.fromResource(R.mipmap.azka))
                .position(new LatLng(lat, lng))
                .snippet("I am Here");

        markers.add(mGoogleMap.addMarker(options));
        if(markers.size()==POLYGON_POINTS){
            drawPolygon();
            
        }
//        marker = mGoogleMap.addMarker(options);
//
//        if (marker1 == null) {
//            marker1 = mGoogleMap.addMarker(options);
//        }else if(marker2 == null){
//            marker2 = mGoogleMap.addMarker(options);
//            drawline();
//        }else{
//            removeEverything();
//            marker1=mGoogleMap.addMarker(options);
//        }
//
//          circle = drawCircle(new LatLng(lat, lng));
    }

    private void drawPolygon() {
        //draw a 5 entry to lines
        PolygonOptions options= new PolygonOptions()
                        .fillColor(0x330000FF)
                        .strokeColor(3)
                        .strokeColor(Color.GRAY);

        for(int i=0; i<POLYGON_POINTS; i++){
            options.add(markers.get(i).getPosition());

        }
        shape = mGoogleMap.addPolygon(options);
    }

    private void removeEverything() {
        //remove for 5 entry
        for(Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
        shape.remove();
        shape=null;

    }

//    private void drawline() {
//        //lines between to point
//        PolylineOptions options= new PolylineOptions()
//                        .add(marker1.getPosition())
//                        .add(marker2.getPosition())
//                        .color(Color.BLUE)
//                        .width(3);
//        line =mGoogleMap.addPolyline(options);

        //Circle around the place

        //private Circle drawCircle(LatLng latLng) {

        //   CircleOptions options = new CircleOptions()
        //           .center(latLng)
        //           .radius(1000)
        //           .fillColor(0x33ff0000)
        //          .strokeColor(Color.BLUE)
        //          .strokeWidth(3);

        //return mGoogleMap.addCircle(options);
        //}
   // }

//    private void removeEverything(){
//        marker1.remove();
//        marker1=null;
//        marker2.remove();
//        marker2=null;
//        line.remove();

      //  circle.remove();
      //  circle=null;
//}

    public  boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        //map differnrt
        switch (item.getItemId()){
            case R.id.mapTypeNone:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.mapTypeNormal:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void dispatchTakePictureIntent( View v) {
        if (v.getId() == R.id.picture) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
            startActivityForResult(intent, 1);
            //onActivityResult(1,1,intent);
        }
    }
}