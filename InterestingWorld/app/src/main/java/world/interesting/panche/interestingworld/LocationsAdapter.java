package world.interesting.panche.interestingworld;

import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by Panche on 01/04/2015.
 */
public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {

    private ArrayList<Location> Locations;
    private int itemLayout;
    private final Context context;


    public  LocationsAdapter(ArrayList<Location> data,  int itemLayout,Context context){
        Locations = data;
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
        Location loc = Locations.get(position);
        holder.name.setText(loc.getName());
        holder.nLikes.setText(loc.getRating());

        Class cl=context.getClass();
        if(cl.getName().equals("world.interesting.panche.interestingworld.MainActivity")) {

            ((MainActivity) context).getmPicasso() //
                    .load(Links.getUrl_images()+loc.getUrl())//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).skipMemoryCache()
                    .placeholder(R.drawable.back1) //
                    .error(R.drawable.not_found) //
                    .fit().centerCrop()//
                    .tag(context)
                    .into(holder.image);
            //((MainActivity) context).getmPicasso() .invalidate("http://"+loc.getUrl());

        }else {

            ((MainActivityUser) context).getmPicasso() //
                    .load(Links.getUrl_images()+loc.getUrl())//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).skipMemoryCache()
                    .placeholder(R.drawable.back1) //
                    .error(R.drawable.not_found) //
                    .fit().centerCrop()//
                    .tag(context)
                    .into(holder.image);
            //((MainActivityUser) context).getmPicasso() .invalidate("http://"+loc.getUrl());
        }

        holder.itemView.setTag(loc);
    }

    @Override
    public int getItemCount() {
        return Locations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener {

        public ImageView image;
        public TextView name;
        public TextView nLikes;


        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            image = (ImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.name);
            nLikes=(TextView) itemView.findViewById(R.id.textViewLikes);

        }

        @Override
        public void onClick(View view) {

            Location loc= (Location) view.getTag();

            Fragment fragment;



            Class cl=view.getContext().getClass();

            if(cl.getName().equals("world.interesting.panche.interestingworld.MainActivity")) {
                ((MainActivity) view.getContext()).SetLocationSelected(loc);
                ((MainActivity) view.getContext()).setFragdetailstab();
                fragment =((MainActivity) view.getContext()).getFragdetailstab();
            }else {
                ((MainActivityUser) view.getContext()).SetLocationSelected(loc);
                ((MainActivityUser) view.getContext()).setFragdetailstab();
                fragment =((MainActivityUser) view.getContext()).getFragdetailstab();
            }
            ((MaterialNavigationDrawer)view.getContext()).setFragmentChild(fragment, loc.getName());
            //((MaterialNavigationDrawer)view.getContext()).onAttachFragment(fragment);
            System.out.println(((MaterialNavigationDrawer)view.getContext()).getSupportFragmentManager().getFragments());


        }

    }
}
