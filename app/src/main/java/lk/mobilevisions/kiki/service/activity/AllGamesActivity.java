package lk.mobilevisions.kiki.service.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.chat.module.dto.ChatMember;
import lk.mobilevisions.kiki.databinding.ActivityAllGamesBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;
import lk.mobilevisions.kiki.service.adapter.AllGAmesAdapter;
import lk.mobilevisions.kiki.service.dto.ServiceModel;

import static com.androidquery.util.AQUtility.getContext;

public class AllGamesActivity extends AppCompatActivity implements View.OnClickListener, AllGAmesAdapter.OnAllGamesItemClickListener {

    ActivityAllGamesBinding binding;

    @Inject
    TvManager tvManager;
    private Context context;
    List<ServiceModel> serviceList = new ArrayList<>();
    GridLayoutManager channelsLayoutManager;
    AllGAmesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_games);
        ((Application) getApplication()).getInjector().inject(this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.newUiBackground));
        }

//        getServices();

        channelsLayoutManager = new GridLayoutManager(context,4);
        binding.gamesRecyclerview.setLayoutManager(channelsLayoutManager);
        mAdapter = new AllGAmesAdapter(this, serviceList, AllGamesActivity.this);
        binding.gamesRecyclerview.setHasFixedSize(true);
        binding.gamesRecyclerview.setItemViewCacheSize(50);
        binding.gamesRecyclerview.setDrawingCacheEnabled(true);
        binding.gamesRecyclerview.setAdapter(mAdapter);

        serviceList = (ArrayList<ServiceModel>) getIntent().getSerializableExtra("gameList");
        binding.gamesRecyclerview.setAdapter(new AllGAmesAdapter(getContext(),
                serviceList, AllGamesActivity.this));
    }

    @Override
    public void onClick(View view) {
    }

    private void getServices() {

        tvManager.getAllServices(new APIListener<List<ServiceModel>>() {
            @Override
            public void onSuccess(List<ServiceModel> result, List<Object> params) {

                serviceList = result;
                binding.gamesRecyclerview.setAdapter(new AllGAmesAdapter(getContext(),
                        serviceList, AllGamesActivity.this));

            }

            @Override
            public void onFailure(Throwable t) {

                Utils.Error.onServiceCallFail(getContext(), t);
            }
        });


    }

    @Override
    public void onAllGamesItemClick(ServiceModel serviceModel, int position, List<ServiceModel> songs) {

    }
}