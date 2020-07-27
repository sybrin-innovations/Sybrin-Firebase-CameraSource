/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sybrin.firebasecamerasourcelibrary.processor;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;
import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.sybrin.firebasecamerasourcelibrary.utils.BitmapUtils;
import com.sybrin.firebasecamerasourcelibrary.utils.FrameMetadata;

import java.nio.ByteBuffer;
import java.util.Timer;

public abstract class VisionProcessorBase<T> implements VisionImageProcessor {

    private static final String TAG = "Sybrin:VisionProcessor";

    private final ActivityManager activityManager;
//    private final Timer fpsTimer = new Timer();

    // Whether this processor is already shut down
    private boolean isShutdown = false;

    // Used to calculate latency, running in the same thread, no sync needed.
//    private int numRuns = 0;
//    private long totalRunMs = 0;
//    private long maxRunMs = 0;
//    private long minRunMs = Long.MAX_VALUE;

    // Frame count that have been processed so far in an one second interval to calculate FPS.
    private int frameProcessedInOneSecondInterval = 0;
//    private int framesPerSecond = 0;

    // To keep the latest images and its metadata.
    @GuardedBy("this")
    private ByteBuffer latestImage;
    @GuardedBy("this")
    private FrameMetadata latestImageMetaData;
    // To keep the images and metadata in process.
    @GuardedBy("this")
    private ByteBuffer processingImage;
    @GuardedBy("this")
    private FrameMetadata processingMetaData;

    protected VisionProcessorBase(Context context) {
        activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    // -----------------Code for processing single still image----------------------------------------
    @Override
    public void processBitmap(Bitmap bitmap) {
        requestDetectInImage(
                InputImage.fromBitmap(bitmap, 0),
                /* originalCameraImage= */ bitmap);
    }

    // -----------------Code for processing live preview frame from Camera1 API-----------------------
    @Override
    public synchronized void processByteBuffer(
            ByteBuffer data, final FrameMetadata frameMetadata) {
        latestImage = data;
        latestImageMetaData = frameMetadata;
        if (processingImage == null && processingMetaData == null) {
            processLatestImage();
        }
    }

    private synchronized void processLatestImage() {
        processingImage = latestImage;
        processingMetaData = latestImageMetaData;
        latestImage = null;
        latestImageMetaData = null;
        if (processingImage != null && processingMetaData != null && !isShutdown) {
            processImage(processingImage, processingMetaData);
        }
    }

    private void processImage(
            ByteBuffer data, final FrameMetadata frameMetadata) {
        // If live viewport is on (that is the underneath surface view takes care of the camera preview
        // drawing), skip the unnecessary bitmap creation that used for the manual preview drawing.
        Bitmap bitmap = BitmapUtils.getBitmap(data, frameMetadata);

        requestDetectInImage(
                InputImage.fromByteBuffer(
                        data,
                        frameMetadata.getWidth(),
                        frameMetadata.getHeight(),
                        frameMetadata.getRotation(),
                        InputImage.IMAGE_FORMAT_NV21),
                bitmap)
                .addOnSuccessListener(results -> processLatestImage());
    }

    // -----------------Common processing logic-------------------------------------------------------
    private Task<T> requestDetectInImage(
            final InputImage image,
            @Nullable final Bitmap originalCameraImage) {
//        final long startMs = SystemClock.elapsedRealtime();
        return detectInImage(image)
                .addOnSuccessListener(
                        results -> {
//                            long currentLatencyMs = SystemClock.elapsedRealtime() - startMs;
////                            numRuns++;
//                            frameProcessedInOneSecondInterval++;
////                            totalRunMs += currentLatencyMs;
////                            maxRunMs = Math.max(currentLatencyMs, maxRunMs);
////                            minRunMs = Math.min(currentLatencyMs, minRunMs);
//
//                            // Only log inference info once per second. When frameProcessedInOneSecondInterval is
//                            // equal to 1, it means this is the first frame processed during the current second.
//                            if (frameProcessedInOneSecondInterval == 1) {
////                                Log.d(TAG, "Max latency is: " + maxRunMs);
////                                Log.d(TAG, "Min latency is: " + minRunMs);
////                                Log.d(TAG, "Num of Runs: " + numRuns + ", Avg latency is: " + totalRunMs / numRuns);
//                                MemoryInfo mi = new MemoryInfo();
//                                activityManager.getMemoryInfo(mi);
//                                long availableMegs = mi.availMem / 0x100000L;
//                                Log.d(TAG, "Memory available in system: " + availableMegs + " MB");
//                            }


                            VisionProcessorBase.this.onSuccess(results, originalCameraImage);
                        })
                .addOnFailureListener(
                        e -> {
                            String error = "Failed to process. Error: " + e.getLocalizedMessage();
                            Log.d(TAG, error);
                            e.printStackTrace();
                            VisionProcessorBase.this.onFailure(e);
                        });
    }

    @Override
    public void stop() {
        isShutdown = true;
//        numRuns = 0;
//        totalRunMs = 0;
//        fpsTimer.cancel();
    }

    protected abstract Task<T> detectInImage(InputImage image);

    protected abstract void onSuccess(@NonNull T results, @NonNull Bitmap originalImage);

    protected abstract void onFailure(@NonNull Exception e);
}
