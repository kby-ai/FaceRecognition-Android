<p align="center">
  <a href="https://play.google.com/store/apps/dev?id=7086930298279250852" target="_blank">
    <img alt="" src="https://github-production-user-asset-6210df.s3.amazonaws.com/125717930/246971879-8ce757c3-90dc-438d-807f-3f3d29ddc064.png" width=500/>
  </a>  
</p>

👏 Product List

https://github.com/kby-ai/Product

👏  We have published the Face Liveness Detection(Face Anti-Spoofing), Face Recognition SDK and ID Card Recognition SDK for the server.

  - [FaceLivenessDetection-Docker](https://github.com/kby-ai/FaceLivenessDetection-Docker)

  - [FaceRecognition-Docker](https://github.com/kby-ai/FaceRecognition-Docker)

  - [IDCardRecognition-Docker](https://github.com/kby-ai/IDCardRecognition-Docker)

# FaceRecognition-Android

## Overview

The demo project demonstrates both Face Liveness Detection and Face Recognition Technology.

> The demo is integrated with KBY-AI's Standard Face Mobile SDK.

  | Basic      | Standard | Premium |
  |------------------|------------------|------------------|
  | Face Detection        | Face Detection    | Face Detection |
  | Face Liveness Detection        | Face Liveness Detection    | Face Liveness Detection |
  | Pose Estimation        | Pose Estimation    | Pose Estimation |
  |         | Face Recognition    | Face Recognition |
  |         |         | 68 points Face Landmark Detection |
  |         |         | Face Quality Calculation |
  |         |         | Face Occlusion Detection |
  |         |         | Eye Closure Detection |
  |         |         | Age, Gender Estimation |

> [Face Liveness Detection - Android(Basic SDK)](https://github.com/kby-ai/FaceLivenessDetection-Android)
> 
> [Face Liveness Detection - iOS(Basic SDK)](https://github.com/kby-ai/FaceLivenessDetection-iOS)
> 
> [Face Recognition - Android(Standard SDK)](https://github.com/kby-ai/FaceRecognition-Android)
> 
> [Face Recognition - iOS(Standard SDK)](https://github.com/kby-ai/FaceRecognition-iOS)
> 
> [Face Recognition - Flutter(Standard SDK)](https://github.com/kby-ai/FaceRecognition-Flutter)
> 
> [Face Recognition - React-Native(Standard SDK)](https://github.com/kby-ai/FaceRecognition-React-Native)
>
> [Face Attribute - Android(Premium SDK)](https://github.com/kby-ai/FaceAttribute-Android)
> 
> [Face Attribute - iOS(Premium SDK)](https://github.com/kby-ai/FaceAttribute-iOS)

## Try the APK

### Google Play

<a href="https://play.google.com/store/apps/details?id=com.kbyai.facerecognition" target="_blank">
  <img alt="" src="https://user-images.githubusercontent.com/125717930/230804673-17c99e7d-6a21-4a64-8b9e-a465142da148.png" height=80/>
</a>

### Google Drive

https://drive.google.com/file/d/1cn_89fYDYhq8ANXs2epO-KBv7p5ZnWcA/view?usp=sharing

## Screenshots
<p float="left">
  <img src="https://github.com/kby-ai/FaceRecognition-Android/assets/125717930/d896a060-0605-48ff-a774-4d767aac1d3c" width=200/>
  <img src="https://github.com/kby-ai/FaceRecognition-Android/assets/125717930/3dba3854-aad1-4b18-b146-7bbdd02241f2" width=200/>
  <img src="https://github.com/kby-ai/FaceRecognition-Android/assets/125717930/84a06eb8-4892-4c09-a6e0-1a4fb364293b" width=200/>
</p>

<p float="left">
  <img src="https://github.com/kby-ai/FaceRecognition-Android/assets/125717930/c0b6ae43-6995-4b6f-b14f-cd0bfec4adc7" width=200/>
  <img src="https://github.com/kby-ai/FaceRecognition-Android/assets/125717930/14a3da8e-743c-48b9-853b-9f3ab7abcf29" width=200/>
  <img src="https://github.com/kby-ai/FaceRecognition-Android/assets/125717930/acbc36b8-dc05-4602-970a-b195f2f4b58c" width=200/>
</p>

## SDK License

The face recognition project relies on kby-ai's SDK, which requires a license for each application ID.

- The code below shows how to use the license: https://github.com/kby-ai/FaceRecognition-Android/blob/4b071e04766d6b6a46882c0fc87cd7e2bcdc395d/app/src/main/java/com/kbyai/facerecognition/MainActivity.kt#L33-L43

- To request a license, please contact us:
```
Email: contact@kby-ai.com

Telegram: @kbyai

WhatsApp: +19092802609

Skype: live:.cid.66e2522354b1049b

Facebook: https://www.facebook.com/KBYAI
```

## About SDK

### 1. Set up
1. Copy the SDK (libfacesdk folder) to the root folder of your project.

2. Add SDK to the project in settings.gradle
```
include ':libfacesdk'
```

3. Add dependency to your build.gradle
```
implementation project(path: ':libfacesdk')
```

### 2. Initializing an SDK

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

### 3. SDK Classes
  - FaceDetectionParam
  
    This class serves as the input parameter for face detection, allowing the inclusion of face liveness detection and specifying the desired liveness detection level.

    | Feature| Type | Name |
    |------------------|------------------|------------------|
    | Check liveness        | boolean    | check_liveness |
    | Check liveness level        | int    | check_liveness_level |

    When check_liveness_level is set to 0, the liveness detection achieves high accuracy.
    When check_liveness_level is set to 1, the liveness detection operates with light weight.

### 4. APIs
#### - Face Detection and Liveness Detection

The FaceSDK offers a single function for detecting faces, allowing the inclusion of face liveness detection and specifying the desired liveness detection level.
```
FaceSDK.faceDetection(bitmap, param)
```

This function requires two parameters: a Bitmap object and a FaceDetectionParam object that enables various processing functionalities.

The return value of the function is a list of FaceBox objects. Each FaceBox object contains the detected face rectangle, liveness score, and facial angles such as yaw, roll, and pitch.


#### - Create Templates

The FaceSDK provides a function that can generate a template from a bitmap image. This template can then be used to verify the identity of the individual captured in the image.

```
byte[] templates = FaceSDK.templateExtraction(bitmap, faceBox);
```

The SDK's template extraction function takes two parameters: a bitmap object and an object of FaceBox. 

The function returns a byte array, which contains the template that can be used for person verification.

#### - Calculation similarity

The "similarityCalculation" function takes a byte array of two templates as a parameter. 

```
float similarity = FaceSDK.similarityCalculation(templates1, templates1);
```

It returns the similarity value between the two templates, which can be used to determine the level of likeness between the two individuals.

#### - Yuv to Bitmap
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

