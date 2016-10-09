package com.fr.bi.cal.generate.timerTask.quartz;

import com.finebi.cube.conf.CubeBuild;
import com.finebi.cube.conf.CubeGenerationManager;
import com.finebi.cube.fun.CubeTaskProvider;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.BuildCubeTask;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.plugin.ExtraClassManager;
import com.fr.third.org.quartz.Job;
import com.fr.third.org.quartz.JobDataMap;
import com.fr.third.org.quartz.JobExecutionContext;
import com.fr.third.org.quartz.JobExecutionException;

import java.util.Date;
import java.util.Set;

/**
 * Created by kary on 16/6/29.
 */

public class JobTask implements Job {

    public JobTask() {
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();
        final long userId = Long.valueOf(data.get("userId").toString());
        String jobName = data.getString("jobName");
//        String sourceName = data.getString("sourceName");

//        boolean tableExisted = null != TimerScheduleAdapter.tableCheck(userId, sourceName);
        /*删除表时会删除该表更新信息，不需要在这边再做检查*/
//        if (!tableExisted && !DBConstant.CUBE_UPDATE_TYPE.GLOBAL_UPDATE.equals(sourceName)) {
//            return;
//        }
        CubeBuild cubeBuild = (CubeBuild) data.get("CubeBuild");
        String message = "timerTask started!Current time is:" + new Date() + "\n Current task：" + jobName + "\nCurrent User：" + userId + "\n";
        BILogger.getLogger().info(message);
        final BuildCubeTask task = new BuildCubeTask(new BIUser(userId), cubeBuild);
        Set<CubeTaskProvider> set = ExtraClassManager.getInstance().getArray(CubeTaskProvider.MARK_STRING);

        boolean conditionMeet = false;
        for (CubeTaskProvider provider : set) {
            // 满足其中任意一个条件就加到列表中去，不再继续查找了
            if (provider.accept(task)) {
                conditionMeet = true;
                provider.addTaskIfConditionMeet(task, new CubeTaskProvider.Action() {
                    @Override
                    public void addTask() {
                        CubeGenerationManager.getCubeManager().addTask(task, userId);
                    }
                });
                break;
            }
        }
        // 如果没有任何一个插件实现条件满足，我们就认为没有限制，直接加进去
        if (!conditionMeet) {
            CubeGenerationManager.getCubeManager().addTask(task, userId);
        }
    }
}
