package lk.mobilevisions.kiki.ui.widgets.countryselect;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import lk.mobilevisions.kiki.R;

public class CountrySpinnerAdapter extends ArrayAdapter<Country> {
    private LayoutInflater mLayoutInflater;

    /**
     * Constructor
     *
     * @param context Context
     */
    public CountrySpinnerAdapter(Context context) {
        super(context, 0);
        mLayoutInflater = LayoutInflater.from(context);
    }

    /**
     * Drop down item view
     *
     * @param position    position of item
     * @param convertView View of item
     * @param parent      parent view of item's view
     * @return covertView
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_country, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.intl_phone_edit__country__item_image);
            viewHolder.mNameView = (TextView) convertView.findViewById(R.id.intl_phone_edit__country__item_name);
            viewHolder.mDialCode = (TextView) convertView.findViewById(R.id.intl_phone_edit__country__item_dialcode);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Country country = getItem(position);
        viewHolder.mImageView.setImageDrawable(TextDrawable.builder().beginConfig().textColor(Color.BLACK).fontSize(50).endConfig()
                .buildRect(String.format("+%s", country.getDialCode()), Color.TRANSPARENT));
        viewHolder.mNameView.setText(country.getName());
        viewHolder.mDialCode.setText("");
        return convertView;
    }

    /**
     * Drop down selected view
     *
     * @param position    position of selected item
     * @param convertView View of selected item
     * @param parent      parent of selected view
     * @return convertView
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Country country = getItem(position);

        if (convertView == null) {
            convertView = new ImageView(getContext());
        }

        ((ImageView) convertView).setMinimumHeight(80);
        ((ImageView) convertView).setImageDrawable(TextDrawable.builder().beginConfig().textColor(Color.WHITE).fontSize(50).endConfig()
                .buildRect(String.format("+%s", country.getDialCode()), Color.TRANSPARENT));

        return convertView;
    }

    /**
     * Fetch flag resource by Country
     *
     * @param country Country
     * @return int of resource | 0 value if not exists
     */
    private int getFlagResource(Country country) {
        return getContext().getResources().getIdentifier("country_" + country.getIso().toLowerCase(), "drawable", getContext().getPackageName());
    }


    /**
     * View holder for caching
     */
    private static class ViewHolder {
        public ImageView mImageView;
        public TextView mNameView;
        public TextView mDialCode;
    }
}
