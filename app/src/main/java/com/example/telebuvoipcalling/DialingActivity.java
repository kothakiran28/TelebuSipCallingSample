package com.example.telebuvoipcalling;

import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipErrorCode;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DialingActivity extends AppCompatActivity implements View.OnClickListener, DailPadBottomSheet.AddMemberListener {
    public String sipAddress = null;
    TextView txtcallername,txtStatus,txtcallno;
    public SipManager manager = null;
    public SipAudioCall call = null;
    public SipProfile me = null;
    ImageView imghangup,imgspeaker,imgmute,imgadd;
    SipAudioCall.Listener listener;
    Handler ringingHandler;
    Runnable ringingRunnable;
    TelebuManager telebuManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialing);
        Intent intent = getIntent();
        telebuManager=new TelebuManager(this);
        telebuManager.enableProximitySensing(true);
        sipAddress = intent.getStringExtra("sipAddress");
        setView();
        if (manager == null) {
            manager = SipManager.newInstance(this);
        }

        try {
              listener = new SipAudioCall.Listener() {
                // Much of the client's interaction with the SIP Stack will
                // happen via listeners.  Even making an outgoing call, don't
                // forget to set up a listener to set things up once the call is established.
                @Override
                public void onCallEstablished(SipAudioCall call) {
                    call.startAudio();
                    //call.toggleMute();
                    call.setSpeakerMode(false);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtStatus.setText("connected");
                            if (ringingHandler!=null&&ringingRunnable!=null) {
                                ringingHandler.removeCallbacks(ringingRunnable);
                            }
                            imgspeaker.setVisibility(View.VISIBLE);
                            imgmute.setVisibility(View.VISIBLE);
                            imgadd.setVisibility(View.VISIBLE);
                            stopService(new Intent(DialingActivity.this,RingtonePlayingService.class));
                        }
                    });
                }

                  @Override
                  public void onCallBusy(SipAudioCall call) {
                      stopService(new Intent(DialingActivity.this,RingtonePlayingService.class));
                      finish();
                  }

                  @Override
                public void onCallEnded(SipAudioCall call) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (ringingHandler!=null&&ringingRunnable!=null) {
                                ringingHandler.removeCallbacks(ringingRunnable);
                            }
                            txtStatus.setText("Ended");
                            stopService(new Intent(DialingActivity.this,RingtonePlayingService.class));
                            finish();
                        }
                    });
                }

                  @Override
                  public void onError(SipAudioCall call, int errorCode, final String errorMessage) {
                      super.onError(call, errorCode, errorMessage);
                      if (ringingHandler!=null&&ringingRunnable!=null) {
                          ringingHandler.removeCallbacks(ringingRunnable);
                      }
                      runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                            Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_LONG).show();
                          }
                      });
                      stopService(new Intent(DialingActivity.this,RingtonePlayingService.class));
                      finish();
                      Log.e("onError",errorMessage+","+errorCode);
                  }

                  @Override
                  public void onRingingBack(SipAudioCall call) {
                      super.onRingingBack(call);  runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              txtStatus.setText("Ringing");
                          }
                      });
                  }
              };
           // txtcallername.setText(sipAddress);
            call = Registeration.manager.makeAudioCall(Registeration.me.getUriString(), sipAddress, listener, 30);
            try {
                startService(new Intent(DialingActivity.this, RingtonePlayingService.class).putExtra("isDialer", true));
                setRingingRunnable();
                // call.answerCall(30);
                // 30 is timeout
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateStatus();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error when trying to close manager", Toast.LENGTH_SHORT).show();
            Log.i("Walkie/InitiateCall", "Error when trying to close manager.", e);
            if (Registeration.me != null) {
                try {
                    Registeration.manager.close(Registeration.me.getUriString());
                } catch (Exception ee) {
                    Log.i("Walkie/InitiateCall",
                            "Error when trying to close manager.", ee);
                    ee.printStackTrace();
                }
            }
            if (call != null) {
                call.close();
            }

        }
    }
    void setView()
    {
        txtcallername = findViewById(R.id.txtcallername);
        txtStatus = findViewById(R.id.txtStatus);
        txtcallno = findViewById(R.id.txtcallno);
        imghangup = findViewById(R.id.imghangup);
        imghangup.setOnClickListener(this);
        imgspeaker = findViewById(R.id.imgspeaker);
        imgspeaker.setOnClickListener(onSpeakerClickListener);
        imgmute = findViewById(R.id.imgmute);
        imgmute.setOnClickListener(onMuteClickListener);
        imgadd = findViewById(R.id.imgadd);
        imgadd.setOnClickListener(this);

    }
    public void updateStatus() {
        String useName = call.getPeerProfile().getDisplayName();
        if(useName == null)
        {
            useName = call.getPeerProfile().getUserName();
        }
        txtcallername.setText(useName);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imghangup:
                try {
                    if (ringingHandler!=null&&ringingRunnable!=null) {
                        ringingHandler.removeCallbacks(ringingRunnable);
                    }
                    if (call!=null)
                    {
                        try {
                            call.endCall();
                            finish();
                        } catch (SipException e) {
                            e.printStackTrace();
                        }
                    }
                    stopService(new Intent(DialingActivity.this,RingtonePlayingService.class));
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.imgadd:
                DailPadBottomSheet dailPadBottomSheet=new DailPadBottomSheet(this);
                dailPadBottomSheet.setAddMemberListener(this);
                dailPadBottomSheet.show(this.getSupportFragmentManager(), dailPadBottomSheet.getTag());
                break;
        }
    }


    View.OnClickListener onSpeakerClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.isSelected()){
                imgspeaker.setSelected(false);
                imgspeaker.setImageResource(R.drawable.ic_speaker);
                call.setSpeakerMode(false);


            }else {
                imgspeaker.setSelected(true);
                imgspeaker.setImageResource(R.drawable.ic_speakeron);
                call.setSpeakerMode(true);
            }
        }
    };

    View.OnClickListener onMuteClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.isSelected()){
                imgmute.setSelected(false);
                imgmute.setImageResource(R.drawable.ic_mute);
                call.toggleMute();
            }else {
                imgmute.setSelected(true);
                imgmute.setImageResource(R.drawable.ic_unmute);
                call.toggleMute();
            }
        }
    };

    void setRingingRunnable(){
        ringingHandler=new Handler();
        ringingRunnable=new Runnable() {
            @Override
            public void run() {
                try {
                    //call = manager.takeAudioCall(intent,listener);
                    call.endCall();
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
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(telebuManager!=null)telebuManager.enableProximitySensing(false);
    }

    @Override
    public void newNumberAdded(String mobileNumber) {
        if (call!=null)
            call.sendDtmf(Integer.parseInt(mobileNumber));
    }
}
