package com.嘤嘤嘤.qwq.MailBox.VexView;

import lk.vexview.hud.VexImageShow;

public class MailBoxHud extends VexImageShow{
    
    public static String id;
    private static String img;
    private static int x;
    private static int y;
    private static int w;
    private static int h;
    private static int ww;
    private static int hh;
    
    public MailBoxHud() {
        super(id,img,x,y,w,h,ww,hh,0);
    }
    
    public static void setHudConfig(String id, String img, int x, int y, int w, int h, int ww, int hh){
        MailBoxHud.id = id;
        MailBoxHud.img = img;
        MailBoxHud.x = x;
        MailBoxHud.y = y;
        MailBoxHud.w = w;
        MailBoxHud.h = h;
        MailBoxHud.ww = ww;
        MailBoxHud.hh = hh;
    }
    
}
