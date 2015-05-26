package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cuneytayyildiz.widget.PullRefreshLayout;
import com.devspark.appmsg.AppMsg;
import com.google.android.gms.fitness.data.DataSet;
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
public class FragmentIndex extends Fragment {

    View inflatedView;

    private SweetAlertDialog pDialog;
    private static Context mcontext;
    ArrayList<Location> list = new ArrayList<Location>();
    PullRefreshLayout layout;
    int option=0;
    MenuItem selected;
    View emptyView;
    AsyncHttpClient client=new AsyncHttpClient();
    Boolean cancel;
    ImageButton reload;
    LocationsAdapter dataset;


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
        System.out.println("ondestroyview");
        cancel=true;
        client.cancelRequests(this.getActivity(),true);
        client.cancelAllRequests(true);
        super.onDestroyView();
    }

    public void loadData()
    {
        list.clear();
        pDialog = new SweetAlertDialog(this.getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Cargando...");

        client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        if(getActivity() instanceof MainActivityUser) {
            params.put("search",((MainActivityUser) getActivity()).getAdvancedSearch());
            params.put("category", ((MainActivityUser) getActivity()).getCategory());
        }else{
            params.put("search",((MainActivity) getActivity()).getAdvancedSearch());
            params.put("category", ((MainActivity) getActivity()).getCategory());
        }


        String url=Links.getUrl_get_locations();



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
                            AppMsg.makeText(FragmentIndex.this.getActivity(), "No se han encontrado datos", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                            layout.setRefreshing(false);
                            materialCardLoad();
                        }
                    }
                }
                pDialog.hide();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(cancel==false) {
                    AppMsg.makeText(FragmentIndex.this.getActivity(), "Parece que hay algún problema con la red", AppMsg.STYLE_CONFIRM).setLayoutGravity(Gravity.BOTTOM).show();
                    pDialog.hide();
                    materialCardLoad();
                    layout.setRefreshing(false);
                }
            }
        });
    }

    public ArrayList setResult (String result) throws JSONException {


        String cadenaJSON = result.toString();//Le pasamos a la variable cadenaJSON una cadena de tipo JSON (en este caso es la creada anteriormente)

        JSONObject jsonObject = new JSONObject(cadenaJSON); //Creamos un objeto de tipo JSON y le pasamos la cadena JSON
        String posts = jsonObject.getString("posts");

        JSONArray array = new JSONArray(posts);
        for(int i=0; i < array.length(); i++) {
            JSONObject jsonChildNode = array.getJSONObject(i);
            jsonChildNode = new JSONObject(jsonChildNode.optString("post").toString());

            Location loc= new Location(jsonChildNode.getString("id"),jsonChildNode.getString("name"),jsonChildNode.getString("description"),
                    jsonChildNode.getString("photo_url"),jsonChildNode.getString("user_name"),jsonChildNode.getString("lastname"),
                    jsonChildNode.getString("id_user"),jsonChildNode.getString("photo_user"),jsonChildNode.getString("lat"),jsonChildNode.getString("lng"),
                    jsonChildNode.getString("address"),jsonChildNode.getString("country"),jsonChildNode.getString("locality"),jsonChildNode.getString("rating"));
            list.add(loc);
        }
        materialCardLoad();
        return  list;
    }
    public void materialCardLoad ()
    {
        System.out.println(cancel);
        if(cancel==false) {

            RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.my_recycler_view);
            recyclerView.setHasFixedSize(true);
             dataset = new LocationsAdapter(list, R.layout.card, this.getActivity());
            recyclerView.setAdapter(dataset);
            LinearLayoutManager mLayoutManager= new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
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
        inflater.inflate(R.menu.menu_search_advanced, menu);
        selected= menu.findItem(R.id.list);
        switch (option) {
            //All
            case 0:
                selected.setIcon(R.drawable.location_white);
                break;
                //Monuments
            case 1:
                selected.setIcon(R.drawable.museum_bar);
                break;
                //Museums
            case 2:
                selected.setIcon(R.drawable.art_bar);
                break;
                //Beachs
            case 3:
                selected.setIcon(R.drawable.beach_bar);
                break;
                //Bar
            case 4:
                selected.setIcon(R.drawable.beer_bar);
                break;
                //Restaurant
            case 5:
                selected.setIcon(R.drawable.restaurant_bar);
                break;
                //Fotografias
            case 6:
                selected.setIcon(R.drawable.photograph_white);
                break;
                //Ocio
            case 7:
                selected.setIcon(R.drawable.leisure_white);
                break;
            case 8:
                selected.setIcon(R.drawable.advanced);
                break;
            default:
                selected.setIcon(R.drawable.location_white);
                break;

        }
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click

        switch (item.getItemId()) {
            //All
            case R.id.category0:
                option=0;
                if(getActivity() instanceof MainActivityUser) {
                    ((MainActivityUser) getActivity()).setAdvancedSearch("");
                    ((MainActivityUser) getActivity()).setCategory(0);
                }else{
                    ((MainActivity) getActivity()).setAdvancedSearch("");
                    ((MainActivity) getActivity()).setCategory(0);
                }
                list.clear();
                loadData();
                selected.setIcon(R.drawable.location_white);
                return true;
            //Monuments
            case R.id.category1:
                option=1;
                if(getActivity() instanceof MainActivityUser) {
                    ((MainActivityUser) getActivity()).setAdvancedSearch("");
                    ((MainActivityUser) getActivity()).setCategory(1);
                }else{
                    ((MainActivity) getActivity()).setAdvancedSearch("");
                    ((MainActivity) getActivity()).setCategory(1);
                }
                list.clear();
                loadData();
                selected.setIcon(R.drawable.museum_bar);
                return true;
            //Museums
            case R.id.category2:
                option=2;
                if(getActivity() instanceof MainActivityUser) {
                    ((MainActivityUser) getActivity()).setAdvancedSearch("");
                    ((MainActivityUser) getActivity()).setCategory(2);
                }else{
                    ((MainActivity) getActivity()).setAdvancedSearch("");
                    ((MainActivity) getActivity()).setCategory(2);
                }
                list.clear();
                loadData();
                selected.setIcon(R.drawable.art_bar);
                return true;
            //Beachs
            case R.id.category3:
                option=3;
                if(getActivity() instanceof MainActivityUser) {
                    ((MainActivityUser) getActivity()).setAdvancedSearch("");
                    ((MainActivityUser) getActivity()).setCategory(3);
                }else{
                    ((MainActivity) getActivity()).setAdvancedSearch("");
                    ((MainActivity) getActivity()).setCategory(3);
                }
                list.clear();
                loadData();
                selected.setIcon(R.drawable.beach_bar);
                return true;
            //Bar
            case R.id.category4:
                option=4;
                if(getActivity() instanceof MainActivityUser) {
                    ((MainActivityUser) getActivity()).setAdvancedSearch("");
                    ((MainActivityUser) getActivity()).setCategory(4);
                }else{
                    ((MainActivity) getActivity()).setAdvancedSearch("");
                    ((MainActivity) getActivity()).setCategory(4);
                }
                list.clear();
                loadData();
                selected.setIcon(R.drawable.beer_bar);
                return true;
            //Restaurant
            case R.id.category5:
                option=5;
                if(getActivity() instanceof MainActivityUser) {
                    ((MainActivityUser) getActivity()).setAdvancedSearch("");
                    ((MainActivityUser) getActivity()).setCategory(5);
                }else{
                    ((MainActivity) getActivity()).setAdvancedSearch("");
                    ((MainActivity) getActivity()).setCategory(5);
                }
                list.clear();
                loadData();
                selected.setIcon(R.drawable.restaurant_bar);
                return true;
            //Fotografias
            case R.id.category6:
                option=6;
                if(getActivity() instanceof MainActivityUser) {
                    ((MainActivityUser) getActivity()).setAdvancedSearch("");
                    ((MainActivityUser) getActivity()).setCategory(6);
                }else{
                    ((MainActivity) getActivity()).setAdvancedSearch("");
                    ((MainActivity) getActivity()).setCategory(6);
                }
                list.clear();
                loadData();
                selected.setIcon(R.drawable.photograph_white);
                return true;
            //Ocio
            case R.id.category7:
                option=7;
                if(getActivity() instanceof MainActivityUser) {
                    ((MainActivityUser) getActivity()).setAdvancedSearch("");
                    ((MainActivityUser) getActivity()).setCategory(7);
                }else{
                    ((MainActivity) getActivity()).setAdvancedSearch("");
                    ((MainActivity) getActivity()).setCategory(7);
                }
                list.clear();
                loadData();
                selected.setIcon(R.drawable.leisure_white);
                return true;
            case R.id.category8:
                option=8;
                FragmentManager fm = this.getActivity().getSupportFragmentManager();
                FragmentDialogAdvanced dFragment = new FragmentDialogAdvanced();
                dFragment.setTargetFragment(this,1);
                dFragment.show(fm, "Dialog Fragment");
                selected.setIcon(R.drawable.advanced);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Callback del dialog fragment avanzado
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == 1)
        {
            if(resultCode == 1)
            {
                list.clear();
                loadData();
            }
        }
    }
}
