package com.example.pc56.firebasemusicstreamingwithstorage;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.media.session.IMediaControllerCallback;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {
    ImageView m;
    private static final int STEP_VALUE = 4000;
    ListView songListView;
    ImageButton pLay, nExt, pRevious;
    TextView selectedfile;
    MediaPlayer mPlayer;
    SeekBar seekBar;
    int currentPlaylistNum;
    songadapter mYadappter;
    ArrayList<SongDetail> mysonglist = new ArrayList<>();
    public Uri uri;
    private boolean isStarted = false;
    private String currentFile = "",currentname=" ";
    private boolean isMovingSeekBar = false;
    DatabaseReference mareferc;
    IMediaControllerCallback s;
    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playsong);
      //  FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mareferc = mDatabase.child("MySongs");

        findViews();
        DownloadSongs();


        mPlayer = new MediaPlayer();
        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                  currentFile =mysonglist.get(position).getUrl();
                currentname=mysonglist.get(position).getName();
                songListView.setSelection(position);
                   startPlay(mysonglist.get(position).getUrl(),mysonglist.get(position).getName());
Log.e("selectd",songListView.getSelectedItem()+"  "+songListView.getSelectedItemPosition());
                //playsong(mysonglist.get(position).getUrl());
            }
        });


    }

    void findViews() {
        seekBar=(SeekBar)findViewById(R.id.seekBar) ;
        selectedfile = (TextView) findViewById(R.id.selecteditem);
        pLay = (ImageButton) findViewById(R.id.play);
        pRevious = (ImageButton) findViewById(R.id.previous);
        nExt = (ImageButton) findViewById(R.id.next);
       // m = (ImageView) findViewById(R.id.mimg);
        songListView = (ListView) findViewById(R.id.listtt);
        pRevious.setOnClickListener(OnButtonClick);
        pLay.setOnClickListener(OnButtonClick);
        nExt.setOnClickListener(OnButtonClick);
        seekBar.setOnSeekBarChangeListener(seekBarChanged);
    }

    void DownloadSongs() {
        mareferc.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              //  long s = dataSnapshot.getChildrenCount();
 try{
     mysonglist.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    SongDetail sss = child.getValue(SongDetail.class);
                    mysonglist.add(sss);
                }
                mYadappter = new songadapter(getApplicationContext(), mysonglist);
                songListView.setAdapter(mYadappter);
                Log.d("User count: ", String.valueOf(mysonglist.size()));
                Log.d("User name: ", String.valueOf(mysonglist.get(1).getName()));
            }catch (Exception e){
     e.printStackTrace();
 }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to connect to DB", Toast.LENGTH_SHORT).show();
            }
        });
        }




    private View.OnClickListener OnButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.play: {
                    if (mPlayer.isPlaying()) {
                        //   handler.removeCallbacks(updatePositinRunnable);
                        mPlayer.pause();
                        pLay.setImageResource(android.R.drawable.ic_media_play);
                    } else {
                        if (isStarted) {
                            mPlayer.start();
                            pLay.setImageResource(android.R.drawable.ic_media_pause);
                            updatePosition();
                        } else {
                            if (!currentname.isEmpty()&&!currentname.isEmpty()&&songListView.getSelectedItem()==null){
                                 startPlay(mysonglist.get(0).getUrl(),mysonglist.get(0).getName());
                            }else {
                                startPlay(currentFile,currentname );

                            }
                        }
                    }
                    break;
                }

                case R.id.next: {
playNextSong(false);

                    break;
                }

                case R.id.previous: {
                 playPreviousSong();

                    break;
                }
            }
        }
    };

    private void startPlay(String file,String name ) {
        selectedfile.setText(name);
SongDetail s=new SongDetail();
        s.setName(name);
        s.setUrl(file);
        Log.i("Selected: ",String.valueOf( mysonglist.indexOf(s)));


        seekBar.setProgress(0);
        mPlayer.stop();
        mPlayer.reset();

        try {
            mPlayer.setDataSource(file);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        seekBar.setMax(mPlayer.getDuration());
        pLay.setImageResource(android.R.drawable.ic_media_pause);
        updatePosition();
        isStarted = true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // handler.removeCallbacks(updatePositinRunnable);
        mPlayer.stop();
        mPlayer.reset();
        mPlayer.release();
        mPlayer = null;
    }

    private void updatePosition() {
        //  handler.removeCallbacks(updatePositinRunnable);
        seekBar.setProgress(mPlayer.getCurrentPosition());
        // handler.postDelayed(updatePositinRunnable, UPDATE_FREQUENCY);
    }
    private SeekBar.OnSeekBarChangeListener seekBarChanged =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (isMovingSeekBar) {
                        mPlayer.seekTo(progress);
                        Log.i("OnSeekBarChangeListener", "OnProgressChanged");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    isMovingSeekBar = true;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    isMovingSeekBar = false;
                }
            };


    private void playNextSong(boolean byStop) {
        ArrayList<SongDetail> currentPlayList = mysonglist;

        currentPlaylistNum++;
        if (currentPlaylistNum >= currentPlayList.size()){
            currentPlaylistNum = 0;
            if (byStop) {
               // stopProximitySensor();
                if (mPlayer != null ) {
                    if (mPlayer != null) {
                        try {
                            mPlayer.stop();
                        } catch (Exception e) {
                        }
                        try {
                            mPlayer.release();
                            mPlayer = null;
                        } catch (Exception e) {
                        }
                    }
                }
                return;
            }
        }
        if (currentPlaylistNum < 0 || currentPlaylistNum >= currentPlayList.size()) {
            return;
        }
        startPlay(currentPlayList.get(currentPlaylistNum).getUrl(),currentPlayList.get(currentPlaylistNum).getName());
    }
    public void playPreviousSong() {
        ArrayList<SongDetail> currentPlayList = mysonglist;

        currentPlaylistNum--;
        if (currentPlaylistNum < 0) {
            currentPlaylistNum = currentPlayList.size() - 1;
        }
        if (currentPlaylistNum < 0 || currentPlaylistNum >= currentPlayList.size()) {
            return;
        }

        startPlay(currentPlayList.get(currentPlaylistNum).getUrl(),currentPlayList.get(currentPlaylistNum).getName());
    }
}
