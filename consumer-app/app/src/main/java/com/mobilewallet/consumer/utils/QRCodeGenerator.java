package com.mobilewallet.consumer.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

public class QRCodeGenerator {

    public static Bitmap generateQRCode(String data, int size) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    data, BarcodeFormat.QR_CODE, size, size, hints);

            int[] pixels = new int[size * size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    pixels[y * size + x] = bitMatrix.get(x, y) ?
                            Color.BLACK : Color.WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap generateQRCodeWithLogo(String data, int size, Bitmap logo) {
        Bitmap qrCode = generateQRCode(data, size);
        if (qrCode == null || logo == null) return qrCode;

        int logoSize = size / 5;
        Bitmap scaledLogo = Bitmap.createScaledBitmap(logo, logoSize, logoSize, false);

        Bitmap combined = qrCode.copy(Bitmap.Config.ARGB_8888, true);
        android.graphics.Canvas canvas = new android.graphics.Canvas(combined);
        int left = (size - logoSize) / 2;
        int top = (size - logoSize) / 2;
        canvas.drawBitmap(scaledLogo, left, top, null);

        return combined;
    }
}