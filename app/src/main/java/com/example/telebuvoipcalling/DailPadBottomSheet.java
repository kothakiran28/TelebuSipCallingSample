package com.example.telebuvoipcalling;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class DailPadBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    private  Context context;

    EditText phone_view;
    Button btn_Add;
    View view;
    ImageButton ibBack;
    AddMemberListener addMemberListener;
    public DailPadBottomSheet(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quickdialpad, container, false);
        this.view=view;
        setView();
        return view;
    }

    public  void setAddMemberListener(AddMemberListener addMemberListener){
        this.addMemberListener=addMemberListener;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);// here i have fragment height 30% of window's height you can set it as per your requirement
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


    }
    void setView(){
        phone_view=view.findViewById(R.id.phone_view);
        phone_view.setText("");
        phone_view.setInputType(InputType.TYPE_NULL);
        phone_view.setRawInputType(InputType.TYPE_CLASS_TEXT);
        phone_view.setTextIsSelectable(true);
        setQuickDialPad(view,phone_view,16);
        ibBack=view.findViewById(R.id.ibBack);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);


    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog=(BottomSheetDialog)super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog bottomSheetDialog1 = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet =  bottomSheetDialog1 .findViewById(R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                BottomSheetBehavior.from(bottomSheet).setSkipCollapsed(true);
                BottomSheetBehavior.from(bottomSheet).setHideable(true);
            }
        });
        bottomSheetDialog.setCancelable(false);
        return bottomSheetDialog;
    }
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

           /* case R.id.img_cancel:
                dismiss();
                break;*/

        }
    }


    private void setQuickDialPad(View view, final EditText phone_view, final int MAX_PHONENUMBER_LEN) {
        LinearLayout dialj, dialNum0, dialx, dialNum9, dialNum8, dialNum7, dialNum6, dialNum5, dialNum4, dialNum3, dialNum2, dialNum1;
        TextView txtzero;
        ImageView delete;
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.dialNum0:
                    case R.id.dialNum1:
                    case R.id.dialNum2:
                    case R.id.dialNum3:
                    case R.id.dialNum4:
                    case R.id.dialNum5:
                    case R.id.dialNum6:
                    case R.id.dialNum7:
                    case R.id.dialNum8:
                    case R.id.dialNum9:
                    case R.id.dialx:
                    case R.id.dialj:
                        if (phone_view.getText().length() < MAX_PHONENUMBER_LEN) {

                            input(v.getTag().toString(), phone_view);
                        }
                        if(addMemberListener!=null){
                            //Dtmf value for * - 10 AND # - 11
                            if (phone_view.getText().toString().trim().equalsIgnoreCase("*")){
                                addMemberListener.newNumberAdded("10");

                            }else if (phone_view.getText().toString().trim().equalsIgnoreCase("#")) {
                                addMemberListener.newNumberAdded("11");
                            }else{
                                addMemberListener.newNumberAdded(phone_view.getText().toString().trim());
                            }
                        }
                        Toast.makeText(getActivity(),"sendDtmf"+"  "+phone_view.getText().toString().trim(),Toast.LENGTH_SHORT).show();
                        dismiss();
                        break;
                    case R.id.delete:
                        delete(phone_view);
                        break;
                    default:
                        break;
                }
            }
        };


        delete = view.findViewById(R.id.delete);
        delete.setOnClickListener(clickListener);
        delete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                phone_view.setText("");
                return false;
            }
        });

        txtzero = view.findViewById(R.id.txtzero);
        ViewPressEffectHelper.attach(txtzero);
        dialNum0 = (LinearLayout) view.findViewById(R.id.dialNum0);
        ViewPressEffectHelper.attach(dialNum0);
        dialNum0.setOnClickListener(clickListener);
        dialNum1 = (LinearLayout) view.findViewById(R.id.dialNum1);
        ViewPressEffectHelper.attach(dialNum1);
        dialNum1.setOnClickListener(clickListener);
        dialNum2 = (LinearLayout) view.findViewById(R.id.dialNum2);
        ViewPressEffectHelper.attach(dialNum2);
        dialNum2.setOnClickListener(clickListener);
        dialNum3 = (LinearLayout) view.findViewById(R.id.dialNum3);
        ViewPressEffectHelper.attach(dialNum3);
        dialNum3.setOnClickListener(clickListener);
        dialNum4 = (LinearLayout) view.findViewById(R.id.dialNum4);
        ViewPressEffectHelper.attach(dialNum4);
        dialNum4.setOnClickListener(clickListener);
        dialNum5 = (LinearLayout) view.findViewById(R.id.dialNum5);
        ViewPressEffectHelper.attach(dialNum5);
        dialNum5.setOnClickListener(clickListener);
        dialNum6 = (LinearLayout) view.findViewById(R.id.dialNum6);
        ViewPressEffectHelper.attach(dialNum6);
        dialNum6.setOnClickListener(clickListener);
        dialNum7 = (LinearLayout) view.findViewById(R.id.dialNum7);
        ViewPressEffectHelper.attach(dialNum7);
        dialNum7.setOnClickListener(clickListener);
        dialNum8 = (LinearLayout) view.findViewById(R.id.dialNum8);
        ViewPressEffectHelper.attach(dialNum8);
        dialNum8.setOnClickListener(clickListener);
        dialNum9 = (LinearLayout) view.findViewById(R.id.dialNum9);
        ViewPressEffectHelper.attach(dialNum9);
        dialNum9.setOnClickListener(clickListener);
        dialj = (LinearLayout) view.findViewById(R.id.dialj);
        ViewPressEffectHelper.attach(dialj);
        dialj.setOnClickListener(clickListener);
        dialx = (LinearLayout) view.findViewById(R.id.dialx);
        ViewPressEffectHelper.attach(dialx);
        dialx.setOnClickListener(clickListener);

    }


    private void input(String str, EditText phone_view) {
        String number = phone_view.getText().toString();
        int numberLenth=phone_view.getText().toString().length();
        int pos=phone_view.getSelectionStart();

        if(pos>numberLenth){
            phone_view.setText(number + str);
            phone_view.setSelection(phone_view.getText().length());
        }else{
            String appendnumber=number.substring(0,pos)+str+number.substring(pos, numberLenth);
            phone_view.setText(appendnumber);
            phone_view.setSelection(pos+1);
        }

    }

    private void delete(EditText phone_view) {
        String number = phone_view.getText().toString();
        int numberLenth=phone_view.getText().toString().length();
        int pos=phone_view.getSelectionStart();
        if(number.length()>0){
            if(pos>0){
                if(numberLenth>pos){
                    String num1=number.substring(0, pos-1);
                    String num2=number.substring(pos, numberLenth);
                    phone_view.setText(num1+num2);
                }else{
                    phone_view.setText(number.substring(0, pos-1));
                }
                try{
                    if(pos>0){
                        phone_view.setSelection(pos-1);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }


            }

        }else{
            phone_view.setSelection(0);
        }
    }

    public interface AddMemberListener{

        void newNumberAdded(String mobileNumber);
    }
}
