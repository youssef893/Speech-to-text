package com.YS.speechtotext;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Main2Activity extends AppCompatActivity implements DialogChangeLanguage.DialogListner
{
    private ActionBarDrawerToggle toggle;
    private String filename;
    private static String code = "en-GB";
    private  static final int REQUEST_CODE = 100;
    private EditText textOutput;
    ArrayList<String>text = new ArrayList<>();
    ArrayList<String>changedTexts = new ArrayList<>();
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        setToggle();
        checkSelfPermission();
        setFile();
    }

    private void checkSelfPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED){
            checkPermissions();
        }
    }

    private void setToggle()
    {
        // Navigation drawer menu
        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void setFile()
    {
        filename = Objects.requireNonNull(getIntent().getExtras()).getString("filename") ; // get file name
        if (filename != null) {
            filename = filename +  ".doc";
        }
        textOutput = findViewById(R.id.word);
        textOutput.addTextChangedListener(textWatcher); // take the last change in text
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .");
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
        }
    }

    private void checkPermissions()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},REQUEST_CODE);
        }
    }

    private boolean checkPermission()
    {
        int result = ContextCompat.checkSelfPermission(Main2Activity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Main2Activity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(Main2Activity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(Main2Activity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }

    public void save(MenuItem item)
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkPermission()) {
                    write_to_file();
                    startActivity(new Intent(Main2Activity.this, MainActivity.class));
                }else
                    requestPermission();
            }
        }
    }

    private void write_to_file()
    {
        String finalText ;
        File folder;
        folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

        File myFile = new File(folder, filename);

        FileOutputStream fstream;
        try {
            fstream = new FileOutputStream(myFile);
            finalText = textOutput.getText().toString();
            fstream.write(finalText.getBytes());
            fstream.close();
            Toast.makeText(getApplicationContext(), "Your file is Saved in Documents", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void speak(View view)
    {
        Intent voicerecogize = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        voicerecogize.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, Objects.requireNonNull(getClass().getPackage()).getName());
        voicerecogize.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voicerecogize.putExtra(RecognizerIntent.EXTRA_LANGUAGE, code);

        try
        {
            startActivityForResult(voicerecogize, REQUEST_CODE);
        }catch (ActivityNotFoundException e)
        {
            Toast.makeText(Main2Activity.this, "Google speech not allow on this device", Toast.LENGTH_LONG).show();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (!changedTexts.isEmpty())
        {
            text.clear();
            text.add(changedTexts.get(changedTexts.size() - 1));
            changedTexts.clear();
        }
        if (requestCode == REQUEST_CODE) {//If RESULT_OK is returned...//
            if (resultCode == RESULT_OK && null != data) {
                //...then retrieve the ArrayList//
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                assert result != null;
                text.add(result.get(0) + "\n");
            }
        }
        if (text.size() != 0)
        {
            StringBuilder word = new StringBuilder(text.get(0));
            for(int i = 1; i < text.size(); i++)
                word.append(text.get(i));
            textOutput.setText(word.toString());
        }
    }

    // to get any change has been done in edittext
    private final TextWatcher textWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s)
        {
            changedTexts.add(textOutput.getText().toString());
        }
    };

    public void changeLanguageBtn(View view)
    {
        openDialog();
    }

    public void openDialog()
    {
        DialogChangeLanguage exampleDialog = new DialogChangeLanguage();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void changeLanguage(String languageName)
    {
        textView = findViewById(R.id.languageName);
        String[] langName = languageName.split("-");
        textView.setText(langName[0]);
        code = langName[1];
    }
}