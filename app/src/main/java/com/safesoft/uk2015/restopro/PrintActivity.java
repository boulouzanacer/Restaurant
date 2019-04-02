package com.safesoft.uk2015.restopro;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.formation.uk2015.login.R;

import java.io.IOException;
import java.net.UnknownHostException;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

public class PrintActivity extends AppCompatActivity {

    public Pos pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

       /*

        String textToPrint = "Your text here qzztazrtazv a reteyrtrtyu tyrtty rty ryt retyrtyrt rty \n بسم الله الرحمان الرحيم";
        Intent intent = new Intent("pe.diegoveloper.printing");
        //intent.setAction(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_TEXT,textToPrint);
        startActivity(intent);

*/
        new Thread() {
            public void run() {
                print();

            }
        }.start();

    }


    public Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }


    private void print(){



        try {
            pos = new Pos("192.168.1.48", Integer.parseInt("9100"), "arabic");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PrintActivity.this, "connected", Toast.LENGTH_LONG).show();
                }
            });

            pos.initPos();
           // pos.setArabicCodePage();
           // pos.printTestArabic("الحمد لله","arabic");

            /////////////////


            String yourText = "الحمد لله";

            pos.printImage(textAsBitmap(yourText, 100, R.color.white));

            //////////////////
          /*  for(int i = 0; i< 90 ; i++){
                pos.setArabicCodePage(i);
                pos.printTestArabic("الحمد لله","arabic");
             //   pos.printTestArabic("سلاك","UTF8");
              //  pos.printTestArabic("نعم", "UTF8");
            }
          */
            pos.printTextNewLine("-------------------------------------------");
            pos.feedAndCut();

            pos.closeIOAndSocket();
            pos = null;

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
