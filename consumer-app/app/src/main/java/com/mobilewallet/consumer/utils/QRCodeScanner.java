package com.mobilewallet.consumer.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.InputStream;

/**
 * Utility class for scanning QR codes from images/files
 */
public class QRCodeScanner {

    private static final String TAG = "QRCodeScanner";

    /**
     * Scan QR code from a bitmap
     */
    public static String scanFromBitmap(Bitmap bitmap) {
        if (bitmap == null) return null;

        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

            LuminanceSource source = new RGBLuminanceSource(width, height, pixels);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

            Result result = new MultiFormatReader().decode(binaryBitmap);
            return result.getText();
        } catch (NotFoundException e) {
            Log.w(TAG, "No QR code found in image");
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error scanning QR code", e);
            return null;
        }
    }

    /**
     * Scan QR code from a URI
     */
    public static String scanFromUri(@NonNull android.content.Context context, @NonNull Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            return scanFromBitmap(bitmap);
        } catch (Exception e) {
            Log.e(TAG, "Error reading image URI", e);
            return null;
        }
    }
}