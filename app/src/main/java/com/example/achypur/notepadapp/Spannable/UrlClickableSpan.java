package com.example.achypur.notepadapp.spannable;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

/**
 * Created by achypur on 10.05.2016.
 */
public class UrlClickableSpan extends ClickableSpan{

    Context mContext;
    String mUrl;

    public UrlClickableSpan(Context context, String url) {
        super();
        mContext = context;
        mUrl = url;
    }

    @Override
    public void onClick(View widget) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
        try {
            mContext.startActivity(browserIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mContext, "Application not found", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(Color.BLUE);
    }
}
