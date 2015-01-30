package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

public class FragmentMap extends Fragment {

    MapView mapView;
    static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    static final LatLng KIEL = new LatLng(53.551, 9.993);
    private GoogleMap map;
    private GoogleMap googleMap;
    static final LatLng TutorialsPoint = new LatLng(21 , 57);


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.map, container, false);
        //mapView = (MapView)v.findViewById(R.id.map);
//        mapView.onCreate(savedInstanceState);
//
//        googleMap = mapView.getMap();
//        googleMap.setMyLocationEnabled(true);

        try {
            if (map == null) {
                map = ((MapFragment)((MaterialNavigationDrawer)this.getActivity()).getFragmentManager().
                        findFragmentById(R.id.map)).getMap();
            }
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            Marker TP = map.addMarker(new MarkerOptions().
                    position(TutorialsPoint).title("TutorialsPoint"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }
    @Override
    public void onResume() {

        try{
            super.onResume();
            mapView.onResume();
        }catch(NullPointerException e){
            Log.d("onResume", "NullPointerException: " + e);
        }
    }

    @Override
    public void onDestroy() {
        try{
            super.onDestroy();
            mapView.onDestroy();
        }catch(NullPointerException e){
            Log.d("onDestroy", "NullPointerException: " + e);
        }
    }

    @Override
    public void onLowMemory() {
        try{
            super.onLowMemory();
            mapView.onLowMemory();
        }catch(NullPointerException e){
            Log.d("onLowMemory", "NullPointerException: " + e);
        }
    }
}
