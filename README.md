<p align="center">
    <a href="https://github.com/AffectionParadise/LightMusic/releases">
        <img src="https://github.com/AffectionParadise/LightMusic/assets/70871914/cb364095-e139-4719-be55-ce75bc883c4b" alt="LightMusic">
    </a>
</p>

<p align="center">
    <a href="https://github.com/AffectionParadise/LightMusic/stargazers">
        <img alt="GitHub Repo stars" src="https://img.shields.io/github/stars/AffectionParadise/LightMusic">
    </a>
    <a href="https://github.com/AffectionParadise/LightMusic/network">
        <img alt="GitHub forks" src="https://img.shields.io/github/forks/AffectionParadise/LightMusic">
    </a>
    <a href="https://github.com/AffectionParadise/LightMusic/issues">
        <img alt="GitHub issues" src="https://img.shields.io/github/issues/AffectionParadise/LightMusic">
    </a>
    <a href="https://github.com/AffectionParadise/LightMusic/blob/master/LICENSE">
        <img alt="GitHub" src="https://img.shields.io/github/license/AffectionParadise/LightMusic">
    </a>
</p>

<h2 align="center">轻音——基于Swing与JavaFX的桌面端音乐播放器</h2>

### 说明

一个基于 Swing + JavaFX 的桌面端音乐软件。

所用技术栈：

- Java 8
- Swing
- JavaFX

已支持的平台：

- Windows 7 及以上
- Mac OS 和 Linux 平台也可自行编译并用第三方软件打包

使用时请仔细阅读软件指南（首次打开软件会显示），目前本项目的原始发布地址只有**GitHub**，其他渠道均为第三方转载，与本项目无关！

**重要：请不要在社交媒体推广此软件！请不要在社交媒体推广此软件！请不要在社交媒体推广此软件！独自安静使用就好！如果软件火了我会立即停止相关的服务！**

### 软件特性

- 支持本地音乐管理
- 支持同步其他平台的用户歌单，无需登录
- 在线音乐收听
- 支持歌单、专辑、歌手、电台搜索
- MV 与视频收看
- 音乐与视频下载功能
- 自定义界面主题
- 更多小功能等你探索~

### 软件截图

以下截图仅供参考，实际界面效果会随着软件更新发生变化。

![image](https://github.com/AffectionParadise/LightMusic/assets/70871914/1f24fa96-ab2a-401d-a572-d2bb236b718d)

### 关于源码
#### 环境
jdk8（**必须是最稳定的8版本**，连7、9、10、11或更高版本都不能使用，且**8自带了JavaFX开发工具包，无需额外下载**）。

#### 运行
```clone``` 后打开该项目，等待 ```Maven``` 初始化完成，运行 ```net.doge.ui.Launcher``` 类。

#### 打包
- **主程序**：在 ```LightMusic``` 项目结构中创建工件，然后构建该工件，就会获得一个关于主程序的可执行的 jar 包，叫做 ```LightMusic.jar```。
- **更新程序**：在 ```LightMusic-Updater``` 项目结构中创建工件，然后构建该工件，就会获得一个关于更新程序的可执行的 jar 包，叫做 ```LightMusic-Updater.jar```。

#### 编译
- 使用第三方软件将这两个 jar 包编译为可执行程序，例如 ```exe4j```。当然，不同的操作系统所使用的软件不同，需要使用者自己去研究。假设是 ```Windows``` 环境，获得了 ```LightMusic.exe``` 和 ```Updater.exe```。
- 编译完成后，满足以下文件位置关系就可以顺利运行和使用了。
```
.
├─jre：运行所需的 jvm 虚拟机环境，通常直接复制你所安装的 jre 目录。此项根据打包软件运行环境设置的不同而变化。此项缺失会导致程序无法启动。
├─plugin：即项目根路径下的 plugin 文件夹，里面是 ffmpeg 插件，遇到不支持的音视频格式时调用以转换格式。此项缺失会导致在极少数需要转换音频格式的场景下失败。
├─resource：即项目根路径下的 resource 文件夹，是程序运行所需的资源文件（包含图标、字体、主题等）。此项缺失会导致程序无法启动。
├─LightMusic.exe：主程序可执行程序。
└─Updater.exe：更新程序可执行程序。此项缺失会导致程序更新失败。
```

**注意事项**：以上环节如果有问题请发送 ```issue```，作者不定时会一一回复解决。**该源码中所有内容仅供个人学习使用，严禁用于商业用途！**

### 鸣谢
以下是为本项目提供了思路的开源项目，本人通过学习他们的源码，用自己的技术重构出一部分代码，为本项目添砖加瓦：
- [Binaryify/NeteaseCloudMusicApi](https://github.com/Binaryify/NeteaseCloudMusicApi)
- [ecitlm/Kugou-api](https://github.com/ecitlm/Kugou-api)
- [jsososo/QQMusicApi](https://github.com/jsososo/QQMusicApi)
- [QiuYaohong/kuwoMusicApi](https://github.com/QiuYaohong/kuwoMusicApi)
- [jsososo/MiguMusicApi](https://github.com/jsososo/MiguMusicApi)
- [SocialSisterYi/bilibili-API-collect](https://github.com/SocialSisterYi/bilibili-API-collect)
- [lyswhut/lx-music-source](https://github.com/lyswhut/lx-music-source)
- [QiuChenlyOpenSource/MusicDownload](https://github.com/QiuChenlyOpenSource/MusicDownload)

### 项目协议

本项目基于 [Apache License 2.0](https://github.com/AffectionParadise/LightMusic/blob/master/LICENSE) 许可证发行，以下协议是对于 Apache License 2.0 的补充，如有冲突，以以下协议为准。

词语约定：本协议中的“本项目”指轻音(LightMusic)项目；“使用者”指签署本协议的使用者；“官方音乐平台”指对本项目内置的所有音乐源的官方平台统称；“版权数据”指包括但不限于图像、音频、名字等在内的他人拥有所属版权的数据。

1. 本项目的数据来源原理是从各官方音乐平台的公开服务器中拉取数据，经过对数据简单地筛选与合并后进行展示，因此本项目不对数据的准确性负责。
2. 使用本项目的过程中可能会产生版权数据，对于这些版权数据，本项目不拥有它们的所有权，为了避免造成侵权，使用者务必在**24小时**内清除使用本项目的过程中所产生的版权数据。
3. 本项目内的官方音乐平台别名为本项目内对官方音乐平台的一个称呼，不包含恶意，如果官方音乐平台觉得不妥，可联系本项目更改或移除。
4. 本项目内使用的部分包括但不限于字体、图片等资源来源于互联网，如果出现侵权可联系本项目移除。
5. 由于使用本项目产生的包括由于本协议或由于使用或无法使用本项目而引起的任何性质的任何直接、间接、特殊、偶然或结果性损害（包括但不限于因商誉损失、停工、计算机故障或故障引起的损害赔偿，或任何及所有其他商业损害或损失）由使用者负责。
6. 本项目完全免费，且开源发布于 GitHub 面向全世界人用作对技术的学习交流，本项目不对项目内的技术可能存在违反当地法律法规的行为作保证，**禁止在违反当地法律法规的情况下使用本项目**，对于使用者在明知或不知当地法律法规不允许的情况下使用本项目所造成的任何违法违规行为由使用者承担，本项目不承担由此造成的任何直接、间接、特殊、偶然或结果性责任。

若你使用了本项目，将代表你接受以上协议。

音乐平台不易，请尊重版权，支持正版。<br>
