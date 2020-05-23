package lk.mobilevisions.kiki.ui.main.videos;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import lk.mobilevisions.kiki.ui.main.home.EpisodesAdapter;

/**
 * Created by dilan on 10/28/16.
 */

public abstract class ViewVideosFragment extends Fragment implements EpisodesAdapter.OnEpisodesItemClickListener  {

    RecyclerView.Adapter episodesAdapter;
    RecyclerView.LayoutManager episodesLayoutManager;

}
