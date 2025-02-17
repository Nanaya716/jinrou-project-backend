/**
 * @Author: nanaya
 * @Date: 2024/07/25/11:49
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定时任务的使用
 * @author pan_junbiao
 **/
@SpringBootTest


public class Task
{
    @Scheduled(cron="0/5 * *  * * ? ")   //每5秒执行一次
    @Test
    public void execute(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置日期格式
        System.out.println("欢迎访问 pan_junbiao的博客 " + df.format(new Date()));
    }
}