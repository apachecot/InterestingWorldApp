package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cuneytayyildiz.widget.PullRefreshLayout;
import com.devspark.appmsg.AppMsg;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import it.gmariotti.cardslib.library.cards.actions.BaseSupplementalAction;
import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
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
    ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
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
        materialCard();
        loadData();
    }
    public void materialCard ()
    {

        final ArrayList<Card> cards = new ArrayList<Card>();

        mCardArrayAdapter = new CardArrayRecyclerViewAdapter(this.getActivity(), cards);
        mRecyclerView = (CardRecyclerView) this.getActivity().findViewById(R.id.cardList);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mCardArrayAdapter);
        }

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

            ArrayList<String> datos = new ArrayList<String>();
            datos.add(jsonChildNode.getString("id"));
            datos.add(jsonChildNode.getString("name"));
            datos.add(jsonChildNode.getString("description"));
            datos.add(jsonChildNode.getString("lat"));
            datos.add(jsonChildNode.getString("lng"));
            datos.add(jsonChildNode.getString("photo_url"));
            datos.add(jsonChildNode.getString("email"));
            list.add(datos);
        }
        materialCardLoad();
        return  list;
    }
    public void materialCardLoad ()
    {

        // Set supplemental actions as text
        final ArrayList<Card> cards = new ArrayList<Card>();

        for (int i = 0; i <list.size(); i++) {
            ArrayList<BaseSupplementalAction> actions = new ArrayList<BaseSupplementalAction>();
            final String id = list.get(i).get(0).toString();
            final String name = list.get(i).get(1).toString();
            final String url = list.get(i).get(5).toString();
            System.out.println(id);

            MaterialLargeImageCard card = MaterialLargeImageCard.with(this.getActivity().getApplicationContext())
                            .setTextOverImage(name)
                            .useDrawableExternal(new MaterialLargeImageCard.DrawableExternal() {
                                @Override
                                public void setupInnerViewElements(ViewGroup parent, View viewImage) {
                                    Picasso.with(getActivity())
                                            .load("http://"+url)
                                            .error(R.drawable.ic_launcher)
                                            .into((ImageView) viewImage);
                                }
                            })
                            .build();
            card.setId(id);
            card.setClickable(true);

            card.setOnClickListener(new Card.OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    Toast.makeText(getActivity()," Click on ActionArea ", Toast.LENGTH_SHORT).show();
                }
            });


            cards.add(card);

        }
        mCardArrayAdapter.clear();
        mCardArrayAdapter.addAll(cards);
        mCardArrayAdapter.notifyDataSetChanged();

        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mCardArrayAdapter);
        }
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
