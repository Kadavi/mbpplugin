
package org.schoolsfirstfcu.mobile.plugin;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import static android.hardware.Camera.Parameters.FLASH_MODE_OFF;
import static android.hardware.Camera.Parameters.FOCUS_MODE_AUTO;
import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;

public class CameraActivity extends Activity {

	private static final String TAG = CameraActivity.class.getSimpleName();
	private static final float ASPECT_RATIO = 126.0f / 86;

	public static String TITLE = "Title";
	public static String QUALITY = "Quality";
	public static String TARGET_WIDTH = "TargetWidth";
	public static String TARGET_HEIGHT = "TargetHeight";
	public static String IMAGE_DATA = "ImageData";
	public static String ERROR_MESSAGE = "ErrorMessage";
	public static int RESULT_ERROR = 2;

	private Camera camera;
	private RelativeLayout layout;
	private FrameLayout cameraPreviewView;
    private ImageView logo;
	private ImageView borderTopLeft;
	private ImageView borderTopRight;
	private ImageView borderBottomLeft;
	private ImageView borderBottomRight;
	private ImageButton captureButton;

	@Override
	protected void onResume() {
		super.onResume();
		try {
			camera = Camera.open();
			configureCamera();
			displayCameraPreview();
		} catch (Exception e) {
			finishWithError("Camera is not accessible");
		}
	}

	private void configureCamera() {
		Camera.Parameters cameraSettings = camera.getParameters();
		cameraSettings.setJpegQuality(100);
		List<String> supportedFocusModes = cameraSettings.getSupportedFocusModes();
		if (supportedFocusModes.contains(FOCUS_MODE_CONTINUOUS_PICTURE)) {
			cameraSettings.setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE);
		} else if (supportedFocusModes.contains(FOCUS_MODE_AUTO)) {
			cameraSettings.setFocusMode(FOCUS_MODE_AUTO);
		}
		cameraSettings.setFlashMode(FLASH_MODE_OFF);
		camera.setParameters(cameraSettings);
	}

	private void displayCameraPreview() {
		cameraPreviewView.removeAllViews();
		cameraPreviewView.addView(new CameraPreview(this, camera));
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();
	}

	private void releaseCamera() {
		if (camera != null) {
			camera.stopPreview();
			camera.release();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		layout = new RelativeLayout(this);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layout.setLayoutParams(layoutParams);
		createCameraPreview();
		//createTopLeftBorder();
		//createTopRightBorder();
		//createBottomLeftBorder();
		//createBottomRightBorder();
		//layoutBottomBorderImagesRespectingAspectRatio();
        createBackground();
		createHeader();


		createCaptureButton();
		setContentView(layout);
	}

	private void createCameraPreview() {
		cameraPreviewView = new FrameLayout(this);
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		cameraPreviewView.setLayoutParams(layoutParams);
		layout.addView(cameraPreviewView);
		layout.addView(new TransparentCenterView(this));
	}

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void createHeader() {

        layout.addView(new HeaderView(this));

        logo = new ImageView(this);
        logo.setScaleType(ScaleType.FIT_XY);
        setBitmap(logo, "sf-logo.png");
        RelativeLayout.LayoutParams layoutParams;

        float density = getResources().getDisplayMetrics().density;

        if (density <= 1.5) {
            layoutParams = new RelativeLayout.LayoutParams(dpToPixels(40), dpToPixels(282));
        } else {
            layoutParams = new RelativeLayout.LayoutParams(dpToPixels(20), dpToPixels(141));
        }

        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        layoutParams.rightMargin = 15;

        logo.setLayoutParams(layoutParams);
        layout.addView(logo);


    }

	private void createBackground() {


		layout.addView(new TransparentCenterView(this));


	}

    public class HeaderView extends View {


        public HeaderView(Context context) {
            super(context);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void draw(Canvas canvas) {

            Display display = getWindowManager().getDefaultDisplay();

            Point size = new Point();
            display.getRealSize(size);

            Path rect = new Path();
            rect.addRect(size.x-90, 0, size.x, size.y, Path.Direction.CW);

            Paint cpaint = new Paint();
            cpaint.setARGB(255, 45, 68, 82);
            cpaint.setAlpha(255);
            cpaint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawPath(rect, cpaint);





        }

    }

	public class TransparentCenterView extends View {


		public TransparentCenterView(Context context) {
			super(context);
		}

		@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
		@Override
		public void draw(Canvas canvas) {

			canvas.translate(-50, -50);

			Display display = getWindowManager().getDefaultDisplay();

			Point size = new Point();
			display.getRealSize(size);

			Path rect = new Path();
			rect.addRect(0, 0, size.x, size.y, Path.Direction.CW);

			Paint cpaint = new Paint();
			cpaint.setARGB(255, 185, 199, 212);
			cpaint.setAlpha(150);
			cpaint.setStyle(Paint.Style.STROKE);
			cpaint.setStrokeWidth(220);
			canvas.drawPath(rect, cpaint);
			canvas.rotate(90);
			canvas.translate(0, -size.x);


            Paint paint = new Paint();
			paint.setARGB(255, 0, 0, 0);
			paint.setAlpha(200);
			paint.setStyle(Paint.Style.FILL);
			paint.setTextSize(38);
            paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			canvas.drawText(getIntent().getStringExtra(TITLE), 110, 85, paint);
		}

	}

    private void createCaptureButton() {
        captureButton = new ImageButton(getApplicationContext());
        setBitmap(captureButton, "capture_button_dark.png");
        captureButton.setBackgroundColor(Color.TRANSPARENT);
        RelativeLayout.LayoutParams layoutParams = null;

        float density = getResources().getDisplayMetrics().density;

        if (density <= 1.5) {
            layoutParams = new RelativeLayout.LayoutParams(dpToPixels(90), dpToPixels(90));
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams.bottomMargin = dpToPixels(11);
            layoutParams.rightMargin = dpToPixels(215);
        } else {
            layoutParams = new RelativeLayout.LayoutParams(dpToPixels(50), dpToPixels(50));
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams.rightMargin = dpToPixels(158);
        }

        captureButton.setLayoutParams(layoutParams);
        captureButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setCaptureButtonImageForEvent(event);
                return false;
            }
        });
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePictureWithAutoFocus();
            }
        });
        layout.addView(captureButton);
    }


	private void createTopLeftBorder() {
		borderTopLeft = new ImageView(this);
		setBitmap(borderTopLeft, "border_top_left.png");
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dpToPixels(50), dpToPixels(50));
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		if (isXLargeScreen()) {
			layoutParams.topMargin = dpToPixels(100);
			layoutParams.leftMargin = dpToPixels(100);
		} else if (isLargeScreen()) {
			layoutParams.topMargin = dpToPixels(50);
			layoutParams.leftMargin = dpToPixels(50);
		} else {
			layoutParams.topMargin = dpToPixels(10);
			layoutParams.leftMargin = dpToPixels(10);
		}
		borderTopLeft.setLayoutParams(layoutParams);
		layout.addView(borderTopLeft);
	}

	private void createTopRightBorder() {
		borderTopRight = new ImageView(this);
		setBitmap(borderTopRight, "border_top_right.png");
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dpToPixels(50), dpToPixels(50));
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		if (isXLargeScreen()) {
			layoutParams.topMargin = dpToPixels(100);
			layoutParams.rightMargin = dpToPixels(100);
		} else if (isLargeScreen()) {
			layoutParams.topMargin = dpToPixels(50);
			layoutParams.rightMargin = dpToPixels(50);
		} else {
			layoutParams.topMargin = dpToPixels(10);
			layoutParams.rightMargin = dpToPixels(10);
		}
		borderTopRight.setLayoutParams(layoutParams);
		layout.addView(borderTopRight);
	}

	private void createBottomLeftBorder() {
		borderBottomLeft = new ImageView(this);
		setBitmap(borderBottomLeft, "border_bottom_left.png");
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dpToPixels(50), dpToPixels(50));
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		if (isXLargeScreen()) {
			layoutParams.leftMargin = dpToPixels(100);
		} else if (isLargeScreen()) {
			layoutParams.leftMargin = dpToPixels(50);
		} else {
			layoutParams.leftMargin = dpToPixels(10);
		}
		borderBottomLeft.setLayoutParams(layoutParams);
		layout.addView(borderBottomLeft);
	}

	private void createBottomRightBorder() {
		borderBottomRight = new ImageView(this);
		setBitmap(borderBottomRight, "border_bottom_right.png");
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dpToPixels(50), dpToPixels(50));
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		if (isXLargeScreen()) {
			layoutParams.rightMargin = dpToPixels(100);
		} else if (isLargeScreen()) {
			layoutParams.rightMargin = dpToPixels(50);
		} else {
			layoutParams.rightMargin = dpToPixels(10);
		}
		borderBottomRight.setLayoutParams(layoutParams);
		layout.addView(borderBottomRight);
	}

	private void layoutBottomBorderImagesRespectingAspectRatio() {
		RelativeLayout.LayoutParams borderTopLeftLayoutParams = (RelativeLayout.LayoutParams)borderTopLeft.getLayoutParams();
		RelativeLayout.LayoutParams borderTopRightLayoutParams = (RelativeLayout.LayoutParams)borderTopRight.getLayoutParams();
		RelativeLayout.LayoutParams borderBottomLeftLayoutParams = (RelativeLayout.LayoutParams)borderBottomLeft.getLayoutParams();
		RelativeLayout.LayoutParams borderBottomRightLayoutParams = (RelativeLayout.LayoutParams)borderBottomRight.getLayoutParams();
		float height = (screenWidthInPixels() - borderTopRightLayoutParams.rightMargin - borderTopLeftLayoutParams.leftMargin) * ASPECT_RATIO;
		borderBottomLeftLayoutParams.bottomMargin = screenHeightInPixels() - Math.round(height) - borderTopLeftLayoutParams.topMargin;
		borderBottomLeft.setLayoutParams(borderBottomLeftLayoutParams);
		borderBottomRightLayoutParams.bottomMargin = screenHeightInPixels() - Math.round(height) - borderTopRightLayoutParams.topMargin;
		borderBottomRight.setLayoutParams(borderBottomRightLayoutParams);
	}

	private int screenWidthInPixels() {
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		return size.x;
	}

	private int screenHeightInPixels() {
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		return size.y;
	}

	private void setCaptureButtonImageForEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			setBitmap(captureButton, "capture_button_pressed.png");
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			setBitmap(captureButton, "capture_button_dark.png");
		}
	}

	private void takePictureWithAutoFocus() {
		if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
			camera.autoFocus(new AutoFocusCallback() {
				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					takePicture();
				}
			});
		} else {
			takePicture();
		}
	}

	private void takePicture() {
		try {
			camera.takePicture(null, null, new PictureCallback() {
				@Override
				public void onPictureTaken(byte[] jpegData, Camera camera) {
					new OutputCapturedImageTask().execute(jpegData);
				}
			});
		} catch (Exception e) {
			finishWithError("Failed to take image");
		}
	}

	private class OutputCapturedImageTask extends AsyncTask<byte[], Void, Void> {

		@Override
		protected Void doInBackground(byte[]... jpegData) {
			try {
				/*String filename = getIntent().getStringExtra(FILENAME);
				int quality = getIntent().getIntExtra(QUALITY, 80);
				File capturedImageFile = new File(getCacheDir(), filename);
				Bitmap capturedImage = getScaledBitmap(jpegData[0]);
				capturedImage = correctCaptureImageOrientation(capturedImage);
				capturedImage.compress(CompressFormat.JPEG, quality, new FileOutputStream(capturedImageFile));
				*/

				String imageData = Base64.encodeToString(jpegData[0], Base64.DEFAULT);

				Intent data = new Intent();
				data.putExtra(IMAGE_DATA, imageData);

				setResult(RESULT_OK, data);
				finish();
			} catch (Exception e) {
				finishWithError("Failed to save image");
			}
			return null;
		}

	}

	private Bitmap getScaledBitmap(byte[] jpegData) {
		int targetWidth = getIntent().getIntExtra(TARGET_WIDTH, -1);
		int targetHeight = getIntent().getIntExtra(TARGET_HEIGHT, -1);
		if (targetWidth <= 0 && targetHeight <= 0) {
			return BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length);
		}

		// get dimensions of image without scaling
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length, options);

		// decode image as close to requested scale as possible
		options.inJustDecodeBounds = false;
		options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);
		Bitmap bitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length, options);

		// set missing width/height based on aspect ratio
		float aspectRatio = ((float)options.outHeight) / options.outWidth;
		if (targetWidth > 0 && targetHeight <= 0) {
			targetHeight = Math.round(targetWidth * aspectRatio);
		} else if (targetWidth <= 0 && targetHeight > 0) {
			targetWidth = Math.round(targetHeight / aspectRatio);
		}

		// make sure we also
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
	}

	private int calculateInSampleSize(BitmapFactory.Options options, int requestedWidth, int requestedHeight) {
		int originalHeight = options.outHeight;
		int originalWidth = options.outWidth;
		int inSampleSize = 1;
		if (originalHeight > requestedHeight || originalWidth > requestedWidth) {
			int halfHeight = originalHeight / 2;
			int halfWidth = originalWidth / 2;
			while ((halfHeight / inSampleSize) > requestedHeight && (halfWidth / inSampleSize) > requestedWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}

	private Bitmap correctCaptureImageOrientation(Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	private void finishWithError(String message) {
		Intent data = new Intent().putExtra(ERROR_MESSAGE, message);
		setResult(RESULT_ERROR, data);
		finish();
	}

	private int dpToPixels(int dp) {
		float density = getResources().getDisplayMetrics().density;
		return Math.round(dp * density);
	}

	private boolean isXLargeScreen() {
		int screenLayout = getResources().getConfiguration().screenLayout;
		return (screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	private boolean isLargeScreen() {
		int screenLayout = getResources().getConfiguration().screenLayout;
		return (screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	private void setBitmap(ImageView imageView, String imageName) {
		try {
			InputStream imageStream = getAssets().open("www/img/" + imageName);
			Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
			imageView.setImageBitmap(bitmap);
			imageStream.close();
		} catch (Exception e) {
			Log.e(TAG, "Could load image", e);
		}
	}

}