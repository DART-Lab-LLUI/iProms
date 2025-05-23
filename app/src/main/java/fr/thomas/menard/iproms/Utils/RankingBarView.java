package fr.thomas.menard.iproms.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Locale;

public class RankingBarView extends View {
    private float[] upperLimits = new float[0];

    private float plotHeight = 40f;
    private float userScore = 0; // Default user score

    private String userText = ""; // Text to be displayed below user score

    private int[] colors = {Color.GREEN, Color.YELLOW, Color.rgb(255, 165, 0), Color.RED}; // Colors for each section
    private String[] texts = {"Low", "Medium", "High", "Very High"}; // Texts for each section

    public RankingBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    /*
    @param upperLimits fractions between 0..6f where each color segment ends
    @param colors       matching color for each segment
    */

    public void setColorSections(float[] upperLimits, int[] colors) {
        // sanity check lengths
        if (upperLimits == null || colors == null || upperLimits.length != colors.length) {
            Log.e("RankingBarView", "setColorSections: invalid arrays");
            return;
        }
        this.upperLimits = upperLimits;
        this.colors = colors;
        invalidate();  // trigger redraw
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();

        float startX = 0;

        if(upperLimits.length>0){
            for (int i = 0; i < upperLimits.length; i++) {
                float endX = (upperLimits[i] / 6f) * getWidth();

                paint.setColor(colors[i]);
                @SuppressLint("DrawAllocation") RectF rectF = new RectF(startX, getHeight() / 2f - plotHeight / 2f, endX, getHeight() / 2f + plotHeight / 2f);
                canvas.drawRoundRect(rectF, 10, 10, paint); // Rayon de 20 pour les coins arrondis

                // Draw text below plot section

                startX = endX;
            }
        }

        // Draw user score indicator
        paint.setColor(Color.BLACK);
        float indicatorX = (userScore / 6f) * getWidth();
        canvas.drawCircle(indicatorX, getHeight() / 2f, 10, paint);

        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        float textWidth = paint.measureText(userText);
        float textLeft = indicatorX - textWidth / 2f;

        if (textLeft < 0) {
            textLeft = 0;
        }
        else if (textLeft + textWidth > getWidth()) {
            textLeft = getWidth() - textWidth;
        }

        canvas.drawText(userText, textLeft, getHeight() / 2f + 50, paint);

    }

    public float rescaleValue(float originalValue, float originalMin, float originalMax, float rankingBarWidth) {

        // New range
        float newMin = 0.0f;
        float newMax = 5.5f;

        // Calculate percentage of original value in original range
        float percentage = (originalValue - originalMin) / (originalMax - originalMin);

        // Map percentage to width of the ranking bar
        float rescaledWidth = percentage * rankingBarWidth;

        // Map width of the ranking bar to the new range
        float rescaledValue = newMin + (rescaledWidth / rankingBarWidth) * (newMax - newMin);

        return rescaledValue;
    }






    // Method to set user's score
    public void setUserScore(float score) {
        this.userScore = score;
        invalidate(); // Trigger redraw
    }

    public void setUserText(String text) {
        this.userText = text;
        invalidate(); // Trigger redraw
    }

}

