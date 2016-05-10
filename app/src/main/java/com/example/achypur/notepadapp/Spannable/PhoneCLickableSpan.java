package com.example.achypur.notepadapp.Spannable;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

public class PhoneCLickableSpan extends ClickableSpan {
    Context mContext;
    String mPhone;

    public PhoneCLickableSpan(Context context, String phone) {
        super();
        mContext = context;
        mPhone = phone;
    }

    @Override
    public void onClick(View widget) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog alertDialog = builder.setMessage("Do you really want to call on this number " + mPhone).
                setNegativeButton("NO", null).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext, "Phone call permissions required", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mPhone));
                mContext.startActivity(intent);
            }
        }).create();
        alertDialog.show();
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(Color.BLUE);
    }

}
