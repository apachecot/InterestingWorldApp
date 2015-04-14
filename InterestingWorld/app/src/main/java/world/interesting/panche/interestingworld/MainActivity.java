package world.interesting.panche.interestingworld;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.squareup.picasso.Picasso;

import java.util.concurrent.Executors;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;



public class MainActivity extends MaterialNavigationDrawer{


    MaterialSection login, explore, photos, lastLocations;
    Location locationSelected;
    String url_full="";
    private Picasso mPicasso;



    @Override
    public void init(Bundle savedInstanceState) {

        mPicasso = new Picasso.Builder(this).executor(Executors.newSingleThreadExecutor()).build();
        //mPicasso.setIndicatorsEnabled(true);

        // night section with section color
        lastLocations = this.newSection(this.getResources().getString(R.string.lastlocations),this.getResources().getDrawable(R.drawable.map), new FragmentIndex()).setSectionColor(Color.parseColor("#03a9f4"));

        explore = this.newSection(this.getResources().getString(R.string.explore), this.getResources().getDrawable(R.drawable.location), new FragmentMap()).setSectionColor(Color.parseColor("#03a9f4"));
                // recorder section with icon and 10 notifications
        photos = this.newSection(this.getResources().getString(R.string.photos), this.getResources().getDrawable(R.drawable.photo), new FragmentPhotos()).setSectionColor(Color.parseColor("#03a9f4"));


        // add your sections to the drawer
        this.addSection(lastLocations);
        this.addSection(explore);
        this.addSection(photos);
        //this.addSection(addlocation);
        //this.addSubheader("Opciones");
        //this.addDivisor();


        //this.closeOptionsMenu();
        this.addMultiPaneSupport();
        this.allowArrowAnimation();
        this.disableLearningPattern();

        //this.getActionBar().setHomeButtonEnabled(true);
       // this.getToolbar().setLogo(R.drawable.location);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.login:
                intentLogin();
                // search action
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Launching new activity
     * */
    public void intentLogin() {
        if(!getCurrentSection().equals(new NewUser()) && !getCurrentSection().equals(new Login())) {
            Fragment fragment = new Login();
            ((MaterialNavigationDrawer) this).setFragmentChild(fragment, "Login");
        }
    }
    public void SetLocationSelected(Location loc)
    {
        locationSelected=loc;
    }
    public Location GetLocationSelected()
    {
        return locationSelected;
    }
    public void SetImageUrlFull(String url)
    {
        url_full=url;
    }
    public String GetImageUrlFull()
    {
        return url_full;
    }

    public Picasso getmPicasso() {
        return mPicasso;
    }
}

