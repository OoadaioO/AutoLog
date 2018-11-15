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

    private static String getCostTime(String methodName) {
        long startTime = startTimeMap.get(methodName);
        long endTime = endTimeMap.get(methodName);
        long detaTime = endTime - startTime;
        return methodName + " cost:" + detaTime + " ms";
    }

    private static void setStartTime(String methodName, long time) {
        startTimeMap.put(methodName, time);
    }

    private static void setEndTime(String methodName, long time) {
        endTimeMap.put(methodName, time);
    }


    public static View.OnClickListener inject(final View.OnClickListener listener) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                setStartTime("onClick", System.currentTimeMillis());
                listener.onClick(view);
                setEndTime("onClick", System.currentTimeMillis());
                Log.d("AutoLog", getCostTime("onClick"));
            }
        };
    }


    public static void main(String[] args) {
        View view = new View(null);
        view.setOnClickListener(inject(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("test");
            }
        }));
    }

}
