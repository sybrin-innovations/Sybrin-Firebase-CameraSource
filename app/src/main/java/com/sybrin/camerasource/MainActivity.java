package com.sybrin.camerasource;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.sybrin.firebasecamerasourcelibrary.camera.CameraSource;
import com.sybrin.firebasecamerasourcelibrary.camera.CameraSourcePreview;
import com.sybrin.firebasecamerasourcelibrary.utils.PermissionsHandler;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "PreviewActivity";

    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;

    private TextView textView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.centerText);
        preview = findViewById(R.id.preview);

        boolean permissionsResult = PermissionsHandler.handlePermissions(MainActivity.this, new Runnable() {
            @Override
            public void run() {
                launchCamera();
            }
        });
        if (permissionsResult) {
            launchCamera();
        }
    }

    private void launchCamera() {
        cameraSource = CameraSource.createCamera(MainActivity.this, preview);
        cameraSource.setMachineLearningFrameProcessor(new BarcodeScannerProcessor(MainActivity.this, textView));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != cameraSource) {
            cameraSource.startCameraSource(preview);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsHandler.onPermissionResult(grantResults);
    }
}
