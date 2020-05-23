package lk.mobilevisions.kiki.ui.base;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import java.util.Locale;

import lk.mobilevisions.kiki.app.Constants;
import lk.mobilevisions.kiki.app.Utils;

/**
 * Base activity contains all the common component of an activity.
 * Progress bars, login progress, network checking frequently used db calls, etc...
 */
public class BaseActivity extends AppCompatActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}




	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);

		Resources res = getResources();
		// Change locale settings in the app.
		DisplayMetrics dm = res.getDisplayMetrics();
		android.content.res.Configuration conf = res.getConfiguration();
		conf.setLocale(new Locale(Utils.SharedPrefUtil.getStringFromSharedPref(this,Constants.KEY_LANGUAGE_PHONE,null)));
		// Use conf.locale = new Locale(...) if targeting lower versions
		res.updateConfiguration(conf, dm);
	}






}
