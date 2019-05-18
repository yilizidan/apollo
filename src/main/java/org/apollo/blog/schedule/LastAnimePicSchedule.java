package org.apollo.blog.schedule;

import lombok.extern.slf4j.Slf4j;
import org.apollo.blog.util.LocalDateTimeUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class LastAnimePicSchedule {

    @Scheduled(cron = "0 0 0/2 * * ?")
    public void downAnimePic() {
        log.info("开始执行拉取最新更新图片定时任务，当前时间：{}", LocalDateTimeUtil.getHourDay());
    }
}