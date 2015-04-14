package world.interesting.panche.interestingworld;

/**
 * Created by Alex on 15/01/2015.
 */

import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;


import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by neokree on 12/12/14.
 */
public class FragmentImageViewer extends DialogFragment {


    View inflatedView;
    ImageView mImageView;
    PhotoViewAttacher mAttacher;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.image_viewer, container, false);

        mImageView = (ImageView) inflatedView.findViewById(R.id.imageViewFull);

        Callback imageLoadedCallback = new Callback() {

            @Override
            public void onSuccess() {
                if(mAttacher!=null){
                    mAttacher.update();
                }else{
                    mAttacher = new PhotoViewAttacher(mImageView);
                }
            }

            @Override
            public void onError() {
                // TODO Auto-generated method stub

            }
        };
        String url="";
        Class cl=this.getActivity().getClass();
        if(cl.getName().equals("world.interesting.panche.interestingworld.MainActivity")) {
            url=((MainActivity) getActivity()).GetImageUrlFull();
            ((MainActivity) getActivity()).getmPicasso()
                    .load("http://"+url).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .error(R.drawable.not_found)
                    .into(mImageView,imageLoadedCallback);
        }else {
            url=((MainActivityUser) getActivity()).GetImageUrlFull();
            ((MainActivityUser) getActivity()).getmPicasso()
                    .load("http://"+url).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .error(R.drawable.not_found)
                    .into(mImageView,imageLoadedCallback);
        }

        return inflatedView;
    }
    @Override
    public void onResume() {
        super.onResume();
        System.out.println("onresume");
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onPause();
        System.out.println("OnDestroy");
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

}