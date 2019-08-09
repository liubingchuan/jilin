package com.xitu.app.utils;


import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类
 * <p>
 */
public class LogUtil {
	private static final Logger log = LoggerFactory.getLogger(LogUtil.class);

    /**
     * 打印信息
     *
     * @param obj
     */
    public static void info(Object obj) {
        try {
            // 获取输出信息的代码的位置
            String location = getCodeLocation();
            // 是否是异常
            if (obj instanceof Exception) {
                Exception e = (Exception) obj;
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw, true));
                String str = sw.toString();
                log.info(location + str);
            } else {
                log.info(location + obj.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印警告
     *
     * @param obj
     */
    public static void warn(Object obj) {
        try {
            // 获取输出信息的代码的位置
            String location = getCodeLocation();
            // 是否是异常
            if (obj instanceof Exception) {
                Exception e = (Exception) obj;
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw, true));
                String str = sw.toString();
                log.warn(location + str);
            } else {
                log.warn(location + obj.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印错误
     *
     * @param obj
     */
    public static void error(Object obj) {
        try {
            // 获取输出信息的代码的位置
            String location = getCodeLocation();
            // 是否是异常
            if (obj instanceof Exception) {
                Exception e = (Exception) obj;
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw, true));
                String str = sw.toString();
                log.error(location + str);
            } else {
                log.error(location + obj.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取调用此函数的代码的位置
     *
     * @return
     */
    public static String getCodeLocation() {
        StringBuffer sb = new StringBuffer();
        try {
            // 获取输出信息的代码的位置
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            // 类名
            sb.append(stacks[3].getClassName());
            sb.append("-");
            // 方法名
            sb.append(stacks[3].getMethodName());
            // 行号
            sb.append("(line: " + stacks[3].getLineNumber() + ")");
        } catch (Exception e) {
        }
        return sb.toString();
    }
}
