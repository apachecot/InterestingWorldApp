package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

public class Profile extends Fragment {

    /** Alpha Toolbar **/

    View inflatedView;

    String[] datos= new String[5];
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflatedView = inflater.inflate(R.layout.profile, container, false);

        datos=loadPreferences();
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setHomeAsUpIndicator(((MainActivityUser) this.getActivity()).getV7DrawerToggleDelegate().getThemeUpIndicator());
        return inflatedView;
    }
    public String[] loadPreferences() {
        String[] datos=new String[5];
        SharedPreferences prefs = this.getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        datos[0] = prefs.getString("id", "-1");
        datos[1] = prefs.getString("name", "");
        datos[2] = prefs.getString("lastname", "");
        datos[3] = prefs.getString("email", "");
        datos[4] = prefs.getString("photo_url", "");

        for(int i=0; i < datos.length; i++) {
            System.out.println(datos[i]);
        }
        SmartImageView myImage = (SmartImageView) inflatedView.findViewById(R.id.my_image);
        myImage.setImageUrl("http://"+datos[4]);
        TextView name = (TextView) inflatedView.findViewById(R.id.textViewName);
        TextView email = (TextView) inflatedView.findViewById(R.id.textViewEmail);
        name.setText(datos[1]+" "+datos[2]);
        email.setText(datos[3]);
        return datos;
    }
}