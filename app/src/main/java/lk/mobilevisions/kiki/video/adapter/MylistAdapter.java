package lk.mobilevisions.kiki.video.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.modules.api.dto.Program;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MylistAdapter extends RecyclerView.Adapter<MylistAdapter.ViewHolder> {

    List<Program> programs;
    private Context context;
    MylistAdapter.OnProgramItemClickListener itemClickListener;

    public  class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageView);

        }
    }

    public MylistAdapter(Context context, List<Program> programs, MylistAdapter.OnProgramItemClickListener itemClickListener) {
        this.context = context;
        this.programs = programs;
        this.itemClickListener = itemClickListener;
    }


    @Override
    public MylistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_mylist_item, parent, false);
        MylistAdapter.ViewHolder viewHolder = new MylistAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MylistAdapter.ViewHolder holder, final int position) {

        Program program = programs.get(position);

        DrawableCrossFadeFactory factory =
                new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
        try {
            Glide.with(context).load(URLDecoder.decode(program.getImage(), "UTF-8"))
                    .centerCrop()
                    .placeholder(R.drawable.program)
                    .thumbnail(Glide.with(context).load(R.raw.kiki_loading_giflow))
                    .transition(withCrossFade(factory))
                    .into(holder.imageView);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.getAdapterPosition()>=0) {
                    itemClickListener.onProgramItemClick(view, programs.get(position),
                            position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return programs.size();
    }


    public interface OnProgramItemClickListener {
        public void onProgramItemClick(View view, Program program, int position);
    }


}