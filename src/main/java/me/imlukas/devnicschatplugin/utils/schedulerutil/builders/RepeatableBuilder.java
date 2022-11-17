package me.imlukas.devnicschatplugin.utils.schedulerutil.builders;

import lombok.Getter;
import me.imlukas.devnicschatplugin.utils.schedulerutil.data.ScheduleBuilderBase;
import me.imlukas.devnicschatplugin.utils.schedulerutil.data.ScheduleData;
import me.imlukas.devnicschatplugin.utils.schedulerutil.data.ScheduleThread;
import me.imlukas.devnicschatplugin.utils.schedulerutil.data.ScheduleTimestamp;

public class RepeatableBuilder extends ScheduleThread implements ScheduleBuilderBase {

    @Getter
    private final ScheduleData data;


    RepeatableBuilder(ScheduleData data) {
        super(data);
        this.data = data;
    }

    public ScheduleTimestamp<ScheduleThread> during(long amount) {
        return new ScheduleTimestamp<>(new ScheduleThread(data), amount, data::setCancelIn);
    }
}
