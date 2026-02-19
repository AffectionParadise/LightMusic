package net.doge.sdk.service.music.tag;

import net.doge.constant.core.data.Tags;
import net.doge.sdk.common.entity.executor.MultiRunnableExecutor;
import net.doge.sdk.service.music.tag.impl.programtag.MeProgramTagReq;

public class ProgramTagReq {
    private static ProgramTagReq instance;

    private ProgramTagReq() {
    }

    public static ProgramTagReq getInstance() {
        if (instance == null) instance = new ProgramTagReq();
        return instance;
    }

    /**
     * 加载节目标签
     *
     * @return
     */
    public void initProgramTag() {
        // 猫耳 猫耳
        Tags.programTags.put("默认", new String[]{"", ""});

        MultiRunnableExecutor executor = new MultiRunnableExecutor();
        executor.submit(() -> MeProgramTagReq.getInstance().initExpProgramTag());
        executor.submit(() -> MeProgramTagReq.getInstance().initProgramIndexTag());
        executor.await();
    }
}
