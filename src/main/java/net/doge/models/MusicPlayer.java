package net.doge.models;

import com.mpatric.mp3agic.*;
import com.sun.media.jfxmedia.locator.Locator;
import it.sauronsoftware.jave.EncoderException;
import javafx.collections.ObservableList;
import javafx.scene.media.AudioEqualizer;
import javafx.scene.media.EqualizerBand;
import javafx.scene.media.MediaPlayer;
import net.doge.constants.*;
import net.doge.ui.PlayerFrame;
import net.doge.utils.*;
import javafx.scene.media.Media;
import javafx.util.Duration;
import lombok.Data;

import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;

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
    // 载入的在线音乐信息，如果是离线音乐则为 null
    private NetMusicInfo netMusicInfo;
    // 当前播放器状态
    private int status;
    // 播放器
    private MediaPlayer mp;

    // 声音数据，用于获取频谱
    private double[] specs = new double[SpectrumConstants.NUM_BANDS];
    private double[] specsOrigin = new double[SpectrumConstants.NUM_BANDS];
    private double[] specsGap = new double[SpectrumConstants.NUM_BANDS];

    public MusicPlayer(PlayerFrame f) {
        this.f = f;
        status = PlayerStatus.EMPTY;
    }

    // 判断是否支持该格式
    public boolean support(String format) {
        for (String fmt : Format.AUDIO_TYPE_SUPPORTED) {
            if (fmt.equals(format.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    // 载入文件
    public void load(String source, NetMusicInfo netMusicInfo) throws IOException, UnsupportedAudioFileException, EncoderException, InvalidDataException, UnsupportedTagException, URISyntaxException {
        load(new AudioFile(source), netMusicInfo);
    }

    public void load(AudioFile source, NetMusicInfo netMusicInfo) throws IOException, UnsupportedAudioFileException, EncoderException, InvalidDataException, UnsupportedTagException, URISyntaxException {
        // 先清除上一次播放数据
        clearMetadata();
        // 初始化 MediaPlayer 对象
        initialMp(source, netMusicInfo);
        // 加载音乐信息
        initialMusicInfo(source, netMusicInfo);
        status = PlayerStatus.LOADED;
    }

    // 清除上一次的播放数据
    private void clearMetadata() {
        musicInfo.setFile(null);
        musicInfo.setFormat(null);
        musicInfo.setName(null);
        musicInfo.setArtist(null);
        musicInfo.setAlbumName(null);
        musicInfo.setInvokeLater(null);
        musicInfo.setAlbumImage(null);
        musicInfo.setDuration(0);
        netMusicInfo = null;
    }

    // 卸载当前文件
    public void unload() {
        if (mp != null) {
            mp.dispose();
            mp = null;
        }
        clearMetadata();
        status = PlayerStatus.EMPTY;
    }

    // 判断播放器是否载入了歌曲
    public boolean loadedMusic() {
        return loadedFile() || loadedNetMusic();
    }

    // 判断播放器是否载入了文件
    public boolean loadedFile() {
        return musicInfo != null && musicInfo.hasFile();
    }

    // 判断播放器是否载入了在线音乐
    public boolean loadedNetMusic() {
        return netMusicInfo != null;
    }

    // 判断是否在播放状态
    public boolean isPlaying() {
        return status == PlayerStatus.PLAYING;
    }

    // 判断是否在空状态
    public boolean isEmpty() {
        return status == PlayerStatus.EMPTY;
    }

    // 判断播放器是否正在播放某文件
    public boolean isPlayingFile(File file) {
        if (!loadedFile() || file == null) return false;
        return musicInfo.getFile().equals(file);
    }

    // 判断播放器是否正在播放在线音乐
    public boolean isPlayingNetMusic() {
        return netMusicInfo != null;
    }

    // 判断播放器是否正在播放某在线音乐
    public boolean isPlayingNetMusic(NetMusicInfo netMusicInfo) {
        if (!loadedNetMusic() || netMusicInfo == null) return false;
        return netMusicInfo.equals(this.netMusicInfo);
    }

    // 判断播放器是否正在播放某对象
    public boolean isPlayingObject(Object o) {
        if (!loadedMusic()) return false;
        if (o instanceof File) return isPlayingFile((File) o);
        else if (o instanceof NetMusicInfo) return isPlayingNetMusic((NetMusicInfo) o);
        return false;
    }

    // 初始化音频信息(pcm wav)
    private void initialMusicInfo(AudioFile source, NetMusicInfo netMusicInfo) throws IOException, EncoderException, InvalidDataException, UnsupportedTagException, UnsupportedAudioFileException {
        this.netMusicInfo = netMusicInfo;

        // 音频格式(以 source 文件为准)
        musicInfo.setFormat(source == null ? netMusicInfo.getFormat() : source.getFormat());
        // 时长(优先考虑 NetMusicInfo 的 duration 属性，有时 getDuration 方法返回的时长不准确)
        musicInfo.setDuration(netMusicInfo != null ? netMusicInfo.getDuration() : source.getDuration());
        // 文件
        musicInfo.setFile(source);

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
//            if (netMusicInfo.hasAlbumImage()) musicInfo.setAlbumImage(netMusicInfo.getAlbumImage());
//            else netMusicInfo.setInvokeLater(() -> musicInfo.setAlbumImage(netMusicInfo.getAlbumImage()));
            GlobalExecutors.imageExecutor.submit(() -> {
                if (!netMusicInfo.hasAlbumImage()) {
                    netMusicInfo.setInvokeLater(() -> {
                        BufferedImage albumImage = netMusicInfo.getAlbumImage();
                        musicInfo.setAlbumImage(albumImage == null ? f.getDefaultAlbumImage() : albumImage);
                    });
                } else musicInfo.setAlbumImage(netMusicInfo.getAlbumImage());
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
                musicInfo.setAlbumImage(albumImage == null ? f.getDefaultAlbumImage() : albumImage);
            });
        }
        // 其他类型的文件信息
        else {
            musicInfo.setName(source.getNameWithoutSuffix());
            musicInfo.setArtist("未知");
            musicInfo.setAlbumName("未知");
            musicInfo.setAlbumImage(f.getDefaultAlbumImage());
        }
    }

    // 初始化 MediaPlayer 对象
    public void initialMp(AudioFile source, NetMusicInfo netMusicInfo) {
        // 加载文件(在线音乐直接播放 url)
        Media media = new Media(source == null ? netMusicInfo.getUrl() : source.toURI().toString());

        if (netMusicInfo != null && netMusicInfo.getSource() == NetMusicSource.BI) {
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

        if (mp != null) {
            mp.dispose();
            mp = null;
        }
        mp = new MediaPlayer(media);
        play();
        // 播放器状态监听器
        f.loadMonitor(mp);
        // 设置频谱更新间隔
        mp.setAudioSpectrumInterval(SpectrumConstants.PLAYER_INTERVAL);
        // 设置频谱更新数量
        mp.setAudioSpectrumNumBands(SpectrumConstants.NUM_BANDS);
        // 设置频谱阈值，默认是 -60
        mp.setAudioSpectrumThreshold(SpectrumConstants.THRESHOLD);
    }

    public void play() {
        if (mp == null) return;
        mp.play();
        status = PlayerStatus.PLAYING;
    }

    // 暂停
    public void pause() {
        if (mp == null) return;
        mp.pause();
        status = PlayerStatus.PAUSING;
    }

    // 继续播放
    public void continuePlay() {
        play();
    }

    // 重新播放
    public void replay() {
        if (mp != null) mp.seek(Duration.seconds(0));
    }

    // 设置音量
    public void setVolume(double volume) {
        if (mp != null) mp.setVolume(volume);
    }

    // 快进
    public void forward(double seconds) {
        if (mp != null) mp.seek(getCurrTimeDuration().add(Duration.seconds(seconds)));
    }

    // 快退
    public void backward(double seconds) {
        if (mp != null) mp.seek(getCurrTimeDuration().subtract(Duration.seconds(seconds)));
    }

    // 设置静音
    public void setMute(boolean isMute) {
        if (mp != null) mp.setMute(isMute);
    }

    // 设置均衡
    public void setBalance(double b) {
        if (mp != null) mp.setBalance(b);
    }

    // 设置当前播放时间
    public void seek(double t) {
        if (mp != null) mp.seek(Duration.seconds(t));
    }

    // 设置播放速率
    public void setRate(double rate) {
        if (mp != null) mp.setRate(rate);
    }

    // 调整均衡器增益
    public void adjustEqualizerBands(double[] ds) {
        if (mp != null) {
            AudioEqualizer audioEqualizer = mp.getAudioEqualizer();
            if (audioEqualizer != null) {
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
        }
    }

    // 获取当前均衡器增益
    public double[] getEqualizerBands() {
        if (mp != null) {
            ObservableList<EqualizerBand> bands = mp.getAudioEqualizer().getBands();
            double[] data = new double[EqualizerData.BAND_NUM];
            for (int i = 0, size = bands.size(); i < size; i++) {
                EqualizerBand band = bands.get(i);
                data[i] = band.getGain();
            }
            return data;
        }
        return null;
    }

    // 获取缓冲完成的时间
    public double getBufferedSeconds() {
        return mp != null && mp.getBufferProgressTime() != null ? mp.getBufferProgressTime().toSeconds() : 0;
    }

    // 获取当前播放时间
    public double getCurrTimeSeconds() {
        return mp != null ? mp.getCurrentTime().toSeconds() : 0;
    }

    // 获取当前播放时间(Duration)
    public Duration getCurrTimeDuration() {
        return mp != null ? mp.getCurrentTime() : null;
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
        return musicInfo != null ? TimeUtils.format(musicInfo.getDuration()) : "00:00";
    }

    // 获取总时间秒
    public double getDurationSeconds() {
        return musicInfo != null ? musicInfo.getDuration() : 0;
    }
}