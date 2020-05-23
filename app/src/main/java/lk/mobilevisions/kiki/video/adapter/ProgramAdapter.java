package lk.mobilevisions.kiki.video.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
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

public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.ViewHolder> {

    private List<Program> programs;
    private Context context;
    OnProgramItemClickListener itemClickListener;
    public ProgramAdapter(Context context, List<Program> programs,OnProgramItemClickListener itemClickListener) {
        this.context = context;
        this.programs = programs;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_program_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Program program = programs.get(position);

//        AnimationDrawable animationDrawable;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//            animationDrawable=(AnimationDrawable)context.getDrawable(R.drawable.anim_image_placeholder);
//        else
//            animationDrawable=(AnimationDrawable)context.getResources().getDrawable(R.drawable.anim_image_placeholder);
//        animationDrawable.start();

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
                itemClickListener.onProgramItemClick(view, programs.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return programs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;


        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);


        }

    }
    public interface OnProgramItemClickListener {
        public void onProgramItemClick(View view, Program program, int position);
    }
}

