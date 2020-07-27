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

package com.sybrin.camerasource;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.sybrin.firebasecamerasourcelibrary.processor.VisionProcessorBase;

import java.util.List;

public class BarcodeScannerProcessor extends VisionProcessorBase<List<Barcode>> {
    private static final String TAG = "Sybrin:BarcodeProcessor";
    private final TextView feedbackTextView;
    private final BarcodeScanner barcodeScanner;

    public BarcodeScannerProcessor(Context context, TextView feedbackTextView) {
        super(context);
        this.feedbackTextView = feedbackTextView;
         new BarcodeScannerOptions.Builder()
             .setBarcodeFormats(Barcode.FORMAT_PDF417)
             .build();
        barcodeScanner = BarcodeScanning.getClient();

    }

    @Override
    public void stop() {
        super.stop();
        barcodeScanner.close();
    }

    @Override
    protected Task<List<Barcode>> detectInImage(InputImage image) {
        return barcodeScanner.process(image);
    }

    @Override
    protected void onSuccess(
            @NonNull List<Barcode> barcodes, @NonNull Bitmap originalImage) {
        if (barcodes.isEmpty()) {
            Log.v(TAG, "No barcode has been detected");
            feedbackTextView.setText("No barcode has been detected");
        }
        for (int i = 0; i < barcodes.size(); ++i) {
            feedbackTextView.setText("Barcode Detected!");
            Barcode barcode = barcodes.get(i);
            logExtrasForTesting(barcode);
        }
    }

    private static void logExtrasForTesting(Barcode barcode) {
        if (barcode != null) {
            Log.v(
                    TAG,
                    String.format(
                            "Detected barcode's bounding box: %s", barcode.getBoundingBox().flattenToString()));
            Log.v(
                    TAG,
                    String.format(
                            "Expected corner point size is 4, get %d", barcode.getCornerPoints().length));
            for (Point point : barcode.getCornerPoints()) {
                Log.v(
                        TAG,
                        String.format("Corner point is located at: x = %d, y = %d", point.x, point.y));
            }
            Log.v(TAG, "barcode display value: " + barcode.getDisplayValue());
            Log.v(TAG, "barcode raw value: " + barcode.getRawValue());
            Barcode.DriverLicense dl = barcode.getDriverLicense();
            if (dl != null) {
                Log.v(TAG, "driver license city: " + dl.getAddressCity());
                Log.v(TAG, "driver license state: " + dl.getAddressState());
                Log.v(TAG, "driver license street: " + dl.getAddressStreet());
                Log.v(TAG, "driver license zip code: " + dl.getAddressZip());
                Log.v(TAG, "driver license birthday: " + dl.getBirthDate());
                Log.v(TAG, "driver license document type: " + dl.getDocumentType());
                Log.v(TAG, "driver license expiry date: " + dl.getExpiryDate());
                Log.v(TAG, "driver license first name: " + dl.getFirstName());
                Log.v(TAG, "driver license middle name: " + dl.getMiddleName());
                Log.v(TAG, "driver license last name: " + dl.getLastName());
                Log.v(TAG, "driver license gender: " + dl.getGender());
                Log.v(TAG, "driver license issue date: " + dl.getIssueDate());
                Log.v(TAG, "driver license issue country: " + dl.getIssuingCountry());
                Log.v(TAG, "driver license number: " + dl.getLicenseNumber());
            }
        }
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Barcode detection failed " + e);
    }
}
