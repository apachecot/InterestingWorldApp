package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */

import android.graphics.Color;

import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cuneytayyildiz.widget.PullRefreshLayout;

import com.devspark.appmsg.AppMsg;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.melnykov.fab.FloatingActionButton;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class FragmentComments extends Fragment implements FragmentDialogComment.onSubmitComment{



    FragmentManager fm;


    View inflatedView;

    private SweetAlertDialog pDialog;
    ArrayList<Comments> list = new ArrayList<Comments>();
    PullRefreshLayout layout;
    MenuItem selected;
    FloatingActionButton fab;
    View emptyView;
    AsyncHttpClient client=new AsyncHttpClient();
    Boolean cancel;
    ImageButton reload;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(cancel==false) {
            inflatedView = inflater.inflate(R.layout.fragment_comments, container, false);
            TextView text = new TextView(this.getActivity());
            text.setText(this.getResources().getString(R.string.profile));
            text.setGravity(Gravity.CENTER);
            setHasOptionsMenu(true);
            fm = this.getActivity().getSupportFragmentManager();
            fab = (FloatingActionButton) inflatedView.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (getActivity().getLocalClassName().equals("MainActivityUser")) {
                        dialogComment();
                    } else {
                        AppMsg.makeText(getActivity(), "Debes estar logueado para poder introducir un comentario", AppMsg.STYLE_ALERT).setLayoutGravity(Gravity.BOTTOM).show();
                    }
                }
            });
            reload = (ImageButton) inflatedView.findViewById(R.id.ic1);
            reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadData();
                }
            });
            layout = (PullRefreshLayout) inflatedView.findViewById(R.id.swipeRefreshLayout);

            // listen refresh event
            layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadData();
                }
            });


        }
        return inflatedView;


    }

    @Override
    public void onDestroyView() {
        client.cancelRequests(this.getActivity(),true);
        super.onDestroyView();
    }
    @Override
    public void onDestroy() {
        cancel=true;
        client.cancelAllRequests(true);
        super.onDestroy();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        cancel=false;
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onStart() {
        materialCardLoad();
        loadData();
        super.onStart();
    }

    public void loadData()
    {

        pDialog = new SweetAlertDialog(this.getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Cargando...");

        Location loc;
        if(this.getActivity().getLocalClassName().equals("MainActivity")) {
            loc = ((MainActivity) getActivity()).GetLocationSelected();
        }else{
            loc = ((MainActivityUser) getActivity()).GetLocationSelected();
        }


        client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id", loc.getId());

        String url=Links.getUrl_get_comments();



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
                            layout.setRefreshing(false);
                            list.clear();
                            materialCardLoad();
                        }
                        AppMsg.makeText(getActivity(), "No se han encontrado comentarios", AppMsg.STYLE_CONFIRM).setLayoutGravity(Gravity.BOTTOM).show();
                    }
                }
                pDialog.hide();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(cancel==false) {
                    AppMsg.makeText(getActivity(), "Parece que hay algún problema con la red", AppMsg.STYLE_CONFIRM).setLayoutGravity(Gravity.BOTTOM).show();
                    pDialog.hide();
                    list.clear();
                    materialCardLoad();
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

            Comments com= new Comments(jsonChildNode.getString("id"),jsonChildNode.getString("id_location"),jsonChildNode.getString("id_user"),
                    jsonChildNode.getString("name")+" "+jsonChildNode.getString("lastname"),jsonChildNode.getString("photo_url"),jsonChildNode.getString("comment")
                    ,jsonChildNode.getString("date"));
            list.add(com);
        }
        materialCardLoad();
        return  list;
    }
    public void materialCardLoad ()
    {
        if(cancel==false) {
            RecyclerView recyclerView = (RecyclerView) inflatedView.findViewById(R.id.my_recycler_view);
            recyclerView.setHasFixedSize(true);
            CommentsAdapter dataset = new CommentsAdapter(list, R.layout.comment, this.getActivity());
            recyclerView.setAdapter(dataset);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            emptyView = (View) inflatedView.findViewById(R.id.empty_view2);
            if (dataset.getItemCount() == 0) {

                recyclerView.setVisibility(View.INVISIBLE);
                emptyView.setVisibility(View.VISIBLE);


            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.INVISIBLE);
            }
            fab.attachToRecyclerView(recyclerView);

            // refresh complete
            layout.setRefreshing(false);
        }

    }


    /**
     * Launching new activity
     * */
    private void dialogComment() {
        // custom dialog
        FragmentDialogComment dFragment = new FragmentDialogComment();
        dFragment.setTargetFragment(this, 0);
        dFragment.show(fm, "Dialog Fragment");
    }

    @Override
    public void onSubmitComment(String State) {
        loadData();
    }


    public void onDeleteComment() {
        loadData();
    }

}
