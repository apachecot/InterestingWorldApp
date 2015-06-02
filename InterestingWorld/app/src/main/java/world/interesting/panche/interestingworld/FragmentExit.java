package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.dd.CircularProgressButton;
import com.devspark.appmsg.AppMsg;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;


/**
 * Created by neokree on 12/12/14.
 */
public class FragmentExit extends Fragment {

    View inflatedView;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.exit, container, false);

        return inflatedView;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void onStart(){
        SweetAlertExit();
        super.onStart();
    }

    public void SweetAlertExit()
    {
        new SweetAlertDialog(this.getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("¡Estas a punto de salir!")
                .setContentText("¿Desea cerrar la aplicación?")
                .setCancelText("No")
                .setConfirmText("Sí")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                        ((MaterialNavigationDrawer) getActivity()).onBackPressed();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                        exit();
                    }
                })
                .show();
    }

    public void exit(){
        this.getActivity().finish();
    }
 }