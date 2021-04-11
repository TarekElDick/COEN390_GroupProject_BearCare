package com.example.coen390_groupproject_bearcare.Director;

import android.content.Context;

import com.example.coen390_groupproject_bearcare.R;

import java.io.File;

public class Cmmon {
    public static String getAppPath(Context context)
    {
        File dir = new File(android.os.Environment.getExternalStorageDirectory()
            +  File.separator + context.getResources().getString(R.string.app_name)
                + File.separator);
        if (!dir.exists())
            dir.mkdir();
        return dir.getPath() + File.separator;
    }
}
