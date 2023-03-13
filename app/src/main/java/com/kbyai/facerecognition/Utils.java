package com.kbyai.facerecognition;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;

import com.kbyai.facesdk.FaceBox;

import java.io.IOException;
import java.io.InputStream;

public class Utils {

    public static Bitmap cropFace(Bitmap src, FaceBox faceBox) {
        int centerX = (faceBox.x1 + faceBox.x2) / 2;
        int centerY = (faceBox.y1 + faceBox.y2) / 2;
        int cropWidth = (int)((faceBox.x2 - faceBox.x1) * 1.4f);

        int cropX1 = centerX - cropWidth / 2;
        int cropY1 = centerY - cropWidth / 2;
        int cropX2 = centerX + cropWidth / 2;
        int cropY2 = centerY + cropWidth / 2;
        if(cropX1 < 0) cropX1 = 0;
        if(cropX2 >= src.getWidth()) cropX2 = src.getWidth() - 1;
        if(cropY1 < 0) cropY1 = 0;
        if(cropY2 >= src.getHeight()) cropY2 = src.getHeight() - 1;


        int cropScaleWidth = 200;
        int cropScaleHeight = 200;
        float scaleWidth = ((float) cropScaleWidth) / (cropX2 - cropX1 + 1);
        float scaleHeight = ((float) cropScaleHeight) / (cropY2 - cropY1 + 1);

        final Matrix m = new Matrix();

        m.setScale(1.0f, 1.0f);
        m.postScale(scaleWidth, scaleHeight);
        final Bitmap cropped = Bitmap.createBitmap(src, cropX1, cropY1, (cropX2 - cropX1 + 1), (cropY2 - cropY1 + 1), m,
                true /* filter */);
        return cropped;
    }

    public static int getOrientation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public static Bitmap getCorrectlyOrientedImage(Context context, Uri photoUri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int orientation = getOrientation(context, photoUri);

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        srcBitmap = BitmapFactory.decodeStream(is);
        is.close();

        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        return srcBitmap;
    }
}
