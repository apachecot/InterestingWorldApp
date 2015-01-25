package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import world.interesting.panche.interestingworld.Preferences;

import java.lang.reflect.Array;

/**
 * Created by neokree on 12/12/14.
 */
public class Login extends ActionBarActivity {

    EditText email,password;
    private ProgressDialog pDialog;
    private BD bd = new BD();
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        email=(EditText)findViewById(R.id.editTextEmail);
        password=(EditText)findViewById(R.id.editTextPassword);


    }
    public void newUser (View view)
    {
        Intent intent = new Intent(this, NewUser.class);
        startActivity(intent);
    }
    public void loginAccept(View view)
    {
        //Inicializamos dialog
        pDialog = new ProgressDialog(Login.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Procesando...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);

        result="";

        AsyncHttpClient client = new AsyncHttpClient();

        String url="http://interestingworld.webcindario.com/search_user.php";
        RequestParams params = new RequestParams();
        params.put("email", email.getText());
        params.put("password",  password.getText());

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
                        setResult(new String(responseBody));
                        System.out.println(getResult());
                        if(!getResult().equals("bien") && !getResult().equals("mal"))
                        {

                        }
                        else
                        {
                            Toast.makeText(Login.this, "El usuario o contraseña, no son correctos", Toast.LENGTH_SHORT).show();
                        }

                    }catch(JSONException ignored)
                    {
                        System.out.println("Falla:"+ignored );
                        try {
                            entrar(setResultUser(new String(responseBody)));
                            Toast.makeText(Login.this, "Comprobación correcta", Toast.LENGTH_SHORT).show();
                        }catch(JSONException e)
                        {
                            System.out.println("Fallo en el 2:"+e);
                            Toast.makeText(Login.this, "El usuario o contraseña, no son correctos", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                pDialog.hide();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                Toast.makeText(Login.this, "Parece que hay algún problema con la red", Toast.LENGTH_SHORT).show();
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


        return  this.result=jsonObject.getString("Estado").toLowerCase();

    }
    public String[] setResultUser (String result) throws JSONException {
        String datos[]=new String[5];
        String cadenaJSON = result.toString();//Le pasamos a la variable cadenaJSON una cadena de tipo JSON (en este caso es la creada anteriormente)

        JSONObject jsonObject = new JSONObject(cadenaJSON); //Creamos un objeto de tipo JSON y le pasamos la cadena JSON
        String posts = jsonObject.getString("posts");

        JSONArray array = new JSONArray(posts);
        for(int i=0; i < array.length(); i++) {
            JSONObject jsonChildNode = array.getJSONObject(i);
            jsonChildNode = new JSONObject(jsonChildNode.optString("post").toString());
            //
            datos[0]=jsonChildNode.getString("id");
            datos[1]=jsonChildNode.getString("name");
            datos[2]=jsonChildNode.getString("lastname");
            datos[3]=jsonChildNode.getString("email");
            datos[4]=jsonChildNode.getString("photo_url");

        }
        return  datos;
    }
    public void entrar(String[] datos)
    {
        loadPreferences();
        savePreferences(datos);
        loadPreferences();
        try {
            Thread.sleep(1000);
            Intent intent = new Intent(this, MainActivityUser.class);
            startActivity(intent);
        } catch(InterruptedException e) {

        }

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


}