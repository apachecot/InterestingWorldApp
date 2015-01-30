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
import android.support.v7.app.ActionBarActivity;
import android.view.View;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;


/**
 * Created by neokree on 12/12/14.
 */
public class NewUser extends ActionBarActivity {

    EditText name,lastname,email,password;
    private ProgressDialog pDialog;
    private BD bd = new BD();
    String result;
    String[] datos= new String[5];
    Uri selectedImage;
    InputStream is;
    File myPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_user);
        name=(EditText)findViewById(R.id.editTextName);
        lastname=(EditText)findViewById(R.id.editTextLastname);
        email=(EditText)findViewById(R.id.editTextEmail);
        password=(EditText)findViewById(R.id.editTextPassword);
        Button bAccept = (Button)findViewById(R.id.buttonAccept);

    }
    public void buttonAccept(View view) throws JSONException, FileNotFoundException {
        //Inicializamos dialog
        pDialog = new ProgressDialog(NewUser.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Procesando...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);
        //Inicializamos el array de los datos de preferencias
        datos[1]= name.getText().toString();
        datos[2]= lastname.getText().toString();
        datos[3]= email.getText().toString();

        result="";

        AsyncHttpClient client = new AsyncHttpClient();

        String url="http://interestingworld.webcindario.com/insert_user.php";
        RequestParams params = new RequestParams();
        params.put("name", name.getText());
        params.put("lastname", lastname.getText());
        params.put("email", email.getText());
        params.put("password",  password.getText());
        //Cargar la imagen
        int numero = (int) (Math.random() *99999999) + 1;
        is = getContentResolver().openInputStream(selectedImage);
        params.put("photo_url", is, numero+"_upload.jpg");


        client.post(url,params,new AsyncHttpResponseHandler() {
            @Override
            public void onStart()
            {
                pDialog.setProgress(0);
                pDialog.show();
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200)
                {

                   try {
                       System.out.println(new String(responseBody));
                       setResult(new String(responseBody));
                       System.out.println(getResult());
                       if(getResult().equals("bien"))
                       {
                          Toast.makeText(NewUser.this, "Registro correcto", Toast.LENGTH_SHORT).show();
                          entrar();
                       }
                       else
                       {
                           Toast.makeText(NewUser.this, "Error en el registro, compruebe los campos", Toast.LENGTH_SHORT).show();
                       }

                   }catch(JSONException e)
                   {
                       System.out.println("Falla:"+e );
                       Toast.makeText(NewUser.this, "Error en el registro, compruebe los campos", Toast.LENGTH_SHORT).show();
                   }
                }
                pDialog.hide();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(NewUser.this, "Parece que hay algún problema con la red", Toast.LENGTH_SHORT).show();
                pDialog.hide();
            }
        });
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
        savePreferences(datos);
        try {
            Thread.sleep(1000);
            Intent intent = new Intent(this, MainActivityUser.class);
            startActivity(intent);
        } catch(InterruptedException e) {}

    }
    //guardar configuración aplicación Android usando SharedPreferences
    public void savePreferences(String[] datos) {
        SharedPreferences prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("id", datos[0]);
        editor.putString("name", datos[1]);
        editor.putString("lastname", datos[2]);
        editor.putString("email", datos[3]);
        editor.putString("photo_url", datos[4]);
        editor.commit();
        System.out.println("Guardadas preferencias");
    }
    public void selectImage (View view)
    {
        Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        int  code = 2;
        startActivityForResult(intent, code);
    }
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            if(data!=null) {
                selectedImage = data.getData();
                is = getContentResolver().openInputStream(selectedImage);
                BufferedInputStream bis = new BufferedInputStream(is);
                Bitmap bitmap = BitmapFactory.decodeStream(bis);
                ImageView iv = (ImageView) findViewById(R.id.ImageViewUser);
                iv.setImageBitmap(bitmap);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: "+e);
        }
    }



}