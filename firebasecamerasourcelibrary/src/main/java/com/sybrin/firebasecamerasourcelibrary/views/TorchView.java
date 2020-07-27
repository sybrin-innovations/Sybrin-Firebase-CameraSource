package com.sybrin.firebasecamerasourcelibrary.views;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.View;

import com.sybrin.firebasecamerasourcelibrary.R;
import com.sybrin.firebasecamerasourcelibrary.camera.CameraSource;
import com.sybrin.firebasecamerasourcelibrary.camera.CameraReadyInterface;
import com.sybrin.firebasecamerasourcelibrary.camera.TorchController;

public class TorchView extends View implements View.OnClickListener {
    private Context context;
    TorchController torchController;
    private CameraSource cameraSource;

    public TorchView(Context context, AttributeSet attrs) {
        super(context);
        this.context = context;
        init();
    }

    private void init(){
        this.setBackgroundResource(R.drawable.flashlight_off);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        CameraSource.getCameraWhenReady(new CameraReadyInterface() {
            @Override
            public void onReady(Camera camera) {
                torchController = new TorchController(context, camera);
            }
        });
        this.setOnClickListener(this);
    }

    private void onClick(){

        if (torchController.checkTorchAvailability()) {
            if (torchController.isTorchOn()) {
                torchController.turnOff(this);
            } else {
                torchController.turnOn(this);
            }
        } else {
            this.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        onClick();
    }
}
