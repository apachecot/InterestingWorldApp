package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 29/01/2015.
 */
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;

public class Settings extends ActionBarActivity {

    /** Alpha Toolbar **/
    private world.interesting.panche.interestingworld.AlphaForeGroundColorSpan mAlphaForegroundColorSpan;
    private SpannableString mSpannableString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_layout);

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


}