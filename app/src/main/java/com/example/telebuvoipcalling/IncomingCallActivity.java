package com.example.telebuvoipcalling;

import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipProfile;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class IncomingCallActivity extends AppCompatActivity implements View.OnClickListener, DailPadBottomSheet.AddMemberListener{
    Button btn_rej,btn_ans;
    TextView txtcallername,txtcallno,txtStatus;
    ImageView imghangup,imgaccept,imgreject,imgspeaker,imgmute,imgadd;
    SipAudioCall inComingCall = null;
    SipAudioCall.Listener listener;
    Intent intent;
    Registeration wt;
    Handler ringingHandler;
    Runnable ringingRunnable;
    LinearLayout lytincoming;
    TelebuManager telebuManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            // TODO Auto-generated method stub
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            // set this flag so this activity will stay in front of the keyguard
            int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
            getWindow().addFlags(flags);
            super.onCreate(savedInstanceState);
            telebuManager=new TelebuManager(this);
            telebuManager.enableProximitySensing(true);
            setContentView(R.layout.activity_incoming_call);
            intent=getIntent().getParcelableExtra("intent");
           // intent.getStringArrayExtra("intent");
            setView();
            startService(new Intent(IncomingCallActivity.this, RingtonePlayingService.class).putExtra("isDialer", false));
            setRingingRunnable();
            txtcallno.setText("Ringing");
            try {
                listener = new SipAudioCall.Listener() {
                    @Override
                    public void onRinging(SipAudioCall call, SipProfile caller) {
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    startService(new Intent(IncomingCallActivity.this, RingtonePlayingService.class).putExtra("isDialer", false));
                                    setRingingRunnable();
                                    txtcallno.setText("Ringing");
                                }
                            });
                            // call.answerCall(30);
                            // 30 is timeout
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                   @Override
                    public void onCalling(SipAudioCall call) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startService(new Intent(IncomingCallActivity.this, RingtonePlayingService.class).putExtra("isDialer", false));
                                setRingingRunnable();
                                txtcallno.setText("Ringing");
                            }
                        });
                    }

                    @Override
                    public void onCallEnded(SipAudioCall call) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtStatus.setText("Ended");
                                stopService(new Intent(IncomingCallActivity.this,RingtonePlayingService.class));
                                finish();
                            }
                        });
                    }
                    };
                //wt.updateStatus(inComingCall);

            }catch(Exception e)
            {
                if(inComingCall!=null)
                {
                    inComingCall.close();
                }
                //e.printStackTrace();
            }
            //txtcallno.setText(inComingCall);
            inComingCall = wt.manager.takeAudioCall(intent,listener);
            txtcallername.setText(inComingCall.getPeerProfile().getUserName());
        }
        catch (Exception e) {
            Log.d("Exception", e.toString());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void setView(){
        txtcallno = findViewById(R.id.txtcallno);
        txtcallername = findViewById(R.id.txtcallername);
        txtStatus = findViewById(R.id.txtStatus);
        imghangup =  findViewById(R.id.imghangup);
        imgaccept =  findViewById(R.id.imgaccept);
        lytincoming = findViewById(R.id.lytincoming);
        imgreject =  findViewById(R.id.imgreject);
        imgaccept.setOnClickListener(this);
        imgreject.setOnClickListener(this);
        imghangup.setOnClickListener(this);
        imgspeaker = findViewById(R.id.imgspeaker);
        imgmute = findViewById(R.id.imgmute);
        imgspeaker.setOnClickListener(onSpeakerClickListener);
        imgmute.setOnClickListener(onMuteClickListener);
        imgadd = findViewById(R.id.imgadd);
        imgadd.setOnClickListener(this);

    }

    View.OnClickListener onSpeakerClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.isSelected()){
                imgspeaker.setSelected(false);
                imgspeaker.setImageResource(R.drawable.ic_speaker);
                inComingCall.setSpeakerMode(false);
            }else {
                imgspeaker.setSelected(true);
                imgspeaker.setImageResource(R.drawable.ic_speakeron);
                inComingCall.setSpeakerMode(true);
            }
        }
    };

    View.OnClickListener onMuteClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.isSelected()){
                imgmute.setSelected(false);
                imgmute.setImageResource(R.drawable.ic_mute);
                inComingCall.toggleMute();
            }else {
                imgmute.setSelected(true);
                imgmute.setImageResource(R.drawable.ic_unmute);
                inComingCall.toggleMute();
            }
        }
    };

    void setRingingRunnable(){
        ringingHandler=new Handler();
        ringingRunnable=new Runnable() {
            @Override
            public void run() {
                try {
                    inComingCall = wt.manager.takeAudioCall(intent,listener);
                    inComingCall.endCall();
                } catch (SipException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(),"Ring Time Out",Toast.LENGTH_SHORT).show();
                finish();
            }
        };
        ringingHandler.postDelayed(ringingRunnable, 50000);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgaccept:
                stopService(new Intent(IncomingCallActivity.this,RingtonePlayingService.class));
                answercall();
                break;
            case R.id.imgreject:
                stopService(new Intent(IncomingCallActivity.this,RingtonePlayingService.class));
                rejectcall();
                finish();
                break;
            case R.id.imghangup:
                stopService(new Intent(IncomingCallActivity.this,RingtonePlayingService.class));
                hangupcall();
                finish();
                break;
            case R.id.imgadd:
                DailPadBottomSheet dailPadBottomSheet=new DailPadBottomSheet(this);
                dailPadBottomSheet.setAddMemberListener(this);
                dailPadBottomSheet.show(this.getSupportFragmentManager(), dailPadBottomSheet.getTag());
                break;
        }
    }

    void answercall()
    {
        try {
           // txtcallername.setText(intent.);
            if (ringingHandler!=null&&ringingRunnable!=null)
            {
                ringingHandler.removeCallbacks(ringingRunnable);
            }
            txtcallno.setVisibility(View.GONE);
            txtStatus.setText("Connected");
            lytincoming.setVisibility(View.GONE);
            imghangup.setVisibility(View.VISIBLE);
            imgspeaker.setVisibility(View.VISIBLE);
            imgmute.setVisibility(View.VISIBLE);
            imgadd.setVisibility(View.VISIBLE);
            inComingCall = wt.manager.takeAudioCall(intent,listener);
            inComingCall.answerCall(30);
            inComingCall.startAudio();
            inComingCall.setSpeakerMode(false);
            if(inComingCall.isMuted())
            {
                // SIP audioCall incoming call
                inComingCall.toggleMute();
            }
            wt.call = inComingCall;
        }catch (Exception e){
          Log.e("exception",e+"");
        }

    }

    void hangupcall()
    {
        try {
            if (ringingHandler!=null&&ringingRunnable!=null) {
                ringingHandler.removeCallbacks(ringingRunnable);
            }
            inComingCall = wt.manager.takeAudioCall(intent,listener);
            inComingCall.endCall();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void rejectcall()
    {
        try {
            if (ringingHandler!=null&&ringingRunnable!=null) {
                ringingHandler.removeCallbacks(ringingRunnable);
            }
            inComingCall = wt.manager.takeAudioCall(intent,listener);
            inComingCall.endCall();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(telebuManager!=null)telebuManager.enableProximitySensing(false);
    }

    @Override
    public void newNumberAdded(String mobileNumber) {
         if (inComingCall!=null)
         inComingCall.sendDtmf(Integer.parseInt(mobileNumber));
    }
}

