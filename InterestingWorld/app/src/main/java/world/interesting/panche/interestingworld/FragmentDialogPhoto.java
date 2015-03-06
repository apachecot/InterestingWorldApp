package world.interesting.panche.interestingworld;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by Alex on 10/02/2015.
 */
public class FragmentDialogPhoto extends DialogFragment {
    public List<String> select_image = new ArrayList<String>();
    String name,url,lat,lng,id,description;
    View view;
    Bundle bundle= new Bundle();
    ImageButton bInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.custom_dialog_photo, container, false);
        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        bInfo = (ImageButton)view.findViewById(R.id.ic4);
        bInfo.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                MoreInfo();
            }
        });


        id = getArguments().getString("id");
        name = getArguments().getString("name");
        url = getArguments().getString("url");
        lat = getArguments().getString("lat");
        lng = getArguments().getString("lng");
        description = getArguments().getString("description");
        LoadImage();

        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void LoadImage()
    {
        ImageView photoDetail = (ImageView) view.findViewById(R.id.ImagePhoto);
        Picasso.with(getActivity())
                .load("http://" + url)
                .error(R.drawable.ic_launcher)
                .fit().centerCrop()
                .into(photoDetail);
    }
    public void MoreInfo()
    {
        Fragment fragment = new FragmentLocationDetail();
        bundle.putString("id_location",id);
        bundle.putString("title",name);
        bundle.putString("description",description);
        bundle.putString("lat",lat);
        bundle.putString("lng", lng);
        bundle.putString("url_photo",url);
        bundle.putString("user_location", "");
        fragment.setArguments(bundle);
        ((MaterialNavigationDrawer)this.getActivity()).setFragmentChild(fragment,name);
        FragmentDialogPhoto.this.dismiss();
    }
}