package com.mobilewallet.consumer.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.utils.Constants;
import com.mobilewallet.consumer.viewmodels.QRViewModel;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QRScannerActivity extends AppCompatActivity {

    private static final String TAG = "QRScannerActivity";
    private static final int CAMERA_PERMISSION_CODE = 1001;

    private PreviewView previewView;
    private TextView tvInstruction;
    private ProgressBar progressBar;
    private FrameLayout scannerOverlay;
    private View btnBack, btnFlash;

    private QRViewModel viewModel;
    private ExecutorService cameraExecutor;
    private BarcodeScanner barcodeScanner;
    private boolean isProcessing = false;
    private boolean isFlashOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        initViews();
        initViewModel();
        setupListeners();

        cameraExecutor = Executors.newSingleThreadExecutor();
        barcodeScanner = BarcodeScanning.getClient();

        if (hasCameraPermission()) {
            startCamera();
        } else {
            requestCameraPermission();
        }
    }

    private void initViews() {
        previewView = findViewById(R.id.previewView);
        tvInstruction = findViewById(R.id.tvInstruction);
        progressBar = findViewById(R.id.progressBar);
        scannerOverlay = findViewById(R.id.scannerOverlay);
        btnBack = findViewById(R.id.btnBack);
        btnFlash = findViewById(R.id.btnFlash);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(QRViewModel.class);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnFlash.setOnClickListener(v -> toggleFlash());
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Camera provider error", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);

        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
        } catch (Exception e) {
            Log.e(TAG, "Camera binding failed", e);
        }
    }

    private void analyzeImage(ImageProxy imageProxy) {
        if (isProcessing) {
            imageProxy.close();
            return;
        }

        @SuppressWarnings("UnsafeOptInUsageError")
        android.media.Image mediaImage = imageProxy.getImage();
        if (mediaImage == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(mediaImage,
                imageProxy.getImageInfo().getRotationDegrees());

        barcodeScanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode barcode : barcodes) {
                        String rawValue = barcode.getRawValue();
                        if (rawValue != null && !rawValue.isEmpty()) {
                            handleQRResult(rawValue);
                            break;
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Barcode scan failed", e))
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private void handleQRResult(String qrPayload) {
        if (isProcessing) return;
        isProcessing = true;

        runOnUiThread(() -> {
            tvInstruction.setText(R.string.processing_qr);
            progressBar.setVisibility(View.VISIBLE);
            scannerOverlay.setAlpha(0.5f);
        });

        viewModel.processScannedQR(qrPayload);
        observeScanResult();
    }

    private void observeScanResult() {
        viewModel.getScanResultLiveData().observe(this, payment -> {
            if (payment != null) {
                Intent intent = new Intent(this, PaymentSuccessActivity.class);
                intent.putExtra(Constants.EXTRA_PAYMENT, payment);
                startActivity(intent);
                finish();
            }
        });

        viewModel.getErrorLiveData().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                isProcessing = false;
                tvInstruction.setText(R.string.point_camera);
                progressBar.setVisibility(View.GONE);
                scannerOverlay.setAlpha(1f);
            }
        });
    }

    private void toggleFlash() {
        // Toggle flash using camera control
        isFlashOn = !isFlashOn;
        btnFlash.setAlpha(isFlashOn ? 1f : 0.5f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) cameraExecutor.shutdown();
        if (barcodeScanner != null) barcodeScanner.close();
    }
}