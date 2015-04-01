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
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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


        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(context) //
                .load("http://"+loc.getUrl()) //
                .placeholder(R.drawable.back1) //
                .error(R.drawable.not_found) //
                .fit().centerCrop()//
                .tag(context).skipMemoryCache() //
                .into(holder.image);


        holder.itemView.setTag(loc);
    }

    @Override
    public int getItemCount() {
        return Locations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnClickListener {

        public ImageView image;
        public TextView name;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            image = (ImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.name);

        }

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

    }
}
