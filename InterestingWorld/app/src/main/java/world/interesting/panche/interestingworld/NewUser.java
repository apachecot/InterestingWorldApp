package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import world.interesting.panche.interestingworld.BD;

/**
 * Created by neokree on 12/12/14.
 */
public class NewUser extends ActionBarActivity {

    EditText name,lastname,email,pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_user);
        name=(EditText)findViewById(R.id.editTextName);
        lastname=(EditText)findViewById(R.id.editTextLastname);
        email=(EditText)findViewById(R.id.editTextEmail);
        pass=(EditText)findViewById(R.id.editTextPassword);
        Button bAccept = (Button)findViewById(R.id.buttonAccept);
    }
    public void buttonAccept(View view)
    {
        BD bd = new BD();

        bd.sendUser(name.getText().toString(),lastname.getText().toString(),email.getText().toString(),pass.getText().toString());
        //bd.sendUser("fa325rdadf","adios","quetal","buenosdias");
        Toast.makeText(NewUser.this,bd.getResult(),Toast.LENGTH_SHORT).show();
    }
}