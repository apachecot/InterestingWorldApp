package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.devspark.appmsg.AppMsg;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by neokree on 12/12/14.
 */
public class FragmentImageViewerProfile extends DialogFragment {


    View inflatedView;
    ImageView mImageView;
    PhotoViewAttacher mAttacher;
    String[] datos= new String[5];
    String result;
    String id_image;
    ImageButton delete;
    String url="";
    Boolean state=false;


    private onDelateImage callback;

    public interface onDelateImage {
        public void onDelateImage(String State);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.image_viewer_profile, container, false);

        mImageView = (ImageView) inflatedView.findViewById(R.id.imageViewFull);

        Callback imageLoadedCallback = new Callback() {

            @Override
            public void onSuccess() {
                if(mAttacher!=null){
                    mAttacher.update();
                }else{
                    mAttacher = new PhotoViewAttacher(mImageView);
                }
            }

            @Override
            public void onError() {
                // TODO Auto-generated method stub

            }
        };
        Class cl=this.getActivity().getClass();
        if(cl.getName().equals("world.interesting.panche.interestingworld.MainActivity")) {
            url=((MainActivity) getActivity()).GetImageUrlFull();
            ((MainActivity) getActivity()).getmPicasso()
                    .load(Links.getUrl_images()+url).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .error(R.drawable.not_found)
                    .into(mImageView,imageLoadedCallback);
        }else {
            ArrayList<String> info_image =((MainActivityUser) getActivity()).GetImageUrlFull();
            url=info_image.get(0);
            id_image=info_image.get(1);
            ((MainActivityUser) getActivity()).getmPicasso()
                    .load(Links.getUrl_images()+url).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .error(R.drawable.not_found)
                    .into(mImageView,imageLoadedCallback);
        }

        //Botton compartir
        delete = (ImageButton)inflatedView.findViewById(R.id.ic1);
        delete.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                SweetAlertImage(id_image);
            }
        });

        return inflatedView;
    }
    @Override
    public void onResume() {
        super.onResume();
        System.out.println("onresume");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            callback = (onDelateImage) getTargetFragment();
        } catch (Exception e) {
            throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");
        }
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onPause();
        System.out.println("OnDestroy");
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }


    //---------------------- Funciones Visitado ----------------------------------------------
    public void SweetAlertImage(final String id)
    {
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Imagen")
                .setContentText("¿Deseas borrar tú imagen?")
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
                        eliminate(id);
                    }
                })
                .show();
    }
    public void eliminate(String id){
        //Inicializamos dialog

        result="";

        AsyncHttpClient client = new AsyncHttpClient();

        String url_image=url;
        String url = Links.getUrl_delete_images();
        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("photo_url",url_image);

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

                        if (result.equals("bien")) {
                            state=true;
                            SweetAlertInfo("Imagen eliminada",state);
                            advert();


                        } else {
                            state=false;
                            SweetAlertInfo("Ups.. algo ha fallado, vuelve a intentarlo",state);

                        }

                    } catch (JSONException e) {
                        state=false;
                        SweetAlertInfo("Ups.. algo ha fallado, vuelve a intentarlo",state);

                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                SweetAlertInfo("Parece que hay algún problema con la red",false);
            }
        });
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
    //-------------------------------------
    public void SweetAlertInfo(String message,Boolean format)
    {
        int style=1;
        if(format==true)
        {
            style=SweetAlertDialog.SUCCESS_TYPE;
        }else{ style=SweetAlertDialog.ERROR_TYPE;}

        new SweetAlertDialog(getActivity(), style)
                .setTitleText(message)
                .setConfirmText("Ok")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        advert();
                        sDialog.dismiss();
                    }
                })
                .show();
    }
    public void advert()
    {
        if(state==true)
        {
            callback.onDelateImage("ok");
            getDialog().dismiss();
        }
    }
}


