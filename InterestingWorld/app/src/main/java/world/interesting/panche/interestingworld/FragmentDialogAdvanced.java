package world.interesting.panche.interestingworld;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.devspark.appmsg.AppMsg;
import com.google.android.gms.maps.SupportMapFragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * Created by Alex on 10/02/2015.
 */
public class FragmentDialogAdvanced extends DialogFragment {
    private SupportMapFragment fragment;
    private BootstrapButton bAccept,bCancel;
    EditText advanced;
    Spinner categoryList;
    Category cat=new Category();
    CheckBox near;
    final String gpsLocationProvider = LocationManager.GPS_PROVIDER;
    final String networkLocationProvider = LocationManager.NETWORK_PROVIDER;


    public FragmentDialogAdvanced() {
        fragment = new SupportMapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_dialog_advanced, container, false);
        getDialog().requestWindowFeature(STYLE_NORMAL);
        getDialog().setTitle("Busqueda avanzada");

        advanced=(EditText)view.findViewById(R.id.editTextAdvanced);
        bAccept = (BootstrapButton)view.findViewById(R.id.dialogButtonOk);
        bAccept.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                   buttonAccept();
            }
        });
        bCancel = (BootstrapButton)view.findViewById(R.id.dialogButtonCancel);
        bCancel.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        near=(CheckBox) view.findViewById(R.id.checkBoxNear);

        categoryList=(Spinner) view.findViewById(R.id.spinnerCategory);
        ArrayAdapter<String> dataAdapter= new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_spinner_dropdown_item,cat.GetList());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryList.setAdapter(dataAdapter);


        return view;
    }

    public void buttonAccept(){

        if(getActivity() instanceof MainActivityUser) {

            if(near.isChecked())
            {
                ((MainActivityUser) getActivity()).setAdvancedSearch(_getLocation());
            }else{
                ((MainActivityUser) getActivity()).setAdvancedSearch(advanced.getText().toString());
            }
            ((MainActivityUser) getActivity()).setCategory(cat.GetIdCategory(String.valueOf(categoryList.getSelectedItem())));
            getTargetFragment().onActivityResult(getTargetRequestCode(), 1, getActivity().getIntent());
            getDialog().dismiss();
        }else{
            if(near.isChecked())
            {
                ((MainActivity) getActivity()).setAdvancedSearch(_getLocation());
            }else{
                ((MainActivity) getActivity()).setAdvancedSearch(advanced.getText().toString());
            }
            ((MainActivity) getActivity()).setCategory(cat.GetIdCategory(String.valueOf(categoryList.getSelectedItem())));
            getTargetFragment().onActivityResult(getTargetRequestCode(), 1, getActivity().getIntent());
            getDialog().dismiss();
        }
    }
    public SupportMapFragment getFragment() {
        return fragment;
    }

    private String _getLocation() {
        LocationManager locationManager =(LocationManager)getActivity().getSystemService(getActivity().LOCATION_SERVICE);

        Location lastKnownLocation_byGps = locationManager.getLastKnownLocation(gpsLocationProvider);
        Location lastKnownLocation_byNetwork =locationManager.getLastKnownLocation(networkLocationProvider);
        String result="";

        if(lastKnownLocation_byGps==null)
        {
            if(lastKnownLocation_byNetwork!=null)
            {
                result=getCity(lastKnownLocation_byNetwork.getLatitude(),lastKnownLocation_byNetwork.getLongitude());
            }else{
                AppMsg.makeText(getActivity(), "No se ha podido obtener su dirección, revise que tenga activado el gps", AppMsg.STYLE_CONFIRM).setLayoutGravity(Gravity.BOTTOM).show();
            }
        }
        else
        {
            result= getCity(lastKnownLocation_byGps.getLatitude(),lastKnownLocation_byGps.getLongitude());
        }

       return result;
    }
    public String getCity(double vlat, double vlng)
    {
        Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(vlat, vlng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses.get(0).getLocality();
    }

}