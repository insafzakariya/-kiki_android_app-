/**
 * Created by Chatura Dilan Perera on 1/9/2016.
 */
package lk.mobilevisions.kiki.ui.main;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import lk.mobilevisions.kiki.ui.main.home.HomeFragment;
import lk.mobilevisions.kiki.ui.main.videos.HotVideosFragment;
import lk.mobilevisions.kiki.ui.main.videos.LiveVideosFragment;
import lk.mobilevisions.kiki.ui.main.videos.SubscribedVideosFragment;
import lk.mobilevisions.kiki.ui.main.videos.VideoTabs;

public class TabViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence titles[];
    int[] icons;


    public TabViewPagerAdapter(FragmentManager fm, CharSequence titles[], int[] icons) {
        super(fm);
        this.titles = titles;
        this.icons = icons;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        if (position == VideoTabs.HOME_VIDEOS.getValue()) {
            fragment = new HomeFragment();
        } else if (position == VideoTabs.HOT_VIDEOS.getValue()) {
            fragment = new HotVideosFragment();
        }else if (position == VideoTabs.LIVE_VIDEOS.getValue()) {
                fragment = new LiveVideosFragment();
        } else if (position == VideoTabs.SUBSCRIBED_VIDEOS.getValue()) {
            fragment = new SubscribedVideosFragment();
        }

        return fragment;

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    public int getPageImage(int position) {
        return icons[position];
    }
}