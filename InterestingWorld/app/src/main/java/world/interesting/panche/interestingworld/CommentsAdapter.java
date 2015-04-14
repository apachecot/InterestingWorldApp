package world.interesting.panche.interestingworld;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;

import java.util.ArrayList;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by Panche on 01/04/2015.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private ArrayList<Comments> Comments;
    private int itemLayout;
    private final Context context;


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
                    .load("http://" + com.getUrl())//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).skipMemoryCache()
                    .placeholder(R.drawable.back1) //
                    .error(R.drawable.not_found) //
                    .fit().centerCrop().transform(new RoundedTransformationPicasso())//
                    .tag(context)
                    .into(holder.image);
            //((MainActivity) context).getmPicasso() .invalidate("http://" + com.getUrl());

        }else {

            ((MainActivityUser) context).getmPicasso() //
                    .load("http://" + com.getUrl())//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).skipMemoryCache()
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

    public static class ViewHolder extends RecyclerView.ViewHolder //implements AdapterView.OnClickListener
    {

        public ImageView image;
        public TextView name;
        public TextView userName;
        public TextView date;

        public ViewHolder(View itemView) {
            super(itemView);

            //itemView.setOnClickListener(this);

            image = (ImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.name);
            userName = (TextView) itemView.findViewById(R.id.username);
            date = (TextView) itemView.findViewById(R.id.date);

        }
        //Desactivo el click para implementación más adelante
        /*
        @Override
        public void onClick(View view) {

            Location loc= (Location) view.getTag();
            Fragment fragment = new FragmentLocationDetailTabs();
            Class cl=view.getContext().getClass();

            if(cl.getName().equals("world.interesting.panche.interestingworld.MainActivity")) {
                ((MainActivity) view.getContext()).SetLocationSelected(loc);
            }else {
                ((MainActivityUser) view.getContext()).SetLocationSelected(loc);
            }
            ((MaterialNavigationDrawer)view.getContext()).setFragmentChild(fragment,loc.getName());

        }
        */

    }
}
