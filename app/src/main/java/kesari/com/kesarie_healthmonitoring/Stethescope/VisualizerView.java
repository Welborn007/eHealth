package kesari.com.kesarie_healthmonitoring.Stethescope;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;

import kesari.com.kesarie_healthmonitoring.R;

public class VisualizerView extends View {
    private static int MAX_AMPLITUDE = 8192;
    private float[] amplitudes;
    public boolean clear = false;
    private int height;
    private int insertIdx = 0;
    private Paint linePaint = new Paint();
    private Paint pointPaint;
    private float[] vectors;
    private int width;

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.linePaint.setColor(getResources().getColor(R.color.colorAccent));
        this.linePaint.setStrokeWidth(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        this.pointPaint = new Paint();
        this.pointPaint.setColor(-16776961);
        this.pointPaint.setStrokeWidth(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    protected void onSizeChanged(int width, int h, int oldw, int oldh) {
        this.width = width;
        this.height = h;
        this.amplitudes = new float[(this.width * 2)];
        this.vectors = new float[(this.width * 4)];
    }

    public void addAmplitude(int amplitude) {
        invalidate();
        float centerHeight = (float) (this.height / 2);
        float scaledHeight = (((float) amplitude) / ((float) MAX_AMPLITUDE)) * (centerHeight - DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        Log.d("AMP", "before: " + scaledHeight);
        int i = this.insertIdx * 2;
        int ampIdx = i + 1;
        this.amplitudes[i] = (float) this.insertIdx;
        this.amplitudes[ampIdx] = scaledHeight;
        int i2 = this.insertIdx * 4;
        int vectorIdx = i2 + 1;
        this.vectors[i2] = (float) this.insertIdx;
        i2 = vectorIdx + 1;
        this.vectors[vectorIdx] = centerHeight;
        vectorIdx = i2 + 1;
        this.vectors[i2] = (float) this.insertIdx;
        this.vectors[vectorIdx] = centerHeight - scaledHeight;
        this.insertIdx++;
        if (this.insertIdx >= this.width) {
            this.amplitudes = new float[(this.width * 2)];
            this.vectors = new float[(this.width * 4)];
            this.insertIdx = 0;
        }
    }

    public void onDraw(Canvas canvas) {
        if (this.insertIdx == 1 || this.clear) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            this.clear = false;
        }
        canvas.drawLines(this.vectors, this.linePaint);
        canvas.drawPoints(this.amplitudes, this.pointPaint);
    }
}