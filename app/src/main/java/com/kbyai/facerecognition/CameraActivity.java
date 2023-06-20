package com.kbyai.facerecognition;


import static androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.kbyai.facesdk.FaceBox;
import com.kbyai.facesdk.FaceDetectionParam;
import com.kbyai.facesdk.FaceSDK;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {

    static String TAG = CameraActivity.class.getSimpleName();
    static int PREVIEW_WIDTH = 720;
    static int PREVIEW_HEIGHT = 1280;

    private ExecutorService cameraExecutorService;
    private PreviewView viewFinder;
    private Preview preview        = null;
    private ImageAnalysis imageAnalyzer  = null;
    private Camera camera         = null;
    private CameraSelector        cameraSelector = null;
    private ProcessCameraProvider cameraProvider = null;

    private FaceView faceView;

    private Context context;

    private Boolean recognized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        context = this;

        viewFinder = findViewById(R.id.preview);
        faceView = findViewById(R.id.faceView);
        cameraExecutorService = Executors.newFixedThreadPool(1);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            viewFinder.post(() ->
            {
                setUpCamera();
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        recognized = false;
    }

    @Override
    public void onPause() {
        super.onPause();

        faceView.setFaceBoxes(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {

                viewFinder.post(() ->
                {
                    setUpCamera();
                });
            }
        }
    }

    private void setUpCamera()
    {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(CameraActivity.this);
        cameraProviderFuture.addListener(() -> {

            // CameraProvider
            try {
                cameraProvider = cameraProviderFuture.get();
            } catch (ExecutionException e) {
            } catch (InterruptedException e) {
            }

            // Build and bind the camera use cases
            bindCameraUseCases();

        }, ContextCompat.getMainExecutor(CameraActivity.this));
    }

    @SuppressLint({"RestrictedApi", "UnsafeExperimentalUsageError"})
    private void bindCameraUseCases()
    {
        int rotation = viewFinder.getDisplay().getRotation();

        cameraSelector = new CameraSelector.Builder().requireLensFacing(SettingsActivity.getCameraLens(this)).build();

        preview = new Preview.Builder()
                .setTargetResolution(new Size(PREVIEW_WIDTH, PREVIEW_HEIGHT))
                .setTargetRotation(rotation)
                .build();

        imageAnalyzer = new ImageAnalysis.Builder()
                .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                .setTargetResolution(new Size(PREVIEW_WIDTH, PREVIEW_HEIGHT))
                // Set initial target rotation, we will have to call this again if rotation changes
                // during the lifecycle of this use case
                .setTargetRotation(rotation)
                .build();

        imageAnalyzer.setAnalyzer(cameraExecutorService, new FaceAnalyzer());

        cameraProvider.unbindAll();

        try {
            camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer);

            preview.setSurfaceProvider(viewFinder.getSurfaceProvider());
        } catch (Exception exc) {
        }
    }

    class FaceAnalyzer implements ImageAnalysis.Analyzer
    {
        @SuppressLint("UnsafeExperimentalUsageError")
        @Override
        public void analyze(@NonNull ImageProxy imageProxy)
        {
            analyzeImage(imageProxy);
        }
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private void analyzeImage(ImageProxy imageProxy)
    {
        if(recognized == true) {
            imageProxy.close();
            return;
        }

        try
        {
            Image image = imageProxy.getImage();

            Image.Plane[] planes = image.getPlanes();
            ByteBuffer yBuffer = planes[0].getBuffer();
            ByteBuffer uBuffer = planes[1].getBuffer();
            ByteBuffer vBuffer = planes[2].getBuffer();

            int ySize = yBuffer.remaining();
            int uSize = uBuffer.remaining();
            int vSize = vBuffer.remaining();

            byte[] nv21 = new byte[ySize + uSize + vSize];
            yBuffer.get(nv21, 0, ySize);
            vBuffer.get(nv21, ySize, vSize);
            uBuffer.get(nv21, ySize + vSize, uSize);

            int cameraMode = 7;
            if(SettingsActivity.getCameraLens(context) == CameraSelector.LENS_FACING_BACK) {
                cameraMode = 6;
            }
            Bitmap bitmap  = FaceSDK.yuv2Bitmap(nv21, image.getWidth(), image.getHeight(), cameraMode);

            FaceDetectionParam faceDetectionParam = new FaceDetectionParam();
            faceDetectionParam.check_liveness = true;
            faceDetectionParam.check_liveness_level = SettingsActivity.getLivenessLevel(this);
            List<FaceBox> faceBoxes = FaceSDK.faceDetection(bitmap, faceDetectionParam);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    faceView.setFrameSize(new Size(bitmap.getWidth(), bitmap.getHeight()));
                    faceView.setFaceBoxes(faceBoxes);
                }
            });

            if(faceBoxes.size() > 0) {
                FaceBox faceBox = faceBoxes.get(0);
                if(faceBox.liveness > SettingsActivity.getLivenessThreshold(context)) {
                    byte[] templates = FaceSDK.templateExtraction(bitmap, faceBox);

                    float maxSimiarlity = 0;
                    Person maximiarlityPerson = null;
                    for(Person person : DBManager.personList) {
                        float similarity = FaceSDK.similarityCalculation(templates, person.templates);
                        if(similarity > maxSimiarlity) {
                            maxSimiarlity = similarity;
                            maximiarlityPerson = person;
                        }
                    }

                    if(maxSimiarlity > SettingsActivity.getIdentifyThreshold(this)) {
                        recognized = true;
                        final Person identifiedPerson = maximiarlityPerson;
                        final float identifiedSimilarity = maxSimiarlity;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap faceImage = Utils.cropFace(bitmap, faceBox);

                                Intent intent = new Intent(context, ResultActivity.class);
                                intent.putExtra("identified_face", faceImage);
                                intent.putExtra("enrolled_face", identifiedPerson.face);
                                intent.putExtra("identified_name", identifiedPerson.name);
                                intent.putExtra("similarity", identifiedSimilarity);
                                intent.putExtra("liveness", faceBox.liveness);
                                intent.putExtra("yaw", faceBox.yaw);
                                intent.putExtra("roll", faceBox.roll);
                                intent.putExtra("pitch", faceBox.pitch);

                                startActivity(intent);
                            }
                        });
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            imageProxy.close();
        }
    }
}