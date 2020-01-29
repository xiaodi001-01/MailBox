package com.tripleying.qwq.MailBox.Utils;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.MailBox;
import com.tripleying.qwq.MailBox.Message;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class UpdateCheck {
    
    // 从远程连接获取最新版本号
    private static ArrayList<String> getVersion(String httpurl, boolean first){
        try{
            URL url = new URL(httpurl);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(3000);
            urlConnection.setReadTimeout(2000);
            HttpURLConnection connection;
            if(urlConnection instanceof HttpURLConnection)
            {
               connection = (HttpURLConnection) urlConnection;
            }
            else
            {
               return null;
               
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            ArrayList<String> urlString = new ArrayList();
            String current;
            while((current = in.readLine()) != null)
            {
                urlString.add(current);
            }
            return urlString;
        }catch(IOException e){
            if(first){
                // 尝试从备用链接获取最新版本号
                return getVersion("https://dogend233.github.io/version.txt", false);
            }else{
                Bukkit.getConsoleSender().sendMessage(Message.updateError);
            }
        }
        return null;
    }
    
    // 比较本插件版本号
    public static void check(CommandSender sender){
        ArrayList<String> info = getVersion("http://qwq.xn--o5raa.com/plugins/mailbox/version.php", true);
        if(info!=null && !info.isEmpty()){
            String[] nsl = info.get(0).split("\\.");
            String[] osl = MailBoxAPI.getVersion().split("\\.");
            for(int i=0;i<3;i++){
                int n = Integer.parseInt(nsl[i]);
                int o = Integer.parseInt(osl[i]);
                if(o==n){
                    if(i==2) sender.sendMessage(Message.updateNewest);
                }else if(o>n){
                    sender.sendMessage(Message.updateNewest);
                    break;
                }else{
                    String msg = "";
                    for(int j=Message.updateNew.size()-1;j>0;j++){
                        msg += Message.updateNew.get(j)+ '\n';
                    }
                    msg += Message.updateNew.get(0);
                    sender.sendMessage(msg.replace("%version%", info.get(0)).replace("%date%", info.get(1)).replace("%download%", MailBox.getInstance().getDescription().getWebsite()+"/download.php"));
                    String[] in = info.get(2).split("#");
                    for(int j=0;j<in.length;j++){
                        sender.sendMessage("§b"+(j+1)+": "+in[j]);
                    }
                    break;
                }
            }
        }
    }
    
    // 比较两个字符串版本号
    public static boolean check(String now, String need){
        String[] nowl = now.split("\\.");
        String[] needl = need.split("\\.");
        int c = nowl.length;
        if(needl.length<c) c = needl.length;
        for(int i=0;i<c;i++){
            int w = Integer.parseInt(nowl[i]);
            int d = Integer.parseInt(needl[i]);
            if(w<d) return false;
        }
        return true;
    }
    
}
