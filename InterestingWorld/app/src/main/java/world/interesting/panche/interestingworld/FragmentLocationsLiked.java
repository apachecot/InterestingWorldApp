package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cuneytayyildiz.widget.PullRefreshLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by neokree on 24/11/14.
 */
public class FragmentLocationsLiked extends Fragment {


    View inflatedView;

    private SweetAlertDialog pDialog;
    ArrayList<Location> list = new ArrayList<Location>();
    String[] datos=new String[5];
    PullRefreshLayout layout;
    View emptyView;
    AsyncHttpClient client=new AsyncHttpClient();
    Boolean cancel;
    ImageButton reload;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.activity_main, container, false);
        TextView text = new TextView(this.getActivity());
        text.setText(this.getResources().getString(R.string.profile));
        text.setGravity(Gravity.CENTER);

        layout = (PullRefreshLayout) inflatedView.findViewById(R.id.swipeRefreshLayout);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        reload = (ImageButton) inflatedView.findViewById(R.id.ic1);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

        return inflatedView;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        cancel=false;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        materialCardLoad();
        loadData();
    }
    @Override
    public void onDestroy() {
        client.cancelAllRequests(true);
        super.onDestroy();
    }
    @Override
    public void onDestroyView() {
        cancel=true;
        client.cancelRequests(this.getActivity(),true);
        super.onDestroyView();
    }

    public void loadData()
    {

        pDialog = new SweetAlertDialog(this.getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Cargando...");
        client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        datos=Preferences.loadPreferences(this.getActivity());
        params.put("id_user", datos[0]);
        String url=Links.getUrl_get_locations_liked();



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


                    }catch(JSONException e)
                    {
                        if(cancel==false) {
                            System.out.println("Falla:" + e);
                            Toast.makeText(getActivity(), "No se han encontrado datos", Toast.LENGTH_SHORT).show();
                            list.clear();
                            materialCardLoad();
                        }
                    }
                }
                pDialog.hide();
                layout.setRefreshing(false);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(cancel==false) {
                    Toast.makeText(getActivity(), "Parece que hay algún problema con la red", Toast.LENGTH_SHORT).show();
                    pDialog.hide();
                    layout.setRefreshing(false);
                }
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

            Location loc= new Location(jsonChildNode.getString("id"),jsonChildNode.getString("name"),jsonChildNode.getString("description"),
                    jsonChildNode.getString("photo_url"),jsonChildNode.getString("name"),jsonChildNode.getString("lastname"),
                    jsonChildNode.getString("id_user"),jsonChildNode.getString("photo_user"),jsonChildNode.getString("lat"),jsonChildNode.getString("lng"),
                    jsonChildNode.getString("address"),jsonChildNode.getString("country"),jsonChildNode.getString("locality"),jsonChildNode.getString("rating"));
            list.add(loc);
        }
        materialCardLoad();
        return  list;
    }
    public void materialCardLoad ()
    {
        if(cancel==false) {
            RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.my_recycler_view);
            recyclerView.setHasFixedSize(true);
            LocationsAdapter dataset = new LocationsAdapter(list, R.layout.card, this.getActivity());
            recyclerView.setAdapter(dataset);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            emptyView = (View) getActivity().findViewById(R.id.empty_view);
            if (dataset.getItemCount() == 0) {

                recyclerView.setVisibility(View.INVISIBLE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.INVISIBLE);
            }
            // refresh complete
            layout.setRefreshing(false);
        }

    }


}
