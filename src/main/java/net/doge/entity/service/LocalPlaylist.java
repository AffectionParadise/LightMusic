package net.doge.entity.service;

import lombok.Data;
import net.doge.entity.service.base.MusicResource;

import javax.swing.*;

/**
 * 本地创建的歌单
 */
@Data
public class LocalPlaylist {
    private boolean isDefault;
    private String name;
    private DefaultListModel<MusicResource> musicListModel;

    public LocalPlaylist(String name) {
        this(name, false);
    }

    public LocalPlaylist(String name, boolean isDefault) {
        this.name = name;
        this.isDefault = isDefault;
        musicListModel = new DefaultListModel<>();
    }
}
