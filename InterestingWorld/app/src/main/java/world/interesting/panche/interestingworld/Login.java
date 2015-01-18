package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

/**
 * Created by neokree on 12/12/14.
 */
public class Login extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


    }
    public void newUser (View view)
    {
        Intent intent = new Intent(this, NewUser.class);
        startActivity(intent);
    }
    public void loginAccept(View view)
    {
        Toast.makeText(Login.this,"hola", Toast.LENGTH_SHORT).show();
    }
}