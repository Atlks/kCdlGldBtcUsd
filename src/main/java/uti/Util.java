package uti;

public class Util {


  public   static void iniLogCfg() {
        // 设置 slf4j-simple 的日志配置
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd HH:mm:ss");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "true");
        System.setProperty("org.slf4j.simpleLogger.showLogName", "true");       // 全类名
        System.setProperty("org.slf4j.simpleLogger.showShortLogName", "false"); // 短类名
    }
}
