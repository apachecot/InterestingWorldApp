package world.interesting.panche.interestingworld;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import android.view.Gravity;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.devspark.appmsg.AppMsg;

import com.google.android.gms.maps.SupportMapFragment;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Alex on 10/02/2015.
 */
public class FragmentDialogComment extends DialogFragment {
    private SupportMapFragment fragment;
    private BootstrapButton bAccept,bCancel;
    EditText comment;
    private SweetAlertDialog pDialog;
    String result;

    private onSubmitComment callback;

    public interface onSubmitComment {
        public void onSubmitComment(String State);
    }

    public FragmentDialogComment() {
        fragment = new SupportMapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_dialog_comment, container, false);
        getDialog().requestWindowFeature(STYLE_NORMAL);
        getDialog().setTitle("Escribe tu comentario");

        comment=(EditText)view.findViewById(R.id.editTextAdvanced);
        Location location=((MainActivityUser) getActivity()).GetLocationSelected();
        bAccept = (BootstrapButton)view.findViewById(R.id.dialogButtonOk);
        bAccept.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                   buttonAccept();
            }
        });
        bCancel = (BootstrapButton)view.findViewById(R.id.dialogButtonCancel);
        bCancel.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            callback = (onSubmitComment) getTargetFragment();
        } catch (Exception e) {
            throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");
        }
        super.onCreate(savedInstanceState);
    }

    public void buttonAccept(){
        //Inicializamos dialog
        pDialog = new SweetAlertDialog(this.getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Cargando...");

        result="";
        AsyncHttpClient client = new AsyncHttpClient();
        User user=((MainActivityUser) getActivity()).getUser();
        Location location=((MainActivityUser) getActivity()).GetLocationSelected();

        if(isValidName(comment.getText().toString())) {

            String url = Links.getUrl_add_comment();
            RequestParams params = new RequestParams();
            params.put("id_user", user.getId());
            params.put("id_location", location.getId());
            params.put("comment", comment.getText());


            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    pDialog.setCancelable(true);
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
                                AppMsg.makeText(getActivity(), "Comentario añadido correctamente", AppMsg.STYLE_INFO).setLayoutGravity(Gravity.BOTTOM).show();
                                pDialog.hide();
                                callback.onSubmitComment("ok");
                                getDialog().dismiss();
                            } else {
                                AppMsg.makeText(getDialog().getOwnerActivity(), "Error al intentar subir los datos, comprueba que todo este correcto", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                                pDialog.hide();
                            }

                        } catch (JSONException e) {
                            System.out.println("Falla:" + e);
                            AppMsg.makeText(getDialog().getOwnerActivity(), "Error al intentar subir los datos, comprueba que todo este correcto", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                            pDialog.hide();
                        }
                    }
                    pDialog.hide();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    AppMsg.makeText(getDialog().getOwnerActivity(), "Parece que hay algún problema con la red", AppMsg.STYLE_CONFIRM).setLayoutGravity(Gravity.BOTTOM).show();

                    pDialog.hide();
                }
            });

        }else
        {
            AppMsg.makeText(getActivity(), "El comentario no puede estar vacio", AppMsg.STYLE_CONFIRM).setLayoutGravity(Gravity.BOTTOM).show();
        }
    }
    public SupportMapFragment getFragment() {
        return fragment;
    }
    // validating password with retype password
    private boolean isValidName(String name) {
        if (name != null && !name.equals("")) {
            return true;
        }
        return false;
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

}