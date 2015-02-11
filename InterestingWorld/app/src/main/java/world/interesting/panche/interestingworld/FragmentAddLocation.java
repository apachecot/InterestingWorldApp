package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

public class FragmentAddLocation extends Fragment {

    EditText name,description;
    ImageView image_button;
    Button bAccept;
    private ProgressDialog pDialog;
    private BD bd = new BD();
    String result;
    String[] datos= new String[5];
    Uri selectedImage;
    InputStream is;

    FragmentManager fm;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        fm= this.getActivity().getSupportFragmentManager();
        View inflatedView = inflater.inflate(R.layout.new_location, container, false);

        return inflatedView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MaterialNavigationDrawer)this.getActivity()).getSupportActionBar().show();
        name=(EditText)this.getActivity().findViewById(R.id.editTextName);
        description=(EditText)this.getActivity().findViewById(R.id.editTextDescription);
        bAccept = (Button)this.getActivity().findViewById(R.id.buttonEnviar);
        bAccept.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                try {
                    buttonAccept(v);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        image_button = (ImageView) getView().findViewById(R.id.ImageViewLocation);
        // set a onclick listener for when the button gets clicked
        image_button.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                selectImage(v);
            }
        });
    }
    public void buttonAccept(View view) throws JSONException, FileNotFoundException {
        //Inicializamos dialog
        pDialog = new ProgressDialog(FragmentAddLocation.this.getActivity());
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Procesando...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);

        datos=loadPreferences();

        result="";

        AsyncHttpClient client = new AsyncHttpClient();

        Double lat=((MainActivityUser) getActivity()).getLatitude();
        Double lng=((MainActivityUser) getActivity()).getLongitude();


        if(lat!=0.0) {
            String url = "http://interestingworld.webcindario.com/insert_location.php";
            RequestParams params = new RequestParams();
            params.put("id_user", datos[0]);
            params.put("name", name.getText());
            params.put("description", description.getText());
            params.put("lat", lat);
            params.put("lng", lng);
            //Cargar la imagen
            int numero = (int) (Math.random() * 99999999) + 1;
            //is = this.getActivity().getContentResolver().openInputStream(selectedImage);
            params.put("photo_url", is, numero + "_upload.jpg");


            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    pDialog.setProgress(0);
                    pDialog.show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == 200) {

                        try {
                            System.out.println(new String(responseBody));
                            setResult(new String(responseBody));
                            System.out.println(getResult());
                            if (getResult().equals("bien")) {
                                Toast.makeText(FragmentAddLocation.this.getActivity(), "Punto de interés añadido correctamente", Toast.LENGTH_SHORT).show();
                                entrar();
                            } else {
                                Toast.makeText(FragmentAddLocation.this.getActivity(), "Error al intentar subir los datos, comprueba que todo este correcto", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            System.out.println("Falla:" + e);
                            Toast.makeText(FragmentAddLocation.this.getActivity(), "Error al intentar subir los datos, comprueba que todo este correcto", Toast.LENGTH_SHORT).show();
                        }
                    }
                    pDialog.hide();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(FragmentAddLocation.this.getActivity(), "Parece que hay algún problema con la red", Toast.LENGTH_SHORT).show();
                    pDialog.hide();
                }
            });
        }else
        {
            Toast.makeText(FragmentAddLocation.this.getActivity(), "Debes marcar el punto de interés antes de publicar", Toast.LENGTH_SHORT).show();
            pDialog.hide();
        }
    }
    public String getResult (){


        return this.result;
    }
    public String setResult (String result) throws JSONException {
        String cadenaJSON = result.toString();//Le pasamos a la variable cadenaJSON una cadena de tipo JSON (en este caso es la creada anteriormente)

        JSONObject jsonObject = new JSONObject(cadenaJSON); //Creamos un objeto de tipo JSON y le pasamos la cadena JSON
        String posts = jsonObject.getString("posts");

        //Entramos en el array de posts
        jsonObject=new JSONObject(posts);

        //A través de los nombres de cada dato obtenemos su contenido
        return  this.result=jsonObject.getString("Estado").toLowerCase();
    }
    public void entrar()
    {
        try {
            Thread.sleep(1000);

            Intent intent = new Intent(this.getActivity(), MainActivityUser.class);
            startActivity(intent);
        } catch(InterruptedException e) {}

    }
    //cargar configuración aplicación Android usando SharedPreferences
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
        return datos;
    }
    public void selectImage (View view)
    {
        Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        int  code = 2;
        startActivityForResult(intent, code);
    }
    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            if(data!=null) {
                selectedImage = data.getData();
                is = this.getActivity().getContentResolver().openInputStream(selectedImage);
                BufferedInputStream bis = new BufferedInputStream(is);
                Bitmap bitmap = BitmapFactory.decodeStream(bis);

                // original measurements
                int origWidth = bitmap.getWidth();
                int origHeight = bitmap.getHeight();

                final int destWidth = 600;//or the width you need

                System.out.println("Original tamaño "+origWidth);

                if(origWidth > destWidth){
                    // picture is wider than we want it, we calculate its target height
                    int destHeight = origHeight/( origWidth / destWidth ) ;
                    // we create an scaled bitmap so it reduces the image, not just trim it
                    Bitmap b2 = Bitmap.createScaledBitmap(bitmap, destWidth, destHeight, false);
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    // compress to the format you want, JPEG, PNG...
                    // 70 is the 0-100 quality percentage
                    b2.compress(Bitmap.CompressFormat.JPEG,70 , outStream);
                    System.out.println("Tamaño comprimido "+b2.getWidth());
                    // we save the file, at least until we have made use of it
                    ImageView iv = (ImageView) this.getActivity().findViewById(R.id.ImageViewLocation);
                    iv.setImageBitmap(b2);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    b2.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                    is = new ByteArrayInputStream(stream.toByteArray());
                }else {

                    ImageView iv = (ImageView) this.getActivity().findViewById(R.id.ImageViewLocation);
                    iv.setImageBitmap(bitmap);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                    is = new ByteArrayInputStream(stream.toByteArray());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: "+e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_location, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.add_location:
                dialogMap();
                // search action
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Launching new activity
     * */
    private void dialogMap() {
        // custom dialog
        FragmentDialogMap dFragment = new FragmentDialogMap();
        // Show DialogFragment
        dFragment.show(fm, "Dialog Fragment");
    }

}
