package com.example.telebuvoipcalling;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;

import static android.media.AudioManager.MODE_IN_COMMUNICATION;
import static android.media.AudioManager.MODE_RINGTONE;

public class RingtonePlayingService extends Service
{
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private Vibrator mVibrator;
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        mAudioManager = ((AudioManager) getSystemService(Context.AUDIO_SERVICE));
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        boolean isDialer=intent.getExtras().getBoolean("isDialer");

        try {
            Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if(isDialer){
                defaultRingtoneUri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.dialer_ringtone);
                //  mAudioManager.setMode(MODE_RINGTONE);
            }else {
                if(mAudioManager.isMusicActive()){
                    mAudioManager.setMode(MODE_IN_COMMUNICATION);
                }else {
                    mAudioManager.setMode(MODE_RINGTONE);
                }
                if ((mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE || mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) && mVibrator != null) {
                    long[] patern = {0,2000,2000};
                    mVibrator.vibrate(patern, 1);
                }
            }
            mMediaPlayer = new MediaPlayer();
            if(!mMediaPlayer.isPlaying()){
                mMediaPlayer.setDataSource(this, defaultRingtoneUri);
                final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                    /*  mMediaPlayer.setLooping(true);*/
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        mMediaPlayer.stop();

        if (mVibrator != null) {
            mVibrator.cancel();
        }
    }
}