package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 18/01/2015.
 */

import com.loopj.android.http.*;

import org.apache.http.Header;


public class BD {

String result="hola";
    public void sendUser(String name, String lastname, String email, String password)
    {
        AsyncHttpClient client = new AsyncHttpClient();

        String url="http://trivialsimpsons.webcindario.com/insertar_user.php?";
        String parametros = "login=" + name + "&avatar=" + lastname + "&email=" + email + "&pass=" + password;

        client.post(url+parametros,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200)
                {
                    setResult(new String(responseBody));

                }
                System.out.println(new String(responseBody));

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                setResult(new String(responseBody));
                System.out.println(new String(responseBody));
            }
        });
    }
    public String getResult ()
    {
        return this.result;
    }
    public String setResult (String result)
    {
      return  this.result=result;
    }

}
