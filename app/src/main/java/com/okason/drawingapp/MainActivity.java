package com.okason.drawingapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar_top;
    private Toolbar mToolbar_bottom;
    private int brushSize = 15;
    private int opacity = 255;
    private int brushColor;
    private CustomView mCustomView;
    ColorPicker cp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCustomView = (CustomView)findViewById(R.id.custom_view);
        mToolbar_top = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(mToolbar_top);

        mToolbar_bottom = (Toolbar)findViewById(R.id.toolbar_bottom);
        mToolbar_bottom.inflateMenu(R.menu.menu_drawing);
        mToolbar_bottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                handleDrawingIconTouched(item.getItemId());
                return false;
            }
        });
        cp = new ColorPicker(MainActivity.this, 11, 195, 255);
    }

    private void handleDrawingIconTouched(int itemId) {
        switch (itemId){
            case R.id.action_delete:
                deleteDialog();
                break;
            case R.id.action_opacity:
                opacityPicker();
                break;
            case R.id.action_brush:
                sizePicker();
                break;
            case R.id.action_color:
                colorPicker();
                break;
            case R.id.action_share:
                shareDrawing();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private void deleteDialog(){
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);
        deleteDialog.setTitle(getString(R.string.delete_drawing));
        deleteDialog.setMessage(getString(R.string.new_drawing_warning));
        deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                mCustomView.eraseAll();
                dialog.dismiss();
            }
        });
        deleteDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        deleteDialog.show();
    }

    private void sizePicker()
    {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final SeekBar seek = new SeekBar(this);
        seek.setMax(30);
        seek.setProgress(brushSize);
        popDialog.setTitle("Current brush size is: " + brushSize);
        popDialog.setView(seek);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Do something here with new value
                brushSize = progress;
            }

            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });

        popDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mCustomView.setBrushSize((float) brushSize);
                        dialog.dismiss();
                    }

                });


        popDialog.create();
        popDialog.show();

    }

    private void opacityPicker()
    {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final SeekBar seek = new SeekBar(this);
        seek.setMax(255);
        seek.setProgress(opacity);
        popDialog.setTitle("Opacity is: " + opacity);
        popDialog.setView(seek);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                opacity = progress;
            }

            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });

        popDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mCustomView.setOpacity(opacity);
                        dialog.dismiss();
                    }

                });


        popDialog.create();
        popDialog.show();

    }


    private void colorPicker() {
        cp.show();
        Button okColor = (Button)cp.findViewById(R.id.okColorButton);

        okColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                brushColor = cp.getColor();
                mCustomView.setColor(brushColor);
                cp.dismiss();
            }
        });
    }

    private void shareDrawing() {
        mCustomView.setDrawingCacheEnabled(true);
        mCustomView.invalidate();
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        File file = new File(path,
                "image_drawing_434.png");
        file.getParentFile().mkdirs();

        try {
            file.createNewFile();
            fOut = new FileOutputStream(file);
        } catch (Exception e) {
        }

        mCustomView.getDrawingCache()
                .compress(Bitmap.CompressFormat.JPEG, 85, fOut);

        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        shareIntent.setType("image/png");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share image"));


    }
}
