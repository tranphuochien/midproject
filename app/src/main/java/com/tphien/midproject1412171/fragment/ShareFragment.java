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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.EmotionServiceRestClient;
import com.microsoft.projectoxford.emotion.contract.RecognizeResult;
import com.microsoft.projectoxford.emotion.rest.EmotionServiceException;
import com.tphien.midproject1412171.R;
import com.tphien.midproject1412171.SelectImage;
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

    // The button to select an image
    private Button mButtonSelectImage;

    // The button to share an image
    private Button mButtonShareImage;

    // The URI of the image selected to detect.
    private Uri mImageUri;

    // The image selected to detect.
    private Bitmap mBitmap;

    // The edit to show status and result.
    private EditText mEditText;

    private EmotionServiceClient client;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_share, container, false);


        mButtonSelectImage = (Button)view.findViewById(R.id.buttonSelectImage);
        mButtonShareImage = (Button)view.findViewById(R.id.buttonShareImage);
        mEditText = (EditText)view.findViewById(R.id.editTextResult);
        tvAnger = (TextView)view.findViewById(R.id.tv_anger);
        tvHappiness = (TextView)view.findViewById(R.id.tv_happiness);
        tvSadness = (TextView)view.findViewById(R.id.tv_sadness);
        tvContempt = (TextView)view.findViewById(R.id.tv_contempt);
        tvDisgust = (TextView)view.findViewById(R.id.tv_disgust);
        tvNeutral = (TextView)view.findViewById(R.id.tv_neutral);
        tvSupprise = (TextView)view.findViewById(R.id.tv_surprise);
        tvFear = (TextView)view.findViewById(R.id.tv_fear);

        mButtonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditText.setText("");

                Intent intent;
                intent = new Intent(context, SelectImage.class);
                startActivityForResult(intent, REQUEST_SELECT_IMAGE);
            }
        });

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



        // Inflate the layout for this fragment
        return view;
    }


    public void doRecognize() {
        mButtonSelectImage.setEnabled(false);

        // Do emotion detection using auto-detected faces.
        try {
            mEditText.append("\nAnalyst...\n");
            new doRequest().execute();
        } catch (Exception e) {
            mEditText.append("Error encountered. Exception is: " + e.toString());
        }
    }

    // Called when image selection is done.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("AnalyzeActivity", "onActivityResult");
        switch (requestCode) {
            case REQUEST_SELECT_IMAGE:
                if(resultCode == RESULT_OK) {
                    // If image is selected successfully, set the image URI and bitmap.
                    mImageUri = data.getData();

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

            mEditText.append("\n\nRecognizing emotions with auto-detected face rectangles...\n");

            if (e != null) {
                mEditText.setText("Error: " + e.getMessage());
                this.e = null;
            } else {
                if (result.size() == 0) {
                    mEditText.append("No emotion detected :(");
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
                mEditText.setSelection(0);
            }

            mButtonSelectImage.setEnabled(true);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
