package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

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
import java.io.InputStream;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;


/**
 * Created by neokree on 12/12/14.
 */
public class NewUser extends Fragment {

    EditText name,lastname,email,password;
    private ProgressDialog pDialog;
    private BD bd = new BD();
    String result;
    String[] datos= new String[5];
    Uri selectedImage;
    InputStream is;
    Handler handler = new Handler();
    CircularProgressButton circularProgressButton;
    View inflatedView;
    ImageView image_button;
    AsyncHttpClient client=new AsyncHttpClient();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.new_user, container, false);
        name=(EditText)inflatedView.findViewById(R.id.editTextName);
        lastname=(EditText)inflatedView.findViewById(R.id.editTextLastname);
        email=(EditText)inflatedView.findViewById(R.id.editTextEmail);
        password=(EditText)inflatedView.findViewById(R.id.editTextLng);
        circularProgressButton=(CircularProgressButton) inflatedView.findViewById(R.id.buttonEnviar);

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setHomeAsUpIndicator(((MainActivity) this.getActivity()).getV7DrawerToggleDelegate().getThemeUpIndicator());

        image_button = (ImageView) inflatedView.findViewById(R.id.ImageViewUser);
        image_button.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                selectImage(v);
            }
        });


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

        return inflatedView;
    }
    @Override
    public void onDestroy() {
        client.cancelAllRequests(true);
        super.onDestroy();
    }
    public void buttonAccept(View view) throws JSONException, FileNotFoundException {
        //Inicializamos dialog
        pDialog = new ProgressDialog(NewUser.this.getActivity());
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Procesando...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);
        //Inicializamos el array de los datos de preferencias
        datos[1]= name.getText().toString();
        datos[2]= lastname.getText().toString();
        datos[3]= email.getText().toString();

        result="";

        if(!name.getText().toString().equals("") && !lastname.getText().toString().equals("") && !email.getText().toString().equals("") && !password.getText().toString().equals("")) {
            client = new AsyncHttpClient();

            String url = "http://interestingworld.webcindario.com/insert_user.php";
            RequestParams params = new RequestParams();
            params.put("name", name.getText());
            params.put("lastname", lastname.getText());
            params.put("email", email.getText());
            params.put("password", password.getText());
            //Cargar la imagen
            int numero1 = (int) (Math.random() * 99999999) + 1;
            int numero2 = (int) (Math.random() * 99999999) + 1;
            params.put("photo_url", is, numero1+""+numero2 + "_user.jpg");




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
                                AppMsg.makeText(NewUser.this.getActivity(), "Registro correcto", AppMsg.STYLE_INFO).setLayoutGravity(Gravity.BOTTOM).show();
                                circularProgressButton.setProgress(100);
                                entrar();
                            } else {
                                AppMsg.makeText(NewUser.this.getActivity(), "Error en el registro, compruebe los campos", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                                circularProgressButton.setProgress(-1);
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        circularProgressButton.setProgress(0);
                                    }
                                }, 1000);
                            }

                        } catch (JSONException e) {
                            System.out.println("Falla:" + e);
                            AppMsg.makeText(NewUser.this.getActivity(), "Error en el registro, compruebe los campos", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
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
                    AppMsg.makeText(NewUser.this.getActivity(), "Parece que hay algún problema con la red", AppMsg.STYLE_CONFIRM).setLayoutGravity(Gravity.BOTTOM).show();
                    circularProgressButton.setProgress(-1);
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            circularProgressButton.setProgress(0);
                        }
                    }, 1000);
                    pDialog.hide();
                }
            });
        }else{
            AppMsg.makeText(NewUser.this.getActivity(), "Los campos no pueden estar vacios", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
            circularProgressButton.setProgress(-1);
            handler.postDelayed(new Runnable() {
                public void run() {
                    circularProgressButton.setProgress(0);
                }
            }, 1000);
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
        Fragment fragment = new Login();
        ((MaterialNavigationDrawer)this.getActivity()).setFragmentChild(fragment, "Login");
    }
    public void selectImage (View view)
    {
        Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        int  code = 2;
        startActivityForResult(intent, code);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

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

                options.inSampleSize = calculateInSampleSize(options,250,250);
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeStream(bis2,null,options);

                //Rotar la imagen
                String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
                Cursor cur = this.getActivity().getContentResolver().query(selectedImage, orientationColumn, null, null, null);
                int orientation = -1;
                if (cur != null && cur.moveToFirst()) {
                    orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
                }
                Matrix matrix = new Matrix();
                matrix.postRotate(orientation);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                ImageView iv = (ImageView) inflatedView.findViewById(R.id.ImageViewUser);
                iv.setImageBitmap(bitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                is = new ByteArrayInputStream(stream.toByteArray());
                System.out.println(bitmap.getHeight() + " " + bitmap.getWidth());

            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: "+e);
        }
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