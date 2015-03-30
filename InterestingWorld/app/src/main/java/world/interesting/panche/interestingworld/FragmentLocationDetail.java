package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FragmentLocationDetail extends Fragment {
    String title_txt,description_txt,lat,lng,url_photo,user_location;
    View inflatedView;
    ImageButton bNavigate,bShare;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();

        //Seteamos la estructura del fragment
        inflatedView = inflater.inflate(R.layout.scroll_one_parrallax_alpha, container, false);

        //Cambiamos el icono del menu por el de volver atras
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if(this.getActivity().getLocalClassName().equals("MainActivity"))
        {
            actionBar.setHomeAsUpIndicator(((MainActivity) this.getActivity()).getV7DrawerToggleDelegate().getThemeUpIndicator());
        }else{
            actionBar.setHomeAsUpIndicator(((MainActivityUser) this.getActivity()).getV7DrawerToggleDelegate().getThemeUpIndicator());
        }



        //TextView title = (TextView) inflatedView.findViewById(R.id.TextViewTitle);
        TextView description = (TextView) inflatedView.findViewById(R.id.TextViewDescription);
        ImageView photoDetail = (ImageView) inflatedView.findViewById(R.id.ImageDetail);

            bundle.getString("id_location", "-1");
             title_txt = bundle.getString("title", "");
             description_txt = bundle.getString("description", "");
             lat=bundle.getString("lat", "");
             lng=bundle.getString("lng", "");
             url_photo=bundle.getString("url_photo", "");
             user_location=bundle.getString("user_location", "");

            //title.setText(title_txt);
            description.setText(description_txt);
            Picasso.with(getActivity())
                    .load("http://" + url_photo)
                    .error(R.drawable.ic_launcher)
                    .fit().centerCrop()
                    .into(photoDetail);

        bShare = (ImageButton)inflatedView.findViewById(R.id.ic3);
        bShare.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                onShareItem(v);
            }
        });
        bNavigate = (ImageButton)inflatedView.findViewById(R.id.ic1);
        bNavigate.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lng+"&mode=w");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        return inflatedView;
    }
    public void onShareItem(View v) {
        // Get access to bitmap image from view
        ImageView ivImage = (ImageView) inflatedView.findViewById(R.id.ImageDetail);
        // Get access to the URI for the bitmap
        Uri bmpUri = getLocalBitmapUri(ivImage);
        if (bmpUri != null) {
            // Construct a ShareIntent with link to image
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TITLE,title_txt);
            shareIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.url_share)+" "+description_txt);
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
