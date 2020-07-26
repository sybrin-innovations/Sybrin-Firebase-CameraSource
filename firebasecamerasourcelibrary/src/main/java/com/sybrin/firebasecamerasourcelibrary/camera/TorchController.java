package com.sybrin.firebasecamerasourcelibrary.camera;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.view.View;

import com.sybrin.firebasecamerasourcelibrary.R;

public class TorchController extends ContextWrapper {
    private Camera camera;
    private boolean isTorchOn = false;

    public TorchController(Context context, Camera camera){
        super(context);
        this.camera = camera;
    }

    public  boolean isTorchOn() {
        return isTorchOn;
    }

    public boolean checkTorchAvailability(){
        return  getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void turnOn(View view){
        view.setBackgroundResource(R.drawable.flashlight_on);
        Camera.Parameters p = camera.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(p);
        isTorchOn = true;

    }

    public void turnOff(View view){
        view.setBackgroundResource(R.drawable.flashlight_off);
        Camera.Parameters p = camera.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(p);
        isTorchOn = false;

    }
}
