package com.techworx.maps_varshini_779380;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
        GoogleMap.OnPolylineClickListener,
        GoogleMap.OnPolygonClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private ArrayList<Marker> mapMarkers;
    private ArrayList<Polyline> polyLines;
    private boolean isDisplayingA, isDisplayingB, isDisplayingC, isDisplayingD, isDisplayingPolygon, cameraAnimated;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mMap;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Marker polygonDistance, ABDistance, BCDistance, CDDistance, DADistance;
    private Polygon polygon;
    private String title, snippet;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                } else {
                    ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                }
            } else {
                finish();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mapMarkers.size() <= 4) {
            title = "";
            snippet = "";

            String country = getCountryName(latLng);
            if (!country.equals("Canada")) {
                return;
            }

            getTitleSnippet(latLng);

            MarkerOptions markerOptions = null;

            if (!isDisplayingA) {
                isDisplayingA = true;
                markerOptions = new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_a));
                Marker marker = mMap.addMarker(markerOptions
                        .title(title)
                        .snippet(snippet));
                marker.setTag("A");
                if (mapMarkers.size() == 0) {
                    mapMarkers.add(marker);
                } else {
                    mapMarkers.set(0, marker);
                }
                if (isDisplayingD) {
                    Polyline polyline = mMap.addPolyline(new PolylineOptions()
                            .clickable(true)
                            .color(Color.RED)
                            .add(mapMarkers.get(0).getPosition(), mapMarkers.get(3).getPosition()));
                    polyline.setTag("AD");
                    if (polyLines.size() == 3) {
                        polyLines.add(polyline);
                    } else {
                        polyLines.set(3, polyline);
                    }
                }
                if (isDisplayingB) {
                    Polyline polyline = mMap.addPolyline(new PolylineOptions()
                            .clickable(true)
                            .color(Color.RED)
                            .add(mapMarkers.get(0).getPosition(), mapMarkers.get(1).getPosition()));
                    polyline.setTag("AB");
                    if (polyLines.size() == 0) {
                        polyLines.add(polyline);
                    } else {
                        polyLines.set(0, polyline);
                    }
                }

            } else if (!isDisplayingB) {
                isDisplayingB = true;
                markerOptions = new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_b));
                Marker marker = mMap.addMarker(markerOptions
                        .title(title)
                        .snippet(snippet));
                marker.setTag("B");
                if (mapMarkers.size() == 1) {
                    mapMarkers.add(marker);
                } else {
                    mapMarkers.set(1, marker);
                }

                if (isDisplayingA) {
                    Polyline polyline = mMap.addPolyline(new PolylineOptions()
                            .clickable(true)
                            .color(Color.RED)
                            .add(mapMarkers.get(0).getPosition(), mapMarkers.get(1).getPosition()));
                    polyline.setTag("AB");
                    if (polyLines.size() == 0) {
                        polyLines.add(polyline);
                    } else {
                        polyLines.set(0, polyline);
                    }
                }
                if (isDisplayingC) {
                    Polyline polyline = mMap.addPolyline(new PolylineOptions()
                            .clickable(true)
                            .color(Color.RED)
                            .add(mapMarkers.get(1).getPosition(), mapMarkers.get(2).getPosition()));
                    polyline.setTag("BC");
                    if (polyLines.size() == 1) {
                        polyLines.add(polyline);
                    } else {
                        polyLines.set(1, polyline);
                    }
                }
            } else if (!isDisplayingC) {
                isDisplayingC = true;
                markerOptions = new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_c));
                Marker marker = mMap.addMarker(markerOptions
                        .title(title)
                        .snippet(snippet));
                marker.setTag("C");
                if (mapMarkers.size() == 2) {
                    mapMarkers.add(marker);
                } else {
                    mapMarkers.set(2, marker);
                }
                if (isDisplayingD) {
                    Polyline polyline = mMap.addPolyline(new PolylineOptions()
                            .clickable(true)
                            .color(Color.RED)
                            .add(mapMarkers.get(2).getPosition(), mapMarkers.get(3).getPosition()));
                    polyline.setTag("CD");
                    if (polyLines.size() == 2) {
                        polyLines.add(polyline);
                    } else {
                        polyLines.set(2, polyline);
                    }
                }
                if (isDisplayingB) {
                    Polyline polyline = mMap.addPolyline(new PolylineOptions()
                            .clickable(true)
                            .color(Color.RED)
                            .add(mapMarkers.get(1).getPosition(), mapMarkers.get(2).getPosition()));
                    polyline.setTag("BC");
                    if (polyLines.size() == 1) {
                        polyLines.add(polyline);
                    } else {
                        polyLines.set(1, polyline);
                    }
                }
            } else if (!isDisplayingD) {
                isDisplayingD = true;
                markerOptions = new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_d));
                Marker marker = mMap.addMarker(markerOptions
                        .title(title)
                        .snippet(snippet));
                marker.setTag("D");
                if (mapMarkers.size() == 3) {
                    mapMarkers.add(marker);
                } else {
                    mapMarkers.set(3, marker);
                }

                if (isDisplayingC) {
                    Polyline polyline = mMap.addPolyline(new PolylineOptions()
                            .clickable(true)
                            .color(Color.RED)
                            .add(mapMarkers.get(2).getPosition(), mapMarkers.get(3).getPosition()));
                    polyline.setTag("CD");
                    if (polyLines.size() == 2) {
                        polyLines.add(polyline);
                    } else {
                        polyLines.set(2, polyline);
                    }
                }
                if (isDisplayingA) {
                    Polyline polyline = mMap.addPolyline(new PolylineOptions()
                            .clickable(true)
                            .color(Color.RED)
                            .add(mapMarkers.get(0).getPosition(), mapMarkers.get(3).getPosition()));
                    polyline.setTag("DA");
                    if (polyLines.size() == 3) {
                        polyLines.add(polyline);
                    } else {
                        polyLines.set(3, polyline);
                    }
                }
            }

            drawPolygon();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        cameraAnimated = false;
        initLocCallback();

        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
        } else {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        mapMarkers = new ArrayList<>();
        polyLines = new ArrayList<>();
        isDisplayingA = false;
        isDisplayingB = false;
        isDisplayingC = false;
        isDisplayingD = false;
        isDisplayingPolygon = false;


        mMap.setOnMapClickListener(this);
        mMap.setOnPolylineClickListener(this);
        mMap.setOnPolygonClickListener(this);

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                for (Marker marker : mapMarkers) {
                    if (isDisplayingA || isDisplayingB || isDisplayingC || isDisplayingD) {
                        if (Math.abs(marker.getPosition().latitude - latLng.latitude) < 0.05 && Math.abs(marker.getPosition().longitude - latLng.longitude) < 0.05) {
                            if (marker.getTag().equals("A")) {
                                if (polyLines.size() > 0) {
                                    polyLines.get(0).remove();
                                    if (ABDistance != null) {
                                        ABDistance.remove();
                                    }
                                }
                                if (polyLines.size() > 3) {
                                    polyLines.get(3).remove();
                                    if (DADistance != null) {
                                        DADistance.remove();
                                    }
                                }
                                marker.remove();
                                isDisplayingA = false;
                            } else if (marker.getTag().equals("B")) {
                                if (polyLines.size() > 0) {
                                    polyLines.get(0).remove();
                                    if (ABDistance != null) {
                                        ABDistance.remove();
                                    }
                                }
                                if (polyLines.size() > 1) {
                                    polyLines.get(1).remove();
                                    if (BCDistance != null) {
                                        BCDistance.remove();
                                    }
                                }
                                marker.remove();
                                isDisplayingB = false;
                            } else if (marker.getTag().equals("C")) {
                                if (polyLines.size() > 1) {
                                    polyLines.get(1).remove();
                                    if (BCDistance != null) {
                                        BCDistance.remove();
                                    }
                                }
                                if (polyLines.size() > 2) {
                                    polyLines.get(2).remove();
                                    if (CDDistance != null) {
                                        CDDistance.remove();
                                    }
                                }
                                marker.remove();
                                isDisplayingC = false;
                            } else if (marker.getTag().equals("D")) {
                                if (polyLines.size() > 2) {
                                    polyLines.get(2).remove();
                                    if (CDDistance != null) {
                                        CDDistance.remove();
                                    }
                                }
                                if (polyLines.size() > 3) {
                                    polyLines.get(3).remove();
                                    if (DADistance != null) {
                                        DADistance.remove();
                                    }
                                }
                                marker.remove();
                                isDisplayingD = false;
                            }
                            if (polygon != null) {
                                isDisplayingPolygon = false;
                                polygon.remove();
                            }
                            if (polygonDistance != null) {
                                polygonDistance.remove();
                            }
                            break;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onPolygonClick(Polygon polygon) {
        List<LatLng> latLngList = new ArrayList<>();
        for (int i = 0; i < polygon.getPoints().size(); i++) {
            latLngList.add(new LatLng(polygon.getPoints().get(i).latitude, polygon.getPoints().get(i).longitude));
        }

        String distanceText = "";
        System.out.println("Clicked: " + polygon.getPoints().toString());


        for (int i = 0; i < 4; i++) {
            double latitude1 = polygon.getPoints().get(i).latitude;
            double longitude1 = polygon.getPoints().get(i).longitude;


            double latitude2 = polygon.getPoints().get(i + 1).latitude;
            double longitude2 = polygon.getPoints().get(i + 1).longitude;


            float[] distance = new float[1];
            Location.distanceBetween(latitude1, longitude1, latitude2, longitude2, distance);

            distance[0] = distance[0] / 1000;
            if (i == 3) {
                distanceText = distanceText + String.format("%.1f", distance[0]) + "km";
            } else {
                distanceText = distanceText + String.format("%.1f", distance[0]) + "km -- ";
            }
        }


        final TextView textView = new TextView(this);


        String textDistance = distanceText;

        textView.setText(textDistance);

        final Paint paintText = textView.getPaint();

        final Rect boundsText = new Rect();
        paintText.getTextBounds(textDistance, 0, textView.length(), boundsText);
        paintText.setTextAlign(Paint.Align.CENTER);

        final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        final Bitmap bmpText = Bitmap.createBitmap(boundsText.width() + 2
                * 10, boundsText.height() + 2 * 10, conf);

        final Canvas canvasText = new Canvas(bmpText);
        paintText.setColor(Color.BLACK);

        canvasText.drawText(textDistance, canvasText.getWidth() / 2,
                canvasText.getHeight() - 10 - boundsText.bottom, paintText);

        LatLng center = getCenterOfPolygon(latLngList);
        System.out.println("Clicked: center: " + center);
        polygonDistance = mMap.addMarker(new MarkerOptions()
                .position(center)
                .icon(BitmapDescriptorFactory.fromBitmap(bmpText)));
    }

    @Override
    public void onPolylineClick(Polyline polyline) {

        double latitude1 = polyline.getPoints().get(0).latitude;
        double longitude1 = polyline.getPoints().get(0).longitude;


        double latitude2 = polyline.getPoints().get(1).latitude;
        double longitude2 = polyline.getPoints().get(1).longitude;

        float[] distance = new float[1];
        Location.distanceBetween(latitude1, longitude1, latitude2, longitude2, distance);

        final TextView textView = new TextView(this);
        distance[0] = distance[0] / 1000;

        String textDistance = String.format("%.1f", distance[0]) + " km";

        textView.setText(textDistance);
        textView.setTextSize(18.0f);

        final Paint paintText = textView.getPaint();

        final Rect boundsText = new Rect();
        paintText.getTextBounds(textDistance, 0, textView.length(), boundsText);
        paintText.setTextAlign(Paint.Align.CENTER);

        final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        final Bitmap bmpText = Bitmap.createBitmap(boundsText.width() + 2
                * 10, boundsText.height() + 2 * 10, conf);

        final Canvas canvasText = new Canvas(bmpText);
        paintText.setColor(Color.BLACK);

        canvasText.drawText(textDistance, canvasText.getWidth() / 2,
                canvasText.getHeight() - 10 - boundsText.bottom, paintText);

        LatLng start = new LatLng(latitude1, longitude1);
        LatLng dest = new LatLng(latitude2, longitude2);

        LatLng center = LatLngBounds.builder().include(start).include(dest).build().getCenter();

        if (polyline.getTag().equals("AB")) {
            ABDistance = mMap.addMarker(new MarkerOptions()
                    .position(center)
                    .icon(BitmapDescriptorFactory.fromBitmap(bmpText)));
        } else if (polyline.getTag().equals("BC")) {
            BCDistance = mMap.addMarker(new MarkerOptions()
                    .position(center)
                    .icon(BitmapDescriptorFactory.fromBitmap(bmpText)));

        } else if (polyline.getTag().equals("CD")) {
            CDDistance = mMap.addMarker(new MarkerOptions()
                    .position(center)
                    .icon(BitmapDescriptorFactory.fromBitmap(bmpText)));

        } else if (polyline.getTag().equals("DA")) {
            DADistance = mMap.addMarker(new MarkerOptions()
                    .position(center)
                    .icon(BitmapDescriptorFactory.fromBitmap(bmpText)));

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
        } else {
            finish();
        }
    }


    protected synchronized void buildGoogleApiClient() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    mMap.setMyLocationEnabled(true);
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsActivity.this, 1000);
                    } catch (IntentSender.SendIntentException ignored) {
                    }
                }
            }
        });
    }

    private void drawPolygon() {
        if (isDisplayingA && isDisplayingB && isDisplayingC && isDisplayingD && !isDisplayingPolygon) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                polygon = mMap.addPolygon(new PolygonOptions()
                        .clickable(true)
                        .strokeColor(0)
                        .fillColor(getColor(R.color.green))
                        .add(mapMarkers.get(0).getPosition(),
                                mapMarkers.get(1).getPosition(),
                                mapMarkers.get(2).getPosition(),
                                mapMarkers.get(3).getPosition()));
                isDisplayingPolygon = true;
            }
        }
    }

    private String getCountryName(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String countryName = "";
        if (addresses != null && addresses.size() > 0) {
            countryName = addresses.get(0).getCountryName();
        }
        return countryName;
    }

    private static LatLng getCenterOfPolygon(List<LatLng> latLngList) {
        double[] centroid = {0.0, 0.0};
        for (int i = 0; i < latLngList.size(); i++) {
            centroid[0] += latLngList.get(i).latitude;
            centroid[1] += latLngList.get(i).longitude;
        }
        int totalPoints = latLngList.size();
        return new LatLng(centroid[0] / totalPoints, centroid[1] / totalPoints);
    }

    private void getTitleSnippet(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String thoroughFare = "";
        String subThoroughFare = "";
        String postalCode = "";
        String city = "";
        String province = "";
        if (addresses != null && addresses.size() > 0) {
            postalCode = addresses.get(0).getPostalCode();
            thoroughFare = addresses.get(0).getThoroughfare();
            subThoroughFare = addresses.get(0).getSubThoroughfare();
            city = addresses.get(0).getLocality();
            province = addresses.get(0).getAdminArea();
        }

        title = thoroughFare + ", " + subThoroughFare + ", " + postalCode;
        snippet = city + ", " + province;
    }

    public void initLocCallback() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                if (!cameraAnimated) {
                    cameraAnimated = true;
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(locationResult.getLocations().get(0).getLatitude(),
                                    locationResult.getLocations().get(0).getLongitude()), 15));
                }
            }
        };
    }

}