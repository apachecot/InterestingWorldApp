package world.interesting.panche.interestingworld;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialAccountListener;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialSectionListener;


public class MainActivityUser extends MaterialNavigationDrawer implements MaterialAccountListener {

    MaterialAccount account;
    MaterialSection lastLocations, explore, photos, addlocation, visitlocation,likedlocations, settingsSection;
    User user;
    String[] datos= new String[5];
    File file_image= new File("");
    String photo_url;
    Double lat=0.0;
    Double lng=0.0;
    String country="";
    String locality="";
    String address="";
    String id_location="";
    Location locationSelected;
    String url_full="";
    String id_image_selected="";
    private Picasso mPicasso;
    Intent i;

    @Override
    public void init(Bundle savedInstanceState) {


        mPicasso = new Picasso.Builder(this).executor(Executors.newSingleThreadExecutor()).build();
        //mPicasso.setIndicatorsEnabled(true);

        //Cargamos las preferencias guardadas
        datos=Preferences.loadPreferences(this);
        user=new User(datos[0],datos[1],datos[2],datos[4],datos[3]);
        photo_url=datos[4];
        //Llamamos a cargar la imagen
        loadImage("http://"+photo_url);

        //Creamos la cuenta, con la imagen por defecto, si existe una imagen en el servidor se cambiará
        account = new MaterialAccount(this.getResources(),datos[1]+" "+datos[2],datos[3],R.drawable.defaultuser,R.drawable.back3);

        //Añadimos la cuenta al menú
        this.addAccount(account);

        // Seteamos la opción de pulsar a la cuenta
        this.setAccountListener(this);

        // Ultimas localizaciones
        lastLocations = this.newSection(this.getResources().getString(R.string.lastlocations),this.getResources().getDrawable(R.drawable.map), new FragmentIndex()).setSectionColor(Color.parseColor("#03a9f4"));

        //Explorar el mapa cercano
        explore = this.newSection(this.getResources().getString(R.string.explore), this.getResources().getDrawable(R.drawable.location), new FragmentMap()).setSectionColor(Color.parseColor("#03a9f4"));

        // Explorar fotografías
        photos = this.newSection(this.getResources().getString(R.string.photos), this.getResources().getDrawable(R.drawable.photo), new FragmentPhotos()).setSectionColor(Color.parseColor("#03a9f4"));

        // Añadir una localización
        addlocation = this.newSection(this.getResources().getString(R.string.addlocation), this.getResources().getDrawable(R.drawable.addlocation), new FragmentAddLocation())
                .setSectionColor(Color.parseColor("#2196f3"),Color.parseColor("#1565c0"));

        //Puntos visitados
        visitlocation = this.newSection(this.getResources().getString(R.string.visitlocation), this.getResources().getDrawable(R.drawable.visit), new FragmentLocationsVisited())
                .setSectionColor(Color.parseColor("#2196f3"),Color.parseColor("#1565c0"));
        //Puntos que te gustan
        likedlocations = this.newSection(this.getResources().getString(R.string.likedlocations), this.getResources().getDrawable(R.drawable.like), new FragmentLocationsLiked())
                .setSectionColor(Color.parseColor("#2196f3"),Color.parseColor("#1565c0"));

        //Para cargar la configuración

        settingsSection = this.newSection(this.getResources().getString(R.string.logout),this.getResources().getDrawable(R.drawable.settings),new MaterialSectionListener() {
            @Override
            public void onClick(MaterialSection section) {
                restoreUser();
            }
        });

        // add your sections to the drawer
        this.addSection(lastLocations);
        this.addSection(explore);
        this.addSection(photos);
        this.addSection(addlocation);
        this.addSection(visitlocation);
        this.addSection(likedlocations);
        //this.addSubheader("Opciones");
        //this.addDivisor();
        this.addBottomSection(settingsSection);



        this.setBackPattern(MaterialNavigationDrawer.BACKPATTERN_BACK_TO_FIRST);
        //this.closeOptionsMenu();
        this.addMultiPaneSupport();
        this.allowArrowAnimation();
        this.disableLearningPattern();

    }


    @Override
    public void onAccountOpening(MaterialAccount account) {
        // open profile activity
        intentProfile();
    }

    @Override
    public void onChangeAccount(MaterialAccount newAccount) {
        // when another account is selected
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    // Función para cargar la imagen provinente de internet
    public void loadImage(String path)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(path, new FileAsyncHttpResponseHandler(/* Context */ this) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

                System.out.println("falla");

            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                // Do something with the file `response`
                file_image=response;
                Bitmap myBitmap = BitmapFactory.decodeFile(file_image.getAbsolutePath());
                imageLoaded(myBitmap);
                System.out.println("bien");

            }
        });
    }
    //Cuando la imagen este cargada se llama a esta función
    public void imageLoaded (Bitmap image)
    {
        //Seteamos la nueva imagen cargada
        account.setPhoto(image);
        //Notificamos que la información ha cambiado
        this.notifyAccountDataChanged();
    }
    public void setPosition(double vlat, double vlng)
    {
        TextView latlng=(TextView)this.findViewById(R.id.textViewLatLng);
        locality="";
        country="";
        address="";

        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(vlat, vlng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            for(int i = addresses.get(0).getMaxAddressLineIndex();i>=0;i--)
            {
                if(i==addresses.get(0).getMaxAddressLineIndex())
                {
                    country=addresses.get(0).getAddressLine(i);
                }
                else
                {
                    if(i==(addresses.get(0).getMaxAddressLineIndex()-1))
                    {
                        locality=addresses.get(0).getAddressLine(i);
                    }
                    else
                    {

                        address = address + " " + addresses.get(0).getAddressLine(i);

                    }
                }
            }
            System.out.println(address + " " + locality + " " + country);
        }
        latlng.setText(address+" "+locality+" "+country);
        lat=vlat;
        lng=vlng;

    }
    public void UnSetPosition()
    {
        locality="";
        country="";
        address="";
        lat=0.0;
        lng=0.0;
    }

    public void intentProfile() {
        Fragment fragment = new FragmentProfile();
        ((MaterialNavigationDrawer) this).setFragmentChild(fragment, "Profile");

    }

    public Double getLatitude()
    {
        return lat;
    }
    public Double getLongitude()
    {
        return lng;
    }

    public void SetLocationSelected(Location loc)
    {
        locationSelected=loc;
    }
    public Location GetLocationSelected()
    {
        return locationSelected;
    }
    public void SetImageUrlFull(String url,String id)
    {
        url_full=url;
        id_image_selected=id;
    }
    public ArrayList<String> GetImageUrlFull()
    {
        ArrayList<String> selected=new ArrayList<String>();
        selected.add(url_full);
        selected.add(id_image_selected);

        return selected;
    }
    public Picasso getmPicasso() {
        return mPicasso;
    }

    public User getUser() {
        return user;
    }

    public String getCountry() {
        return country;
    }

    public String getLocality() {
        return locality;
    }

    public String getAddress() {
        return address;
    }

    public void restoreUser()
    {
        Preferences.RestorePreferences(this);
        i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }


}

