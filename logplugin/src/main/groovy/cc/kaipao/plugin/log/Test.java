package cc.kaipao.plugin.log;


import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * email:xubo@idongjia.cn
 * time:2018/11/14
 *
 * @author xb
 */
public class Test {


    private static Map<String, Long> startTimeMap = new HashMap<>();
    private static Map<String, Long> endTimeMap = new HashMap<>();

    private static String getCostTime(String methodName) {
        long startTime = startTimeMap.get(methodName);
        long endTime = endTimeMap.get(methodName);
        long detaTime = endTime - startTime;
        return methodName + " cost:" + detaTime + " ms";
    }


    public static void testRun(Runnable runnable) {
        runnable.run();
    }
    public static void setStartTime(String methodName, long time) {
        startTimeMap.put(methodName, time);
    }

    public static void setEndTime(String methodName, long time) {
        endTimeMap.put(methodName, time);
    }



    public static void main(String[] args) {

        setStartTime("main", System.currentTimeMillis());

        setEndTime("main", System.currentTimeMillis());

        String result = getCostTime("main");

        Log.d("LogPlugin", result);
        testRun(()->{
            System.out.println("hello");
        });

    }
}
class Log{
    static void d(String tag, String msg) {


    }
}
