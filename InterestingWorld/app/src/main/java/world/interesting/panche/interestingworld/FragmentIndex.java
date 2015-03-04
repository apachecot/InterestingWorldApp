package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.appmsg.AppMsg;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.cards.actions.BaseSupplementalAction;
import it.gmariotti.cardslib.library.cards.actions.IconSupplementalAction;
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
    private ProgressDialog pDialog;
    ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.activity_main, container, false);
        TextView text = new TextView(this.getActivity());
        text.setText(this.getResources().getString(R.string.profile));
        text.setGravity(Gravity.CENTER);
        setHasOptionsMenu(false);


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

        // Set supplemental actions as text
        final ArrayList<Card> cards = new ArrayList<Card>();
/*
        for (int i = 0; i <5; i++) {
            ArrayList<BaseSupplementalAction> actions = new ArrayList<BaseSupplementalAction>();
            //final String url=list.get(0).get(5).toString();

            // Set supplemental actions
            IconSupplementalAction t1 = new IconSupplementalAction(getActivity(), R.id.ic1);
            t1.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    Toast.makeText(getActivity(), "Click en " + card.getTitle(), Toast.LENGTH_SHORT).show();
                }
            });
            actions.add(t1);

            IconSupplementalAction t2 = new IconSupplementalAction(getActivity(), R.id.ic2);
            t2.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    Toast.makeText(getActivity(), " Click en " + card.getTitle(), Toast.LENGTH_SHORT).show();
                }
            });
            actions.add(t2);
            MaterialLargeImageCard card =
                    MaterialLargeImageCard.with(getActivity())
                            .setTextOverImage("hola")
                            //.setTitle("This is my favorite local beach ")
                            //.setSubTitle("A wonderful place")
                            .useDrawableExternal(new MaterialLargeImageCard.DrawableExternal() {
                                @Override
                                public void setupInnerViewElements(ViewGroup parent, View viewImage) {

                                    //Picasso.with(getActivity()).setIndicatorsEnabled(true);  //only for debug tests
                                    Picasso.with(getActivity())
                                            .load("http://")
                                            .error(R.drawable.ic_launcher)
                                            .into((ImageView) viewImage);
                                }
                            })
                            .setupSupplementalActions(R.layout.hover_sample1, actions)
                            .build();
            card.setOnClickListener(new Card.OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    Toast.makeText(getActivity(), " Click on ActionArea ", Toast.LENGTH_SHORT).show();
                }
            });
            cards.add(card);
        }
*/
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

        pDialog = new ProgressDialog(this.getActivity());
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Procesando...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);
        AsyncHttpClient client = new AsyncHttpClient();

        String url="http://interestingworld.webcindario.com/consulta_locations.php";



        client.post(url,new AsyncHttpResponseHandler() {
            @Override
            public void onStart()
            {
                pDialog.setProgress(0);
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
                    }
                }
                pDialog.hide();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                AppMsg.makeText(FragmentIndex.this.getActivity(), "Parece que hay algún problema con la red", AppMsg.STYLE_CONFIRM).setLayoutGravity(Gravity.BOTTOM).show();
                pDialog.hide();
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
        System.out.println("hola?"+list.get(0).get(3));
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

            // Set supplemental actions
            IconSupplementalAction t1 = new IconSupplementalAction(getActivity(), R.id.ic1);
            t1.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    Toast.makeText(getActivity(), "Click en " + name, Toast.LENGTH_SHORT).show();
                }
            });
            actions.add(t1);

            IconSupplementalAction t2 = new IconSupplementalAction(getActivity(), R.id.ic2);
            t2.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    Toast.makeText(getActivity(), " Click en " + url, Toast.LENGTH_SHORT).show();
                }
            });
            actions.add(t2);
            IconSupplementalAction t3 = new IconSupplementalAction(getActivity(), R.id.ic3);
            t3.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    Toast.makeText(getActivity(), " Click en " + card.getId(), Toast.LENGTH_SHORT).show();
                }
            });
            actions.add(t3);
            MaterialLargeImageCard card =
                    MaterialLargeImageCard.with(getActivity())
                            .setTextOverImage(name)
                                    //.setTitle("This is my favorite local beach ")
                                    //.setSubTitle("A wonderful place")
                            .useDrawableExternal(new MaterialLargeImageCard.DrawableExternal() {
                                @Override
                                public void setupInnerViewElements(ViewGroup parent, View viewImage) {

                                    //Picasso.with(getActivity()).setIndicatorsEnabled(true);  //only for debug tests
                                    Picasso.with(getActivity())
                                            .load("http://"+url)
                                            .error(R.drawable.ic_launcher)
                                            .into((ImageView) viewImage);
                                }
                            })
                            .setupSupplementalActions(R.layout.hover_sample1, actions)
                            .build();
            card.setId(id);
            card.setClickable(true);

            cards.add(card);
            card.setOnClickListener(new Card.OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    AppMsg.makeText(FragmentIndex.this.getActivity(), "Parece que hay algún problema con la red", AppMsg.STYLE_CONFIRM).setLayoutGravity(Gravity.BOTTOM).show();
                }
            });
        }
        mCardArrayAdapter.clear();
        mCardArrayAdapter.addAll(cards);
        mCardArrayAdapter.notifyDataSetChanged();

        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mCardArrayAdapter);
        }

    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem item3  = menu.findItem(R.id.add_location);
        item3.setVisible(false);
    }

}
