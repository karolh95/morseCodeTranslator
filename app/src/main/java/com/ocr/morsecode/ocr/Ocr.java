package com.ocr.morsecode.ocr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.ocr.morsecode.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

public class Ocr extends AsyncTask<Void, Void, Void> {
    private static final int OPTIONS_IN_SAMPLE_SIZE = 1;
    private static final String LANG = "eng";
    private static final String DIRECTORY_NAME = "/Tesseract/";
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + DIRECTORY_NAME;
    static final String TMP = DATA_PATH + "tmp.jpeg";
    private static final String TESSDATA = "tessdata";

    private WeakReference<OcrActivity> activity;
    private TessBaseAPI api;
    private String text;

    Ocr(OcrActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    Thread getThread() {
        return Thread.currentThread();
    }

    @Override
    protected void onPreExecute() {
        activity.get().runOnUiThread(() -> {
            Button okButton = activity.get().findViewById(R.id.button_ok);
            okButton.setEnabled(false);
        });
        log("Prepare Tesseract working directory");
        prepareDirectory();
        log("Prepare Tesseract data files");
        copyTessDataFiles();
        log("Prepare Tesseract API");
        api = new TessBaseAPI();
        api.init(DATA_PATH, LANG);

    }

    @Override
    protected Void doInBackground(Void... voids) {

        log("Read image file");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = OPTIONS_IN_SAMPLE_SIZE;

        Uri uri = Uri.parse(TMP);
        Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath(), options);

        Pix image = ReadFile.readBitmap(bitmap);
        api.setImage(image);

        log("Recognize text from an image...");
        text = api.getUTF8Text();
//        api.getHOCRText(0);

        log("Operation complete!");
        api.clear();
        api.end();

        activity.get().runOnUiThread(() -> {
                    TextView textView = activity.get().findViewById(R.id.text);
                    textView.setText(text);
                    activity.get().findViewById(R.id.button_ok).setEnabled(true);
                    activity.get().findViewById(R.id.progressBar).setVisibility(View.GONE);
                }
        );
        return null;
    }

    private void log(String msg) {
        TextView logTextView = activity.get().findViewById(R.id.log);
        activity.get().runOnUiThread(
                () -> logTextView.append(msg + "\n")
        );
    }

    /**
     * Prepare directory on external storage
     */
    private void prepareDirectory() {

        File dir = new File(DATA_PATH + TESSDATA);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                log("ERROR: Creation of directory " + DATA_PATH + TESSDATA + " failed, check does Android Manifest have permission to write to external storage.");
            } else {
                log("Created directory " + DATA_PATH + TESSDATA);
            }
        }
    }

    /**
     * Copy tessdata files (located on assets/tessdata) to destination directory
     **/
    private void copyTessDataFiles() {
        try {
            String[] fileList = activity.get().getAssets().list(TESSDATA);

            assert fileList != null;
            for (String fileName : fileList) {

                // open file within the assets folder
                // if it is not already there copy it to the sdcard
                String pathToDataFile = DATA_PATH + TESSDATA + "/" + fileName;
                if (!(new File(pathToDataFile)).exists()) {

                    InputStream in = activity.get().getAssets().open(TESSDATA + "/" + fileName);

                    OutputStream out = new FileOutputStream(pathToDataFile);

                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();

                    log("Copied " + fileName + " to " + TESSDATA);
                }
            }
        } catch (IOException e) {
            log("Unable to copy files to " + TESSDATA + ": " + e.toString());
        }
    }

    void init() {
        prepareDirectory();
        copyTessDataFiles();
    }
}
