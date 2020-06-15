package com.example.telebuvoipcalling;

import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class IncomingCallActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_rej,btn_ans;
    TextView txtcallername,txtcallno,txtStatus;
    ImageView imghangup,imgaccept,imgreject;
    SipAudioCall inComingCall = null;
    SipAudioCall.Listener listener;
    Intent intent;
    Registeration wt;
    public SipManager manager = null;
    Handler ringingHandler;
    Runnable ringingRunnable;
    LinearLayout lytincoming;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            // TODO Auto-generated method stub
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            // set this flag so this activity will stay in front of the keyguard
            int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
            getWindow().addFlags(flags);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_incoming_call);
            intent=getIntent().getParcelableExtra("intent");
           // intent.getStringArrayExtra("intent");
            if(manager ==null)
            {
                manager = SipManager.newInstance(this);
            }

            try {
                listener = new SipAudioCall.Listener() {
                    @Override
                    public void onRinging(SipAudioCall call, SipProfile caller) {
                        try {
                            startService(new Intent(IncomingCallActivity.this, RingtonePlayingService.class).putExtra("isDialer", true));
                            setRingingRunnable();
                            txtcallno.setText("Ringing");

                            // call.answerCall(30);
                            // 30 is timeout
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCallEnded(SipAudioCall call) {
                        txtStatus.setText("Ended");
                        stopService(new Intent(IncomingCallActivity.this,RingtonePlayingService.class));
                        finish();
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
            //txtcallno.setText(inComingCall);
            inComingCall = manager.takeAudioCall(intent,listener);
            txtcallername.setText(inComingCall.getLocalProfile().getSipDomain());
        }
        catch (Exception e) {
            Log.d("Exception", e.toString());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    void setRingingRunnable(){
        ringingHandler=new Handler();
        ringingRunnable=new Runnable() {
            @Override
            public void run() {
                try {
                    inComingCall = manager.takeAudioCall(intent,listener);
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
        }
    }

    void answercall()
    {
        try {
           // txtcallername.setText(intent.);
            txtStatus.setText("Connected");
            lytincoming.setVisibility(View.GONE);
            imghangup.setVisibility(View.VISIBLE);
            inComingCall = manager.takeAudioCall(intent,listener);
            inComingCall.answerCall(30);
            inComingCall.startAudio();
            inComingCall.setSpeakerMode(true);
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
            inComingCall = manager.takeAudioCall(intent,listener);
            inComingCall.endCall();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void rejectcall()
    {
        try {
            inComingCall = manager.takeAudioCall(intent,listener);
            inComingCall.endCall();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

    }
}

