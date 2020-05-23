package lk.mobilevisions.kiki.audio.fragment;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.model.dto.Genre;
import lk.mobilevisions.kiki.databinding.FragmentAllSongsBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

public class AllSongsFragment extends Fragment {

    @Inject
    TvManager tvManager;
    FragmentAllSongsBinding binding;
    private List<Genre> audioGenre = new ArrayList<>();


    public AllSongsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_songs, container, false);
//        ((Application) getActivity().getApplication()).getInjector().inject(this);

//        getGenre();

        binding.tableLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                System.out.println("efhkefbjkf 333 " + tab.getPosition());
                AudioSongsFragment.newInstance(audioGenre.get(tab.getPosition()).getName());
                //do stuff here
//                int position = tab.getPosition();
//                changeTab(position);
//                System.out.println("cjcjcjcjc " + position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return binding.getRoot();
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getChildFragmentManager(),
                getActivity()));


    }

    private void changeTabsFont() {
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/FuturaNext-Medium.otf");
        ViewGroup vg = (ViewGroup) binding.tableLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(face);
                }
            }
        }
    }

//    private void getGenre() {
//        tvManager.getAllAudioGenre(new APIListener<List<Genre>>() {
//            @Override
//            public void onSuccess(List<Genre> genres, List<Object> params) {
//                System.out.println("checking genres 6666  " + genres.size());
//                //for (int i = 1; i >= 0; i--) {
//                //    genres.remove(i);
//                // }
//                audioGenre = genres;
//
//                Genre genre = new Genre();
//                genre.setId(1);
//                genre.setName("All Songs");
//                genre.setDescription("All Songs");
//                audioGenre.add(0, genre);
//                setupViewPager(binding.viewPager);
//                binding.tableLayout.setupWithViewPager(binding.viewPager);
//                changeTabsFont();
//                new Handler().postDelayed(
//                        new Runnable() {
//                            @Override
//                            public void run() {
//                                binding.tableLayout.getTabAt(0).select();
//                            }
//                        }, 100);
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                System.out.println("checking genres 5555  " + t.getLocalizedMessage());
//                Utils.Error.onServiceCallFail(getContext(), t);
//            }
//        });
//    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = audioGenre.size();

        private Context context;

        public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            System.out.println("rfhrkfrffghgf " + position);
            return AudioSongsFragment.newInstance(audioGenre.get(position).getName());
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return audioGenre.get(position).getName();
        }
    }


}
