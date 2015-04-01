package world.interesting.panche.interestingworld;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Alex on 10/02/2015.
 */
public class FragmentDialogMap extends DialogFragment {
    private SupportMapFragment fragment;
    private MapView mMapView;
    private GoogleMap mMap;
    private Bundle mBundle;
    private BootstrapButton bAccept,bCancel;
    private MarkerOptions marker;
    public FragmentDialogMap() {
        fragment = new SupportMapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_dialog_map, container, false);
        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.map, fragment).commit();
        bAccept = (BootstrapButton)view.findViewById(R.id.dialogButtonOk);
        bAccept.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                ((MainActivityUser) getActivity()).setPosition(marker.getPosition().latitude,marker.getPosition().longitude);
                getDialog().dismiss();
            }
        });
        bCancel = (BootstrapButton)view.findViewById(R.id.dialogButtonCancel);
        bCancel.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            System.out.println("error map");
// TODO handle this situation
        }
        System.out.println("OncreateView");
        mMapView = (MapView) view.findViewById(R.id.map);
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

        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;
    }

    private void setUpMapIfNeeded(View inflatedView) {
        if (mMap == null) {
            mMap = ((MapView) inflatedView.findViewById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mMap=fragment.getMap();
        setUpMap();
    }

    private void setUpMap() {

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng point) {
                // TODO Auto-generated method stub
                marker.position(point);
                mMap.clear();
                mMap.addMarker(marker);

            }
        });
       marker=new MarkerOptions().position(new LatLng(41.41775257865992,
               2.2058293414581683)).title("Marker").draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMap.addMarker(marker);
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


    public SupportMapFragment getFragment() {
        return fragment;
    }

}