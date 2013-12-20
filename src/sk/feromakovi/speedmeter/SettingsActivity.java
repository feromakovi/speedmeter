package sk.feromakovi.speedmeter;

import sk.feromakovi.speedmeter.util.FontLoader;
import sk.feromakovi.speedmeter.util.Strings;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	
	private FontLoader mFontLoader;
	
	private ListPreference mUnitsPreference;
	private ListPreference mDisplayModePreference;
	private ListPreference mFontPreference;
	private DialogPreference mSpeedSizePreference;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
		mFontLoader = new FontLoader(getApplicationContext());
		
		mUnitsPreference = (ListPreference) findPreference("units_system");
		mDisplayModePreference = (ListPreference) findPreference("display_mode");
		mSpeedSizePreference = (DialogPreference) findPreference("speed_view_size");
		mFontPreference = (ListPreference) findPreference("font");
		
		try{
			mFontPreference.setEntryValues(mFontLoader.getFiles());
			mFontPreference.setEntries(mFontLoader.getSelectionList());
		}catch(Exception e){
			e.printStackTrace();
		}		
		
		refreshSummaries();
	}	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		refreshSummaries();
	}
	
	public static Intent getIntent(Context context){
		return new Intent(context, SettingsActivity.class);
	}
	
	@SuppressWarnings("deprecation")
	private void refreshSummaries(){
		mUnitsPreference.setSummary(mUnitsPreference.getEntries()[mUnitsPreference.findIndexOfValue(mUnitsPreference.getValue())]);
		mDisplayModePreference.setSummary(mDisplayModePreference.getEntries()[mDisplayModePreference.findIndexOfValue(mDisplayModePreference.getValue())]);
		this.mSpeedSizePreference.setSummary(getString(R.string.preference_text_secondary_speed_view_size, getPreferenceScreen().getSharedPreferences().getInt("speed_view_size", 100)));
		mFontPreference.setSummary(getString(R.string.preference_text_secondary_font, Strings.niceFileName(mFontPreference.getValue())));
	}
}
