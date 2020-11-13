package cn.lx.ihrm.system.service.impl;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * cn.lx.ihrm.system.service.impl
 *
 * @Author Administrator
 * @date 11:56
 */
public class CalendarTest {

    @Test
    public void test1(){
        //构造日期
        Calendar instance = Calendar.getInstance();
        //设置月份
        instance.set(Calendar.MONTH,Integer.parseInt("11")-1);
        //设置时分秒
        instance.set(Calendar.HOUR_OF_DAY,0);
        instance.set(Calendar.MINUTE,0);
        instance.set(Calendar.SECOND,0);

        //当前月第一天
        instance.set(Calendar.DAY_OF_MONTH,instance.getActualMinimum(Calendar.DAY_OF_MONTH));
        Date before = instance.getTime();

        //设置时分秒
        instance.set(Calendar.HOUR_OF_DAY,23);
        instance.set(Calendar.MINUTE,59);
        instance.set(Calendar.SECOND,59);

        //当前月最后一天
        instance.set(Calendar.DAY_OF_MONTH,instance.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date after = instance.getTime();

        System.out.println(after);
        System.out.println(before);
    }

}
