package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class FragmentLocationDetail extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();

        //Seteamos la estructura del fragment
        View inflatedView = inflater.inflate(R.layout.scroll_one_parrallax_alpha, container, false);

        //Cambiamos el icono del menu por el de volver atras
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if(this.getActivity().getLocalClassName().equals("MainActivity"))
        {
            actionBar.setHomeAsUpIndicator(((MainActivity) this.getActivity()).getV7DrawerToggleDelegate().getThemeUpIndicator());
        }else{
            actionBar.setHomeAsUpIndicator(((MainActivityUser) this.getActivity()).getV7DrawerToggleDelegate().getThemeUpIndicator());
        }


        //TextView title = (TextView) inflatedView.findViewById(R.id.TextViewTitle);
        TextView description = (TextView) inflatedView.findViewById(R.id.TextViewDescription);
        ImageView photoDetail = (ImageView) inflatedView.findViewById(R.id.ImageDetail);
        if(bundle!=null) {
            bundle.getString("id_location", "-1");
            String title_txt = bundle.getString("title", "");
            String description_txt = bundle.getString("description", "");
            String lat=bundle.getString("lat", "");
            String lng=bundle.getString("lng", "");
            String url_photo=bundle.getString("url_photo", "");
            String user_location=bundle.getString("user_location", "");

            //title.setText(title_txt);
            description.setText(description_txt);
            Picasso.with(getActivity())
                    .load("http://" + url_photo)
                    .error(R.drawable.ic_launcher)
                    .fit().centerCrop()
                    .into(photoDetail);
        }


        return inflatedView;
    }
}
