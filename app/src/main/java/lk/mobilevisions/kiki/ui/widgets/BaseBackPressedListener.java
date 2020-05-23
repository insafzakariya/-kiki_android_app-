package lk.mobilevisions.kiki.ui.widgets;

import androidx.fragment.app.FragmentActivity;

public class  BaseBackPressedListener implements OnBackPressedListener {
    private final FragmentActivity activity;

    public BaseBackPressedListener(FragmentActivity activity) {
        this.activity = activity;
    }

    @Override
    public void doBack() {

    }
}