package net.doge.util.core.http;

import net.doge.util.core.http.listener.DownloadListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author Doge
 * @description 网络工具类
 * @date 2020/12/15
 */
public class HttpUtil {
    /**
     * 下载文件
     *
     * @param url
     * @param dest
     * @throws Exception
     */
    public static void download(String url, String dest, Map<String, String> headers) throws Exception {
        download(url, dest, headers, null);
    }

    /**
     * 下载文件
     *
     * @param url
     * @param dest
     * @throws Exception
     */
    public static void download(String url, String dest, DownloadListener listener) throws Exception {
        download(url, dest, null, listener);
    }

    /**
     * 下载文件，监听下载进度
     *
     * @param url
     * @param dest
     * @param headers
     * @param listener
     * @throws Exception
     */
    public static void download(String url, String dest, Map<String, String> headers, DownloadListener listener) throws Exception {
        HttpResponse resp = HttpRequest.get(url)
                .headers(headers)
                // 注意这里是异步执行，否则会等待流中数据全部初始化才继续
                .execute();
        try (InputStream in = new BufferedInputStream(resp.bodyStream());
             OutputStream out = new BufferedOutputStream(Files.newOutputStream(Paths.get(dest)))) {
            // 以流的形式下载文件
            byte[] buffer = new byte[1024];
            // 文件大小
            long totalSize = resp.contentLength(), finishedSize = 0;
            boolean hasListener = listener != null;
            if (hasListener) listener.totalSizeInitialized(totalSize);
            int read;
            // 如果没有数据了会返回 -1，如果还有会返回数据的长度
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
                finishedSize += read;
                if (hasListener) {
                    if (listener.canInterrupt()) break;
                    listener.progress(finishedSize, totalSize);
                }
            }
            out.flush();
        }
    }
}
