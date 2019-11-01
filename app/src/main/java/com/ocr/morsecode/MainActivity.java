package com.ocr.morsecode;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ocr.morsecode.list.ListActivity;
import com.ocr.morsecode.ocr.OcrActivity;
import com.ocr.morsecode.tools.MorseCode;
import com.ocr.morsecode.tools.RequestPermissions;

import java.util.Objects;
import java.util.function.UnaryOperator;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_OCR = 1;
    private static final int REQUEST_CODE_LIST = 2;
    private static final String KEY_STATE_INPUT = "input";
    private static final String KEY_STATE_OUTPUT = "output";
    private static final String LABEL = "OUTPUT TEXT";
    private String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private UnaryOperator<String> operation;
    private RequestPermissions requestTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        operation = MorseCode::encode;

        requestTool = new RequestPermissions();

        TextView textView = findViewById(R.id.output);
        textView.setOnLongClickListener((view) -> copyToClipboard(textView));
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        TextView input = findViewById(R.id.input);
        TextView output = findViewById(R.id.output);

        input.setText(savedInstanceState.getString(KEY_STATE_INPUT));
        output.setText(savedInstanceState.getString(KEY_STATE_OUTPUT));

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        TextView input = findViewById(R.id.input);
        TextView output = findViewById(R.id.output);

        outState.putString(KEY_STATE_INPUT, input.getText().toString());
        outState.putString(KEY_STATE_OUTPUT, output.getText().toString());

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Button action = findViewById(R.id.action);
        switch (item.getItemId()) {
            case R.id.list:
                Intent listIntent = new Intent(this, ListActivity.class);
                startActivityForResult(listIntent, REQUEST_CODE_LIST);
                break;
            case R.id.decode:
                action.setText(R.string.decode);
                operation = MorseCode::decode;
                break;
            case R.id.encode:
                action.setText(R.string.encode);
                operation = MorseCode::encode;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK || data.getExtras() == null)
            return;
        Bundle bundle = data.getExtras();
        TextView textView = findViewById(R.id.input);
        String extras;
        switch (requestCode) {
            case REQUEST_CODE_OCR:
                extras = bundle.getString(OcrActivity.EXTRAS);
                break;
            case REQUEST_CODE_LIST:
                extras = bundle.getString(ListActivity.EXTRAS);
                break;
            default:
                extras = textView.getText().toString();
                break;
        }
        textView.setText(extras);
        operation = MorseCode::encode;
        code(null);

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startOcrActivity(View view) {
        if (requestTool.isPermissionsGranted(this, PERMISSIONS)) {
            Intent ocrActivity = new Intent(this, OcrActivity.class);
            startActivityForResult(ocrActivity, REQUEST_CODE_OCR);
        } else
            requestTool.requestPermissions(this, PERMISSIONS);
    }

    public void code(View view) {
        TextView input = findViewById(R.id.input);
        String text = input.getText().toString();
        TextView output = findViewById(R.id.output);
        output.setText(operation.apply(text));
    }

    public boolean copyToClipboard(TextView textView) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(LABEL, textView.getText().toString());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this, R.string.copy, Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        boolean grantedAllPermissions = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                grantedAllPermissions = false;
            }
        }

        if (grantResults.length != permissions.length || (!grantedAllPermissions)) {
            requestTool.onPermissionDenied();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}