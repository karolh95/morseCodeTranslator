package com.ocr.morsecode.ocr;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.ocr.morsecode.BuildConfig;
import com.ocr.morsecode.R;

import java.io.File;

public class OcrActivity extends AppCompatActivity {

    public static final String EXTRAS = "EXTRAS";
    private static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";
    private static final int REQUEST_CODE_CAMERA = 1;

    private Ocr ocr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        ocr = new Ocr(this);
        ocr.init();
        startCameraActivity();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode != Activity.RESULT_OK)
                cancel(null);
            else
                ocr.execute();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void apply(View view) {
        TextView textView = findViewById(R.id.text);
        Bundle bundle = new Bundle();
        bundle.putString(EXTRAS, textView.getText().toString());

        Intent returnIntent = new Intent();
        returnIntent.putExtras(bundle);

        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void cancel(View view) {
        if (ocr != null) {
            ocr.getThread().interrupt();
            ocr = null;
        }
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public void startCameraActivity() {
        File file = new File(Ocr.TMP);
        Uri uri = FileProvider.getUriForFile(this, AUTHORITY, file);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
    }

}
