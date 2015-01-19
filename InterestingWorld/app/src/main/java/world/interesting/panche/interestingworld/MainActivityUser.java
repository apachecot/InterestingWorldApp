package world.interesting.panche.interestingworld;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import it.neokree.materialnavigationdrawer.MaterialAccount;
import it.neokree.materialnavigationdrawer.MaterialAccountListener;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.MaterialSection;


public class MainActivityUser extends MaterialNavigationDrawer implements MaterialAccountListener {

    MaterialAccount account;
    MaterialSection lastLocations, explore, photos, addlocation, last, settingsSection;
    String[] datos= new String[5];

    @Override
    public void init(Bundle savedInstanceState) {

        datos=loadPreferences();

        // add first account
        account = new MaterialAccount(datos[1]+" "+datos[2],datos[3],this.getResources().getDrawable(R.drawable.defaultuser),this.getResources().getDrawable(R.drawable.back1));

        this.addAccount(account);

        // set listener
        this.setAccountListener(this);

        // night section with section color
        lastLocations = this.newSection(this.getResources().getString(R.string.explore), new FragmentIndex());

        explore = this.newSection(this.getResources().getString(R.string.explore), this.getResources().getDrawable(R.drawable.location), new FragmentIndex());

                //  profile = this.newSection(this.getResources().getString(R.string.profile),this.getResources().getDrawable(R.drawable.user),new FragmentIndex());
/*
        explore = this.newSection(this.getResources().getString(R.string.explore),this.getResources().getDrawable(R.drawable.location),new MaterialSectionListener() {
            @Override
            public void onClick(MaterialSection section) {
                Toast.makeText(MainActivity.this, "Section 2 Clicked", Toast.LENGTH_SHORT).show();

                // deselect section when is clicked
                section.unSelect();
            }
        });
        */
                // recorder section with icon and 10 notifications
                photos = this.newSection(this.getResources().getString(R.string.photos), this.getResources().getDrawable(R.drawable.photo), new FragmentIndex()).setNotifications(10);
        // night section with icon, section color and notifications
        addlocation = this.newSection(this.getResources().getString(R.string.addlocation), this.getResources().getDrawable(R.drawable.addlocation), new FragmentAdd())
                .setSectionColor(Color.parseColor("#2196f3"),Color.parseColor("#1565c0")).setNotifications(150);


        Intent i = new Intent(this,Profile.class);
        settingsSection = this.newSection(this.getResources().getString(R.string.settings),this.getResources().getDrawable(R.drawable.settings),i);

        // add your sections to the drawer
        this.addSection(lastLocations);
        this.addSection(explore);
        this.addSection(photos);
        this.addSection(addlocation);
        //this.addSubheader("Opciones");
        //this.addDivisor();
        this.addBottomSection(settingsSection);

        this.setBackPattern(MaterialNavigationDrawer.BACKPATTERN_BACK_TO_FIRST);
    }


    @Override
    public void onAccountOpening(MaterialAccount account) {
        // open profile activity
        Intent i = new Intent(this,Profile.class);
        startActivity(i);
    }

    @Override
    public void onChangeAccount(MaterialAccount newAccount) {
        // when another account is selected
    }

    //cargar configuración aplicación Android usando SharedPreferences
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
        return datos;
    }

}

