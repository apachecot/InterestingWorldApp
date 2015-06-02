package world.interesting.panche.interestingworld;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.concurrent.Executors;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;



public class MainActivity extends MaterialNavigationDrawer{


    MaterialSection login, explore, photos, lastLocations,exitSection;
    Location locationSelected;
    String url_full="";
    private Picasso mPicasso;
    Fragment fraglastlocations, fragexplore, fragphotos,fragdetailstab,fragdetails,fragphotosdetail,fragcomments,fragexit;
    Boolean doubleBackToExitPressedOnce=false;
    String advanced="";
    int category=0;



    @Override
    public void init(Bundle savedInstanceState) {

        mPicasso = new Picasso.Builder(this).executor(Executors.newSingleThreadExecutor()).build();
        //mPicasso.setIndicatorsEnabled(true);

        fragdetailstab=new FragmentLocationDetailTabs();
        fragdetails=new FragmentLocationDetail();
        fragphotosdetail=new FragmentPhotosDetail();
        fragcomments=new FragmentComments();
        fragexit=new FragmentExit();

        fraglastlocations=new FragmentIndex();


        //Últimos puntos de interés
        lastLocations = this.newSection(this.getResources().getString(R.string.lastlocations),this.getResources().getDrawable(R.drawable.map),fraglastlocations).setSectionColor(Color.parseColor("#03a9f4"));

        fragexplore=new FragmentMap();
        //Explorar el mapa cercano
        explore = this.newSection(this.getResources().getString(R.string.explore), this.getResources().getDrawable(R.drawable.location), fragexplore).setSectionColor(Color.parseColor("#03a9f4"));

        fragphotos=new FragmentPhotos();
        // Últimas fotografías
        photos = this.newSection(this.getResources().getString(R.string.photos), this.getResources().getDrawable(R.drawable.photo), fragphotos).setSectionColor(Color.parseColor("#03a9f4"));

        exitSection = this.newSection(this.getResources().getString(R.string.exit),this.getResources().getDrawable(R.drawable.map),fragexit ).setSectionColor(Color.parseColor("#03a9f4"));

        // add your sections to the drawer
        this.addSection(lastLocations);
        this.addSection(explore);
        this.addSection(photos);
        //this.addSection(addlocation);
        //this.addSubheader("Opciones");
        //this.addDivisor();

        this.setBackPattern(MaterialNavigationDrawer.BACKPATTERN_CUSTOM);
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
    @Override
    protected MaterialSection backToSection(MaterialSection currentSection) {

        if(currentSection == lastLocations) {
            return exitSection;
        }
        else{
            return lastLocations;
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

    public Fragment getFragphotosdetail() {
        return fragphotosdetail;
    }

    public Fragment getFragdetails() {
        return fragdetails;
    }

    public Fragment getFragdetailstab() {
        return fragdetailstab;
    }

    public Fragment getFragcomments() {
        return fragcomments;
    }

    public void setFragcomments() {
        FragmentManager fm =this.getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragcomments).commit();
        this.fragcomments =  new FragmentComments();
    }

    public void setFragphotosdetail() {
        FragmentManager fm =this.getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragphotosdetail).commit();
        this.fragphotosdetail =  new FragmentPhotosDetail();
    }

    public void setFragdetails() {
        FragmentManager fm =this.getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragdetails).commit();
        this.fragdetails = new FragmentLocationDetail();
    }

    public void setFragdetailstab() {
        FragmentManager fm =this.getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragdetailstab).commit();
        this.fragdetailstab = new FragmentLocationDetailTabs();
    }
    public void setAdvancedSearch(String advn)
    {
        this.advanced=advn;
    }
    public String getAdvancedSearch()
    {
        System.out.println(this.advanced);
        return this.advanced;
    }
    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public MaterialSection getLogin() {
        return login;
    }

    public MaterialSection getExplore() {
        return explore;
    }

    public MaterialSection getPhotos() {
        return photos;
    }

    public MaterialSection getLastLocations() {
        return lastLocations;
    }

    public Location getLocationSelected() {
        return locationSelected;
    }
}

