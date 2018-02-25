package com.example.usuario1.tarea5;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import static java.lang.Thread.sleep;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager location;
    private Location loc;
    private Geocoder geo;
    private Punto punto;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //mientras no se tengan permisos de ubicacion se le pediran al usuario
        while (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PackageManager.PERMISSION_GRANTED);
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        location=(LocationManager) getSystemService(Context.LOCATION_SERVICE) ;//clase con la que se administra todo lo relacionado con los servicios de ubicacion
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        loc=location.getLastKnownLocation(LocationManager.GPS_PROVIDER);//se obtiene la ultima localizacion conocida y se almacena en un objeto Location
        geo=new Geocoder(this);//clase que devolvera datos de una localizacion dada
        if (getIntent().getExtras()!=null){//si la actividad tiene extras tendra un punto a guardar en la base de datos
            punto=(Punto) getIntent().getExtras().getSerializable("Punto");
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;//variable relacionada con el mapa del layout
        if (punto!=null){
            //Se asigna una location con las coordenandas del punto
            Location loc = new Location(LocationManager.GPS_PROVIDER);
            loc.setLatitude(Double.parseDouble(punto.getCoorx()));
            loc.setLongitude(Double.parseDouble(punto.getCoory()));
            Marker mark = null;
            //Se crea un marcador y se añade a la lista
            if (punto.isVisitado()){
                mark = mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title(punto.getNombre()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
            }
            else{
                mark = mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title(punto.getNombre()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }
            mark.setTag(punto);
        }
        //escuchador de una pulsacion larga sobre el mapa almacenara la ubicacion en un objeto punto y lo pasara a la actividad principal
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener(){
            @Override
            public void onMapLongClick(LatLng latLng) {
                try {
                    Punto p=new Punto((geo.getFromLocation(latLng.latitude,latLng.longitude,1).get(0).getAddressLine(0)),String.valueOf(latLng.latitude),String.valueOf(latLng.longitude),false);//objeto punto con la ubicacion pulsada
                    Intent intent=getIntent();
                    intent.putExtra("Punto",p);//se agrega el punto al intent que ha lanzado esta activity
                    setResult(RESULT_OK,intent);
                    finish();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
