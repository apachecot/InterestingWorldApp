package world.interesting.panche.interestingworld;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by Alex on 19/01/2015.
 */
public class Preferences extends Activity {


    //guardar configuración aplicación Android usando SharedPreferences
    public void savePreferences(String[] datos) {
        SharedPreferences prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("id", datos[0]);
        editor.putString("name", datos[1]);
        editor.putString("lastname", datos[2]);
        editor.putString("email", datos[3]);
        editor.putString("photo_url", datos[4]);
        editor.commit();
        System.out.println("Guardadas preferencias");
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
        return datos;
    }
}