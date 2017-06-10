package com.tphien.midproject1412171.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.EmotionServiceRestClient;
import com.microsoft.projectoxford.emotion.contract.RecognizeResult;
import com.microsoft.projectoxford.emotion.rest.EmotionServiceException;
import com.tphien.midproject1412171.AnimationView;
import com.tphien.midproject1412171.R;
import com.tphien.midproject1412171.tool.ImageHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ShareFragment extends Fragment {
    private static Context context;
    // Flag to indicate which task is to be performed.
    private static final int REQUEST_SELECT_IMAGE = 0;
    private static View view;
    private TextView tvAnger, tvHappiness, tvSadness, tvContempt, tvDisgust, tvFear, tvSupprise, tvNeutral;

    // The button to share an image
    private Button mButtonShareImage;

    // The URI of the image selected to detect.
    private Uri mImageUri;

    // The image selected to detect.
    private Bitmap mBitmap;

    // The edit to show status and result.
    private TextView mEditText;

    private EmotionServiceClient client;

    FloatingActionButton fab;
    FloatingActionButton fab1;
    FloatingActionButton fab3;

    //Save the FAB's active status
    //false -> fab = close
    //true -> fab = open
    private boolean FAB_Status = false;

    //Animations
    Animation show_fab_1;
    Animation hide_fab_1;
    Animation show_fab_3;
    Animation hide_fab_3;
    AnimationView anim_view;

    // Flag to indicate the request of the next task to be performed
    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_SELECT_IMAGE_IN_ALBUM = 1;

    // The URI of photo taken with camera
    private Uri mUriPhotoTaken;

    public ShareFragment(Context context) {
        ShareFragment.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getString(R.string.subscription_key).startsWith("Please")) {
            new AlertDialog.Builder(context)
                    .setTitle(getString(R.string.add_subscription_key_tip_title))
                    .setMessage(getString(R.string.add_subscription_key_tip))
                    .setCancelable(false)
                    .show();
            return;
        }

        if (client == null) {
            client = new EmotionServiceRestClient(getString(R.string.subscription_key));
        }

    }

    private void initFloatingBtn() {
        //Floating Action Buttons
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab1 = (FloatingActionButton) view.findViewById(R.id.fab_1);
        fab3 = (FloatingActionButton) view.findViewById(R.id.fab_3);

        //Animations
        show_fab_1 = AnimationUtils.loadAnimation(getActivity().getApplication(), R.anim.fab1_show);
        hide_fab_1 = AnimationUtils.loadAnimation(getActivity().getApplication(), R.anim.fab1_hide);
        show_fab_3 = AnimationUtils.loadAnimation(getActivity().getApplication(), R.anim.fab3_show);
        hide_fab_3 = AnimationUtils.loadAnimation(getActivity().getApplication(), R.anim.fab3_hide);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!FAB_Status) {
                    //Display FAB menu
                    expandFAB();
                    FAB_Status = true;
                } else {
                    //Close FAB menu
                    hideFAB();
                    FAB_Status = false;
                }
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity().getApplication(), "Floating Action Button 1", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Save the photo taken to a temporary file.
                    File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    try {
                        File file = File.createTempFile("IMG_", ".jpg", storageDir);
                        mUriPhotoTaken = Uri.fromFile(file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriPhotoTaken);
                        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                    } catch (IOException e) {
                        Log.d("camera error", e.toString());
                    }
                }
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity().getApplication(), "Floating Action Button 3", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_share, container, false);

        mButtonShareImage = (Button)view.findViewById(R.id.buttonShareImage);
        mEditText = (TextView)view.findViewById(R.id.editTextResult);
        tvAnger = (TextView)view.findViewById(R.id.tv_anger);
        tvHappiness = (TextView)view.findViewById(R.id.tv_happiness);
        tvSadness = (TextView)view.findViewById(R.id.tv_sadness);
        tvContempt = (TextView)view.findViewById(R.id.tv_contempt);
        tvDisgust = (TextView)view.findViewById(R.id.tv_disgust);
        tvNeutral = (TextView)view.findViewById(R.id.tv_neutral);
        tvSupprise = (TextView)view.findViewById(R.id.tv_surprise);
        tvFear = (TextView)view.findViewById(R.id.tv_fear);

        mButtonShareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName = saveBitmap(mBitmap);
                assert fileName != null;
                File file = new File(fileName);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                startActivity(intent);
            }
        });
        mEditText.setText("Select an image to analyze");
        initFloatingBtn();

        // Get AnimationView reference see animation_main.xml
        anim_view = (AnimationView) view.findViewById(R.id.anim_view);
        anim_view.loadAnimation("spark", 18,0,0);

        // Inflate the layout for this fragment
        return view;
    }

    public void doRecognize() {
        fab.setEnabled(false);
        // Do emotion detection using auto-detected faces.
        try {
            mEditText.setText("\nAnalyst...\n");
            new doRequest().execute();
        } catch (Exception e) {
            mEditText.setText("Error encountered. Exception is: " + e.toString());
        }
    }

    // Called when image selection is done.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
            case REQUEST_SELECT_IMAGE_IN_ALBUM:
                if (resultCode == RESULT_OK) {
                    if (data == null || data.getData() == null) {
                        mImageUri = mUriPhotoTaken;
                    } else {
                        mImageUri = data.getData();
                    }

                    mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            mImageUri, getActivity().getContentResolver());
                    if (mBitmap != null) {
                        // Show the image on screen.
                        ImageView imageView = (ImageView)view.findViewById(R.id.selectedImage);
                        imageView.setImageBitmap(mBitmap);

                        // Add detection log.
                        Log.d("AnalyzeActivity", "Image: " + mImageUri + " resized to " + mBitmap.getWidth()
                                + "x" + mBitmap.getHeight());
                        doRecognize();

                    }
                }
                break;
            default:
                break;
        }
    }

    private String saveBitmap(Bitmap bmp)
    {
        String filename = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/" + "itraveltmpfile.jpg";

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            return  filename;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private List<RecognizeResult> processWithAutoFaceDetection() throws EmotionServiceException, IOException {
        Log.d("emotion", "Start emotion detection with auto-face detection");

        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        long startTime = System.currentTimeMillis();
        // -----------------------------------------------------------------------
        // KEY SAMPLE CODE STARTS HERE
        // -----------------------------------------------------------------------

        List<RecognizeResult> result = null;
        //
        // Detect emotion by auto-detecting faces in the image.
        //
        result = this.client.recognizeImage(inputStream);

        String json = gson.toJson(result);
        Log.d("result", json);

        Log.d("emotion", String.format("Detection done. Elapsed time: %d ms", (System.currentTimeMillis() - startTime)));
        // -----------------------------------------------------------------------
        // KEY SAMPLE CODE ENDS HERE
        // -----------------------------------------------------------------------
        return result;
    }

    private class doRequest extends AsyncTask<String, String, List<RecognizeResult>> {
        // Store error message
        private Exception e = null;

        @Override
        protected List<RecognizeResult> doInBackground(String... args) {
            try {
                return processWithAutoFaceDetection();
            } catch (Exception e) {
                this.e = e;    // Store error
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<RecognizeResult> result) {
            super.onPostExecute(result);
            // Display based on error existence

            mEditText.setText("\n\nRecognizing emotions with auto-detected face rectangles...\n");

            if (e != null) {
                mEditText.setText("Error: " + e.getMessage());
                this.e = null;
            } else {
                if (result.size() == 0) {
                    mEditText.setText("No emotion detected :(");
                } else {
                    Integer count = 0;
                    // Covert bitmap to a mutable bitmap by copying it
                    Bitmap bitmapCopy = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    Canvas faceCanvas = new Canvas(bitmapCopy);
                    //faceCanvas.drawBitmap(mBitmap, 0, 0, null);
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(5);
                    paint.setColor(Color.RED);

                    for (RecognizeResult r : result) {
                        tvAnger.setText(String.format("Anger: %1$.5f", r.scores.anger));
                        tvContempt.setText(String.format("Contempt: %1$.5f", r.scores.contempt));
                        tvDisgust.setText(String.format("Disgust: %1$.5f", r.scores.disgust));
                        tvFear.setText(String.format("Fear: %1$.5f", r.scores.fear));
                        tvHappiness.setText(String.format("Happy: %1$.5f", r.scores.happiness));
                        tvNeutral.setText(String.format("Neutral: %1$.5f", r.scores.neutral));
                        tvSadness.setText(String.format("Sad: %1$.5f", r.scores.sadness));
                        tvSupprise.setText(String.format("Surprise: %1$.5f", r.scores.surprise));

                        if (r.scores.neutral > 0.5f) {
                            anim_view.playAnimation();
                        }
                        faceCanvas.drawRect(r.faceRectangle.left,
                                r.faceRectangle.top,
                                r.faceRectangle.left + r.faceRectangle.width,
                                r.faceRectangle.top + r.faceRectangle.height,
                                paint);
                        count++;
                    }
                    ImageView imageView = (ImageView)view.findViewById(R.id.selectedImage);
                    imageView.setImageDrawable(new BitmapDrawable(getResources(), mBitmap));
                    //imageView.invalidate();
                }
            }

            fab.setEnabled(true);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        anim_view=null;
    }

    private void expandFAB() {
        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.rightMargin += (int) (fab1.getWidth() * 1.0);
        layoutParams.bottomMargin += (int) (fab1.getHeight() * 0.15);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(show_fab_1);
        fab1.setClickable(true);


        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.rightMargin += (int) (fab3.getWidth() * 0.15);
        layoutParams3.bottomMargin += (int) (fab3.getHeight() * 1.0);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(show_fab_3);
        fab3.setClickable(true);
    }

    private void hideFAB() {

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.rightMargin -= (int) (fab1.getWidth() * 1.0);
        layoutParams.bottomMargin -= (int) (fab1.getHeight() * 0.15);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(hide_fab_1);
        fab1.setClickable(false);


        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.rightMargin -= (int) (fab3.getWidth() * 0.15);
        layoutParams3.bottomMargin -= (int) (fab3.getHeight() * 1.0);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(hide_fab_3);
        fab3.setClickable(false);
    }
}
