package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.cuneytayyildiz.widget.PullRefreshLayout;
import com.devspark.appmsg.AppMsg;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.melnykov.fab.FloatingActionButton;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

public class FragmentPhotosDetail extends Fragment {
    View inflatedView;
    GridViewAdapter gridAdapter;
    private SweetAlertDialog pDialog;
    ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
    private final List<String> urls = new ArrayList<String>();
    public List<String> select_image = new ArrayList<String>();
    FragmentManager fm;
    FloatingActionButton fab;
    MenuItem selected;
    PullRefreshLayout layout;
    GridView gv;
    String result,id;
    String[] datos= new String[5];
    Uri selectedImage;
    InputStream is;
    TextView emptyView;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        inflatedView = inflater.inflate(R.layout.photos_list_detail, container, false);
        TextView text = new TextView(this.getActivity());
        text.setText(this.getResources().getString(R.string.photos));
        text.setGravity(Gravity.CENTER);
        setHasOptionsMenu(true);
        fm= this.getActivity().getSupportFragmentManager();

        emptyView = (TextView) inflatedView.findViewById(R.id.empty_view);
        gridAdapter=new GridViewAdapter(this.getActivity());

        gv = (GridView) inflatedView.findViewById(R.id.grid_view);
        gv.setAdapter(gridAdapter);
        gv.setOnScrollListener(new SampleScrollListener(this.getActivity()));
        gv.setEmptyView(emptyView);

        fab= (FloatingActionButton) inflatedView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(getActivity().getLocalClassName().equals("MainActivityUser")) {
                    selectImage(v);
                }else{
                    AppMsg.makeText(getActivity(), "Debes estar loggeado para poder introducir un comentario", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                }
            }
        });
        Location loc;
        if(this.getActivity().getLocalClassName().equals("MainActivity")) {
            loc = ((MainActivity) getActivity()).GetLocationSelected();
        }else{
            loc = ((MainActivityUser) getActivity()).GetLocationSelected();
        }
        id= loc.getId();


        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                select_image=gridAdapter.getInfoSelectedPhoto(position);
                ViewFull();
            }
        });

        layout = (PullRefreshLayout) inflatedView.findViewById(R.id.swipeRefreshLayout);

        // listen refresh event
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                urls.clear();
                loadData();

            }
        });
        urls.clear();
        loadData();
        return inflatedView;
    }

    public void loadData()
    {
        pDialog = new SweetAlertDialog(this.getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Cargando...");
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        Location loc;
        if(this.getActivity().getLocalClassName().equals("MainActivity")) {
            loc = ((MainActivity) getActivity()).GetLocationSelected();
        }else{
            loc = ((MainActivityUser) getActivity()).GetLocationSelected();
        }
        params.put("id", loc.getId());

        String url="http://interestingworld.webcindario.com/consulta_photos_detail.php";

        client.post(url,params,new AsyncHttpResponseHandler() {
            @Override
            public void onStart()
            {
                pDialog.setCancelable(true);
                pDialog.show();
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200)
                {

                    try {
                        System.out.println(new String(responseBody));
                        setResult(new String(responseBody));
                        layout.setRefreshing(false);


                    }catch(JSONException e)
                    {
                        System.out.println("Falla:"+e );
                        layout.setRefreshing(false);
                    }
                }
                pDialog.hide();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pDialog.hide();
                layout.setRefreshing(false);
            }
        });

    }

    public ArrayList setResult (String result) throws JSONException {

        list.clear();
        String cadenaJSON = result.toString();//Le pasamos a la variable cadenaJSON una cadena de tipo JSON (en este caso es la creada anteriormente)

        JSONObject jsonObject = new JSONObject(cadenaJSON); //Creamos un objeto de tipo JSON y le pasamos la cadena JSON
        String posts = jsonObject.getString("posts");

        JSONArray array = new JSONArray(posts);
        for(int i=0; i < array.length(); i++) {
            JSONObject jsonChildNode = array.getJSONObject(i);
            jsonChildNode = new JSONObject(jsonChildNode.optString("post").toString());

            ArrayList<String> datos = new ArrayList<String>();
            datos.add(jsonChildNode.getString("photo_url"));
            list.add(datos);
            urls.add(jsonChildNode.getString("photo_url"));
        }
        gridAdapter.changeModelList(urls,list);
        fab.attachToListView(gv);
        return  list;
    }


    public void ViewFull()
    {

        FragmentImageViewer dFragment = new FragmentImageViewer();
        // Supply num input as an argument.
        // Show DialogFragment
        Class cl=this.getActivity().getClass();
        if(cl.getName().equals("world.interesting.panche.interestingworld.MainActivity")) {
            ((MainActivity) getActivity()).SetImageUrlFull(select_image.get(0));
        }else {
            ((MainActivityUser) getActivity()).SetImageUrlFull(select_image.get(0));
        }
        dFragment.show(fm, "Dialog Photo");

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

                options.inSampleSize = calculateInSampleSize(options,640,480);
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeStream(bis2,null,options);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                is = new ByteArrayInputStream(stream.toByteArray());
                SweetAlert();

            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: "+e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public void SweetAlert()
    {
        new SweetAlertDialog(this.getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("")
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
                            buttonAccept();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show();
    }
    public void buttonAccept() throws JSONException, FileNotFoundException {
        //Inicializamos dialog
        pDialog = new SweetAlertDialog(this.getActivity());
        pDialog = new SweetAlertDialog(this.getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Cargando...");

        datos=loadPreferences();

        result="";

        AsyncHttpClient client = new AsyncHttpClient();


        String url = "http://interestingworld.webcindario.com/insert_photo_location.php";
        RequestParams params = new RequestParams();
        params.put("id_user", datos[0]);
        params.put("id_location", id);

        //Cargar la imagen
        int numero = (int) (Math.random() * 99999999) + 1;
        //is = this.getActivity().getContentResolver().openInputStream(selectedImage);
        params.put("photo_url", is, numero + "_upload.jpg");


        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {

                    try {
                        System.out.println(new String(responseBody));
                        setResult2(new String(responseBody));
                        System.out.println(getResult());
                        if (getResult().equals("bien")) {
                            AppMsg.makeText(FragmentPhotosDetail.this.getActivity(), "Imagen subida correctamente", AppMsg.STYLE_INFO).setLayoutGravity(Gravity.BOTTOM).show();
                        } else {
                            AppMsg.makeText(FragmentPhotosDetail.this.getActivity(), "Error al intentar subir la imágen", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                        }

                    } catch (JSONException e) {
                        System.out.println("Falla:" + e);
                        AppMsg.makeText(FragmentPhotosDetail.this.getActivity(), "Error al intentar subir la imágen", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();

                    }
                }
                pDialog.hide();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                AppMsg.makeText(FragmentPhotosDetail.this.getActivity(), "Parece que hay algún problema con la red", AppMsg.STYLE_CONFIRM).setLayoutGravity(Gravity.BOTTOM).show();
                pDialog.hide();
            }
        });
    }

    public String getResult (){


        return this.result;
    }
    public String setResult2 (String result) throws JSONException {
        String cadenaJSON = result.toString();//Le pasamos a la variable cadenaJSON una cadena de tipo JSON (en este caso es la creada anteriormente)

        JSONObject jsonObject = new JSONObject(cadenaJSON); //Creamos un objeto de tipo JSON y le pasamos la cadena JSON
        String posts = jsonObject.getString("posts");

        //Entramos en el array de posts
        jsonObject=new JSONObject(posts);

        //A través de los nombres de cada dato obtenemos su contenido
        return  this.result=jsonObject.getString("Estado").toLowerCase();
    }
    public String[] loadPreferences() {
        String[] datos=new String[5];
        SharedPreferences prefs = this.getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
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
