package sk.feromakovi.speedmeter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	
	private ListPreference mUnitsPreference;
	private DialogPreference mSpeedSizePreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		mUnitsPreference = (ListPreference) findPreference("units_system");
		mSpeedSizePreference = (DialogPreference) findPreference("speed_view_size");
		refreshSummaries();
	}	
	
	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
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
	
	private void refreshSummaries(){
		mUnitsPreference.setSummary(mUnitsPreference.getEntries()[mUnitsPreference.findIndexOfValue(mUnitsPreference.getValue())]);
		this.mSpeedSizePreference.setSummary(getString(R.string.preference_text_secondary_speed_view_size, getPreferenceScreen().getSharedPreferences().getInt("speed_view_size", 100)));
	}
}
