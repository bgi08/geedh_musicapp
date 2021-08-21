package com.example.vennamusic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class musicAdapter extends RecyclerView.Adapter<musicAdapter.ViewHolder>
{
List<MusicList> list;
Context context;
private int playingposition=0;
SongChangeListener songChangeListener;

    public musicAdapter(List<MusicList> list, Context context) {
        this.list = list;
        this.context = context;
        this.songChangeListener=((SongChangeListener)context);
    }
    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public musicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.music_adapter,null);
return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull musicAdapter.ViewHolder holder, int position) {
        MusicList list2=list.get(position);

if(list2.Isplaying())
{
    playingposition=position;
    holder.rootlayout.setBackgroundResource(R.drawable.round_back_blue_10);

}
else
{
    holder.rootlayout.setBackgroundResource(R.drawable.round_back_10);
}
String generateDuration=String.format(Locale.getDefault(),"%02d:%02d",
        TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(list2.getDuration())),
        TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(list2.getDuration()))-
        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(list2.getDuration()))));

holder.title.setText(list2.getTitle());
holder.artist.setText(list2.getArtist());
holder.musicduration.setText(generateDuration);

holder.rootlayout.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        list.get(playingposition).setIsplaying(false);
list2.setIsplaying(true);
songChangeListener.onSongChanged(position);
notifyDataSetChanged();
    }
});
    }
public void updateSonglist(List<MusicList> list)
{
    this.list=list;
    notifyDataSetChanged();
}
    @Override
    public int getItemCount() {
        return list.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder
    {
        RelativeLayout rootlayout;
        TextView title,artist,musicduration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rootlayout=itemView.findViewById(R.id.rootlayout);
            title=itemView.findViewById(R.id.musictitle);
            artist=itemView.findViewById(R.id.musicartist);
            musicduration=itemView.findViewById(R.id.musicduration);
        }
    }
}
