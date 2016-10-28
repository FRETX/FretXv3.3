package fretx.version3;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Arrays;

import rocks.fretx.audioprocessing.Chord;

// Tuner View
public class TunerView extends RelativeLayout
{
	private MainActivity mActivity;
    //protected Audio audio;
    protected Resources resources;
	private float[] tuning = {82.41f, 110.00f, 146.83f, 196.00f, 246.94f, 329.63f};
	private String[] tuningNames = {"E","A","D","G","B","E"};
	private String[] allNotes = {"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "G", "G#"};
	//TODO: to be expanded for non-standard tunings and gStrings-like interaction (which needs a layout overhaul)
    protected int width;
    protected int height;

	public void setmActivity(MainActivity mActivity){
		this.mActivity = mActivity;
	}
    // Constructor
    public TunerView(Context context, AttributeSet attrs)
    {
	super(context, attrs);
		resources = getResources();
	    this.setWillNotDraw(false);
	    invalidate();
    }

    // On Size Changed
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
	// Save the new width and height
	width = w;
	height = h;
    }

    // On Draw
    @Override
    protected void onDraw(Canvas canvas)
    {
	    TextView centerNote = (TextView) findViewById(R.id.textTunerCenterNote);
	    TextView leftNote = (TextView) findViewById(R.id.textTunerLeftNote);
	    TextView rightNote = (TextView) findViewById(R.id.textTunerRightNote);
	    TextView pitchText = (TextView) findViewById(R.id.textTunerPitch);
	    TextView pitchDifferenceText = (TextView) findViewById(R.id.textPitchDifference);
//		TextView indicatorMinus20 = (TextView) findViewById(R.id.textIndicatorMinus20);
	    TextView indicatorMinus40 = (TextView) findViewById(R.id.textIndicatorMinus40);
//	    TextView indicatorPlus20 = (TextView) findViewById(R.id.textIndicatorPlus20);
	    TextView indicatorPlus40 = (TextView) findViewById(R.id.textIndicatorPlus40);
	    RelativeLayout tunerContainer = (RelativeLayout) findViewById(R.id.relativeLayoutTunerMeter);
//	    RotatableImageView tunerHand = (RotatableImageView) findViewById(R.id.tunerHand);

	    float pitch = mActivity.audio.getPitch();

	    if(pitch > -1){
		    float[] differences = tuning.clone();
		    for (int i = 0; i < differences.length; i++) {
			    differences[i] -= pitch;
			    differences[i] = Math.abs(differences[i]);
		    }
		    int minIndex = getMinIndex(differences);
//		    Log.d("differences", differences.toString());

		    centerNote.setText(tuningNames[minIndex]);
		    int noteIndex = Arrays.asList(allNotes).indexOf(tuningNames[minIndex]);
		    int prevNoteIndex = noteIndex - 1;
		    int nextNoteIndex = noteIndex + 1;
		    if (noteIndex == 0) prevNoteIndex = allNotes.length - 1;
		    if (noteIndex == allNotes.length - 1) nextNoteIndex = 0;
//		    Log.d("prevNoteIndex", Integer.toString(prevNoteIndex));
//		    Log.d("nextNoteIndex", Integer.toString(nextNoteIndex));
		    leftNote.setText(allNotes[prevNoteIndex]);
		    rightNote.setText(allNotes[nextNoteIndex]);
		    pitchText.setText(Integer.toString((int) Math.round(pitch)) + " Hz");

		    double currentPitchInCents = (1200 * Math.log(pitch) / Math.log(2));
		    double centerPitchInCents = (1200 * Math.log(tuning[minIndex]) / Math.log(2));
		    double pitchDifference = centerPitchInCents - currentPitchInCents;

		    pitchDifferenceText.setText(Double.toString(Math.round(pitchDifference)));

		    double totalRangeInCents = 80;
		    double halfRangeInCents = totalRangeInCents / 2;

		    //Angle calculation
		    int x = indicatorMinus40.getLeft();
		    int y = tunerContainer.getHeight() - indicatorMinus40.getTop();
		    double alpha = Math.atan2( (double) y , (double)( tunerContainer.getWidth()/2 - x ) );
		    alpha = Math.toDegrees(alpha);
		    double totalAngleRange = 180 - 2*alpha;

		    double targetAngle;
		    if(pitchDifference > 0){
			    if(pitchDifference > 40){
				    targetAngle = 90 + alpha;
			    } else {
				    targetAngle = 90 + Math.abs(pitchDifference) * totalAngleRange / totalRangeInCents;
			    }

		    } else {
			    if(pitchDifference < -40){
				    targetAngle = alpha;
			    } else {
				    targetAngle = 90 - Math.abs(pitchDifference) * totalAngleRange / totalRangeInCents;
			    }

		    }


////		    tunerHand.setAngle( (int) Math.round(targetAngle) );
//		    ImageView imageView = (ImageView) findViewById(R.id.tunerHand);
////		    Bitmap myImg = BitmapFactory.decodeResource(getResources(), R.drawable.agujaboton2);
//
//		    Matrix matrix = new Matrix();
////		    imageView.setScaleType(ImageView.ScaleType.MATRIX);   //required
//		    matrix.postRotate((float) targetAngle, tunerContainer.getWidth()/2, tunerContainer.getHeight());
//		    imageView.setImageMatrix(matrix);
////		    imageView.setRotation((float) targetAngle);

		    targetAngle = Math.toRadians(targetAngle);
		    //reverse direction to match the left-to-right increasing frequency
		    targetAngle *= -1;

		    Paint paint = new Paint();
		    if (Math.abs(pitchDifference) < 10) {
			    paint.setStrokeWidth(6.0f);
			    paint.setColor(Color.GREEN);
		    } else {
			    paint.setStrokeWidth(8.0f);
			    paint.setColor(Color.RED);
		    }

		    canvas.drawLine(width/2, height,
				    tunerContainer.getWidth() / 2 + (float) Math.sin(targetAngle) * y,
				    tunerContainer.getHeight() - (float) Math.cos(Math.abs(targetAngle)) * y, paint);


	    } else {
		    pitchText.setText("");
		    pitchDifferenceText.setText("");
	    }




//	    Log.d("TunerView", Float.toString(mActivity.audio.getPitch()));
	    invalidate();
//	    requestLayout();

	// Set up the paint and draw the outline
//	paint.setStrokeWidth(3);
//	paint.setAntiAlias(true);
//	paint.setColor(resources.getColor(android.R.color.darker_gray));
//	paint.setStyle(Style.STROKE);
//	canvas.drawRoundRect(outlineRect, 10, 10, paint);
//
//	// Set the cliprect
//	canvas.clipRect(clipRect);
//
//	// Translate to the clip rect
//	canvas.translate(clipRect.left, clipRect.top);
    }

	//Utility
	private static int getMinIndex(float[] array) {
		float minValue = Float.MAX_VALUE;
		int minIndex = -1;
		for (int i = 0; i < array.length; i++) {
			if (array[i] < minValue) {
				minValue = array[i];
				minIndex = i;
			}
		}
		return minIndex;
	}
}
