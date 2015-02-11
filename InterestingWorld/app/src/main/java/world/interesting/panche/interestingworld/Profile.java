package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

public class Profile extends ActionBarActivity {

    /** Alpha Toolbar **/
    private world.interesting.panche.interestingworld.AlphaForeGroundColorSpan mAlphaForegroundColorSpan;
    private SpannableString mSpannableString;

    String[] datos= new String[5];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        datos=loadPreferences();
        String title = "Recipe";

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ColorDrawable cd = new ColorDrawable(getResources().getColor(R.color.colorPrimary));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        actionBar.setBackgroundDrawable(cd);

        cd.setAlpha(1);

        world.interesting.panche.interestingworld.ScrollViewHelper scrollViewHelper = (world.interesting.panche.interestingworld.ScrollViewHelper)findViewById(R.id.scrollViewHelper);
        scrollViewHelper.setOnScrollViewListener(new world.interesting.panche.interestingworld.ScrollViewHelper.OnScrollViewListener() {

            @Override
            public void onScrollChanged(world.interesting.panche.interestingworld.ScrollViewHelper v, int l, int t, int oldl, int oldt) {
                setTitleAlpha(255 - getAlphaforActionBar(v.getScrollY()));
                cd.setAlpha(getAlphaforActionBar(v.getScrollY()));
            }

            private int getAlphaforActionBar(int scrollY) {
                int minDist = 0, maxDist = 550;
                if(scrollY>maxDist){
                    return 255; }
                else {
                    if (scrollY < minDist) {
                        return 0;
                    } else {
                        return (int) ((255.0 / maxDist) * scrollY);
                    }
                }
            }
        });

        mSpannableString = new SpannableString(title);
        mAlphaForegroundColorSpan = new world.interesting.panche.interestingworld.AlphaForeGroundColorSpan(0xFFFFFF);
    }

    private void setTitleAlpha(float alpha) {
        if(alpha<1){ alpha = 1; }
        mAlphaForegroundColorSpan.setAlpha(alpha);
        mSpannableString.setSpan(mAlphaForegroundColorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(mSpannableString);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }
    public String[] loadPreferences() {
        String[] datos=new String[5];
        SharedPreferences prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        datos[0] = prefs.getString("id", "-1");
        datos[1] = prefs.getString("name", "");
        datos[2] = prefs.getString("lastname", "");
        datos[3] = prefs.getString("email", "");
        datos[4] = prefs.getString("photo_url", "");

        for(int i=0; i < datos.length; i++) {
            System.out.println(datos[i]);
        }
        SmartImageView myImage = (SmartImageView) this.findViewById(R.id.my_image);
        myImage.setImageUrl("http://"+datos[4]);
        TextView name = (TextView) findViewById(R.id.textViewName);
        TextView email = (TextView) findViewById(R.id.textViewEmail);
        name.setText(datos[1]+" "+datos[2]);
        email.setText(datos[3]);
        return datos;
    }
    public void setData ()
    {
        SmartImageView myImage = (SmartImageView) this.findViewById(R.id.my_image);
        myImage.setImageUrl("http://"+datos[4]);
        TextView name = (TextView) findViewById(R.id.textViewName);
        TextView email = (TextView) findViewById(R.id.textViewEmail);
        name.setText(datos[1]+" "+datos[2]);
        email.setText(datos[3]);
    }
}