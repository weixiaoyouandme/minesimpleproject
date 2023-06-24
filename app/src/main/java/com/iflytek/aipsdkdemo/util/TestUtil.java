package com.iflytek.aipsdkdemo.util;

import android.util.Log;

import com.iflytek.util.Logs;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TestUtil {
    private final static int BUFFER_SIZE = 4096;
    private static final String TAG = "TestUtil";

    public static byte[] inputStream2Byte(InputStream in) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
            outStream.write(data, 0, count);
        }

        data = null;
        return outStream.toByteArray();
    }

    public static InputStream file2InputStream(String filePath)
            throws FileNotFoundException {
        return new FileInputStream(new File(filePath));
    }

    public static byte[] file2Byte(String filePath)
            throws FileNotFoundException, IOException {
        return inputStream2Byte(file2InputStream(filePath));
    }

    public static void byte2File(byte[] data, String filePath) {
        File file = new File(filePath);
        OutputStream output = null;
        BufferedOutputStream bufferedOutput = null;
        try {
            output = new FileOutputStream(file);
            bufferedOutput = new BufferedOutputStream(output);
            bufferedOutput.write(data);
        } catch (IOException e) {
//                e.printStackTrace();
            Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][byte2File] " + " e:" + e.getMessage());
        } finally {
            closeQuietly(bufferedOutput);
            closeQuietly(output);
        }
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
//                e.printStackTrace();
            Logs.e("AIPSDKDemo", "[" + Thread.currentThread().getName() + "][" + TAG + "][closeQuietly] " + " e:" + e.getMessage());
        }
    }
}
