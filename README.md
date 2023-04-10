<p align="center">
  <img src="https://user-images.githubusercontent.com/125717930/225975240-24b9a8ad-8cc6-4d5f-9a91-1435951b0bd7.png" width="120" alt="Nest Logo" />
</p>

# FaceRecognition-Android

## Introduction

Facial recognition technology is demonstrated here that can enroll and identify individuals while also verifying face liveness in real-time.

The demonstration showcases the capability to enroll individuals from photo galleries on Android devices and identify them in real-time using a camera.

## Try the APK

You can test the demo APK by following the link below:

https://drive.google.com/file/d/1cn_89fYDYhq8ANXs2epO-KBv7p5ZnWcA/view?usp=sharing

## SDK License

The face recognition project relies on kby-ai's SDK, which requires a license for each application ID.

- The code below shows how to use the license: https://github.com/kby-ai/FaceRecognition-Android/blob/4b071e04766d6b6a46882c0fc87cd7e2bcdc395d/app/src/main/java/com/kbyai/facerecognition/MainActivity.kt#L33-L43

- To request a license, please contact us:
```
Email: contact@kby-ai.com

Telegram: @kbyai

WhatsApp: +19092802609

Skype: live:.cid.66e2522354b1049b
```

## About SDK

### Set up
1. Copy the SDK (libfacesdk folder) to the root folder of your project.

2. Add SDK to the project in settings.gradle
```
include ':libfacesdk'
```

3. Add dependency to your build.gradle
```
implementation project(path: ':libfacesdk')
```

### Initializing an SDK

- Step One

To begin, you need to activate the SDK using the license that you have received.
```
FaceSDK.setActivation("...")
```

If activation is successful, the return value will be SDK_SUCCESS. Otherwise, an error value will be returned.

- Step Two

After activation, call the SDK's initialization function.
```
FaceSDK.init(getAssets());
```
If initialization is successful, the return value will be SDK_SUCCESS. Otherwise, an error value will be returned.

### Face Detection and Liveness Detection

The FaceSDK offers a single function for detecting face and liveness detection, which can be used as follows:
```
FaceSDK.faceDetection(bitmap)
```

This function takes a single parameter, which is a bitmap object. The return value of the function is a list of FaceBox objects. Each FaceBox object contains the detected face rectangle, liveness score, and facial angles such as yaw, roll, and pitch.

### Create Templates

The FaceSDK provides a function that can generate a template from a bitmap image. This template can then be used to verify the identity of the individual captured in the image.

```
byte[] templates = FaceSDK.templateExtraction(bitmap, faceBox);
```

The SDK's template extraction function takes two parameters: a bitmap object and an object of FaceBox. 

The function returns a byte array, which contains the template that can be used for person verification.

### Calculation similiarity

The "similarityCalculation" function takes a byte array of two templates as a parameter. 

```
float similarity = FaceSDK.similarityCalucation(templates1, templates1);
```

It returns the similarity value between the two templates, which can be used to determine the level of likeness between the two individuals.

### Yuv to Bitmap
The SDK provides a function called yuv2Bitmap, which converts a yuv frame to a bitmap. Since camera frames are typically in yuv format, this function is necessary to convert them to bitmaps. The usage of this function is as follows:
```
Bitmap bitmap = FaceSDK.yuv2Bitmap(nv21, image.getWidth(), image.getHeight(), 7);
```
The first parameter is an nv21 byte array containing the yuv data. 

The second parameter is the width of the yuv frame, and the third parameter is its height. 

The fourth parameter is the conversion mode, which is determined by the camera orientation.

To determine the appropriate conversion mode, the following method can be used:
```
 1        2       3      4         5            6           7          8

 888888  888888      88  88      8888888888  88                  88  8888888888
 88          88      88  88      88  88      88  88          88  88      88  88
 8888      8888    8888  8888    88          8888888888  8888888888          88
 88          88      88  88
 88          88  888888  888888
```
