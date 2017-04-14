package com.rafael.tesseractexample.tools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by rafael on 10/04/17.
 */

public class OCRTool {

    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/TesseractSample/";
    private static final String TESSDATA = "tessdata";

    private static final String lang = "por";

    private TessBaseAPI tessBaseApi;

    private Activity mActivity;

    private Bitmap mBitmap;

    public OCRTool(Bitmap bitmap, Activity activity) {
        this.mBitmap = bitmap;
        this.mActivity = activity;
        prepareTesseract();
    }

    public String execute(){

        tessBaseApi = new TessBaseAPI();
        tessBaseApi.init(DATA_PATH, lang);

        /*
        //EXTRA SETTINGS
        //For example if we only want to detect numbers
        tessBaseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "1234567890");

        //blackList Example
        tessBaseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "!@#$%^&*()_+=-qwertyuiop[]}{POIU" +
                "YTRWQasdASDfghFGHjklJKLl;L:'\"\\|~`xcvXCVbnmBNM,./<>?");
        */

        tessBaseApi.setImage(mBitmap);
        String extractedText = "empty result";
        try {
            extractedText = tessBaseApi.getUTF8Text();
        } catch (Exception e) {
            //Error in recognizing text
        }
        tessBaseApi.end();

        return extractedText;

    }

    private void prepareTesseract() {
        try {
            prepareDirectory(DATA_PATH + TESSDATA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        copyTessDataFiles(TESSDATA);
    }

    private void prepareDirectory(String path) {

        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                //ERROR: Creation of directory failed, check does Android Manifest have permission to write to external storage
            }
        } else {
            //Created directory
        }
    }

    private void copyTessDataFiles(String path) {
        try {
            String fileList[] = mActivity.getAssets().list(path);

            for (String fileName : fileList) {

                // open file within the assets folder
                // if it is not already there copy it to the sdcard
                String pathToDataFile = DATA_PATH + path + "/" + fileName;
                if (!(new File(pathToDataFile)).exists()) {

                    InputStream in = mActivity.getAssets().open(path + "/" + fileName);

                    OutputStream out = new FileOutputStream(pathToDataFile);

                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                }
            }
        } catch (IOException e) {
            //Unable to copy files to tessdata
        }
    }

    public TessBaseAPI getApi(){
        return this.tessBaseApi;
    }
}
