/**
 * Created by Chatura Dilan Perera on 3/9/2016.
 */
package lk.mobilevisions.kiki.ui.main;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.modules.widgets.slidingtabs.SlidingTabLayout;

public class IconicSlidingTabLayout extends SlidingTabLayout {

    protected int mTabViewImageViewId;

    public IconicSlidingTabLayout(Context context) {
        super(context, null);
    }

    public IconicSlidingTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public IconicSlidingTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setCustomTabView(int layoutResId, int textViewId, int imageViewId) {
        mTabViewLayoutId = layoutResId;
        mTabViewTextViewId = textViewId;
        mTabViewImageViewId = imageViewId;
    }

    protected void populateTabStrip() {
        final TabViewPagerAdapter adapter = (TabViewPagerAdapter) mViewPager.getAdapter();
        final OnClickListener tabClickListener = new TabClickListener();

        for (int i = 0; i < adapter.getCount(); i++) {
            View tabView = null;
            TextView tabTitleView = null;
            ImageView tabImageView = null;

            if (mTabViewLayoutId != 0) {
                // If there is a custom tab view layout id set, try and inflate it
                tabView = LayoutInflater.from(getContext()).inflate(mTabViewLayoutId, mTabStrip,
                        false);
                tabTitleView = (TextView) tabView.findViewById(mTabViewTextViewId);
                tabImageView = (ImageView) tabView.findViewById(mTabViewImageViewId);
            }

            if (tabView == null) {
                tabView = createDefaultTabView(getContext());
            }

            if (tabTitleView == null && TextView.class.isInstance(tabView)) {
                tabTitleView = (TextView) tabView;
            }

            if (tabImageView == null && TextView.class.isInstance(tabView)) {
                tabImageView = (ImageView) tabView;
            }

            if (mDistributeEvenly) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                lp.width = 0;
                lp.weight = 1;
            }

            tabTitleView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
            tabTitleView.setText(adapter.getPageTitle(i));
            tabImageView.setImageResource(adapter.getPageImage(i));
            tabView.setOnClickListener(tabClickListener);
            String desc = mContentDescriptions.get(i, null);
            if (desc != null) {
                tabView.setContentDescription(desc);
            }

            mTabStrip.addView(tabView);
            if (i == mViewPager.getCurrentItem()) {
                tabView.setSelected(true);
            }
        }
    }

}
