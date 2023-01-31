package net.doge.models;

import com.sun.media.jfxmedia.locator.Locator;
import javafx.collections.ObservableList;
import javafx.scene.media.AudioEqualizer;
import javafx.scene.media.EqualizerBand;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import lombok.Data;
import net.doge.constants.*;
import net.doge.models.entity.AudioFile;
import net.doge.models.entity.NetMusicInfo;
import net.doge.models.entity.SimpleMusicInfo;
import net.doge.ui.PlayerFrame;
import net.doge.utils.MusicUtils;
import net.doge.utils.StringUtils;
import net.doge.utils.TimeUtils;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author yzx
 * @description
 * @date 2020/12/7
 */
@Data
public class MusicPlayer {
    // 播放界面
    private PlayerFrame f;
    // 当前载入的文件的信息
    private SimpleMusicInfo musicInfo = new SimpleMusicInfo();
    // 载入的本地音乐信息
    private AudioFile audioFile;
    // 载入的在线音乐信息，如果是本地音乐则为 null
    private NetMusicInfo netMusicInfo;
    // 当前播放器状态
    private int status;
    // 播放器
    private MediaPlayer mp;

    // 频谱数据
    public double[] specs = new double[SpectrumConstants.NUM_BANDS];
    public double[] specsOrigin = new double[SpectrumConstants.NUM_BANDS];
    public double[] specsGap = new double[SpectrumConstants.NUM_BANDS];

    public MusicPlayer(PlayerFrame f) {
        this.f = f;
        status = PlayerStatus.EMPTY;
    }

    // 判断是否支持该格式
    public boolean support(String format) {
        for (String fmt : Format.AUDIO_TYPE_SUPPORTED) {
            if (!fmt.equals(format.toLowerCase())) continue;
            return true;
        }
        return false;
    }

    // 载入文件
    public void load(String source, NetMusicInfo netMusicInfo) {
        load(new AudioFile(source), netMusicInfo);
    }

    public void load(AudioFile source, NetMusicInfo netMusicInfo) {
        // 先清除上一次播放数据
        clearMetadata();
        // 释放上一个 MediaPlayer 对象
        disposeMp();
        // 加载音乐信息
        initMusicInfo(source, netMusicInfo);
        status = PlayerStatus.LOADED;
    }

    // 清除上一次的播放数据
    private void clearMetadata() {
        musicInfo.setFormat(null);
        musicInfo.setName(null);
        musicInfo.setArtist(null);
        musicInfo.setAlbumName(null);
        musicInfo.setDuration(0);
        musicInfo.setInvokeLater(null);
        musicInfo.setAlbumImage(null);
        audioFile = null;
        netMusicInfo = null;

        for (int i = 0, len = specsOrigin.length; i < len; i++) {
            specsOrigin[i] = 0;
            specsGap[i] = specs[i];
        }
    }

    // 卸载当前文件
    public void unload() {
        disposeMp();
        clearMetadata();
        status = PlayerStatus.EMPTY;
    }

    // 判断播放器是否载入了歌曲
    public boolean loadedMusic() {
        return loadedAudioFile() || loadedNetMusic();
    }

    // 判断播放器是否载入了文件
    public boolean loadedAudioFile() {
        return netMusicInfo == null && audioFile != null;
    }

    // 判断播放器是否载入了指定文件
    public boolean loadedAudioFile(AudioFile file) {
        if (!loadedAudioFile()) return false;
        return audioFile.equals(file);
    }

    // 判断播放器是否载入了在线音乐
    public boolean loadedNetMusic() {
        return netMusicInfo != null;
    }

    // 判断播放器是否载入了指定在线音乐
    public boolean loadedNetMusic(NetMusicInfo netMusicInfo) {
        if (!loadedNetMusic()) return false;
        return netMusicInfo.equals(this.netMusicInfo);
    }

    // 判断播放器是否载入了指定对象
    public boolean loadedObject(Object o) {
        if (o instanceof AudioFile) return loadedAudioFile((AudioFile) o);
        else if (o instanceof NetMusicInfo) return loadedNetMusic((NetMusicInfo) o);
        return false;
    }

    // 判断是否在空状态
    public boolean isEmpty() {
        return status == PlayerStatus.EMPTY;
    }

    // 判断是否在就绪状态
    public boolean isLoaded() {
        return status == PlayerStatus.LOADED;
    }

    // 判断是否在播放状态
    public boolean isPlaying() {
        return status == PlayerStatus.PLAYING;
    }

    // 判断是否在暂停状态
    public boolean isPaused() {
        return status == PlayerStatus.PAUSED;
    }

    // 判断是否在停止状态
    public boolean isStopped() {
        return status == PlayerStatus.STOPPED;
    }

    // 初始化音频信息(pcm wav)
    private void initMusicInfo(AudioFile source, NetMusicInfo netMusicInfo) {
        this.audioFile = source;
        this.netMusicInfo = netMusicInfo;

        // 音频格式(以 source 文件为准)
        musicInfo.setFormat(source == null ? netMusicInfo.getFormat() : source.getFormat());
        // 时长(优先考虑 NetMusicInfo 的 duration 属性，有时 getDuration 方法返回的时长不准确)
        musicInfo.setDuration(netMusicInfo != null ? netMusicInfo.getDuration() : source.getDuration());

        // 在线音乐的信息
        if (netMusicInfo != null) {
            String name = netMusicInfo.getName();
            String artist = netMusicInfo.getArtist();
            String albumName = netMusicInfo.getAlbumName();

            // 歌曲名称
            musicInfo.setName(StringUtils.isEmpty(name) ? "未知" : name);
            // 艺术家
            musicInfo.setArtist(StringUtils.isEmpty(artist) ? "未知" : artist);
            // 专辑名称
            musicInfo.setAlbumName(StringUtils.isEmpty(albumName) ? "未知" : albumName);
            // 专辑图片
            GlobalExecutors.imageExecutor.submit(() -> {
                if (!netMusicInfo.hasAlbumImage()) {
                    netMusicInfo.setInvokeLater(() -> {
                        BufferedImage albumImage = netMusicInfo.getAlbumImage();
                        musicInfo.setAlbumImage(albumImage != null ? albumImage : f.defaultAlbumImage);
                        f.showAlbumImage();
                    });
                } else {
                    musicInfo.setAlbumImage(netMusicInfo.getAlbumImage());
                    f.showAlbumImage();
                }
            });
            return;
        }
        // MP3 文件的其他信息
        if (musicInfo.isMp3()) {
            String artist = source.getArtist();
            String albumName = source.getAlbum();
            // 歌曲名称
            musicInfo.setName(source.hasSongName() ? source.getSongName() : source.getNameWithoutSuffix());
            // 艺术家
            musicInfo.setArtist(StringUtils.isEmpty(artist) ? "未知" : artist);
            // 专辑
            musicInfo.setAlbumName(StringUtils.isEmpty(albumName) ? "未知" : albumName);

            // 获取 MP3 专辑图片
            GlobalExecutors.imageExecutor.submit(() -> {
                BufferedImage albumImage = MusicUtils.getAlbumImage(source);
                musicInfo.setAlbumImage(albumImage != null ? albumImage : f.defaultAlbumImage);
                f.showAlbumImage();
            });
        }
        // 其他类型的文件信息
        else {
            musicInfo.setName(source.getNameWithoutSuffix());
            musicInfo.setArtist("未知");
            musicInfo.setAlbumName("未知");
            musicInfo.setAlbumImage(f.defaultAlbumImage);
            f.showAlbumImage();
        }
    }

    // 释放 MediaPlayer 对象
    public void disposeMp() {
        if (mp == null) return;
        // 可能会造成死锁，交给线程池处理
        Future<?> future = GlobalExecutors.requestExecutor.submit(() -> mp.dispose());
        try {
            future.get(100, TimeUnit.MILLISECONDS);
        } catch (Exception e) {

        } finally {
            if (!future.isDone()) future.cancel(true);
        }
        mp = null;
    }

    private void initRequestHeaders(NetMusicInfo netMusicInfo, Media media) {
        if (netMusicInfo == null || netMusicInfo.getSource() != NetMusicSource.BI) return;
        try {
            // 由于 Media 类不能重写，只能通过反射机制设置请求头
            Field field = Media.class.getDeclaredField("jfxLocator");
            field.setAccessible(true);
            Locator locator = (Locator) field.get(media);
            locator.setConnectionProperty("referer", "http://www.bilibili.com/");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // 初始化 MediaPlayer 对象
    public void initMp() {
        // 加载文件(在线音乐直接播放 url)
        Media media = new Media(audioFile != null ? audioFile.toURI().toString() : netMusicInfo.getUrl());
        initRequestHeaders(netMusicInfo, media);
        mp = new MediaPlayer(media);
        // 初始化 MediaPlayer 设置
        f.initPlayer();
    }

    // 播放
    public boolean play() {
        if (mp == null) return false;
        mp.play();
        status = PlayerStatus.PLAYING;
        return true;
    }

    // 暂停
    public void pause() {
        if (mp == null) return;
        mp.pause();
        status = PlayerStatus.PAUSED;
    }

    // 停止
    public void stop() {
        if (mp == null) return;
        status = PlayerStatus.STOPPED;
    }

    // 重新播放
    public void replay() {
        if (mp == null) return;
        mp.seek(Duration.seconds(0));
        status = PlayerStatus.PLAYING;
    }

    // 设置音量
    public void setVolume(double volume) {
        if (mp == null) return;
        mp.setVolume(volume);
    }

    // 设置静音
    public void setMute(boolean mute) {
        if (mp == null) return;
        mp.setMute(mute);
    }

    // 设置均衡
    public void setBalance(double b) {
        if (mp == null) return;
        mp.setBalance(b);
    }

    // 设置当前播放时间
    public void seek(double t) {
        if (mp == null) return;
        mp.seek(Duration.seconds(t));
        // 如果停止播放，改变播放进度时先暂停，不然会自动播放
        if(isStopped()) pause();
    }

    // 设置播放速率
    public void setRate(double rate) {
        if (mp == null) return;
        mp.setRate(rate);
    }

    // 调整均衡器增益
    public void adjustEqualizerBands(double[] ds) {
        if (mp == null) return;
        AudioEqualizer audioEqualizer = mp.getAudioEqualizer();
        if (audioEqualizer == null) return;
        ObservableList<EqualizerBand> bands = audioEqualizer.getBands();
        bands.get(0).setCenterFrequency(31.25);
        bands.get(1).setCenterFrequency(62.5);
        for (int i = 0, size = bands.size(); i < size; i++) {
            EqualizerBand band = bands.get(i);
            double val = ds[i];
            val = Math.min(val, EqualizerBand.MAX_GAIN);
            val = Math.max(val, EqualizerBand.MIN_GAIN);
            band.setGain(val);
        }
    }

    // 获取缓冲完成的时间
    public double getBufferedSeconds() {
        if (mp == null) return 0;
        Duration progressTime = mp.getBufferProgressTime();
        return progressTime != null ? progressTime.toSeconds() : 0;
    }

    // 获取当前播放时间
    public double getCurrTimeSeconds() {
        return mp != null ? mp.getCurrentTime().toSeconds() : 0;
    }

    // 获取当前进度比例
    public double getCurrScale() {
        double s = getCurrTimeSeconds() / musicInfo.getDuration();
        return s <= 1 ? s : Double.isInfinite(s) ? 0 : 1;
    }

    // 获取当前进度字符串
    public String getCurrTimeString() {
        return TimeUtils.format(getCurrTimeSeconds());
    }

    // 获取总时间字符串
    public String getDurationString() {
        return TimeUtils.format(musicInfo.getDuration());
    }

    // 获取总时间秒
    public double getDurationSeconds() {
        return musicInfo.getDuration();
    }
}