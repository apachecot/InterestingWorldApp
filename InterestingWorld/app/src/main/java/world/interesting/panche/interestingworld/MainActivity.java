package world.interesting.panche.interestingworld;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;



public class MainActivity extends MaterialNavigationDrawer{


    MaterialSection login, explore, photos, lastLocations;

    @Override
    public void init(Bundle savedInstanceState) {


        // night section with section color
        lastLocations = this.newSection(this.getResources().getString(R.string.lastlocations),this.getResources().getDrawable(R.drawable.map), new FragmentIndex()).setSectionColor(Color.parseColor("#CC0000"));

        explore = this.newSection(this.getResources().getString(R.string.explore), this.getResources().getDrawable(R.drawable.location), new FragmentMap()).setSectionColor(Color.parseColor("#03a9f4"));
                // recorder section with icon and 10 notifications
        photos = this.newSection(this.getResources().getString(R.string.photos), this.getResources().getDrawable(R.drawable.photo), new FragmentLocationDetail()).setSectionColor(Color.parseColor("#03a9f4"));

        Intent i = new Intent(this,Login.class);
        login = this.newSection(this.getResources().getString(R.string.login),this.getResources().getDrawable(R.drawable.user),i);

        // add your sections to the drawer
        this.addSection(lastLocations);
        this.addSection(explore);
        this.addSection(photos);
        //this.addSection(addlocation);
        //this.addSubheader("Opciones");
        //this.addDivisor();
        this.addBottomSection(login);

        this.closeOptionsMenu();
        //this.addMultiPaneSupport();
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
    private void intentLogin() {
        Intent i = new Intent(this, Login.class);
        startActivity(i);
    }


}

