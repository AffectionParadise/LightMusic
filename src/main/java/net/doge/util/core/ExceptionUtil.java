package net.doge.util.core;

import javafx.scene.media.MediaException;
import net.doge.constant.core.lang.I18n;
import net.doge.exception.IllegalMediaException;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.dialog.TipDialog;

/**
 * @Author Doge
 * @Description 异常处理工具类
 * @Date 2020/12/15
 */
public class ExceptionUtil {
    private static final String NO_NET_MSG = I18n.getText("noNetMsg");
    private static final String TIME_OUT_MSG = I18n.getText("timeOutMsg");
    private static final String API_ERROR_MSG = I18n.getText("apiErrorMsg");
    private static final String GET_RESOURCE_FAILED_MSG = I18n.getText("getResourceFailedMsg");
    private static final String UNSUPPORTED_AUDIO_FILE_MSG = I18n.getText("unsupportedAudioFileMsg");
    private static final String INVALID_AUDIO_FILE_MSG = I18n.getText("invalidAudioFileMsg");

    /**
     * 处理异常
     *
     * @param e
     * @return
     */
    public static void handleRequestException(Exception e, MainFrame f) {
        e.printStackTrace();
        new TipDialog(f, API_ERROR_MSG).showDialog();
    }

    /**
     * 处理音乐资源获取异常
     *
     * @param e
     * @return
     */
    public static void handleMusicResourceException(Exception e, MainFrame f) {
        e.printStackTrace();
        new TipDialog(f, GET_RESOURCE_FAILED_MSG).showDialog();
    }

    /**
     * 处理播放异常
     *
     * @param e
     * @return
     */
    public static void handlePlaybackException(Exception e, MainFrame f) {
        e.printStackTrace();
        if (e instanceof MediaException) new TipDialog(f, UNSUPPORTED_AUDIO_FILE_MSG).showDialog();
        else if (e instanceof IllegalArgumentException || e instanceof IllegalMediaException)
            new TipDialog(f, INVALID_AUDIO_FILE_MSG).showDialog();
        else new TipDialog(f, GET_RESOURCE_FAILED_MSG).showDialog();
    }
}
