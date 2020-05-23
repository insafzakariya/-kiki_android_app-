package lk.mobilevisions.kiki.video.adapter;

import android.content.Context;
import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.modules.api.dto.Program;

public class SlidingImageAdapter extends PagerAdapter {


    private List<Program> imageModelArrayList;
    private LayoutInflater inflater;
    private Context context;


    public SlidingImageAdapter(Context context, List<Program> imageModelArrayList) {
        this.context = context;
        this.imageModelArrayList = imageModelArrayList;
        if(context!=null)
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return imageModelArrayList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.image);
        try {
            Picasso.with(context).load(URLDecoder.decode(imageModelArrayList.get(position).getImage(), "UTF-8")).fit().centerCrop()
                    .into(imageView);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}