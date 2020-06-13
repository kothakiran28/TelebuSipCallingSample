package com.example.telebuvoipcalling;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipProfile;
import android.telephony.TelephonyManager;
import android.util.Log;

public class IncomingCallReceiver extends BroadcastReceiver {
    // Process the incoming call answers it and hands over it to walkie Activity
    // SipAudioCall handles an internet call over Sip

    @Override
    public void onReceive(Context context, Intent intent) {
       Intent intent1=new Intent(context,IncomingCallActivity.class);
       intent1.putExtra("intent",intent);
       context.startActivity(intent1);
    }
}


