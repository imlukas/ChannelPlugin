package me.imlukas.devnicschatplugin.utils.schedulerutil.builders;

import lombok.Getter;
import me.imlukas.devnicschatplugin.utils.schedulerutil.data.ScheduleBuilderBase;
import me.imlukas.devnicschatplugin.utils.schedulerutil.data.ScheduleData;

public class RepeatableT2 implements ScheduleBuilderBase {

    @Getter
    private final ScheduleData data;

    RepeatableT2(ScheduleData data) {
        this.data = data;
    }

    public RepeatableBuilder run(Runnable runnable) {
        data.setRunnable(runnable);
        return new RepeatableBuilder(data);
    }
}
