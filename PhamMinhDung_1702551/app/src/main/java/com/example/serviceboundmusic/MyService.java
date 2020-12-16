package com.example.serviceboundmusic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyService extends Service {

    MediaPlayer mediaPlayer;
    IBinder binder;
    //int song;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ServiceDemo", "Đã gọi onCreate()");


        mediaPlayer = MediaPlayer.create(this, R.raw.buocquamuacodon_vu);
        mediaPlayer.seekTo(0);
        mediaPlayer.setVolume(0.5f, 0.5f);
        binder = new MyBinder(); // do MyBinder được extends Binder

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("ServiceDemo", "Đã gọi onBind()");
        // trả về đối tượng binder cho ActivityMain

        return binder;

    }
    // Kết thúc một Service
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("ServiceDemo", "Đã gọi onBind()");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        return super.onUnbind(intent);
    }
    public class MyBinder extends Binder {

        // phương thức này trả về đối tượng MyService
        public MyService getService() {

            return MyService.this;
        }
        public int getDuration(){
            return mediaPlayer.getDuration();
        }
        public int getCurrentPosition(){
            return mediaPlayer.getCurrentPosition();
        }
    }
    public void setRepeat(boolean r){
        mediaPlayer.setLooping(r);
    }
    public void setVolume(float a, float b){
        mediaPlayer.setVolume(a, b);
    }
    public void start(){

        mediaPlayer.start();
    }
    public void setSeekTo(int x){
        mediaPlayer.seekTo(x);
    }
    public void pause(){
        mediaPlayer.pause();
    }
}
// Xây dựng một đối tượng riêng để chơi nhạc
