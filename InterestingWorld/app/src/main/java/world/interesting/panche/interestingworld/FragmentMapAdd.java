package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class FragmentMapAdd extends Fragment {

    private MapView mMapView;
    private GoogleMap mMap;
    private Bundle mBundle;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.map, container, false);

        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            System.out.println("error map");
// TODO handle this situation
        }
        System.out.println("OncreateView");
        mMapView = (MapView) inflatedView.findViewById(R.id.map);
        mMapView.onCreate(mBundle);
        mMapView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        // The user took their finger off the map,
                        // they probably just moved it to a new place.
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // The user is probably moving the map.
                        break;
                }

                // Return false so that the map still moves.
                return false;
            }
        });
        setUpMapIfNeeded(inflatedView);

        return inflatedView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;
        if (mMap != null) {setUpMap();}
    }

    private void setUpMapIfNeeded(View inflatedView) {
        if (mMap == null) {
            mMap = ((MapView) inflatedView.findViewById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        mMap.addMarker(new MarkerOptions().position(new LatLng(41.41775257865992,
                2.2058293414581683)).title("Marker").draggable(true));
        mMap.setMyLocationEnabled(true);
        centerCity();
        System.out.println("Mapa seteado");

        //mMap.animateCamera(CameraUpdateFactory.zoomBy(15));
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        System.out.println("onresume");
        setUpMap();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }
    public void centerCity()
    {
        LatLng madrid = new LatLng(41.41775257865992,
                2.2058293414581683);
        CameraPosition camPos = new CameraPosition.Builder()
                .target(madrid)   //Centramos el mapa en Madrid
                .zoom(10)         //Establecemos el zoom en 19
                .build();

        CameraUpdate camUpd3 =
                CameraUpdateFactory.newCameraPosition(camPos);

        mMap.animateCamera(camUpd3);
    }
}
