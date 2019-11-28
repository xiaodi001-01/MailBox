package com.嘤嘤嘤.qwq.MailBox;

import java.util.List;

public class GlobalConfig {
    public static boolean enVexView;
    public static boolean enVault;
    
    private static String mailDisplay_SYSTEM;
    private static String mailDisplay_PLAYER;
    public static String pluginPrefix;
    public static String normal;
    public static String success;
    public static String warning;
    public static String fileDiv;
    public static String fileCmdPlayer;
    public static String expiredDay;
    public static List<Integer> player_out;
    public static String vaultDisplay;
    public static int vaultMax;
    
    public static boolean setVexView(boolean vv){
        enVexView = vv;
        return enVexView;
    }
    
    public static boolean setVault(boolean v){
        enVault = v;
        return enVault;
    }
    
    public static void setGlobalConfig(
        String pluginPrefix,
        String normal,
        String success,
        String warning,
        String mailDisplay_SYSTEM,
        String mailDisplay_PLAYER,
        String fileDiv,
        String fileCmdPlayer,
        String expiredDay,
        List<Integer> player_out,
        String vaultDisplay,
        int vaultMax
    ){
        // 全局
        GlobalConfig.pluginPrefix = pluginPrefix;// 插件提示信息前缀
        GlobalConfig.normal = normal;// 普通 插件信息颜色
        GlobalConfig.success = success;// 成功 插件信息颜色
        GlobalConfig.warning = warning;// 失败 插件信息颜色
        GlobalConfig.mailDisplay_SYSTEM = mailDisplay_SYSTEM;// system 邮件显示名称
        GlobalConfig.mailDisplay_PLAYER = mailDisplay_PLAYER;// player 邮件显示名称
        // 附件
        GlobalConfig.fileDiv = fileDiv;// 分割符
        GlobalConfig.fileCmdPlayer = fileCmdPlayer;// 领取邮件的玩家变量
        // player邮件
        GlobalConfig.expiredDay = expiredDay;// 过期时间
        GlobalConfig.player_out = player_out;// 玩家发件量
        // [Vault]设置
        GlobalConfig.vaultDisplay = vaultDisplay;// 显示名称
        GlobalConfig.vaultMax = vaultMax;// 单次邮件发送最大值
    }
    
    public static String getTypeName(String type) {
        switch (type) {
            case "system":
                return mailDisplay_SYSTEM;
            case "player": 
                return mailDisplay_PLAYER;
            default:
                return null;
        }
    }
}
