package net.doge.sdk.core;

import net.doge.model.task.Task;
import net.doge.sdk.common.SdkCommon;
import net.doge.ui.component.panel.LoadingPanel;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class DownloadReq {
    /**
     * 下载文件
     *
     * @param urlPath
     * @param dest
     * @throws Exception
     */
    public void download(String urlPath, String dest) throws Exception {
        File file = new File(dest);
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", SdkCommon.USER_AGENT);
        conn.connect();
        int code = conn.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            throw new Exception("文件读取失败");
        }
        InputStream fis = new BufferedInputStream(conn.getInputStream());
        OutputStream toClient = new BufferedOutputStream(new FileOutputStream(file));
        // 以流的形式下载文件
        byte[] buffer = new byte[10240];
        int read;
        // 如果没有数据了会返回 -1，如果还有会返回数据的长度
        while ((read = fis.read(buffer)) != -1) {
            //读取多少输出多少
            toClient.write(buffer, 0, read);
        }
        toClient.flush();
        toClient.close();
        fis.close();
    }

    /**
     * 下载文件，同时设置等待面板百分比
     *
     * @param comp
     * @param urlPath
     * @param dest
     * @throws Exception
     */
    public void download(Component comp, String urlPath, String dest, Map<String, Object> headers) throws Exception {
        File file = new File(dest);
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", SdkCommon.USER_AGENT);
        // 请求头
        if (headers != null) {
            for (String key : headers.keySet()) {
                conn.setRequestProperty(key, (String) headers.get(key));
            }
        }
        conn.connect();
        int code = conn.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            throw new Exception("文件读取失败");
        }
        InputStream fis = new BufferedInputStream(conn.getInputStream());
        OutputStream toClient = new BufferedOutputStream(new FileOutputStream(file));
        // 以流的形式下载文件
        byte[] buffer = new byte[10240];
        // 文件大小
        long fileSize = conn.getContentLength(), hasRead = 0;
        int read;
        // 如果没有数据了会返回 -1，如果还有会返回数据的长度
        while ((read = fis.read(buffer)) != -1) {
            hasRead += read;
            if (comp instanceof LoadingPanel)
                ((LoadingPanel) comp).setText("加载歌曲文件，" + String.format("%.1f", (double) hasRead / fileSize * 100) + "%");
            //读取多少输出多少
            toClient.write(buffer, 0, read);
        }
        toClient.flush();
        toClient.close();
        fis.close();
    }

    /**
     * 通过 Task 下载文件，设置 percent
     *
     * @param task
     * @throws Exception
     */
    public void download(Task task, Map<String, Object> headers) throws Exception {
        File file = new File(task.getDest());
        URL url = new URL(task.getUrl());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", SdkCommon.USER_AGENT);
        // 请求头
        if (headers != null) {
            for (String key : headers.keySet()) {
                conn.setRequestProperty(key, (String) headers.get(key));
            }
        }
        conn.connect();
        int code = conn.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            throw new Exception("文件读取失败");
        }
        InputStream fis = new BufferedInputStream(conn.getInputStream());
        OutputStream toClient = new BufferedOutputStream(new FileOutputStream(file));
        // 以流的形式下载文件
        byte[] buffer = new byte[10240];
        // 文件大小
        long fileSize = conn.getContentLength(), hasRead = 0;
        task.setTotal(fileSize);
        int read;
        // 如果没有数据了会返回 -1，如果还有会返回数据的长度
        while ((read = fis.read(buffer)) != -1) {
            // 中断任务后跳出
            if (task.isInterrupted()) break;
            hasRead += read;
            task.setPercent((double) hasRead / fileSize * 100);
            task.setFinished(hasRead);
            // 读取多少输出多少
            toClient.write(buffer, 0, read);
        }
        toClient.flush();
        toClient.close();
        fis.close();
    }
}
