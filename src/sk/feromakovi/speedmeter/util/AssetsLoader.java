package sk.feromakovi.speedmeter.util;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetManager;

public abstract class AssetsLoader {
	
	protected AssetManager mManager;
	protected String mFolder;

	protected AssetsLoader(final Context context, final String folder){
		this.mManager = context.getAssets();
		this.mFolder = folder;
	}
	
	public String[] getFiles(){
		try {
			return this.mManager.list(mFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
