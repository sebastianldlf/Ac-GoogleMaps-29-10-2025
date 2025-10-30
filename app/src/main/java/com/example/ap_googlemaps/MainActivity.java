package com.example.ap_googlemaps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity  implements OnMapReadyCallback {

    private GoogleMap myMap;
    private final Map<com.google.android.gms.maps.model.Marker, String> markerExtraInfo = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            android.widget.Toast.makeText(this, "No se pudo cargar el mapa", android.widget.Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;

        // Personalizar InfoWindow
        myMap.setInfoWindowAdapter(new FamilyInfoWindowAdapter(this, markerExtraInfo));

        // Habilitar controles nativos de zoom
        myMap.getUiSettings().setZoomControlsEnabled(true);

        // Centrar inicialmente en Bolivia (Cochabamba por defecto)
        LatLng cochabambaCenter = new LatLng(-17.3895, -66.1568);
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cochabambaCenter, 6.8f));

        // Agregar ubicaciones familiares
        addFamilyMarkers();

        // Manejo simple de error de carga del mapa
        myMap.setOnMapLoadedCallback(() -> {
            // Mapa cargado correctamente
        });

        // Botones de zoom personalizados
        ImageButton btnZoomIn = findViewById(R.id.btnZoomIn);
        ImageButton btnZoomOut = findViewById(R.id.btnZoomOut);
        if (btnZoomIn != null) {
            btnZoomIn.setOnClickListener(v -> myMap.animateCamera(CameraUpdateFactory.zoomIn()));
        }
        if (btnZoomOut != null) {
            btnZoomOut.setOnClickListener(v -> myMap.animateCamera(CameraUpdateFactory.zoomOut()));
        }
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture){
        super.onPointerCaptureChanged(hasCapture);
    }

    private void addFamilyMarkers() {
        // Abuela (Cochabamba): Pasaje Márquez (aprox)
        addMarker(
                new LatLng(-17.3839, -66.1530),
                "Abuela",
                "Pasaje Márquez (Cochabamba)",
                Category.GRANDPARENT,
                "Vivió muchos años en el Pasaje Márquez, cerca del Jardín Botánico."
        );

        // Mis padres y yo (Santa Cruz)
        addMarker(
                new LatLng(-17.74625, -63.1959444444),
                "Mis padres y yo",
                "Condominio Costanera Blue (Santa Cruz)",
                Category.PARENT,
                "Hogar familiar principal."
        );

        // Mi hermano
        addMarker(
                new LatLng(-17.69975, -63.1293055556),
                "Hermano",
                "Condominio Valle del Cartago (Santa Cruz)",
                Category.SIBLING,
                "Vive cerca del cuarto anillo."
        );

        // Mi hermana (Argentina)
        addMarker(
                new LatLng(-31.4169, -64.1850),
                "Hermana",
                "Córdoba (Argentina), cerca de la estación de buses",
                Category.SIBLING,
                "Departamento temporal durante estudios."
        );

        // Mi otra abuela (Cochabamba)
        addMarker(
                new LatLng(-17.3890, -66.1560),
                "Abuela",
                "Torres Soffer (Cochabamba)",
                Category.GRANDPARENT,
                "Residencia en edificio céntrico."
        );

        // Mi tía (Estados Unidos)
        addMarker(
                new LatLng(25.9607, -80.1393),
                "Tía",
                "20350 West Country Club Dr, Aventura (EE.UU.)",
                Category.AUNT,
                "Cerca del Aventura Circle."
        );
    }

    private void addMarker(LatLng position, String title, String snippet, Category category, String extraInfo) {
        BitmapDescriptor icon = bitmapFromVector(this, getDrawableForCategory(category));
        MarkerOptions options = new MarkerOptions()
                .position(position)
                .title(title)
                .snippet(snippet)
                .icon(icon != null ? icon : BitmapDescriptorFactory.defaultMarker(getHueForCategory(category)));
        com.google.android.gms.maps.model.Marker marker = myMap.addMarker(options);
        if (marker != null) {
            markerExtraInfo.put(marker, extraInfo);
        }
    }

    private float getHueForCategory(Category category) {
        switch (category) {
            case GRANDPARENT:
                return BitmapDescriptorFactory.HUE_ORANGE;
            case PARENT:
                return BitmapDescriptorFactory.HUE_BLUE;
            case SIBLING:
                return BitmapDescriptorFactory.HUE_GREEN;
            case AUNT:
                return BitmapDescriptorFactory.HUE_RED;
            default:
                return BitmapDescriptorFactory.HUE_ROSE;
        }
    }

    private int getDrawableForCategory(Category category) {
        switch (category) {
            case GRANDPARENT:
                return R.drawable.ic_marker_grandparent;
            case PARENT:
                return R.drawable.ic_marker_parent;
            case SIBLING:
                return R.drawable.ic_marker_sibling;
            case AUNT:
                return R.drawable.ic_marker_aunt;
            default:
                return R.drawable.ic_marker_parent;
        }
    }

    private BitmapDescriptor bitmapFromVector(Context context, int vectorResId) {
        try {
            android.graphics.drawable.Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
            if (vectorDrawable == null) return null;
            int width = vectorDrawable.getIntrinsicWidth() > 0 ? vectorDrawable.getIntrinsicWidth() : 96;
            int height = vectorDrawable.getIntrinsicHeight() > 0 ? vectorDrawable.getIntrinsicHeight() : 96;
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        } catch (Exception e) {
            android.widget.Toast.makeText(this, "Error con ícono de marcador", android.widget.Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private enum Category { GRANDPARENT, PARENT, SIBLING, AUNT }

    private static class FamilyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View window;
        private final Map<com.google.android.gms.maps.model.Marker, String> extraInfo;

        FamilyInfoWindowAdapter(Context context, Map<com.google.android.gms.maps.model.Marker, String> extraInfo) {
            this.window = LayoutInflater.from(context).inflate(R.layout.info_window, null);
            this.extraInfo = extraInfo;
        }

        @Override
        public View getInfoWindow(@NonNull com.google.android.gms.maps.model.Marker marker) {
            render(marker);
            return window;
        }

        @Override
        public View getInfoContents(@NonNull com.google.android.gms.maps.model.Marker marker) {
            return null; // Usamos getInfoWindow
        }

        private void render(com.google.android.gms.maps.model.Marker marker) {
            TextView title = window.findViewById(R.id.title);
            TextView snippet = window.findViewById(R.id.snippet);
            TextView extra = window.findViewById(R.id.extra);
            title.setText(marker.getTitle());
            snippet.setText(marker.getSnippet());
            String extraStr = extraInfo.get(marker);
            extra.setText(extraStr != null ? extraStr : "");
        }
    }
}