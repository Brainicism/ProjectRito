package com.brainicism.projectrito;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.type.core.common.Region;
import com.robrua.orianna.type.exception.APIException;


public class AboutPage extends AppCompatActivity {
    public static String c = "Smi Uonoda";
    public static String c1 = "Hoizumi Kanayo";
    public static String c2 = "Nukerss";
    public static String c3 = "Mishikino Naki";
    public static String c4 = "Honger";
    public static String c5 = "MarkVion";
    public static String c6 = "TheTrueCarry";
    public static String c7 = "HeilHongers";
    public static String c8 = "Kirishima Touka";
    public static String c9 = "ArkaREEEEE poggers";
    public static boolean firstRun = false;
    public static final String TAG = AboutPage.class.getName();
    public static int numCon;
    public static SpannableString ss;
    public static ClickableSpan[] contributorClickSpan = new ClickableSpan[10];
    public static TextView oriannaCon, picassoCon, gifCon, gsonCon;
    public static String span;
    public static TextView contributorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);
        RiotAPI.setRegion(Region.NA);
        oriannaCon = (TextView) findViewById(R.id.oriannaCon);
        picassoCon = (TextView) findViewById(R.id.picassoCon);
        gifCon = (TextView) findViewById(R.id.gifCon);
        gsonCon = (TextView) findViewById(R.id.gsonCon);
        numCon = 0;
        oriannaCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), InternalWebView.class);
                intent.putExtra("URL", "https://github.com/robrua/Orianna");
                startActivity(intent);
            }
        });
        picassoCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), InternalWebView.class);
                intent.putExtra("URL", "http://square.github.io/picasso/");
                startActivity(intent);
            }
        });
        gifCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), InternalWebView.class);
                intent.putExtra("URL", "https://github.com/koral--/android-gif-drawable");
                startActivity(intent);
            }
        });
        gsonCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), InternalWebView.class);
                intent.putExtra("URL", "https://github.com/google/gson");
                startActivity(intent);
            }
        });

        span = "Developed and maintained by \n " + c + " \n Painstakingly bug-tested by  \n " + c1 + " \n Special thanks to  \n " + c2 + " \n " + c3 + " \n " + c4 + " \n " + c5 + " \n " + c6 + " \n " + c7 + " \n " + c8 + " \n " + c9;
        ss = new SpannableString(span);

        addClickableSpan(44492014, getApplicationContext());
        addClickableSpan(45355869, getApplicationContext());
        addClickableSpan(39537280, getApplicationContext());
        addClickableSpan(40668440, getApplicationContext());
        addClickableSpan(34307058, getApplicationContext());
        addClickableSpan(36777777, getApplicationContext());
        addClickableSpan(30213439, getApplicationContext());
        addClickableSpan(52401618, getApplicationContext());
        addClickableSpan(36814870, getApplicationContext());
        addClickableSpan(87763368, getApplicationContext());

        ss.setSpan(contributorClickSpan[0], span.indexOf(c), span.indexOf(c) + c.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(contributorClickSpan[1], span.indexOf(c1), span.indexOf(c1) + c1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(contributorClickSpan[2], span.indexOf(c2), span.indexOf(c2) + c2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(contributorClickSpan[3], span.indexOf(c3), span.indexOf(c3) + c3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(contributorClickSpan[4], span.indexOf(c4), span.indexOf(c4) + c4.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(contributorClickSpan[5], span.indexOf(c5), span.indexOf(c5) + c5.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(contributorClickSpan[6], span.indexOf(c6), span.indexOf(c6) + c6.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(contributorClickSpan[7], span.indexOf(c7), span.indexOf(c7) + c7.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(contributorClickSpan[8], span.indexOf(c8), span.indexOf(c8) + c8.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(contributorClickSpan[9], span.indexOf(c9), span.indexOf(c9) + c9.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        contributorText = (TextView) findViewById(R.id.contributionsText);
        contributorText.setTextSize(17);
        contributorText.setText(ss);
        contributorText.setLinkTextColor(Color.parseColor("#0645AD"));
        contributorText.setMovementMethod(LinkMovementMethod.getInstance());
        contributorText.setHighlightColor(Color.TRANSPARENT);
        if (!firstRun) { //only recheck names if not run
            UpdateContributor check = new UpdateContributor();
            check.execute();
            firstRun = true;
        }
    }

    public static void addClickableSpan(final int sumID, final Context mContext) {
        contributorClickSpan[numCon] = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent(mContext, InternalWebView.class);
                intent.putExtra("URL", "http://www.lolking.net/summoner/na/" + sumID + "#matches");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        numCon++;
    }

    class UpdateContributor extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                c = RiotAPI.getSummonerName(44492014);
                c1 = RiotAPI.getSummonerName(45355869);
                c2 = RiotAPI.getSummonerName(39537280);
                c3 = RiotAPI.getSummonerName(40668440);
                c4 = RiotAPI.getSummonerName(34307058);
                c5 = RiotAPI.getSummonerName(36777777);
                c6 = RiotAPI.getSummonerName(30213439);
                c7 = RiotAPI.getSummonerName(52401618);
                c8 = RiotAPI.getSummonerName(36814870);
                c9 = RiotAPI.getSummonerName(87763368);
            } catch (APIException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            span = "Developed and maintained by \n " + c + " \n Painstakingly bug-tested by  \n " + c1 + " \n Special thanks to  \n " + c2 + " \n " + c3 + " \n " + c4 + " \n " + c5 + " \n " + c6 + " \n " + c7 + " \n " + c8 + " \n " + c9;
            ss = new SpannableString(span);
            contributorText.setLinkTextColor(Color.parseColor("#3366BB"));

            ss.setSpan(contributorClickSpan[0], span.indexOf(c), span.indexOf(c) + c.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(contributorClickSpan[1], span.indexOf(c1), span.indexOf(c1) + c1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(contributorClickSpan[2], span.indexOf(c2), span.indexOf(c2) + c2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(contributorClickSpan[3], span.indexOf(c3), span.indexOf(c3) + c3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(contributorClickSpan[4], span.indexOf(c4), span.indexOf(c4) + c4.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(contributorClickSpan[5], span.indexOf(c5), span.indexOf(c5) + c5.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(contributorClickSpan[6], span.indexOf(c6), span.indexOf(c6) + c6.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(contributorClickSpan[7], span.indexOf(c7), span.indexOf(c7) + c7.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(contributorClickSpan[8], span.indexOf(c8), span.indexOf(c8) + c8.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(contributorClickSpan[9], span.indexOf(c9), span.indexOf(c9) + c9.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            contributorText = (TextView) findViewById(R.id.contributionsText);
            contributorText.setText(ss);
            contributorText.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}


