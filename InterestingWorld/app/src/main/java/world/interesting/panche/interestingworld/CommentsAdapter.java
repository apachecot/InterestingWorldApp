package world.interesting.panche.interestingworld;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.devspark.appmsg.AppMsg;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by Panche on 01/04/2015.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private ArrayList<Comments> Comments;
    private int itemLayout;
    private final Context context;
    private String result;


    public CommentsAdapter(ArrayList<Comments> data, int itemLayout, Context context){
        Comments = data;
        this.itemLayout = itemLayout;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comments com = Comments.get(position);
        holder.name.setText(com.getComment());
        holder.userName.setText(com.getUser_name());
        holder.date.setText(com.getDate());

        Class cl=context.getClass();
        if(cl.getName().equals("world.interesting.panche.interestingworld.MainActivity")) {

            ((MainActivity) context).getmPicasso() //
                    .load(Links.getUrl_images() + com.getUrl())//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).skipMemoryCache()
                    .placeholder(R.drawable.back1) //
                    .error(R.drawable.not_found) //
                    .fit().centerCrop().transform(new RoundedTransformationPicasso())//
                    .tag(context)
                    .into(holder.image);
            //((MainActivity) context).getmPicasso() .invalidate("http://" + com.getUrl());

        }else {

            ((MainActivityUser) context).getmPicasso() //
                    .load(Links.getUrl_images() + com.getUrl())//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).skipMemoryCache()
                    .placeholder(R.drawable.back1) //
                    .error(R.drawable.not_found) //
                    .fit().centerCrop().transform(new RoundedTransformationPicasso())//
                    .tag(context)
                    .into(holder.image);
           // ((MainActivityUser) context).getmPicasso() .invalidate("http://" + com.getUrl());
        }

        holder.itemView.setTag(com);

    }


    @Override
    public int getItemCount() {
        return Comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener
    {

        public ImageView image;
        public TextView name;
        public TextView userName;
        public TextView date;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            image = (ImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.name);
            userName = (TextView) itemView.findViewById(R.id.username);
            date = (TextView) itemView.findViewById(R.id.date);

        }

        //Al hacer click si es comentario propio preguntar si desea eliminar
        @Override
        public void onClick(View view) {

            Comments com= (Comments) view.getTag();
            Class cl=view.getContext().getClass();

            if(cl.getName().equals("world.interesting.panche.interestingworld.MainActivityUser")) {
                User us=((MainActivityUser) view.getContext()).getUser();
                if(us.getId().equals(com.getId_user()))
                {
                    SweetAlertComment(com.getId());
                }
            }


        }
    }
    //---------------------- Funciones Visitado ----------------------------------------------
    public void SweetAlertComment(final String id)
    {
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Comentario")
                .setContentText("¿Deseas borrar tú comentario?")
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
                        eliminate(id);
                    }
                })
                .show();
    }
    public void eliminate(String id){
        //Inicializamos dialog

        result="";

        AsyncHttpClient client = new AsyncHttpClient();


        String url = Links.getUrl_delete_comments();
        RequestParams params = new RequestParams();
        params.put("id", id);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {

                    try {
                        System.out.println(new String(responseBody));
                        setResult(new String(responseBody));

                        if (result.equals("bien")) {
                            SweetAlertInfo("Comentario eliminado",true);
                            Fragment fr=((MainActivityUser) context).getFragcomments();
                            fr.onStart();
                        } else {

                            SweetAlertInfo("Ups.. algo ha fallado, vuelve a intentarlo",false);

                        }

                    } catch (JSONException e) {
                        SweetAlertInfo("Ups.. algo ha fallado, vuelve a intentarlo",false);

                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                SweetAlertInfo("Parece que hay algún problema con la red",false);
            }
        });
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
    //-------------------------------------
    public void SweetAlertInfo(String message,Boolean format)
    {
        int style=1;
        if(format==true)
        {
            style=SweetAlertDialog.SUCCESS_TYPE;
        }else{ style=SweetAlertDialog.ERROR_TYPE;}

        new SweetAlertDialog(context, style)
                .setTitleText(message)
                .setConfirmText("Ok")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                })
                .show();
    }
}
