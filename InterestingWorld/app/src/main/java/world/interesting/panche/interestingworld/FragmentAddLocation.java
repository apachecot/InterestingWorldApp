package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.devspark.appmsg.AppMsg;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

public class FragmentAddLocation extends Fragment {

    EditText name,description;
    ImageView image_button;
    private ProgressDialog pDialog;
    String result;
    String[] datos= new String[5];
    Uri selectedImage;
    InputStream is;
    Handler handler = new Handler();
    CircularProgressButton circularProgressButton;
    Spinner categoryList;
    Category cat=new Category();
    AsyncHttpClient client=new AsyncHttpClient();
    File image;

    FragmentManager fm;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        fm= this.getActivity().getSupportFragmentManager();
        View inflatedView = inflater.inflate(R.layout.new_location, container, false);
        inflatedView.invalidate();
        EditText editAdress =(EditText)inflatedView.findViewById(R.id.editTextAdress);
        EditText editCity =(EditText)inflatedView.findViewById(R.id.editTextCity);
        EditText editCountry =(EditText)inflatedView.findViewById(R.id.editTextCountry);
        name=(EditText)inflatedView.findViewById(R.id.editTextName);
        description=(EditText)inflatedView.findViewById(R.id.editTextDescription);
        editAdress.setText("", TextView.BufferType.EDITABLE);
        editCity.setText("", TextView.BufferType.EDITABLE);
        editCountry.setText("", TextView.BufferType.EDITABLE);
        System.out.println(name.getText()+"antes");
        name.setText("", TextView.BufferType.EDITABLE);
       System.out.println(name.getText()+"despues");
        description.setText("", TextView.BufferType.EDITABLE);
        circularProgressButton=(CircularProgressButton) inflatedView.findViewById(R.id.buttonEnviar);


        return inflatedView;
    }
    @Override
    public void onDestroyView() {
        client.cancelRequests(this.getActivity(),true);
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MaterialNavigationDrawer)this.getActivity()).getSupportActionBar().show();
        name=(EditText)this.getActivity().findViewById(R.id.editTextName);
        description=(EditText)this.getActivity().findViewById(R.id.editTextDescription);
        addItemsSpinner();


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
        image_button = (ImageView) getView().findViewById(R.id.ImageViewLocation);
        // set a onclick listener for when the button gets clicked
        image_button.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {

                selectImage(v);
            }
        });
    }
    @Override
    public void onDestroy() {
        client.cancelAllRequests(true);
        super.onDestroy();
    }

    public void buttonAccept(View view) throws JSONException, FileNotFoundException {
        //Inicializamos dialog
        pDialog = new ProgressDialog(FragmentAddLocation.this.getActivity());
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Procesando...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);

        datos=Preferences.loadPreferences(this.getActivity());

        result="";

        client = new AsyncHttpClient();

        Double lat=((MainActivityUser) getActivity()).getLatitude();
        Double lng=((MainActivityUser) getActivity()).getLongitude();
        name.setError(null);
        if(isValidName(name.getText().toString())) {

            if (lat != 0.0)
            {
                if(is!=null) {
                    String url = Links.getUrl_add_location();

                    RequestParams params = new RequestParams();
                    params.put("id_user", datos[0]);
                    params.put("name", name.getText());
                    params.put("description", description.getText());
                    params.put("id_category", cat.GetIdCategory(String.valueOf(categoryList.getSelectedItem())));
                    params.put("lat", lat);
                    params.put("lng", lng);
                    EditText editAdress = (EditText) getActivity().findViewById(R.id.editTextAdress);
                    EditText editCity = (EditText) getActivity().findViewById(R.id.editTextCity);
                    EditText editCountry = (EditText) getActivity().findViewById(R.id.editTextCountry);
                    params.put("address", editAdress.getText());
                    params.put("country", editCity.getText());
                    params.put("locality", editCountry.getText());

                    //Cargar la imagen
                    int numero = (int) (Math.random() * 99999999) + 1;
                    int numero2 = (int) (Math.random() * 99999999) + 1;
                    int numero3 = (int) (Math.random() * 99999999) + 1;

                    params.put("photo_url", is, numero + "" + numero2 + "" + numero3 + "_location.jpg");
                    //params.setContentEncoding(HTTP.UTF_8);


                    client.post(url, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            circularProgressButton.setProgress(50);
                            circularProgressButton.setIndeterminateProgressMode(true);
                            //setCharset("UTF-8");

                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200) {

                                try {
                                    System.out.println(new String(responseBody));
                                    setResult(new String(responseBody));
                                    System.out.println(getResult());
                                    if (getResult().equals("bien")) {
                                        AppMsg.makeText(FragmentAddLocation.this.getActivity(), "Punto de interés añadido correctamente", AppMsg.STYLE_INFO).setLayoutGravity(Gravity.BOTTOM).show();
                                        ((MainActivityUser) getActivity()).UnSetPosition();
                                        circularProgressButton.setProgress(100);
                                        entrar();
                                    } else {
                                        AppMsg.makeText(FragmentAddLocation.this.getActivity(), "Error al intentar subir los datos, comprueba que todo este correcto", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                                        circularProgressButton.setProgress(-1);
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                circularProgressButton.setProgress(0);
                                            }
                                        }, 1000);
                                    }

                                } catch (JSONException e) {
                                    System.out.println("Falla:" + e);
                                    AppMsg.makeText(FragmentAddLocation.this.getActivity(), "Error al intentar subir los datos, comprueba que todo este correcto", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
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
                            AppMsg.makeText(FragmentAddLocation.this.getActivity(), "Parece que hay algún problema con la red", AppMsg.STYLE_CONFIRM).setLayoutGravity(Gravity.BOTTOM).show();
                            circularProgressButton.setProgress(-1);
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    circularProgressButton.setProgress(0);
                                }
                            }, 1000);
                            pDialog.hide();
                        }
                    });
                }else
                {
                    AppMsg.makeText(FragmentAddLocation.this.getActivity(), "Debes seleccionar introducir una fotografía", AppMsg.STYLE_CONFIRM).setLayoutGravity(Gravity.BOTTOM).show();
                    circularProgressButton.setProgress(-1);
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            circularProgressButton.setProgress(0);
                        }
                    }, 1000);
                    pDialog.hide();
                }
            } else {
                AppMsg.makeText(FragmentAddLocation.this.getActivity(), "Debes marcar el punto de interés antes de publicar", AppMsg.STYLE_CONFIRM).setLayoutGravity(Gravity.BOTTOM).show();
                circularProgressButton.setProgress(-1);
                handler.postDelayed(new Runnable() {
                    public void run() {
                        circularProgressButton.setProgress(0);
                    }
                }, 1000);
                pDialog.hide();
            }
        }else
        {
            name.setError("Es necesario indicar un nombre");
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
    public void entrar()
    {
        handler.postDelayed(new Runnable() {
            public void run() {
                circularProgressButton.setProgress(0);
                cleanActivity();
            }
        }, 1000);
    }
    public void cleanActivity()
    {
        EditText editAdress =(EditText)getActivity().findViewById(R.id.editTextAdress);
        EditText editCity =(EditText)getActivity().findViewById(R.id.editTextCity);
        EditText editCountry =(EditText)getActivity().findViewById(R.id.editTextCountry);
        editAdress.setText("");
        editCity.setText("");
        editCountry.setText("");
        name.setText("");
        description.setText("");
        ImageView iv = (ImageView) this.getActivity().findViewById(R.id.ImageViewLocation);
        iv.setImageDrawable(getResources().getDrawable(R.drawable.imagelocation));
        ((MaterialNavigationDrawer)getActivity()).onBackPressed();

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
                        //Añadimos la imagen a la vista
                        ImageView iv = (ImageView) this.getActivity().findViewById(R.id.ImageViewLocation);
                        iv.setImageBitmap(bitmap);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);

                        is = new ByteArrayInputStream(stream.toByteArray());
                        System.out.println(bitmap.getHeight() + " " + bitmap.getWidth());

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
                        //Añadimos la imagen a la vista
                        ImageView iv = (ImageView) this.getActivity().findViewById(R.id.ImageViewLocation);
                        iv.setImageBitmap(bitmap);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);

                        is = new ByteArrayInputStream(stream.toByteArray());
                        System.out.println(bitmap.getHeight() + " " + bitmap.getWidth());


                    }
                } catch (FileNotFoundException e) {
                    System.out.println("Error: " + e);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_location, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.add_location:
                dialogMap();
                // search action
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Launching new activity
     * */
    private void dialogMap() {
        // custom dialog
        FragmentDialogMap dFragment = new FragmentDialogMap();
        // Show DialogFragment
        dFragment.show(fm, "Dialog Fragment");
    }

    public void addItemsSpinner(){

        categoryList=(Spinner) this.getActivity().findViewById(R.id.spinnerCategory);
        System.out.println(cat.GetList());
        ArrayAdapter<String> dataAdapter= new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_spinner_dropdown_item,cat.GetList());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryList.setAdapter(dataAdapter);

    }

    // validating password with retype password
    private boolean isValidName(String name) {
        if (name != null && !name.equals("")) {
            return true;
        }
        return false;
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

}
