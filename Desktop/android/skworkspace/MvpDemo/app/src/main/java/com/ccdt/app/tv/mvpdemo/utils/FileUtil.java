package com.ccdt.app.tv.mvpdemo.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by wudz on 2017/1/16.
 */

public class FileUtil {
    public static void textToFile(final String strFilename, final String strBuffer)
    {
        try
        {
            File fileText = new File(strFilename);
            FileWriter fileWriter = new FileWriter(fileText);
            fileWriter.write(strBuffer);
            fileWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public static String getFromAssets(Context context, String fileName){
        try {
            InputStreamReader inputReader = new InputStreamReader( context.getResources().getAssets().open(fileName) );
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            String Result="";
            while((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
