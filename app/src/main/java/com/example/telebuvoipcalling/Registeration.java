package com.example.telebuvoipcalling;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.net.sip.SipSession;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.ParseException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class Registeration extends AppCompatActivity implements View.OnTouchListener
{
    public String sipAddress = null;
    public SipManager manager = null;
    // SipManager,SipProfile,SipAudioCall  is default Class in android belonging to
    // android .net.sip package
    public SipProfile me = null;
    public SipAudioCall call = null;
    public IncomingCallReceiver receiver;

    /// Integer Variables
    private static  final int CALL_ADDRESS = 1;
    private static  final int SET_AUTH_INFO =2;
    private static  final int UPDATE_SETTING_DIALOG =3;
    private static  final int HANG_UP =4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walkie);
        ToggleButton but = (ToggleButton)findViewById(R.id.pushToTalk);
        but.setOnTouchListener(this); // Listenes on Touch
        // Intent Filter is used to fire the IncomingCallReceiver
        // when someone tries to call on the sip address used by the application

        IntentFilter filter =new IntentFilter();
        filter.addAction("android.SipDemo.INCOMING_CALL");    ////  #### Important HERE
        // Add a new Intent action to match against
        receiver = new IncomingCallReceiver();
        this.registerReceiver(receiver,filter);


        // Screen on off can cause problems on pushto talk
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        intializeManager();
        if (isPermissionGranted())
        {

        }


    }



    @Override
    public  void onStart()
    {
        super.onStart();
        // When we get back from the preference setting Activity, assume
        // settings have changed, and re-re
        //intializeManager();

    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(call!=null)
        {
            // Call is SIPAudioCall class instance
            call.close();
        }
        closeLocalProfile();
        if(receiver!=null)
        {
            // If receiver is still present make him unregister
            this.unregisterReceiver(receiver);
        }


    }
    public void intializeManager()
    {
        if(manager ==null)
        {
            manager = SipManager.newInstance(this);
        }
        intializeLocalProfile();

    }
    /**   IMPORTANT
     * Logs you into your SIP provider, registering this device as the location to
     * send SIP calls to for your SIP address.
     */
        public void intializeLocalProfile()
        {
            if(manager==null)
            {
                // First need to intilaize the manager
              return;
            }
            if(me!=null)
            {
                // SIPprofile is not null
                // mANAGER IS NOT NULL AND profile is not null
                // Close everything because want to intialize new Profile
                closeLocalProfile();
            }

            // Now storing the new Data
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String username = prefs.getString("namePref", "");
            String domain = prefs.getString("domainPref", "");
            String password = prefs.getString("passPref", "");
            // These is Keys mentioned in the xml file in Preference Activity
            Log.e("SipProfile",username+","+domain+","+password);

            if (username.length() == 0 || domain.length() == 0 || password.length() == 0) {
                // You entered nothing
                showDialog(UPDATE_SETTING_DIALOG);
                return;
            }

            // Received the  New Profile Details :
            try
            {
                //183.82.2.22
                //202.65.140.55
                SipProfile.Builder builder = new SipProfile.Builder("1003","202.65.140.55:5070");
                builder.setPassword("1234abcd");
                builder.setProtocol("UDP");
                builder.setPort(5070);
                builder.setOutboundProxy("202.65.140.55:5070");
                builder.setSendKeepAlive(true);
                builder.setAutoRegistration(true);
                me = builder.build();
                // After Building Profile Send Intent to Incoming Calls
                Intent i =new Intent();
                i.setAction("android.SipDemo.INCOMING_CALL");
                // pARAMETERS for GetBroadCast  : Context context, int requestCode, Intent intent, int flags
                //  fillIn(Intent, int) to allow the current data or type value overwritten, even if it is already set.
                PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, Intent.FILL_IN_DATA);
                // Completed PendingIntent send
                manager.open(me, pi,null);
                manager.register(me, 5*60*1000, new SipRegistrationListener() {
                    @Override
                    public void onRegistering(String s) {
                        Log.e("onRegistering","onRegistering....");
                        updateStatus("Registering with SIP Server....");
                    }

                    @Override
                    public void onRegistrationDone(String s, long l) {
                        Log.e("onRegistering","onRegistrationDone....");
                        updateStatus("Ready");
                    }

                    @Override
                    public void onRegistrationFailed(String s, int i, String errorMessage) {
                        updateStatus("Registeration Failed Please Check settings");
                        Log.e("onRegistering","onRegistrationFailed....");
                        Log.e("errormessage",errorMessage+"********");
                        Log.e("errorcode",i+"");
                        Log.e("localProfileUri",s+"");
                    }
                });
                // me = Local pROFILE
                // pi =  pending Intent
                // null =SIPRegisterationListener
                // Setting Registeration Listerner
                    // getURIString from me Profile
                Log.e("uristring",me.getUriString()+"");
              /*  manager.setRegistrationListener(me.getUriString(), new SipRegistrationListener() {
                    @Override
                    public void onRegistering(String s) {
                        Log.e("onRegistering","onRegistering....");
                        updateStatus("Registering with SIP Server....");
                    }

                    @Override
                    public void onRegistrationDone(String s, long l) {
                        Log.e("onRegistering","onRegistrationDone....");
                        updateStatus("Ready");
                    }

                    @Override
                    public void onRegistrationFailed(String s, int i, String errorMessage) {
                        updateStatus("Registeration Failed Please Check settings");
                        Log.e("onRegistering","onRegistrationFailed....");
                        Log.e("errormessage",errorMessage+"********");
                        Log.e("errorcode",i+"");
                        Log.e("localProfileUri",s+"");
                    }
                });*/
            }catch (ParseException pe)
            {
                pe.printStackTrace();
                updateStatus("Connection Error ParseException Error");
            }
            catch(SipException se)
            {
                se.printStackTrace();
                updateStatus("Conection error SipException error");
            }

        }

    private void updateStatus(final String status)
        {
            this.runOnUiThread(new Runnable() {
                public void run() {
                    TextView labelView = (TextView) findViewById(R.id.walkie);
                    labelView.setText(status);
                }
            });


         }

    public void closeLocalProfile()
        {
            // Unregister your device from server
            if(manager == null)
            {
                return;
                // Nothing to close
            }
            try {
                if (me != null) {
                    // Manger not null and profile also not null
                    manager.close(me.getUriString());
                    Log.e("Walkie/onDestroy", me.getUriString());
                }
            } catch (Exception ee) {
                Toast.makeText(getApplicationContext(),"Failed to close Local Profile", Toast.LENGTH_SHORT).show();
                Log.d("Walkie/onDestroy", "Failed to close local profile.", ee);
            }
        }

    public void intiateCall()
    {
        //Make an outgoing call
        updateStatus(sipAddress);
        try {
            SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                // Much of the client's interaction with the SIP Stack will
                // happen via listeners.  Even making an outgoing call, don't
                // forget to set up a listener to set things up once the call is established.
                @Override
                public void onCallEstablished(SipAudioCall call) {
                    call.startAudio();
                    call.setSpeakerMode(true);
                    //call.toggleMute();
                    updateStatus("onCallEstablished");
                }

                @Override
                public void onCallEnded(SipAudioCall call) {
                    updateStatus("Ready. on Call Ended");
                }
            };

            call = manager.makeAudioCall(me.getUriString(), sipAddress, listener, 30);

        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Error when trying to close manager", Toast.LENGTH_SHORT).show();
            Log.i("Walkie/InitiateCall", "Error when trying to close manager.", e);
            if (me != null) {
                try {
                    manager.close(me.getUriString());
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
    public void updateStatus(SipAudioCall inComingCall) {
        String useName = call.getPeerProfile().getDisplayName();
        if(useName == null)
        {
            useName = call.getPeerProfile().getUserName();
        }
        updateStatus(useName + "@" + call.getPeerProfile().getSipDomain());

    }

    /**
     * Updates whether or not the user's voice is muted, depending on whether the button is pressed.
     * @param v The View where the touch event is being fired.
     * @param event The motion to act on.
     * @return boolean Returns false to indicate that the parent view should handle the touch event
     * as it normally would.
     */
    public boolean onTouch(View v, MotionEvent event) {
        if (call == null) {
            return false;
        } else if (event.getAction() == MotionEvent.ACTION_DOWN && call != null && call.isMuted()) {
            call.toggleMute();
        } else if (event.getAction() == MotionEvent.ACTION_UP && !call.isMuted()) {
            call.toggleMute();
        }
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, CALL_ADDRESS, 0, "Call someone");
        menu.add(0, SET_AUTH_INFO, 0, "Edit your SIP Info.");
        menu.add(0, HANG_UP, 0, "End Current Call.");

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CALL_ADDRESS:
                showDialog(CALL_ADDRESS);
                break;
            case SET_AUTH_INFO:
                updatePreferences();
                break;
            case HANG_UP:
                if(call != null) {
                    try {
                        call.endCall();
                    } catch (SipException se) {
                        Toast.makeText(getApplicationContext(),"Option Error Call Closed", Toast.LENGTH_SHORT).show();
                    }
                    call.close();
                }
                break;
        }
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case CALL_ADDRESS:

                LayoutInflater factory = LayoutInflater.from(this);
                final View textBoxView = factory.inflate(R.layout.call_address_dialog, null);
                final EditText textField = (EditText)textBoxView.findViewById(R.id.callDialog);
                return new AlertDialog.Builder(this)
                        .setTitle("Call Someone.")
                        .setView(textBoxView)
                        .setPositiveButton(
                                android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        sipAddress = textField.getText().toString();
                                       intiateCall();

                                    }
                                })
                        .setNegativeButton(
                                android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // Noop.
                                    }
                                })
                        .create();

            case UPDATE_SETTING_DIALOG:
                return new AlertDialog.Builder(this)
                        .setMessage("Please update your SIP Account Settings.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                updatePreferences();
                            }
                        })
                        .setNegativeButton(
                                android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // Noop.
                                    }
                                })
                        .create();
        }
        return null;
    }

    public  boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.MODIFY_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MODIFY_PHONE_STATE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    intializeManager();
                } else {
                    //Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void updatePreferences() {
        Intent settingsActivity = new Intent(getBaseContext(),
                SipSettings.class);
        startActivity(settingsActivity);
    }
}

