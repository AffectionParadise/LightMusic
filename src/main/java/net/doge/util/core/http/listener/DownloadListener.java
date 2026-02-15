package net.doge.util.core.http.listener;

public interface DownloadListener {
    default void totalSizeInitialized(long totalSize) {
    }

    void progress(long finishedSize, long totalSize);

    default boolean canInterrupt() {
        return false;
    }
}
