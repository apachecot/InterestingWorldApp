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
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.dd.CircularProgressButton;
import com.devspark.appmsg.AppMsg;
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
    Handler handler = new Handler();
    CircularProgressButton circularProgressButton;
    Spinner categoryList;
    Category cat=new Category();

    FragmentManager fm;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        fm= this.getActivity().getSupportFragmentManager();

        View inflatedView = inflater.inflate(R.layout.new_location, container, false);
        circularProgressButton=(CircularProgressButton) inflatedView.findViewById(R.id.buttonEnviar);


        return inflatedView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MaterialNavigationDrawer)this.getActivity()).getSupportActionBar().show();
        name=(EditText)this.getActivity().findViewById(R.id.editTextName);
        description=(EditText)this.getActivity().findViewById(R.id.editTextDescription);
        addItemsSpinner();


        circularProgressButton.setOnClickListener(new View.OnClickListener() {
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
        name.setError(null);
        if(isValidName(name.getText().toString())) {

            if (lat != 0.0) {
                String url = "http://interestingworld.webcindario.com/insert_location.php";
                RequestParams params = new RequestParams();
                params.put("id_user", datos[0]);
                params.put("name", name.getText());
                params.put("description", description.getText());
                params.put("id_category", cat.GetIdCategory(String.valueOf(categoryList.getSelectedItem())));
                params.put("lat", lat);
                params.put("lng", lng);
                //Cargar la imagen
                int numero = (int) (Math.random() * 99999999) + 1;
                //is = this.getActivity().getContentResolver().openInputStream(selectedImage);
                params.put("photo_url", is, numero + "_upload.jpg");


                client.post(url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        circularProgressButton.setProgress(50);
                        circularProgressButton.setIndeterminateProgressMode(true);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {

                            try {
                                System.out.println(new String(responseBody));
                                setResult(new String(responseBody));
                                System.out.println(getResult());
                                if (getResult().equals("bien")) {
                                    AppMsg.makeText(FragmentAddLocation.this.getActivity(), "Punto de interés añadido correctamente", AppMsg.STYLE_INFO).setLayoutGravity(Gravity.BOTTOM).show();
                                    ((MainActivityUser) getActivity()).UnSetPosition();
                                    circularProgressButton.setProgress(100);
                                    entrar();
                                } else {
                                    AppMsg.makeText(FragmentAddLocation.this.getActivity(), "Error al intentar subir los datos, comprueba que todo este correcto", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                                    circularProgressButton.setProgress(-1);
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            circularProgressButton.setProgress(0);
                                        }
                                    }, 1000);
                                }

                            } catch (JSONException e) {
                                System.out.println("Falla:" + e);
                                AppMsg.makeText(FragmentAddLocation.this.getActivity(), "Error al intentar subir los datos, comprueba que todo este correcto", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                                circularProgressButton.setProgress(-1);
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        circularProgressButton.setProgress(0);
                                    }
                                }, 1000);
                            }
                        }
                        pDialog.hide();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        AppMsg.makeText(FragmentAddLocation.this.getActivity(), "Parece que hay algún problema con la red", AppMsg.STYLE_CONFIRM).setLayoutGravity(Gravity.BOTTOM).show();
                        circularProgressButton.setProgress(-1);
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                circularProgressButton.setProgress(0);
                            }
                        }, 1000);
                        pDialog.hide();
                    }
                });
            } else {
                AppMsg.makeText(FragmentAddLocation.this.getActivity(), "Debes marcar el punto de interés antes de publicar", AppMsg.STYLE_CONFIRM).setLayoutGravity(Gravity.BOTTOM).show();
                circularProgressButton.setProgress(-1);
                handler.postDelayed(new Runnable() {
                    public void run() {
                        circularProgressButton.setProgress(0);
                    }
                }, 1000);
                pDialog.hide();
            }
        }else
        {
            name.setError("Es necesario indicar un nombre");
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
        handler.postDelayed(new Runnable() {
            public void run() {
                circularProgressButton.setProgress(0);
                changeActivity();
            }
        }, 1000);
    }
    public void changeActivity()
    {
        ((MaterialNavigationDrawer)this.getActivity()).setSection(((MainActivityUser) getActivity()).lastLocations);
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
                InputStream is2 = this.getActivity().getContentResolver().openInputStream(selectedImage);
                BufferedInputStream bis = new BufferedInputStream(is);
                BufferedInputStream bis2 = new BufferedInputStream(is2);


                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(bis,null, options);

                options.inSampleSize = calculateInSampleSize(options,640,480);
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeStream(bis2,null,options);

                ImageView iv = (ImageView) this.getActivity().findViewById(R.id.ImageViewLocation);
                iv.setImageBitmap(bitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                is = new ByteArrayInputStream(stream.toByteArray());

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

    public void addItemsSpinner(){

        categoryList=(Spinner) this.getActivity().findViewById(R.id.spinnerCategory);
        System.out.println(cat.GetList());
        ArrayAdapter<String> dataAdapter= new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_spinner_dropdown_item,cat.GetList());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryList.setAdapter(dataAdapter);

    }

    // validating password with retype password
    private boolean isValidName(String name) {
        if (name != null && !name.equals("")) {
            return true;
        }
        return false;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
