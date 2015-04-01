package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cuneytayyildiz.widget.PullRefreshLayout;
import com.devspark.appmsg.AppMsg;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;



/**
 * Created by neokree on 24/11/14.
 */
public class FragmentIndex extends Fragment {

    CardRecyclerView mRecyclerView;
    View inflatedView;
    CardArrayRecyclerViewAdapter mCardArrayAdapter;
    private SweetAlertDialog pDialog;
    private static Context mcontext;
    ArrayList<Location> list = new ArrayList<Location>();
    PullRefreshLayout layout;
    int category=0;
    MenuItem selected;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.activity_main, container, false);
        TextView text = new TextView(this.getActivity());
        text.setText(this.getResources().getString(R.string.profile));
        text.setGravity(Gravity.CENTER);
        setHasOptionsMenu(true);

        layout = (PullRefreshLayout) inflatedView.findViewById(R.id.swipeRefreshLayout);

        // listen refresh event
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });



        return inflatedView;

    }

    @Override
    public void onStart() {
        super.onStart();
        materialCardLoad();
        loadData();
    }

    public void loadData()
    {

        pDialog = new SweetAlertDialog(this.getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Cargando...");

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("category", category);

        String url="http://interestingworld.webcindario.com/consulta_locations.php";



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
                        System.out.println("Falla:"+e );
                        AppMsg.makeText(FragmentIndex.this.getActivity(), "Se ha producido un error al descargar los datos", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                        layout.setRefreshing(false);
                    }
                }
                pDialog.hide();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                AppMsg.makeText(FragmentIndex.this.getActivity(), "Parece que hay algún problema con la red", AppMsg.STYLE_CONFIRM).setLayoutGravity(Gravity.BOTTOM).show();
                pDialog.hide();
                list.clear();
                materialCardLoad();
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

            Location loc= new Location(jsonChildNode.getString("id"),jsonChildNode.getString("name"),jsonChildNode.getString("description"),
                    jsonChildNode.getString("photo_url"),jsonChildNode.getString("email"),"",jsonChildNode.getString("lat"),jsonChildNode.getString("lng"));
            list.add(loc);
        }
        materialCardLoad();
        return  list;
    }
    public void materialCardLoad ()
    {

        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new LocationsAdapter(list, R.layout.card,this.getActivity()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // refresh complete
        layout.setRefreshing(false);

    }
    /*
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem item3  = menu.findItem(R.id.add_location);
        item3.setVisible(false);
    }*/

    //Buscador por categorias
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        selected= menu.findItem(R.id.list);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click

        switch (item.getItemId()) {
            //All
            case R.id.category0:
                category=0;
                loadData();
                selected.setIcon(R.drawable.location_white);
                return true;
            //Monuments
            case R.id.category1:
                category=1;
                loadData();
                selected.setIcon(R.drawable.museum_bar);
                return true;
            //Museums
            case R.id.category2:
                category=2;
                loadData();
                selected.setIcon(R.drawable.art_bar);
                return true;
            //Beachs
            case R.id.category3:
                category=3;
                loadData();
                selected.setIcon(R.drawable.beach_bar);
                return true;
            //Bar
            case R.id.category4:
                category=4;
                loadData();
                selected.setIcon(R.drawable.beer_bar);
                return true;
            //Restaurant
            case R.id.category5:
                category=5;
                loadData();
                selected.setIcon(R.drawable.restaurant_bar);
                return true;
            //Fotografias
            case R.id.category6:
                category=6;
                loadData();
                selected.setIcon(R.drawable.photograph_white);
                return true;
            //Ocio
            case R.id.category7:
                category=7;
                loadData();
                selected.setIcon(R.drawable.leisure_white);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
