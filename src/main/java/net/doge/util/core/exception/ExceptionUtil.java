package net.doge.util.core.exception;

import javafx.scene.media.MediaException;
import net.doge.constant.core.lang.I18n;
import net.doge.exception.io.ConnectRuntimeException;
import net.doge.exception.io.TimeoutRuntimeException;
import net.doge.exception.media.IllegalMediaException;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.dialog.TipDialog;
import net.doge.util.core.log.LogUtil;

import java.net.SocketTimeoutException;

/**
 * @author Doge
 * @description 异常处理工具类
 * @date 2020/12/15
 */
public class ExceptionUtil {
    private static final String NET_ERROR_MSG = I18n.getText("netErrorMsg");
    private static final String TIME_OUT_MSG = I18n.getText("timeOutMsg");
    private static final String GET_RESOURCE_FAILED_MSG = I18n.getText("getResourceFailedMsg");
    private static final String UNSUPPORTED_AUDIO_FILE_MSG = I18n.getText("unsupportedAudioFileMsg");
    private static final String INVALID_AUDIO_FILE_MSG = I18n.getText("invalidAudioFileMsg");

    /**
     * 将异常抛出为运行时异常
     *
     * @param e
     * @return
     */
    public static void throwRuntimeException(Exception e) {
        if (e instanceof SocketTimeoutException) throw new TimeoutRuntimeException(TIME_OUT_MSG);
        throw new ConnectRuntimeException(NET_ERROR_MSG);
    }

    /**
     * 处理并发执行异常
     *
     * @param e
     * @return
     */
    public static void handleAsyncException(Exception e) {
        // InterruptedException ExecutionException
        LogUtil.error(e.getCause());
    }

    /**
     * 处理资源获取异常
     *
     * @param e
     * @return
     */
    public static void handleResourceException(Exception e, MainFrame f) {
        LogUtil.error(e);
        String msg;
        if (e instanceof ConnectRuntimeException) msg = NET_ERROR_MSG;
        else if (e instanceof TimeoutRuntimeException) msg = TIME_OUT_MSG;
        else msg = GET_RESOURCE_FAILED_MSG;
        new TipDialog(f, msg).showDialog();
    }

    /**
     * 处理播放异常
     *
     * @param e
     * @return
     */
    public static void handlePlaybackException(Exception e, MainFrame f) {
        LogUtil.error(e);
        String msg;
        if (e instanceof MediaException) msg = UNSUPPORTED_AUDIO_FILE_MSG;
        else if (e instanceof IllegalArgumentException || e instanceof IllegalMediaException)
            msg = INVALID_AUDIO_FILE_MSG;
        else if (e instanceof ConnectRuntimeException) msg = NET_ERROR_MSG;
        else if (e instanceof TimeoutRuntimeException) msg = TIME_OUT_MSG;
        else msg = GET_RESOURCE_FAILED_MSG;
        new TipDialog(f, msg).showDialog();
    }
}
