-keepparameternames
-keep class com.sybrin.firebasecamerasourcelibrary.camera.CameraSource
-keepclassmembers class com.sybrin.firebasecamerasourcelibrary.camera.CameraSource{
    public *;
}
-keep class com.sybrin.firebasecamerasourcelibrary.camera.CameraSourcePreview
-keepclassmembers class com.sybrin.firebasecamerasourcelibrary.camera.CameraSourcePreview{
    public *;
}
-keep class com.sybrin.firebasecamerasourcelibrary.processor.VisionProcessorBase{
     void processBitmap(android.graphics.Bitmap);
     void processByteBuffer(java.nio.ByteBuffer, com.sybrin.firebasecamerasourcelibrary.utils.FrameMetadata);
     void stop();
}
-keep class com.sybrin.firebasecamerasourcelibrary.processor.VisionProcessorBase
-keepclassmembers class com.sybrin.firebasecamerasourcelibrary.processor.VisionProcessorBase{
    public *;
}
-keep class com.sybrin.firebasecamerasourcelibrary.utils.PermissionsHandler
-keepclassmembers class com.sybrin.firebasecamerasourcelibrary.utils.PermissionsHandler{
    public *;
}

-keep class com.sybrin.firebasecamerasourcelibrary.views.TorchView
-keepclassmembers class com.sybrin.firebasecamerasourcelibrary.views.TorchView{
    public *;
}