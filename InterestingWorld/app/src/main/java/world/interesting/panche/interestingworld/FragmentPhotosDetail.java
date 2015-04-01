package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
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

public class FragmentPhotosDetail extends Fragment {
    View inflatedView;
    GridViewAdapter gridAdapter;
    private SweetAlertDialog pDialog;
    ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
    private final List<String> urls = new ArrayList<String>();
    public List<String> select_image = new ArrayList<String>();
    FragmentManager fm;
    int category=0;
    MenuItem selected;
    PullRefreshLayout layout;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        inflatedView = inflater.inflate(R.layout.photos_list_detail, container, false);
        TextView text = new TextView(this.getActivity());
        text.setText(this.getResources().getString(R.string.photos));
        text.setGravity(Gravity.CENTER);
        setHasOptionsMenu(true);
        fm= this.getActivity().getSupportFragmentManager();

        gridAdapter=new GridViewAdapter(this.getActivity());

        GridView gv = (GridView) inflatedView.findViewById(R.id.grid_view);
        gv.setAdapter(gridAdapter);
        gv.setOnScrollListener(new SampleScrollListener(this.getActivity()));


        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                select_image=gridAdapter.getInfoSelectedPhoto(position);
                dialogPhoto();
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

        return  list;
    }

    private void dialogPhoto() {
        // custom dialog
        FragmentDialogPhoto dFragment = new FragmentDialogPhoto();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("id", select_image.get(0));
        args.putString("url", select_image.get(5));
        args.putString("lat", select_image.get(3));
        args.putString("lng", select_image.get(4));
        args.putString("name", select_image.get(1));
        args.putString("description", select_image.get(2));
        dFragment.setArguments(args);
        // Show DialogFragment
        dFragment.show(fm, "Dialog Photo");
    }
    public List<String> getInfoPhoto()
    {
        return select_image;
    }

}
