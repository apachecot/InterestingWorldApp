package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cuneytayyildiz.widget.PullRefreshLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FragmentPhotosUser extends Fragment implements FragmentImageViewerProfile.onDelateImage {
    View inflatedView;
    GridViewAdapterImages gridAdapter;
    private SweetAlertDialog pDialog;
    ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
    private final List<String> urls = new ArrayList<String>();
    public List<String> select_image = new ArrayList<String>();
    FragmentManager fm;
    MenuItem selected;
    View emptyView;
    AsyncHttpClient client=new AsyncHttpClient();
    ImageButton reload;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        inflatedView = inflater.inflate(R.layout.photos_list_user, container, false);
        TextView text = new TextView(this.getActivity());
        text.setText(this.getResources().getString(R.string.photos));
        text.setGravity(Gravity.CENTER);
        setHasOptionsMenu(true);
        fm= this.getActivity().getSupportFragmentManager();

        gridAdapter=new GridViewAdapterImages(this.getActivity());
        emptyView = (View) inflatedView.findViewById(R.id.empty_view);

        GridView gv = (GridView) inflatedView.findViewById(R.id.grid_view);
        gv.setAdapter(gridAdapter);
        gv.setOnScrollListener(new SampleScrollListener(this.getActivity()));
        gv.setEmptyView(emptyView);
        reload = (ImageButton) inflatedView.findViewById(R.id.ic1);

        // listen refresh event
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDataPhotosUser();
            }
        });

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                select_image=gridAdapter.getInfoSelectedPhoto(position);
                ViewFull();
            }
        });



        loadDataPhotosUser();
        return inflatedView;
    }
    @Override
    public void onDestroy() {
        client.cancelAllRequests(true);
        super.onDestroy();
    }
    @Override
    public void onDestroyView() {
        client.cancelRequests(this.getActivity(),true);
        super.onDestroyView();
    }
    public void loadDataPhotosUser()
    {
        urls.clear();
        list.clear();
        pDialog = new SweetAlertDialog(this.getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Cargando...");
        client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        String[] datos=Preferences.loadPreferences(this.getActivity());
        params.put("id", datos[0]);

        String url=Links.getUrl_get_images_user();

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
                        pDialog.hide();


                    }catch(JSONException e)
                    {
                        System.out.println("Falla:"+e );
                        pDialog.hide();
                    }
                }
                pDialog.hide();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pDialog.hide();

            }
        });

    }

    public ArrayList setResult (String result) throws JSONException {

        list.clear();
        urls.clear();
        String cadenaJSON = result.toString();//Le pasamos a la variable cadenaJSON una cadena de tipo JSON (en este caso es la creada anteriormente)

        JSONObject jsonObject = new JSONObject(cadenaJSON); //Creamos un objeto de tipo JSON y le pasamos la cadena JSON
        String posts = jsonObject.getString("posts");

        JSONArray array = new JSONArray(posts);
        for(int i=0; i < array.length(); i++) {
            JSONObject jsonChildNode = array.getJSONObject(i);
            jsonChildNode = new JSONObject(jsonChildNode.optString("post").toString());

            ArrayList<String> datos = new ArrayList<String>();
            datos.add(jsonChildNode.getString("photo_url"));
            datos.add(jsonChildNode.getString("id"));
            datos.add(jsonChildNode.getString("rating"));
            list.add(datos);
            urls.add(jsonChildNode.getString("photo_url"));
        }
        gridAdapter.changeModelList(urls,list);


        return  list;
    }

    public void ViewFull()
    {

        FragmentImageViewerProfile dFragment = new FragmentImageViewerProfile();
        dFragment.setTargetFragment(this, 0);
        // Supply num input as an argument.
        // Show DialogFragment
        Class cl=this.getActivity().getClass();
        if(cl.getName().equals("world.interesting.panche.interestingworld.MainActivity")) {
            ((MainActivity) getActivity()).SetImageUrlFull(select_image.get(0));
        }else {
            ((MainActivityUser) getActivity()).SetImageUrlFull(select_image.get(0),select_image.get(1));
        }
        dFragment.show(fm, "Dialog Photo");

    }

    @Override
    public void onDelateImage(String State) {
        loadDataPhotosUser();

    }

}
