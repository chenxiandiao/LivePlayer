package com.cxd.android.liveplayer.utils;

import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Rancune@126.com on 2016/7/29.
 */
@SuppressWarnings("unused")
public class IOUtils {
    private static final String TAG = "IOUtils";

    private static final String CHARSET = "UTF-8";

    public static final int HTTP_TIME_OUT = 10 * 1000;

    private IOUtils() {
    }

    /**
     * @param closeables the Closeable object array to be closed, or null,in which case we do nothing
     */
    public static void closeQuietly(@Nullable Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        try {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        } catch (IOException ignored) {
            KasLog.w(TAG, "IOException thrown while closing Closeable.");
        }
    }

    /**
     * @param in  inputstream
     * @param out outputstream
     * @throws IOException
     */
    public static long copy(InputStream in, OutputStream out) throws IOException {
        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        if (!(out instanceof BufferedOutputStream)) {
            out = new BufferedOutputStream(out);
        }
        long count = 0L;
        int len;
        byte[] buffer = new byte[4096];
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
            count += len;
        }
        out.flush();
        return count;
    }

    /**
     * @param input inputstream
     * @return byte[]
     * @throws IOException
     */
    @Nullable
    public static byte[] toByteArray(InputStream input) throws IOException {
        if (input == null) {
            return null;
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    /**
     * @param in      inputstream
     * @param charset charset, default UTF-8
     * @return the string from the inputstream, or empty string if the inputstring is null
     * @throws IOException
     */
    public static String readString(InputStream in, String charset) throws IOException {
        if (in == null) {
            return "";
        }
        if (charset == null || charset.isEmpty()) {
            charset = CHARSET;
        }
        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        Reader reader = new InputStreamReader(in, charset);
        StringBuilder builder = new StringBuilder();
        char[] buf = new char[1024];
        int len;
        while ((len = reader.read(buf)) != -1) {
            builder.append(buf, 0, len);
        }
        return builder.toString();
    }

    /**
     * @param out     outputstream
     * @param text    content
     * @param charset charset,default UTF-8
     * @throws IOException write a string to the outputstream
     */
    public static void writeString(OutputStream out, String text, String charset) throws IOException {
        if (out == null || text == null) {
            return;
        }
        if (charset == null || charset.isEmpty()) {
            charset = CHARSET;
        }
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new StringReader(text));
            writer = new BufferedWriter(new OutputStreamWriter(out, charset));
            char[] buffer = new char[1024];
            int len;
            while ((len = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, len);
            }
            writer.flush();
        } finally {
            closeQuietly(writer, reader);
        }
    }

    /**
     * @param path    file path
     * @param charset charset,default UTF-8
     * @return a string from the file,  or empty string
     */
    public static String readFile(String path, String charset) {
        if (path == null || path.isEmpty()) {
            return "";
        }

        File file = new File(path);
        if (!file.exists() || file.isDirectory()) {
            return "";
        }
        InputStream in = null;
        String str = "";
        try {
            in = new FileInputStream(file);
            str = readString(in, charset);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(in);
        }
        return str;
    }

    /**
     * @param text    content
     * @param charset charset,default UTF-8
     * @param file    the destination file
     * @return whether suceess
     * write the string to the file
     */
    public static boolean writeFile(String text, String charset, File file) {
        if (text == null || file == null || file.isDirectory()) {
            return false;
        }
        File tmpFile = new File(file.getParent(), file.getName() + ".tmp");
        OutputStream out = null;
        boolean saved = false;
        try {
            out = new FileOutputStream(tmpFile);
            writeString(out, text, charset);
            saved = true;
        } catch (IOException e) {
            e.printStackTrace();
            saved = false;
        } finally {
            closeQuietly(out);
            if (saved && !tmpFile.renameTo(file)) {
                saved = false;
            }
        }
        return saved;
    }

    /**
     * @param strDir file or directory path
     * @return whether success
     */
    public static boolean deleteTree(String strDir) {
        if (null == strDir || strDir.length() == 0)
            return false;

        File rootDir = new File(strDir);
        if (!rootDir.exists()) {
            return false;
        }
        if (!rootDir.isDirectory()) {
            return rootDir.delete();
        }

        String[] strFileList = rootDir.list();

        if (strFileList != null) {
            for (String aStrFileList : strFileList) {
                File f = new File(strDir + File.separator + aStrFileList);
                if (!f.exists()) {
                    continue;
                }
                if (f.isDirectory()) {
                    deleteTree(f.getPath());
                } else {
                    //noinspection ResultOfMethodCallIgnored
                    f.delete();
                }
            }
        }

        return rootDir.delete();

    }

    public static boolean downloadFileSync(String fileUrl, File targetFile) {
        if (isEmpty(fileUrl) || targetFile == null) {
            return false;
        }
        if (targetFile.exists() && !targetFile.delete()) {
            return false;
        }
        boolean success = false;
        InputStream in = null;
        OutputStream out = null;
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setConnectTimeout(HTTP_TIME_OUT);
            connection.setReadTimeout(HTTP_TIME_OUT);
            if (connection.getResponseCode() == 200) {
                in = connection.getInputStream();
                if (in != null) {
                    out = new FileOutputStream(targetFile);
                    IOUtils.copy(in, out);
                    success = true;
                }
            }
        } catch (Exception ignored) {
            success = false;
        } finally {
            closeQuietly(in, out);
        }
        return success;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty() || str.equals("null");
    }

}
