package uk.co.frips.sample.barapp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import uk.co.frips.sample.barapp.list.BarListFragment;
import uk.co.frips.sample.barapp.maps.MapsFragment;

public class BarActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MapsFragment mapsFragment =
                (MapsFragment) getSupportFragmentManager().findFragmentByTag(MapsFragment.FRAGMENT_TAG);

        if (mapsFragment == null) {
            mapsFragment = MapsFragment.newInstance();
        }

        BarListFragment barListFragment =
                (BarListFragment) getSupportFragmentManager().findFragmentByTag(BarListFragment.FRAGMENT_TAG);

        if (barListFragment == null) {
            barListFragment = BarListFragment.newInstance();
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), barListFragment, mapsFragment);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private BarListFragment mBarListFragment;
        private MapsFragment mMapsFragment;

        public SectionsPagerAdapter(FragmentManager fm, BarListFragment barListFragment, MapsFragment mapsFragment) {
            super(fm);
            mBarListFragment = barListFragment;
            mMapsFragment = mapsFragment;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mBarListFragment;
                case 1:
                default:
                    return mMapsFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(mBarListFragment.getTitle());
                case 1:
                default:
                    return getString(mMapsFragment.getTitle());
            }
        }
    }
}
