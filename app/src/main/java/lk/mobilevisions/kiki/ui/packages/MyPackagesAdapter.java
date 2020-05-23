/**
 * Created by Chatura Dilan Perera on 9/10/2016.
 */
package lk.mobilevisions.kiki.ui.packages;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.modules.api.dto.Package;

public class MyPackagesAdapter extends RecyclerView.Adapter<MyPackagesAdapter.ViewHolder>  {

    List<Package> packages;
    private Context context;
    MyPackagesAdapter.OnPackagesItemClickListener itemClickListener;
    boolean isOpenPackage;


    public  class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewPackageName;
        public ImageView imageViewPackage;
        public View rootView;
        public ViewHolder(View view) {
            super(view);
            rootView = view;
            textViewPackageName = (TextView) view.findViewById(R.id.textViewPackageName);
            imageViewPackage = (ImageView) view.findViewById(R.id.imageViewPackageImage);
        }
    }

    public MyPackagesAdapter(Context context, List<Package> packages, boolean isOpenPackage, MyPackagesAdapter.OnPackagesItemClickListener itemClickListener) {
        this.context = context;
        this.packages = packages;
        this.itemClickListener = itemClickListener;
        this.isOpenPackage = isOpenPackage;
    }

    @Override
    public MyPackagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_listitem_packages, parent, false);
        MyPackagesAdapter.ViewHolder viewHolder = new MyPackagesAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyPackagesAdapter.ViewHolder holder, int position) {

        holder.textViewPackageName.setText(packages.get(position).getName());

        if(packages.get(position).isCurrent()) {
            holder.textViewPackageName.setText(holder.textViewPackageName.getText() + " (Current Package)");
        }

        if(isOpenPackage){
            holder.imageViewPackage.setImageResource(R.drawable.ic_package_opened);
        }else{
            if(!packages.get(position).isCanSubscribe()){
                holder.rootView.setAlpha(0.2f);
            }
            holder.imageViewPackage.setImageResource(R.drawable.ic_package_closed);
        }

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onPackagesItemClick(view, packages.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return packages.size();
    }

    public interface OnPackagesItemClickListener {
        public void onPackagesItemClick(View view, Package thePackage, int position);
    }
}
