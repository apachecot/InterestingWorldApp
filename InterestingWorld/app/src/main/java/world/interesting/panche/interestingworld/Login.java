package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import com.dd.CircularProgressButton;
import com.devspark.appmsg.AppMsg;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by neokree on 12/12/14.
 */
public class Login extends ActionBarActivity {

    EditText email,password;
    private ProgressDialog pDialog;
    private BD bd = new BD();
    String result;
    Handler handler = new Handler();
    CircularProgressButton circularProgressButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        email=(EditText)findViewById(R.id.editTextEmail);
        password=(EditText)findViewById(R.id.editTextLng);
        circularProgressButton=(CircularProgressButton) findViewById(R.id.buttonAccept);


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
                circularProgressButton.setProgress(50);
                circularProgressButton.setIndeterminateProgressMode(true);
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
                            AppMsg.makeText(Login.this, "El usuario o contraseña, no son correctos", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                            circularProgressButton.setProgress(-1);
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    circularProgressButton.setProgress(0);
                                }
                            }, 1000);
                        }

                    }catch(JSONException ignored)
                    {
                        System.out.println("Falla:"+ignored );
                        try {
                            circularProgressButton.setProgress(100);
                            entrar(setResultUser(new String(responseBody)));
                            AppMsg.makeText(Login.this, "Comprobación correcta", AppMsg.STYLE_INFO).setLayoutGravity(Gravity.BOTTOM).show();
                        }catch(JSONException e)
                        {
                            System.out.println("Fallo en el 2:"+e);
                            AppMsg.makeText(Login.this, "El usuario o contraseña, no son correctos", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                            circularProgressButton.setProgress(-1);
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    circularProgressButton.setProgress(0);
                                }
                            }, 1000);
                        }
                    }
                }
                pDialog.hide();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                AppMsg.makeText(Login.this, "Parece que hay algún problema con la red", AppMsg.STYLE_CONFIRM).setLayoutGravity(Gravity.BOTTOM).show();
                circularProgressButton.setProgress(-1);
                handler.postDelayed(new Runnable() {
                    public void run() {
                        circularProgressButton.setProgress(0);
                    }
                }, 1000);
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
        handler.postDelayed(new Runnable() {
            public void run() {
                circularProgressButton.setProgress(0);
                changeActivity();
            }
        }, 1000);



    }
    public void changeActivity()
    {
        Intent intent = new Intent(this, MainActivityUser.class);
        startActivity(intent);
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