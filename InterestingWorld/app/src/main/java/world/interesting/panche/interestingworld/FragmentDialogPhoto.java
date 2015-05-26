package world.interesting.panche.interestingworld;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by Alex on 10/02/2015.
 */
public class FragmentDialogPhoto extends DialogFragment {
    public List<String> select_image = new ArrayList<String>();
    String name,url,lat,lng,id,description,id_image;
    View view;
    Bundle bundle= new Bundle();
    ImageButton bInfo,bShare,bNavigate,bLike;
    String[] datos= new String[5];
    String result;
    Location loc;


    private onRateImage callback;

    public interface onRateImage {
        public void onRateImage(String State);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.custom_dialog_photo, container, false);
        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        bInfo = (ImageButton)view.findViewById(R.id.ic4);
        bShare = (ImageButton)view.findViewById(R.id.ic3);
        bInfo.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                MoreInfo();
            }
        });
        bShare.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                onShareItem(v);
            }
        });
        //Boton me gusta
        bLike = (ImageButton)view.findViewById(R.id.ic2);
        bLike.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                if(getActivity().getLocalClassName().equals("MainActivityUser")) {
                    SweetAlertLike();
                }else{
                    AppMsg.makeText(getActivity(), "Debes estar loggeado para poder marcar que te gusta", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                }
            }
        });

        Class cl=this.getActivity().getClass();
        if(!cl.getName().equals("world.interesting.panche.interestingworld.MainActivity")) {


            ArrayList<String> info_image =((MainActivityUser) getActivity()).GetImageUrlFull();
            loc=((MainActivityUser)getActivity()).GetLocationSelected();
            id_image=info_image.get(1);

        }

        id = getArguments().getString("id");
        name = getArguments().getString("name");
        url = getArguments().getString("url");
        lat = getArguments().getString("lat");
        lng = getArguments().getString("lng");
        description = getArguments().getString("description");
        LoadImage();
        bNavigate = (ImageButton)view.findViewById(R.id.ic1);
        bNavigate.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lng+"&mode=w");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            callback = (onRateImage) getTargetFragment();
        } catch (Exception e) {
            throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void LoadImage()
    {
        ImageView photoDetail = (ImageView) view.findViewById(R.id.ImagePhoto);

        Class cl=getActivity().getClass();
        if(cl.getName().equals("world.interesting.panche.interestingworld.MainActivity")) {

            ((MainActivity) getActivity()).getmPicasso() //
                    .load(Links.getUrl_images()+url).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .error(R.drawable.not_found) //
                    .into(photoDetail);
            ((MainActivity) getActivity()).getmPicasso() .invalidate("http://"+url);

        }else {

            ((MainActivityUser) getActivity()).getmPicasso() //
                    .load(Links.getUrl_images()+url).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .error(R.drawable.not_found) //
                    .into(photoDetail);
            ((MainActivityUser) getActivity()).getmPicasso() .invalidate("http://"+url);
        }

    }
    public void MoreInfo()
    {
        Fragment fragment = new FragmentLocationDetailTabs();
        ((MaterialNavigationDrawer)this.getActivity()).setFragmentChild(fragment,name);
        ((MaterialNavigationDrawer)this.getActivity()).onAttachFragment(fragment);
        FragmentDialogPhoto.this.dismiss();
    }
    public void onShareItem(View v) {
        // Get access to bitmap image from view
        ImageView ivImage = (ImageView) view.findViewById(R.id.ImagePhoto);
        // Get access to the URI for the bitmap
        Uri bmpUri = getLocalBitmapUri(ivImage);
        if (bmpUri != null) {
            // Construct a ShareIntent with link to image
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TITLE,name);
            shareIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.url_share));
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image/*");
            // Launch sharing dialog for image
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        } else {
                    new SweetAlertDialog(this.getActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Parece que algo ha fallado!")
                    .show();
        }
    }

    // Returns the URI path to the Bitmap displayed in specified ImageView
    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
    //-------------- Funciones LIKE ----------------------------------------------
    public void SweetAlertLike()
    {
        new SweetAlertDialog(this.getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(loc.getName())
                .setContentText("¿Te gusta esta imagen?")
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
                            like();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show();
    }

    public void like() throws JSONException, FileNotFoundException {

        datos=Preferences.loadPreferences(this.getActivity());

        result="";

        AsyncHttpClient client = new AsyncHttpClient();


        String url = Links.getUrl_add_rating_image();
        RequestParams params = new RequestParams();
        params.put("id_user", datos[0]);
        params.put("id_image", id_image);

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
                        if (getResult().equals("borrado")) {
                            SweetAlertInfo("Ya no te gusta esta imagen",false);
                            updateImages();
                            callback.onRateImage("ok");
                        } else {
                            if (getResult().equals("insertado")) {
                                SweetAlertInfo("Te gusta esta imagen",true);
                                updateImages();
                                callback.onRateImage("ok");
                            }else {
                                SweetAlertInfo("Ups.. se ha producido un error",false);
                            }
                        }

                    } catch (JSONException e) {
                        System.out.println("Falla:" + e);
                        SweetAlertInfo("Ups... se ha producido un error",false);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                SweetAlertInfo("Parece que hay algún problema con la red",false);

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
    public void SweetAlertInfo(String message,Boolean format)
    {
        int style=1;
        if(format==true)
        {
            style=SweetAlertDialog.SUCCESS_TYPE;
        }else{ style=SweetAlertDialog.ERROR_TYPE;}

        new SweetAlertDialog(this.getActivity(), style)
                .setTitleText(message)
                .setConfirmText("Ok")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                })
                .show();
    }

    public void updateImages()
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client = new AsyncHttpClient();
        String url = Links.getUrl_update_image_location();
        RequestParams params = new RequestParams();
        params.put("id_location", id);


        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {

            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

}