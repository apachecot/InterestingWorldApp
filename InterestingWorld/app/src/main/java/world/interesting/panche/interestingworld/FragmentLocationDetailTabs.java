package world.interesting.panche.interestingworld;

/**
 * Created by Panche on 31/03/2015.
 */
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

/**
 * Created by neokree on 30/12/14.
 */
public class FragmentLocationDetailTabs extends Fragment implements MaterialTabListener {
    MaterialTabHost tabHost;
    ViewPager pager;
    ViewPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_layout,container,false);

        ((MaterialNavigationDrawer)getActivity()).changeToolbarColor(getResources().getColor(R.color.fbutton_default_color),getResources().getColor(R.color.fbutton_default_shadow_color));

        tabHost = (MaterialTabHost) view.findViewById(R.id.tabHost);
        tabHost.setPrimaryColor(getResources().getColor(R.color.fbutton_default_color));
        tabHost.setAccentColor(getResources().getColor(R.color.white));
        pager = (ViewPager) view.findViewById(R.id.pager );
        pager.setOffscreenPageLimit(2);

        //Cambiamos el icono del menu por el de volver atras
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if(this.getActivity().getLocalClassName().equals("MainActivity"))
        {
            actionBar.setHomeAsUpIndicator(((MainActivity) this.getActivity()).getV7DrawerToggleDelegate().getThemeUpIndicator());
        }else{
            actionBar.setHomeAsUpIndicator(((MainActivityUser) this.getActivity()).getV7DrawerToggleDelegate().getThemeUpIndicator());
        }

        // init view pager
        adapter = new ViewPagerAdapter(this.getFragmentManager());
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
                tabHost.setSelectedNavigationItem(position);

            }
        });

        // insert all tabs from pagerAdapter data
        for (int i = 0; i < adapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setText(adapter.getPageTitle(i))
                            .setTabListener(this)
            );

        }

        tabHost.setSelectedNavigationItem(0);

        return view;
    }

    @Override
    public void onTabSelected(MaterialTab materialTab) {
        pager.setCurrentItem(materialTab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab materialTab) {

    }

    @Override
    public void onTabUnselected(MaterialTab materialTab) {

    }


    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        public Fragment getItem(int num) {
            Fragment frag;
            switch(num)
            {
                case 0:
                    frag=getFragment(0);
                break;
                case 1:
                    frag=getFragment(1);
                break;
                case 2:
                    frag=getFragment(2);
                break;
                default:
                    frag=getFragment(3);
                break;
            }
            return frag;

        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
           CharSequence option;
            switch(position)
            {
                case 0:
                    option="Detalles";
                    break;
                case 1:
                    option= "Imagenes";
                    break;
                case 2:
                    option="Comentarios";
                    break;
                default:
                    option="Detalles";
                    break;
            }
            return option;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub

            FragmentManager manager = ((Fragment) object).getFragmentManager();
            FragmentTransaction trans = manager.beginTransaction();
            trans.remove((Fragment) object);
            trans.commit();

            super.destroyItem(container, position, object);
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        System.out.println("RESUME TAB");


    }

    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onDestroyView() {
        System.out.println("OnDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        System.out.println("OnDestroy");
        if(this.getActivity().getLocalClassName().equals("MainActivity")) {
            ((MainActivity) this.getActivity()).setFragcomments();
            ((MainActivity) this.getActivity()).setFragdetails();
            ((MainActivity) this.getActivity()).setFragphotosdetail();
        }else
        {
            ((MainActivityUser) this.getActivity()).setFragcomments();
            ((MainActivityUser) this.getActivity()).setFragdetails();
            ((MainActivityUser) this.getActivity()).setFragphotosdetail();
        }
        super.onDestroy();
    }
    public Fragment getFragment(int num)
    {
        Fragment frag;
        switch(num)
        {
            case 0:
                if(this.getActivity().getLocalClassName().equals("MainActivity")) {
                    frag = ((MainActivity) getActivity()).getFragdetails();
                }else{
                    frag = ((MainActivityUser) getActivity()).getFragdetails();
                }
                break;
            case 1:
                if(this.getActivity().getLocalClassName().equals("MainActivity")) {
                    frag = ((MainActivity) getActivity()).getFragphotosdetail();
                }else{
                    frag = ((MainActivityUser) getActivity()).getFragphotosdetail();
                }
                break;
            case 2:
                if(this.getActivity().getLocalClassName().equals("MainActivity")) {
                    frag = ((MainActivity) getActivity()).getFragcomments();
                }else{
                    frag = ((MainActivityUser) getActivity()).getFragcomments();
                }
                break;
            default:
                if(this.getActivity().getLocalClassName().equals("MainActivity")) {
                    frag = ((MainActivity) getActivity()).getFragdetails();
                }else{
                    frag = ((MainActivityUser) getActivity()).getFragdetails();
                }
                break;
        }
        return frag;
    }
}