package com.YS.speechtotext;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements DialogNewFile.DialogListner
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void create(View view)
    {
        openDialog();
    }

    public void openDialog()
    {
        DialogNewFile exampleDialog = new DialogNewFile();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }


    @Override
    public void applyTexts(String filename)
    {
        Intent intent = new Intent(MainActivity.this, Main2Activity.class);
        intent.putExtra("filename",filename );
        startActivity(intent);
    }
}
