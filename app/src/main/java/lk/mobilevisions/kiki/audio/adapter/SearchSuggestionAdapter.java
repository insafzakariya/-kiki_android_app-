package lk.mobilevisions.kiki.audio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;

public class SearchSuggestionAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<String> mArrayList = new ArrayList<>();
    OnSearchSugTextviewItemClickListener itemClickListener;

    public SearchSuggestionAdapter(Context mContext, List<String> mArrayList, OnSearchSugTextviewItemClickListener itemClickListener ) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.itemClickListener = itemClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_suggestion_view, parent, false);
        return new SearchSuggestionViewHolder(view);

    }


    public void setList(List<String> mArrayList) {
        this.mArrayList.addAll(mArrayList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initLayoutOne((SearchSuggestionViewHolder) holder, position);


    }

    private void initLayoutOne(final SearchSuggestionViewHolder holder, int pos) {
        final String current = mArrayList.get(pos);
        holder.searchSugTextview.setText(current);


        holder.rvHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onSearchSugTextviewItemClick(mArrayList.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition(),mArrayList);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class SearchSuggestionViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.search_sug_list_relative)
        RelativeLayout rvHorizontal;

        @BindView(R.id.search_sug_text)
        TextView searchSugTextview;



        public SearchSuggestionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnSearchSugTextviewItemClickListener {
        public void onSearchSugTextviewItemClick(String string, int position,List<String> songs);
    }

}