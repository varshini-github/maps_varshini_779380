package com.techworx.maps_varshini_779380;

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
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
        GoogleMap.OnPolylineClickListener,
        GoogleMap.OnPolygonClickListener {

    private GoogleMap mMap;
    private String title, snippet;
    private ArrayList<Marker> mapMarkers;
    private ArrayList<Polyline> polyLines;
    private Polygon polygon;
    boolean isDisplayingA, isDisplayingB, isDisplayingC, isDisplayingD, isDisplayingPolygon;
    private Marker polygonDistance, ABDistance, BCDistance, CDDistance, DADistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapMarkers = new ArrayList<>();
        polyLines = new ArrayList<>();
        isDisplayingD = false;
        isDisplayingC = false;
        isDisplayingB = false;
        isDisplayingA = false;
        isDisplayingPolygon = false;


        LatLng canada = new LatLng(50.0000, -85.0000);
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                canada, 8);
        mMap.animateCamera(location);
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
                                    if(BCDistance!=null){
                                        BCDistance.remove();
                                    }
                                }
                                if (polyLines.size() > 2) {
                                    polyLines.get(2).remove();
                                    if(CDDistance!=null){
                                        CDDistance.remove();
                                    }
                                }
                                marker.remove();
                                isDisplayingC = false;
                            } else if (marker.getTag().equals("D")) {
                                if (polyLines.size() > 2) {
                                    polyLines.get(2).remove();
                                    if(CDDistance!=null){
                                        CDDistance.remove();
                                    }
                                }
                                if (polyLines.size() > 3) {
                                    polyLines.get(3).remove();
                                    if(DADistance!=null){
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

    private static LatLng getCenterOfPolygon(List<LatLng> latLngList) {
        double[] centroid = {0.0, 0.0};
        for (int i = 0; i < latLngList.size(); i++) {
            centroid[0] += latLngList.get(i).latitude;
            centroid[1] += latLngList.get(i).longitude;
        }
        int totalPoints = latLngList.size();
        return new LatLng(centroid[0] / totalPoints, centroid[1] / totalPoints);
    }
}