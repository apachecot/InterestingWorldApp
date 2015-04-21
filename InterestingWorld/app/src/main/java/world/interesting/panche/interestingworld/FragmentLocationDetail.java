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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dd.CircularProgressButton;
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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FragmentLocationDetail extends Fragment {
    String title_txt,description_txt,lat,lng,url_photo,user_location,id,address,locality,country;
    View inflatedView;
    ImageButton bNavigate,bShare,bPhoto,bVisited,bLike;
    Uri selectedImage;
    InputStream is;
    private ProgressDialog pDialog;
    String result;
    String[] datos= new String[5];
    FragmentManager fm;
    AsyncHttpClient client=new AsyncHttpClient();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Seteamos la estructura del fragment
        inflatedView = inflater.inflate(R.layout.location_detail, container, false);
         fm = this.getActivity().getSupportFragmentManager();

        //Cambiamos el icono del menu por el de volver atras
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if(this.getActivity().getLocalClassName().equals("MainActivity"))
        {
            actionBar.setHomeAsUpIndicator(((MainActivity) this.getActivity()).getV7DrawerToggleDelegate().getThemeUpIndicator());
        }else{
            actionBar.setHomeAsUpIndicator(((MainActivityUser) this.getActivity()).getV7DrawerToggleDelegate().getThemeUpIndicator());
        }



        //TextView title = (TextView) inflatedView.findViewById(R.id.TextViewTitle);
        TextView description = (TextView) inflatedView.findViewById(R.id.TextViewDescription);
        TextView street = (TextView) inflatedView.findViewById(R.id.textViewAddress);
        TextView user = (TextView) inflatedView.findViewById(R.id.textViewUser);
        ImageView photoDetail = (ImageView) inflatedView.findViewById(R.id.ImageDetail);
        ImageView photoUser = (ImageView) inflatedView.findViewById(R.id.imageViewUser);
        ImageView imageMap = (ImageView) inflatedView.findViewById(R.id.ImageMapStatic);
        TextView nLikes = (TextView) inflatedView.findViewById(R.id.textViewNlikes);

        Location loc;
        if(this.getActivity().getLocalClassName().equals("MainActivity")) {
            loc = ((MainActivity) getActivity()).GetLocationSelected();
        }else{
            loc = ((MainActivityUser) getActivity()).GetLocationSelected();
        }
        title_txt = loc.getName();
        description_txt = loc.getDescription();
        lat=loc.getLat();
        lng=loc.getLng();
        url_photo=loc.getUrl();
        user_location=loc.getUser();
        id= loc.getId();
        address=loc.getAddress();
        country=loc.getCountry();
        locality=loc.getLocality();
        if(!country.equals("")) {
            street.setText(address + " " + locality + "," + country);
        }
        user.setText(loc.getUser()+" "+loc.getLastname());
        nLikes.setText(loc.getRating());
        Drawable shape = getResources(). getDrawable(R.drawable.circle);
        nLikes.setBackground(shape);

        //title.setText(title_txt);
        description.setText(description_txt);
        Class cl=this.getActivity().getClass();
        if(cl.getName().equals("world.interesting.panche.interestingworld.MainActivity")) {
            ((MainActivity) getActivity()).getmPicasso()
                    .load("http://"+url_photo)//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).skipMemoryCache()
                    .error(R.drawable.not_found)
                    .fit().centerCrop()
                    .into(photoDetail);
            ((MainActivity) getActivity()).getmPicasso()
                    .load("http://"+loc.getPhoto_user())//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).skipMemoryCache()
                    .error(R.drawable.not_found)
                    .fit().centerCrop().transform(new RoundedTransformationPicasso())//
                    .into(photoUser);

            ((MainActivity) getActivity()).getmPicasso().load("http://maps.googleapis.com/maps/api/staticmap?center="+lat+","+lng+"&zoom=16&size=400x400&" +
                    "&markers=color:blue%7Clabel:"+title_txt.charAt(0)+"%7C"+lat+","+lng)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE,NetworkPolicy.NO_STORE).skipMemoryCache()
                    .error(R.drawable.not_found)
                    .fit().centerCrop()
                    .into(imageMap);
            ((MainActivity) getActivity()).getmPicasso() .invalidate("http://"+url_photo);
            ((MainActivity) getActivity()).getmPicasso() .invalidate("http://maps.googleapis.com/maps/api/staticmap?center="+lat+","+lng+"&zoom=16&size=400x400&" +
                    "&markers=color:blue%7Clabel:"+title_txt.charAt(0)+"%7C"+lat+","+lng);
        }else {
            ((MainActivityUser) getActivity()).getmPicasso()
                    .load("http://"+url_photo)//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .error(R.drawable.not_found)
                    .fit().centerCrop()
                    .into(photoDetail);
            ((MainActivityUser) getActivity()).getmPicasso()
                    .load("http://"+loc.getPhoto_user())//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .error(R.drawable.not_found)
                    .fit().centerCrop().transform(new RoundedTransformationPicasso())//
                    .into(photoUser);

            ((MainActivityUser) getActivity()).getmPicasso().load("http://maps.googleapis.com/maps/api/staticmap?center=" + lat + "," + lng + "&zoom=16&size=400x400&" +
                    "&markers=color:blue%7Clabel:" + title_txt.charAt(0) + "%7C" + lat + "," + lng)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE,NetworkPolicy.NO_STORE)
                    .error(R.drawable.not_found)
                    .fit().centerCrop()
                    .into(imageMap);
            ((MainActivityUser) getActivity()).getmPicasso() .invalidate("http://"+url_photo);
            ((MainActivityUser) getActivity()).getmPicasso() .invalidate("http://maps.googleapis.com/maps/api/staticmap?center="+lat+","+lng+"&zoom=16&size=400x400&" +
                    "&markers=color:blue%7Clabel:"+title_txt.charAt(0)+"%7C"+lat+","+lng);
        }

        //Botton compartir
        bShare = (ImageButton)inflatedView.findViewById(R.id.ic3);
        bShare.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                onShareItem(v);
            }
        });
        //Boton visitado
        bVisited = (ImageButton)inflatedView.findViewById(R.id.ic2);
        bVisited.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                if(getActivity().getLocalClassName().equals("MainActivityUser")) {
                    SweetAlertVisited();
                }else{
                    AppMsg.makeText(getActivity(), "Debes estar loggeado para poder marcar como visitado", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                }
            }
        });
        //Boton me gusta
        bLike = (ImageButton)inflatedView.findViewById(R.id.ic5);
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
        //Boton subir imagen
        bPhoto = (ImageButton)inflatedView.findViewById(R.id.ic4);
        bPhoto.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                if(getActivity().getLocalClassName().equals("MainActivityUser")) {
                    selectImage(v);
                }else{
                    AppMsg.makeText(getActivity(), "Debes estar loggeado para poder introducir una fotografía", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                }
            }
        });
        //Boton navegar hacía el lugar
        bNavigate = (ImageButton)inflatedView.findViewById(R.id.ic1);
        bNavigate.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                SweetAlertPath();
            }
        });

        return inflatedView;
    }
    @Override
    public void onDestroy() {
        client.cancelAllRequests(true);
        super.onDestroy();
    }
    public void onShareItem(View v) {
        // Get access to bitmap image from view
        ImageView ivImage = (ImageView) inflatedView.findViewById(R.id.ImageDetail);
        // Get access to the URI for the bitmap
        Uri bmpUri = getLocalBitmapUri(ivImage);
        if (bmpUri != null) {
            // Construct a ShareIntent with link to image
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TITLE,title_txt);
            shareIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.url_share)+" "+description_txt);
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

    // ----------------------------------Funciones Subir Imagen --------------------------------------
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

                options.inSampleSize = calculateInSampleSize(options,480,480);
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

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                is = new ByteArrayInputStream(stream.toByteArray());
                SweetAlertImage();

            }
        } catch (FileNotFoundException e) {
            new SweetAlertDialog(this.getActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Parece que algo ha fallado!")
                    .show();
        } catch (IOException e) {
            new SweetAlertDialog(this.getActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Parece que algo ha fallado!")
                    .show();
        }
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height;
            final int halfWidth = width;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize ++;
            }
        }

        return inSampleSize;
    }

    public void SweetAlertImage()
    {
        new SweetAlertDialog(this.getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(title_txt)
                .setContentText("Deseas subir la fotografía?")
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
                            UploadImage();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show();
    }

    public void UploadImage() throws JSONException, FileNotFoundException {
        //Inicializamos dialog
        pDialog = new ProgressDialog(this.getActivity());
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Procesando...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);

        datos=Preferences.loadPreferences(this.getActivity());

        result="";

        client = new AsyncHttpClient();


        String url = "http://interestingworld.webcindario.com/insert_photo_location.php";
        RequestParams params = new RequestParams();
        params.put("id_user", datos[0]);
        params.put("id_location", id);

        //Cargar la imagen
        int numero = (int) (Math.random() * 99999999) + 1;
        int numero2 = (int) (Math.random() * 99999999) + 1;
        params.put("photo_url", is, numero+""+numero2 + "_location.jpg");


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
                            AppMsg.makeText(FragmentLocationDetail.this.getActivity(), "Imagen subida correctamente", AppMsg.STYLE_INFO).setLayoutGravity(Gravity.BOTTOM).show();
                        } else {
                            AppMsg.makeText(FragmentLocationDetail.this.getActivity(), "Error al intentar subir la imágen", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                        }

                    } catch (JSONException e) {
                        System.out.println("Falla:" + e);
                        AppMsg.makeText(FragmentLocationDetail.this.getActivity(), "Error al intentar subir la imágen", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();

                    }
                }
                pDialog.hide();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                AppMsg.makeText(FragmentLocationDetail.this.getActivity(), "Parece que hay algún problema con la red", AppMsg.STYLE_CONFIRM).setLayoutGravity(Gravity.BOTTOM).show();
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

    //---------------------- Funciones Visitado ----------------------------------------------
    public void SweetAlertVisited()
    {
        new SweetAlertDialog(this.getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(title_txt)
                .setContentText("¿Marcar como visitado?")
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


        String url = "http://interestingworld.webcindario.com/insert_location_visited.php";
        RequestParams params = new RequestParams();
        params.put("id_user", datos[0]);
        params.put("id_location", id);

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
                            SweetAlertInfo("Punto de interés no visitado",false);
                        } else {
                            if (getResult().equals("insertado")) {
                                SweetAlertComment("Punto de interés marcado como visitado.");
                            }else {
                                AppMsg.makeText(FragmentLocationDetail.this.getActivity(), "Ups.. algo ha fallado, vuelve a intentarlo", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                            }
                        }

                    } catch (JSONException e) {
                        System.out.println("Falla:" + e);
                        AppMsg.makeText(FragmentLocationDetail.this.getActivity(), "Error al intentar subir la imágen", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();

                    }
                }
                pDialog.hide();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                AppMsg.makeText(FragmentLocationDetail.this.getActivity(), "Parece que hay algún problema con la red", AppMsg.STYLE_CONFIRM).setLayoutGravity(Gravity.BOTTOM).show();
                pDialog.hide();
            }
        });
    }
    //-------------- Funciones LIKE ----------------------------------------------
    public void SweetAlertLike()
    {
        new SweetAlertDialog(this.getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(title_txt)
                .setContentText("¿Te gusta este punto de interés?")
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
        //Inicializamos dialog
        pDialog = new ProgressDialog(this.getActivity());
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Procesando...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);

        datos=Preferences.loadPreferences(this.getActivity());

        result="";

        client = new AsyncHttpClient();


        String url = "http://interestingworld.webcindario.com/insert_rating_location.php";
        RequestParams params = new RequestParams();
        params.put("id_user", datos[0]);
        params.put("id_location", id);

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
                            SweetAlertInfo("Ya no te gusta este punto de interés",false);
                        } else {
                            if (getResult().equals("insertado")) {
                                SweetAlertComment("Has marcado que te gusta");
                            }else {
                                AppMsg.makeText(FragmentLocationDetail.this.getActivity(), "Ups.. algo ha fallado, vuelve a intentarlo", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                            }
                        }

                    } catch (JSONException e) {
                        System.out.println("Falla:" + e);
                        AppMsg.makeText(FragmentLocationDetail.this.getActivity(), "Error al intentar publicar tu voto", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();

                    }
                }
                pDialog.hide();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                AppMsg.makeText(FragmentLocationDetail.this.getActivity(), "Parece que hay algún problema con la red", AppMsg.STYLE_CONFIRM).setLayoutGravity(Gravity.BOTTOM).show();
                pDialog.hide();
            }
        });
    }
//--------------------- Funciones Comentarios ---------------------------------------
    public void SweetAlertComment(String related)
    {
        new SweetAlertDialog(this.getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Tú opinión importa")
                .setContentText(related+".\n ¿Quieres dejar un comentario sobre que te ha parecido '" + title_txt + "'?")
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
                        dialogComment();
                    }
                })
                .show();
    }
    private void dialogComment() {
        // custom dialog
        FragmentDialogComment dFragment = new FragmentDialogComment();
        // Show DialogFragment
        dFragment.show(fm, "Dialog Fragment");
    }

    //-------------------------------------
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

    public void PathLocation(String mode)
    {
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lng+"&mode="+mode);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void SweetAlertPath()
    {
        new SweetAlertDialog(this.getActivity(), SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Marcar recorrido desde mi ubicación")
                .setContentText("Recuerda que debes tener el gps activado para poder utilizar esta función, no te olvides de marcar como visitado el punto de interés una vez lo hayas hecho.\n" +
                        "Selecciona como deseas desplazarte al lugar.")
                .setCancelText("Coche")
                .setConfirmText("Andando")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                        PathLocation("c");
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                        PathLocation("w");

                    }
                })
                .show();
    }
}
