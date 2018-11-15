package cc.kaipao.plugin.log.utils;

import java.io.File;

/**
 * <p>
 * email:xubo@idongjia.cn
 * time:2018/11/12
 *
 * @author xb
 */
public class TextUtil {

    public static String path2ClassName(String pathName) {
        return pathName.replace(File.pathSeparator, "").replace(".class", "");
        
    }
}
