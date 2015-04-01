package world.interesting.panche.interestingworld;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by Alex on 10/02/2015.
 */
public class FragmentDialogPhoto extends DialogFragment {
    public List<String> select_image = new ArrayList<String>();
    String name,url,lat,lng,id,description;
    View view;
    Bundle bundle= new Bundle();
    ImageButton bInfo,bShare,bNavigate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.custom_dialog_photo, container, false);
        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        bInfo = (ImageButton)view.findViewById(R.id.ic4);
        bShare = (ImageButton)view.findViewById(R.id.ic3);
        bInfo.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                MoreInfo();
            }
        });
        bShare.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                onShareItem(v);
            }
        });


        id = getArguments().getString("id");
        name = getArguments().getString("name");
        url = getArguments().getString("url");
        lat = getArguments().getString("lat");
        lng = getArguments().getString("lng");
        description = getArguments().getString("description");
        LoadImage();
        bNavigate = (ImageButton)view.findViewById(R.id.ic1);
        bNavigate.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lng+"&mode=w");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

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
                .error(R.drawable.ic_launcher).skipMemoryCache()
                .into(photoDetail);
    }
    public void MoreInfo()
    {
        Fragment fragment = new FragmentLocationDetailTabs();
        Location loc= new Location(id,name,description,url,"","",lat,lng);
        if(this.getActivity().getLocalClassName().equals("MainActivity")) {
            ((MainActivity) getActivity()).SetLocationSelected(loc);
        }else {
            ((MainActivityUser) getActivity()).SetLocationSelected(loc);
        }
        ((MaterialNavigationDrawer)this.getActivity()).setFragmentChild(fragment,name);
        FragmentDialogPhoto.this.dismiss();
    }
    public void onShareItem(View v) {
        // Get access to bitmap image from view
        ImageView ivImage = (ImageView) view.findViewById(R.id.ImagePhoto);
        // Get access to the URI for the bitmap
        Uri bmpUri = getLocalBitmapUri(ivImage);
        if (bmpUri != null) {
            // Construct a ShareIntent with link to image
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TITLE,name);
            shareIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.url_share));
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image/*");
            // Launch sharing dialog for image
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        } else {
                    new SweetAlertDialog(this.getActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Parece que algo ha fallado!")
                    .show();
        }
    }

    // Returns the URI path to the Bitmap displayed in specified ImageView
    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}