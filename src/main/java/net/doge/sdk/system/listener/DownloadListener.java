package net.doge.sdk.system.listener;

public interface DownloadListener {
    default void totalSizeInitialized(long totalSize) {
    }

    void progress(long finishedSize, long totalSize);

    default boolean shouldContinue() {
        return true;
    }
}
