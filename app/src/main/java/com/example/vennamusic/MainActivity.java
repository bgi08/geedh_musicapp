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
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements SongChangeListener {
List<MusicList> musicLists=new ArrayList<>();
RecyclerView musicView;
musicAdapter musicAdapter;
MediaPlayer mediaPlayer;
TextView starttime,endtime;
boolean isplaying=false;
SeekBar playbar;
ImageView playpauseimg;
Timer timer;
int currentsongposition=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decodeview=getWindow().getDecorView();

        int options=View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decodeview.setSystemUiVisibility(options);

        setContentView(R.layout.activity_main);
        LinearLayout searchbtn=findViewById(R.id.search_btn);
        LinearLayout menubtn=findViewById(R.id.menu_btn);
        musicView= (RecyclerView) findViewById(R.id.musicRecyclerView);
        CardView playpausecard=findViewById(R.id.playandpause);
        playpauseimg =findViewById(R.id.playpauseImg);
        ImageView nextBtn=findViewById(R.id.next_btn);
        ImageView prevBtn=findViewById(R.id.previous_btn);
        starttime=findViewById(R.id.startTime);
        endtime=findViewById(R.id.endTime);
        playbar=findViewById(R.id.playerSeekBar);
        musicView.setHasFixedSize(true);
        musicView.setLayoutManager(new LinearLayoutManager(this));

        mediaPlayer=new MediaPlayer();

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
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    int nextsongposition=currentsongposition+1;
                    if(nextsongposition>=musicLists.size())
                    {
                        nextsongposition=0;
                    }
                musicLists.get(currentsongposition).setIsplaying(false);
                    musicLists.get(nextsongposition).setIsplaying(true);
                    musicAdapter.updateSonglist(musicLists);
                    musicView.scrollToPosition(nextsongposition);

                    onSongChanged(nextsongposition);
            }
        });
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int previoussongposition=currentsongposition-1;
                if(previoussongposition<=musicLists.size())
                {
                    previoussongposition=musicLists.size()-1;
                }
                musicLists.get(currentsongposition).setIsplaying(false);
                musicLists.get(previoussongposition).setIsplaying(true);
                musicAdapter.updateSonglist(musicLists);
                musicView.scrollToPosition(previoussongposition);

                onSongChanged(previoussongposition);
            }
        });
        playpausecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isplaying) {
                    isplaying = false;
                    mediaPlayer.pause();
                    playpauseimg.setImageResource(R.drawable.play_icon);
                }
                else
                {
                    isplaying=true;
                    mediaPlayer.start();
                    playpauseimg.setImageResource(R.drawable.pause_icon);
                }
            }

        });
        playbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b)
                {
                    if(isplaying)
                    {
                        mediaPlayer.seekTo(i);

                    }
                    else
                    {
                        mediaPlayer.seekTo(0);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

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
                final String getFilename= cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                final String getArtistname= cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                long cursorid=cursor.getLong((cursor.getColumnIndex(MediaStore.Audio.Media._ID)));

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

    @Override
    public void onSongChanged(int position) {
        currentsongposition=position;
if(mediaPlayer.isPlaying())
{
    mediaPlayer.pause();
    mediaPlayer.reset();
}
mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
new Thread(new Runnable() {
    @Override
    public void run() {
try {
    mediaPlayer.setDataSource(MainActivity.this,musicLists.get(position).getMusicfile());
    mediaPlayer.prepare();
}
catch(IOException e)
{
    e.printStackTrace();
    Toast.makeText(MainActivity.this,"Unable to play the selected track",Toast.LENGTH_SHORT).show();
}

    }
}).start();
mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        int getTotalDuration= mediaPlayer.getDuration();
        String generateDuration=String.format(Locale.getDefault(),"%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(getTotalDuration),
                TimeUnit.MILLISECONDS.toSeconds(getTotalDuration)-
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getTotalDuration)));
        endtime.setText(generateDuration);
isplaying=true;
mediaPlayer.start();
playbar.setMax(getTotalDuration);
playpauseimg.setImageResource(R.drawable.pause_icon);
    }
});
timer=new Timer();
timer.scheduleAtFixedRate(new TimerTask() {
    @Override
    public void run() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int getCurrentDuration=mediaPlayer.getCurrentPosition();
                String generateDuration=String.format(Locale.getDefault(),"%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(getCurrentDuration),
                        TimeUnit.MILLISECONDS.toSeconds(getCurrentDuration)-
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrentDuration)));
                playbar.setProgress(getCurrentDuration);
                starttime.setText(generateDuration);
            }
        });

    }
},1000,1000);
mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.reset();

        timer.purge();
        timer.cancel();
        isplaying=false;
        playpauseimg.setImageResource(R.drawable.play_icon);
        playbar.setProgress(0);
    }
});
    }

}