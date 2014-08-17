package com.iHealth;
import java.io.*;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
public class SaveData {
	File myFile;
	FileOutputStream fOut;
	OutputStreamWriter myOutWriter;


	public void WriteTxt( String text) {
		try{
			Log.d("SaveData__", "writing: " + text);
			myOutWriter.append(text);
		} catch(Exception e) {
			Log.d("SaveData__", "can't writeTxt");
			e.printStackTrace();
		}

	}	
	
	public void CreatFile(String FileName) throws IOException
	{		
		try{
			new File("/sdcard/IHealthRecord").mkdir();
		} catch(Exception e) {
			Log.d("SaveData__", "can't CreatFile");
			e.printStackTrace();
		}
		
		String str = Environment.getExternalStorageDirectory().toString();
		myFile= new File(Environment.getExternalStorageDirectory(),"/IHealthRecord/"+FileName+".csv");
		
		fOut = new FileOutputStream(myFile);
		myOutWriter =new OutputStreamWriter(fOut);

	}
	
	public void EndSave() {
		try {
			myOutWriter.close();
			fOut.close();
		} catch(Exception e){
			Log.d("SaveData__", "can't end save");
			e.printStackTrace();
		}
	}
	}
