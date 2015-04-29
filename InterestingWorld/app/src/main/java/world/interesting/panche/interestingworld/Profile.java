package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.devspark.appmsg.AppMsg;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.image.SmartImageView;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Profile extends Fragment {

    /** Alpha Toolbar **/

    View inflatedView;
    CircularProgressButton circularProgressButton,circularProgressButtonEliminate;
    private ProgressDialog pDialog;
    EditText newname,newlastname,newemail;
    AsyncHttpClient client=new AsyncHttpClient();
    String result;
    Handler handler = new Handler();

    String[] datos= new String[5];
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflatedView = inflater.inflate(R.layout.profile, container, false);

        datos=Preferences.loadPreferences(this.getActivity());
        SmartImageView myImage = (SmartImageView) inflatedView.findViewById(R.id.my_image);
        myImage.setImageUrl("http://"+datos[4]);
        TextView name = (TextView) inflatedView.findViewById(R.id.textViewName);
        TextView lastname = (TextView) inflatedView.findViewById(R.id.textViewLastname);
        TextView email = (TextView) inflatedView.findViewById(R.id.textViewEmail);
        newname = (EditText) inflatedView.findViewById(R.id.editTextName);
        newlastname = (EditText) inflatedView.findViewById(R.id.editTextLastname);
        newemail = (EditText) inflatedView.findViewById(R.id.editTextEmail);


        name.setText(datos[1]);
        lastname.setText(datos[2]);
        email.setText(datos[3]);
        newname.setText(datos[1]);
        newlastname.setText(datos[2]);
        newemail.setText(datos[3]);
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        circularProgressButton=(CircularProgressButton) inflatedView.findViewById(R.id.buttonEnviar);

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

        circularProgressButtonEliminate=(CircularProgressButton) inflatedView.findViewById(R.id.buttonEliminar);

        circularProgressButtonEliminate.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                  SweetAlertEliminate();
            }
        });

        actionBar.setHomeAsUpIndicator(((MainActivityUser) this.getActivity()).getV7DrawerToggleDelegate().getThemeUpIndicator());
        return inflatedView;
    }
    public void buttonAccept(View view) throws JSONException, FileNotFoundException {
        //Inicializamos dialog
        pDialog = new ProgressDialog(this.getActivity());
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Procesando...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);
        //Inicializamos el array de los datos de preferencias

        result="";

        if(!newname.getText().toString().equals("") && !newlastname.getText().toString().equals("") && !newemail.getText().toString().equals("")) {
            client = new AsyncHttpClient();

            String url = "http://interestingworld.webcindario.com/edit_user.php";
            RequestParams params = new RequestParams();
            params.put("id",datos[0]);
            params.put("name", newname.getText());
            params.put("lastname", newlastname.getText());
            params.put("email", newemail.getText());

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
                                AppMsg.makeText(getActivity(), "Usuario modificado correctamente", AppMsg.STYLE_INFO).setLayoutGravity(Gravity.BOTTOM).show();
                                circularProgressButton.setProgress(100);
                                updatePreferences();
                            } else {
                                AppMsg.makeText(getActivity(), "Error en el registro, compruebe los campos", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                                circularProgressButton.setProgress(-1);
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        circularProgressButton.setProgress(0);
                                    }
                                }, 1000);
                            }

                        } catch (JSONException e) {
                            System.out.println("Falla:" + e);
                            AppMsg.makeText(getActivity(), "Error en el registro, compruebe los campos", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
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
                    AppMsg.makeText(getActivity(), "Parece que hay algún problema con la red", AppMsg.STYLE_CONFIRM).setLayoutGravity(Gravity.BOTTOM).show();
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
            AppMsg.makeText(getActivity(), "Los campos no pueden estar vacios", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
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
    public void updatePreferences()
    {
        String[] datos2= new String[5];
        datos2[0]=datos[0];
        datos2[1]=newname.getText().toString();
        datos2[2]=newlastname.getText().toString();
        datos2[3]=newemail.getText().toString();
        datos2[4]=datos[4];
        Preferences.savePreferences(datos2,this.getActivity());
    }

    //---------------------- Funciones Borrado usuario ----------------------------------------------
    public void SweetAlertEliminate()
    {
        new SweetAlertDialog(this.getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("¡Cuidado!")
                .setContentText("¿Estas seguro de querer eliminar tu cuenta?" +
                        "\nRecuerda que una vez eliminada no podrás recuperar tus datos.")
                .setCancelText("No")
                .setConfirmText("Sí")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                        try {
                            visited();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show();
    }
    public void visited() throws JSONException, FileNotFoundException {
        //Inicializamos dialog
        pDialog = new ProgressDialog(this.getActivity());
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Procesando...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);

        datos=Preferences.loadPreferences(this.getActivity());

        result="";

        client = new AsyncHttpClient();


        String url = "http://interestingworld.webcindario.com/delete_user.php";
        RequestParams params = new RequestParams();
        params.put("id", datos[0]);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {

                    try {
                        System.out.println(new String(responseBody));
                        setResult(new String(responseBody));
                        System.out.println(getResult());
                        if (getResult().equals("bien")) {
                            ((MainActivityUser) getActivity()).restoreUser();
                        } else {
                            AppMsg.makeText(getActivity(), "Ups.. algo ha fallado, vuelve a intentarlo", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                        }

                    } catch (JSONException e) {
                        System.out.println("Falla:" + e);
                        AppMsg.makeText(getActivity(), "Error al intentar subir la imágen", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();

                    }
                }
                pDialog.hide();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                AppMsg.makeText(getActivity(), "Parece que hay algún problema con la red", AppMsg.STYLE_CONFIRM).setLayoutGravity(Gravity.BOTTOM).show();
                pDialog.hide();
            }
        });
    }

}