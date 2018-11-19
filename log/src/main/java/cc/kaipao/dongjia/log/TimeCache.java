package cc.kaipao.dongjia.log;

import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * email:xubo@idongjia.cn
 * time:2018/11/14
 *
 * @author xb
 */
public class TimeCache {

    private static Map<String, Long> startTimeMap = new HashMap<>();
    private static Map<String, Long> endTimeMap = new HashMap<>();

    public static String getCostTime(String methodName) {
        long startTime = startTimeMap.get(methodName);
        long endTime = endTimeMap.get(methodName);
        long detaTime = endTime - startTime;
        return methodName + " cost:" + detaTime + " ms";
    }

    public static void setStartTime(String methodName, long time) {
        startTimeMap.put(methodName, time);
    }

    public static void setEndTime(String methodName, long time) {
        endTimeMap.put(methodName, time);
    }


}
