package sk.feromakovi.speedmeter.util;

import android.content.Context;

public final class FontLoader extends AssetsLoader{
	
	private static final String FOLDER_NAME = "fonts";

	public FontLoader(Context context) {
		super(context, FOLDER_NAME);
	}

	public String[] getSelectionList(){
		String[] fileNames = getFiles();
		if(fileNames != null)
			for(int i = 0; i < fileNames.length; i++)
				fileNames[i] = Strings.niceFileName(fileNames[i]);
		return fileNames;
	}

	
}
