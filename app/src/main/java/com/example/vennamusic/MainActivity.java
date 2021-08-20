package com.example.vennamusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
List<MusicList> musicLists=new ArrayList<>();
RecyclerView musicView;
musicAdapter musicAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decodeview=getWindow().getDecorView();

        int options=View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decodeview.setSystemUiVisibility(options);

        setContentView(R.layout.activity_main);

        LinearLayout searchbtn=findViewById(R.id.search_btn);
        LinearLayout menubtn=findViewById(R.id.menu_btn);
        RecyclerView musicView= (RecyclerView) findViewById(R.id.musicRecyclerView);
        CardView playpausecard=findViewById(R.id.playandpause);
        ImageView playpauseimg=findViewById(R.id.playpauseImg);
        ImageView nextBtn=findViewById(R.id.next_btn);
        ImageView prevBtn=findViewById(R.id.previous_btn);
        musicView.setHasFixedSize(true);
        musicView.setLayoutManager(new LinearLayoutManager(this));

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
        {
            getMusicFiles();

        }

        else
        {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},11);
            }
            else
                getMusicFiles();
        }

    }
    private void getMusicFiles()
    {
        ContentResolver contentResolver=getContentResolver();
        Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor=contentResolver.query(uri,null,MediaStore.Audio.Media.DATA+" LIKE?",new String[]{"%.mp3%"},null);
        if(cursor==null)
        {
        Toast.makeText(this,"Something is wrong!!",Toast.LENGTH_SHORT).show();
        }
        else if(!cursor.moveToNext())
        {
    Toast.makeText(this,"No music found",Toast.LENGTH_SHORT).show();
        }
        else
        {
            while(cursor.moveToNext())
            {
                final String getFilename= String.valueOf(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                final String getArtistname= String.valueOf((cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                long cursorid=cursor.getColumnIndex(MediaStore.Audio.Media._ID);

                Uri musicfileuri= ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,cursorid);
                String getduration ="00:00";

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q)
                {
                    getduration=cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)));


                }
                MusicList musicList=new MusicList(getFilename,getArtistname,getduration,false,musicfileuri);
                musicLists.add(musicList);
            }
            musicAdapter=new musicAdapter(musicLists,MainActivity.this);
            musicView.setAdapter(musicAdapter);
        }
        cursor.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getMusicFiles();
        } else {
            Toast.makeText(this, "Permission declined by user", Toast.LENGTH_SHORT).show();

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus)
        {       View decodeview=getWindow().getDecorView();
                int options=View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                decodeview.setSystemUiVisibility(options);

        }
    }
}