package world.interesting.panche.interestingworld;

import android.content.Intent;
import android.os.Bundle;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.MaterialSection;



public class MainActivity extends MaterialNavigationDrawer{


    MaterialSection login, explore, photos, lastLocations;

    @Override
    public void init(Bundle savedInstanceState) {

        // night section with section color
        lastLocations = this.newSection(this.getResources().getString(R.string.explore), new FragmentIndex());

        explore = this.newSection(this.getResources().getString(R.string.explore), this.getResources().getDrawable(R.drawable.location), new FragmentIndex());
                // recorder section with icon and 10 notifications
                photos = this.newSection(this.getResources().getString(R.string.photos), this.getResources().getDrawable(R.drawable.photo), new FragmentIndex()).setNotifications(10);

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


    }

    @Override
    protected MaterialSection backToSection(MaterialSection currentSection) {

        return currentSection;
    }

}

