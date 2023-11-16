package net.doge.sdk.system;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import net.doge.sdk.common.SdkCommon;
import net.doge.sdk.system.listener.DownloadListener;

import java.io.*;
import java.util.Map;

public class DownloadReq {
    private static DownloadReq instance;

    private DownloadReq() {
    }

    public static DownloadReq getInstance() {
        if (instance == null) instance = new DownloadReq();
        return instance;
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
    public void download(String url, String dest, Map<String, String> headers, DownloadListener listener) throws Exception {
        HttpResponse resp = HttpRequest.get(url)
                .header(Header.USER_AGENT, SdkCommon.USER_AGENT)
                .headerMap(headers, true)
                .setFollowRedirects(true)
                // 注意这里是异步执行，否则会等待流中数据全部初始化才继续
                .executeAsync();
        try (InputStream in = new BufferedInputStream(resp.bodyStream());
             OutputStream out = new BufferedOutputStream(new FileOutputStream(dest))) {
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
