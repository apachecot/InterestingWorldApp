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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by neokree on 12/12/14.
 */
public class Login extends Fragment {

    EditText email,password;
    private ProgressDialog pDialog;
    private BD bd = new BD();
    String result;
    Handler handler = new Handler();
    CircularProgressButton circularProgressButtonAccept,circularProgressButtonNew;
    View inflatedView;
    AsyncHttpClient client=new AsyncHttpClient();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.login, container, false);

        email=(EditText)inflatedView.findViewById(R.id.editTextEmail);
        password=(EditText)inflatedView.findViewById(R.id.editTextLng);
        circularProgressButtonAccept=(CircularProgressButton) inflatedView.findViewById(R.id.buttonAccept);
        circularProgressButtonNew=(CircularProgressButton) inflatedView.findViewById(R.id.buttonAlta);

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setHomeAsUpIndicator(((MainActivity) this.getActivity()).getV7DrawerToggleDelegate().getThemeUpIndicator());

        circularProgressButtonAccept.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                loginAccept(v);
            }
        });

        circularProgressButtonNew.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                newUser(v);
            }
        });

        return inflatedView;
    }
    @Override
    public void onDestroy() {
        client.cancelAllRequests(true);
        super.onDestroy();
    }
    public void newUser (View view)
    {
            Fragment fragment = new NewUser();
            ((MaterialNavigationDrawer)this.getActivity()).setFragmentChild(fragment,"Nuevo usuario");

    }
    public void loginAccept(View view)
    {
        //Inicializamos dialog


        pDialog = new ProgressDialog(Login.this.getActivity());
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Procesando...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);

        result="";

        client = new AsyncHttpClient();

        String url="http://interestingworld.webcindario.com/search_user.php";
        RequestParams params = new RequestParams();
        params.put("email", email.getText());
        params.put("password",  password.getText());

        client.post(url,params,new AsyncHttpResponseHandler() {
            @Override
            public void onStart()
            {
                circularProgressButtonAccept.setProgress(50);
                circularProgressButtonAccept.setIndeterminateProgressMode(true);
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
                            AppMsg.makeText(Login.this.getActivity(), "El usuario o contraseña, no son correctos", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                            circularProgressButtonAccept.setProgress(-1);
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    circularProgressButtonAccept.setProgress(0);
                                }
                            }, 1000);
                        }

                    }catch(JSONException ignored)
                    {
                        System.out.println("Falla:"+ignored );
                        try {
                            circularProgressButtonAccept.setProgress(100);
                            entrar(setResultUser(new String(responseBody)));
                            AppMsg.makeText(Login.this.getActivity(), "Comprobación correcta", AppMsg.STYLE_INFO).setLayoutGravity(Gravity.BOTTOM).show();
                        }catch(JSONException e)
                        {
                            System.out.println("Fallo en el 2:"+e);
                            AppMsg.makeText(Login.this.getActivity(), "El usuario o contraseña, no son correctos", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                            circularProgressButtonAccept.setProgress(-1);
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    circularProgressButtonAccept.setProgress(0);
                                }
                            }, 1000);
                        }
                    }
                }
                pDialog.hide();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                AppMsg.makeText(Login.this.getActivity(), "Parece que hay algún problema con la red", AppMsg.STYLE_CONFIRM).setLayoutGravity(Gravity.BOTTOM).show();
                circularProgressButtonAccept.setProgress(-1);
                handler.postDelayed(new Runnable() {
                    public void run() {
                        circularProgressButtonAccept.setProgress(0);
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
        Preferences.savePreferences(datos,this.getActivity());
        handler.postDelayed(new Runnable() {
            public void run() {
                circularProgressButtonAccept.setProgress(0);
                changeActivity();
            }
        }, 1000);



    }
    public void changeActivity()
    {
        Intent intent = new Intent(this.getActivity(), MainActivityUser.class);
        startActivity(intent);
        this.getActivity().finish();
    }
}