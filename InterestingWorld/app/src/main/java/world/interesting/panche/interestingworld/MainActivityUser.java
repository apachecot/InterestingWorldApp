package world.interesting.panche.interestingworld;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialAccountListener;


public class MainActivityUser extends MaterialNavigationDrawer implements MaterialAccountListener {

    MaterialAccount account;
    MaterialSection lastLocations, explore, photos, addlocation, visitlocation, settingsSection;
    String[] datos= new String[5];
    File file_image= new File("");
    String photo_url;

    @Override
    public void init(Bundle savedInstanceState) {

        //Cargamos las preferencias guardadas
        datos=loadPreferences();
        photo_url=datos[4];
        //Llamamos a cargar la imagen
        loadImage("http://"+photo_url);

        //Creamos la cuenta, con la imagen por defecto, si existe una imagen en el servidor se cambiará
        account = new MaterialAccount(this.getResources(),datos[1]+" "+datos[2],datos[3],R.drawable.defaultuser,R.drawable.back1);

        //Añadimos la cuenta al menú
        this.addAccount(account);

        // Seteamos la opción de pulsar a la cuenta
        this.setAccountListener(this);

        // Ultimas localizaciones
        lastLocations = this.newSection(this.getResources().getString(R.string.lastlocations),this.getResources().getDrawable(R.drawable.map), new FragmentIndex()).setSectionColor(Color.parseColor("#CC0000"));

        //Explorar el mapa cercano
        explore = this.newSection(this.getResources().getString(R.string.explore), this.getResources().getDrawable(R.drawable.location), new FragmentMap()).setSectionColor(Color.parseColor("#9c27b0"));

        // Explorar fotografías
        photos = this.newSection(this.getResources().getString(R.string.photos), this.getResources().getDrawable(R.drawable.photo), new FragmentAdd()).setSectionColor(Color.parseColor("#03a9f4"));

        // Añadir una localización
        addlocation = this.newSection(this.getResources().getString(R.string.addlocation), this.getResources().getDrawable(R.drawable.addlocation), new FragmentAddLocation())
                .setSectionColor(Color.parseColor("#2196f3"),Color.parseColor("#1565c0"));

        visitlocation = this.newSection(this.getResources().getString(R.string.visitlocation), this.getResources().getDrawable(R.drawable.visit), new FragmentAdd())
                .setSectionColor(Color.parseColor("#2196f3"),Color.parseColor("#1565c0"));

        //Para cargar la configuración
        Intent i = new Intent(this,Settings.class);
        settingsSection = this.newSection(this.getResources().getString(R.string.settings),this.getResources().getDrawable(R.drawable.settings),i);

        // add your sections to the drawer
        this.addSection(lastLocations);
        this.addSection(explore);
        this.addSection(photos);
        this.addSection(addlocation);
        this.addSection(visitlocation);
        //this.addSubheader("Opciones");
        //this.addDivisor();
        this.addBottomSection(settingsSection);

        this.setBackPattern(MaterialNavigationDrawer.BACKPATTERN_BACK_TO_FIRST);
        this.closeOptionsMenu();
        //this.addMultiPaneSupport();
        this.allowArrowAnimation();
        this.disableLearningPattern();
    }


    @Override
    public void onAccountOpening(MaterialAccount account) {
        // open profile activity
        Intent i = new Intent(this,Profile.class);
        startActivity(i);
    }

    @Override
    public void onChangeAccount(MaterialAccount newAccount) {
        // when another account is selected
    }

    //cargar configuración aplicación Android usando SharedPreferences
    public String[] loadPreferences() {
        String[] datos=new String[5];
        SharedPreferences prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        datos[0] = prefs.getString("id", "-1");
        datos[1] = prefs.getString("name", "");
        datos[2] = prefs.getString("lastname", "");
        datos[3] = prefs.getString("email", "");
        datos[4] = prefs.getString("photo_url", "");

        for(int i=0; i < datos.length; i++) {
            System.out.println(datos[i]);
        }
        return datos;
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
}

