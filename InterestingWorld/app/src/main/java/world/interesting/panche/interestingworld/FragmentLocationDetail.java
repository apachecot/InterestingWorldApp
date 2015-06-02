package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

public class FragmentLocationDetail extends Fragment implements FragmentDialogComment.onSubmitComment {
    String title_txt,description_txt,lat,lng,url_photo,user_location,id,address,locality,country;
    View inflatedView;
    ImageButton bNavigate,bShare,bPhoto,bVisited,bLike,bDelete;
    Uri selectedImage;
    InputStream is;
    private ProgressDialog pDialog;
    String result;
    String[] datos= new String[5];
    FragmentManager fm;
    AsyncHttpClient client=new AsyncHttpClient();
    TextView nLikes;
    Location loc;
    File image;
    User userActual;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Seteamos la estructura del fragment
        inflatedView = inflater.inflate(R.layout.location_detail, container, false);
         fm = this.getActivity().getSupportFragmentManager();

        //TextView title = (TextView) inflatedView.findViewById(R.id.TextViewTitle);
        TextView description = (TextView) inflatedView.findViewById(R.id.TextViewDescription);
        TextView street = (TextView) inflatedView.findViewById(R.id.textViewAddress);
        TextView user = (TextView) inflatedView.findViewById(R.id.textViewUser);
        TextView category = (TextView) inflatedView.findViewById(R.id.textViewCategory);
        ImageView photoDetail = (ImageView) inflatedView.findViewById(R.id.ImageDetail);
        ImageView photoUser = (ImageView) inflatedView.findViewById(R.id.imageViewUser);
        ImageView imageMap = (ImageView) inflatedView.findViewById(R.id.ImageMapStatic);
        nLikes = (TextView) inflatedView.findViewById(R.id.textViewNlikes);



        if(this.getActivity().getLocalClassName().equals("MainActivity")) {

            loc = ((MainActivity) getActivity()).GetLocationSelected();
        }else{
            loc = ((MainActivityUser) getActivity()).GetLocationSelected();
            datos = Preferences.loadPreferences(getActivity());
            bDelete = (ImageButton)inflatedView.findViewById(R.id.imageButtonDelete);
            if(datos[0].equals(loc.getId_user())) {
                bDelete.setVisibility(View.VISIBLE);
                bDelete.setOnClickListener(new View.OnClickListener() {
                    // Start new list activity
                    public void onClick(View v) {
                        SweetAlertDeleted();
                    }
                });
            }
            else
            {
                bDelete.setVisibility(View.INVISIBLE);
            }
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
        switch (Integer.parseInt(loc.getCategory())) {
            //Monuments
            case 1:
                category.setText(getActivity().getResources().getString(R.string.monument));
                break;
            //Museums
            case 2:
                category.setText(getActivity().getResources().getString(R.string.art));
                break;
            //Beachs
            case 3:
                category.setText(getActivity().getResources().getString(R.string.beach));
                break;
            //Bar
            case 4:
                category.setText(getActivity().getResources().getString(R.string.bar));
                break;
            //Restaurant
            case 5:
                category.setText(getActivity().getResources().getString(R.string.restaurant));
                break;
            //Fotografias
            case 6:
                category.setText(getActivity().getResources().getString(R.string.photograph));
                break;
            //Ocio
            case 7:
                category.setText(getActivity().getResources().getString(R.string.leisure));
                break;
            default:
                category.setText(getActivity().getResources().getString(R.string.monument));
                break;

        }
        if(!country.equals("")) {
            street.setText(address + " " + locality + "," + country);
        }
        user.setText(loc.getUser()+" "+loc.getLastname());
        nLikes.setText(loc.getRating());
        Drawable shape = getResources(). getDrawable(R.drawable.circle);
        //nLikes.setBackground(shape);

        //title.setText(title_txt);
        description.setText(description_txt);
        Class cl=this.getActivity().getClass();
        if(cl.getName().equals("world.interesting.panche.interestingworld.MainActivity")) {
            ((MainActivity) getActivity()).getmPicasso()
                    .load(Links.getUrl_images()+url_photo)//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).skipMemoryCache()
                    .error(R.drawable.not_found)
                    .fit().centerCrop()
                    .into(photoDetail);
            ((MainActivity) getActivity()).getmPicasso()
                    .load(Links.getUrl_images()+loc.getPhoto_user())//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).skipMemoryCache()
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
                    .load(Links.getUrl_images()+url_photo)//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .error(R.drawable.not_found)
                    .fit().centerCrop()
                    .into(photoDetail);
            ((MainActivityUser) getActivity()).getmPicasso()
                    .load(Links.getUrl_images()+loc.getPhoto_user())//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
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
                    AppMsg.makeText(getActivity(), "Debes estar logueado para poder marcar como visitado", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
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
                    AppMsg.makeText(getActivity(), "Debes estar logueado para poder marcar que te gusta", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                }
            }
        });

        nLikes.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                if(getActivity().getLocalClassName().equals("MainActivityUser")) {
                    SweetAlertLike();
                }else{
                    AppMsg.makeText(getActivity(), "Debes estar logueado para poder marcar que te gusta", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
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
                    AppMsg.makeText(getActivity(), "Debes estar logueado para poder introducir una fotografía", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
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
    public void onDestroyView() {
        client.cancelRequests(this.getActivity(),true);
        super.onDestroyView();
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

        final String[] option = new String[] { "Hacer una fotografía", "Buscar una fotografía"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.select_dialog_item, option);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle("Elige una opción");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which) {
                switch (which)
                {
                    case 0:
                        take_photo();
                        break;
                    case 1:
                        image_path();
                        break;
                    default:
                        break;
                }

            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();

    }
    //Seleccionar una imagen por la ruta
    public void image_path()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        int  code = 2;
        startActivityForResult(intent, code);
    }
    //Realizar una fotografía nueva
    public void take_photo()
    {
        Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        //folder stuff
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "Interesting");
        imagesFolder.mkdirs();

        image = new File(imagesFolder, "QR_" + timeStamp + ".png");
        selectedImage = Uri.fromFile(image);

        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImage);
        startActivityForResult(imageIntent, 1);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                try {

                    is = this.getActivity().getContentResolver().openInputStream(selectedImage);
                    InputStream is2 = this.getActivity().getContentResolver().openInputStream(selectedImage);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    BufferedInputStream bis2 = new BufferedInputStream(is2);


                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(bis, null, options);
                    //Comprimimos la imagen
                    options.inSampleSize = calculateInSampleSize(options, 480, 480);
                    options.inJustDecodeBounds = false;
                    Bitmap bitmap = BitmapFactory.decodeStream(bis2, null, options);

                    //Rotar la imagen
                    String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
                    Cursor cur = this.getActivity().getContentResolver().query(selectedImage, orientationColumn, null, null, null);
                    Matrix matrix = new Matrix();
                    int orientation = -1;
                    if (cur != null && cur.moveToFirst()) {
                        orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));

                        matrix.postRotate(orientation);
                    }
                    else
                    {
                        try {
                            ExifInterface exif = new ExifInterface(
                                    image.getAbsolutePath());    //Since API Level 5
                            String exifOrientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                            orientation = exif.getAttributeInt(
                                    ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_NORMAL);
                            int rotate = 0;
                            switch (orientation) {
                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    rotate = 270;
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    rotate = 180;
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    rotate = 90;
                                    break;

                            }
                            matrix.postRotate(rotate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);

                    is = new ByteArrayInputStream(stream.toByteArray());
                    System.out.println(bitmap.getHeight() + " " + bitmap.getWidth());
                    SweetAlertImage();

                } catch (FileNotFoundException e) {
                    System.out.println("Error: " + e);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == 2) {

                try {
                    if (data != null) {

                        selectedImage = data.getData();
                        is = this.getActivity().getContentResolver().openInputStream(selectedImage);
                        InputStream is2 = this.getActivity().getContentResolver().openInputStream(selectedImage);
                        BufferedInputStream bis = new BufferedInputStream(is);
                        BufferedInputStream bis2 = new BufferedInputStream(is2);


                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(bis, null, options);
                        //Comprimimos la imagen
                        options.inSampleSize = calculateInSampleSize(options, 480, 480);
                        options.inJustDecodeBounds = false;
                        Bitmap bitmap = BitmapFactory.decodeStream(bis2, null, options);

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
                        System.out.println(bitmap.getHeight() + " " + bitmap.getWidth());
                        SweetAlertImage();

                    }
                }catch (FileNotFoundException e) {
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


        String url = Links.getUrl_add_image_location();
        RequestParams params = new RequestParams();
        params.put("id_user", datos[0]);
        params.put("id_location", id);

        //Cargar la imagen
        int numero = (int) (Math.random() * 99999999) + 1;
        int numero2 = (int) (Math.random() * 99999999) + 1;
        int numero3 = (int) (Math.random() * 99999999) + 1;
        params.put("photo_url", is, numero+""+numero2+""+numero3 + "_location.jpg");


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


        String url = Links.getUrl_add_location_visited();
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


        String url = Links.getUrl_add_rating_location();
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
                            loc.setRating(Integer.parseInt(loc.getRating())-1+"");
                            nLikes.setText(loc.getRating());
                        } else {
                            if (getResult().equals("insertado")) {
                                SweetAlertComment("Has marcado que te gusta");
                                loc.setRating(Integer.parseInt(loc.getRating())+1+"");
                                nLikes.setText(loc.getRating());
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
    //Fragment del comentario
    private void dialogComment() {
        FragmentDialogComment dFragment = new FragmentDialogComment();
        dFragment.setTargetFragment(this, 0);
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
    //---------------------------Funciones marcar recorrido------------------------
    public void PathLocation(String mode)
    {
        //Comprobamos que pueda acceder a la app de google maps
        try {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lng + "&mode=" + mode);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }catch (ActivityNotFoundException e) {
            //En caso de no tener redirigir al market
            try {
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.google.android.apps.maps")));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.apps.maps")));
            }
            e.printStackTrace();
        }
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

    //----------------------------Funcion Borrar punto de interés------------------------------
    public void SweetAlertDeleted()
    {
        new SweetAlertDialog(this.getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(title_txt)
                .setContentText("¿Deseas eliminar el punto de interés?")
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
                            deleted();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show();
    }


    public void deleted() throws JSONException, FileNotFoundException {
        //Inicializamos dialog
        pDialog = new ProgressDialog(this.getActivity());
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Procesando...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);

        datos=Preferences.loadPreferences(this.getActivity());

        result="";

        client = new AsyncHttpClient();


        String url = Links.getUrl_delete_location();
        RequestParams params = new RequestParams();
        params.put("id", id);

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
                            SweetAlertInfo("Punto de interés eliminado",false);
                            ((MaterialNavigationDrawer)getActivity()).onBackPressed();
                        } else {
                            AppMsg.makeText(FragmentLocationDetail.this.getActivity(), "Ups.. algo ha fallado, vuelve a intentarlo", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
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

    @Override
    public void onSubmitComment(String State) {

    }

}
