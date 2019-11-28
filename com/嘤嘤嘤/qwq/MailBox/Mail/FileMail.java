package com.嘤嘤嘤.qwq.MailBox.Mail;

import com.嘤嘤嘤.qwq.MailBox.API.Listener.MailCollectEvent;
import com.嘤嘤嘤.qwq.MailBox.API.Listener.MailSendEvent;
import com.嘤嘤嘤.qwq.MailBox.API.MailBoxAPI;
import com.嘤嘤嘤.qwq.MailBox.GlobalConfig;
import com.嘤嘤嘤.qwq.MailBox.Utils.DateTime;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FileMail extends TextMail implements ItemMail, CommandMail{
    
    // 附件名
    protected String fileName;
    // 附件是否启用指令
    protected boolean hasCommand;
    // 指令列表
    protected List<String> commandList;
    // 指令描述
    protected List<String> commandDescription;
    // 附件是否含有物品
    protected boolean hasItem;
    // 物品列表
    protected ArrayList<ItemStack> itemList;
    // 附件金币
    protected double coin;
    
    public FileMail(String type, int id, String sender, List<String> recipient, String topic, String content, String date, String filename){
        super(type, id, sender, recipient, topic, content, date);
        this.fileName = filename;
        getFile();
    }
    
    public FileMail(String type, int id, String sender, List<String> recipient, String topic, String content, String date, String filename, ArrayList<ItemStack> isl, List<String> cl, List<String> cd, double coin){
        super(type, id, sender, recipient, topic, content, date);
        this.fileName = filename;
        this.itemList = isl;
        this.commandList = cl;
        this.commandDescription = cd;
        this.hasItem = !isl.isEmpty();
        this.hasCommand = !cl.isEmpty();
        this.coin = coin;
    }
    
    // 获取附件信息
    private void getFile(){
        fileHasCommand();
        if(hasCommand){
            getFileCommandList();
            getFileCommandDescription();
        }
        getFileItemList();
        getFileMoney();
    }
    
    // 设置玩家领取邮件
    @Override
    public boolean Collect(Player p){
        // 判断收件人
        if(type.equals("player") && !recipient.contains(p.getName())){
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"你不是这个邮件的收件人！");
            return false;
        }
        // 发送邮件附件
        if(hasItem){
            if(giveItem(p)){
                p.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"附件发送完毕.");
            }else{
                Bukkit.getConsoleSender().sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"玩家："+p.getName()+" 领取 "+typeName+" - "+id+" 邮件附件失败.");
                return false;
            }
        }
        // 执行邮件指令
        if(hasCommand){
            if(doCommand(p)){
                p.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"指令执行完毕.");
            }else{
                Bukkit.getConsoleSender().sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"玩家："+p.getName()+" 执行 "+typeName+" - "+id+" 邮件指令失败.");
            }
        }
        // 设置玩家领取邮件
        if(MailBoxAPI.setCollect(type, id, p.getName())){
            // 给钱
            if(giveCoin(p, coin)) p.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"你获得了"+coin+GlobalConfig.vaultDisplay+", 余额："+MailBoxAPI.getEconomyFormat(MailBoxAPI.getEconomyBalance(p))+GlobalConfig.vaultDisplay);
            MailCollectEvent mce = new MailCollectEvent(this, p);
            Bukkit.getServer().getPluginManager().callEvent(mce);
            p.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"邮件领取成功！");
            Bukkit.getConsoleSender().sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"玩家："+p.getName()+" 领取了 "+typeName+" - "+id+" 邮件.");
            return true;
        }else{
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"邮件领取失败！");
            Bukkit.getConsoleSender().sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"玩家："+p.getName()+" 领取 "+typeName+" - "+id+" 邮件失败.");
            return false;
        }
    }
    
    // 发送这封邮件
    @Override
    public boolean Send(Player p){
        if(!(this instanceof FileMail)){
            return super.Send(p);
        }else{
            if(id==0){
                // 新建邮件
                // 判断玩家背包里是否有想要发送的物品
                if(!itemList.isEmpty() && !p.hasPermission("mailbox.admin.send.check.item")){
                    if(!hasItem(itemList, p)){
                        return false;
                    }
                }
                // 判断玩家钱够不够
                if(GlobalConfig.enVault && coin!=0 && !p.hasPermission("mailbox.admin.send.check.coin")){
                    if(MailBoxAPI.getEconomyBalance(p)<coin){
                        p.sendMessage(GlobalConfig.normal+"[邮件预览]：你这钱不够啊");
                        return false;
                    }
                }
                // 获取时间
                date = DateTime.get("ymdhms");
                try {
                    // 生成一个文件名
                    fileName = MailBoxAPI.getMD5(type);
                } catch (IOException ex) {
                    p.sendMessage(GlobalConfig.normal+"[邮件预览]：生成文件名失败");
                    return false;
                }
                if(MailBoxAPI.saveMailFiles(this)){
                    // 删除玩家背包里想要发送的物品
                    if(removeItem(itemList, p)){
                        if(MailBoxAPI.setSend(type, id, sender, getRecipientString(), topic, content, date, fileName)){
                            // 扣钱
                            if(removeCoin(p, coin)) p.sendMessage(GlobalConfig.normal+"[邮件预览]：花费了"+coin+GlobalConfig.vaultDisplay);
                            MailSendEvent mse = new MailSendEvent(this, p);
                            Bukkit.getServer().getPluginManager().callEvent(mse);
                            return true;
                        }else{
                            p.sendMessage(GlobalConfig.normal+"[邮件预览]：邮件发送至数据库失败");
                            return false;
                        }
                    }else{
                        p.sendMessage(GlobalConfig.normal+"[邮件预览]：从背包中移除发送物品失败");
                        DeleteFile();
                        return false;
                    }
                }else{
                    p.sendMessage(GlobalConfig.normal+"[邮件预览]：保存为附件失败");
                    if(p.isOp())p.sendMessage(GlobalConfig.normal+"[邮件预览]：附件名:"+fileName);
                    return false;
                }

            }else{
                //TODO 修改已有邮件
                return false;
            }
        }
    }
    
    // 删除这封邮件
    @Override
    public boolean Delete(Player p){
        if(DeleteFile()){
            return DeleteData(p);
        }else{
            return false;
        }
    }
    
    // 删除这封邮件的附件
    public boolean DeleteFile(){
        return MailBoxAPI.setDeleteFile(type,fileName);
    }
    
    
    private void getFileMoney() {
        coin = MailBoxAPI.getFileMoney(type, fileName)[0];
    }

    // 判断附件是否启用指令
    @Override
    public void fileHasCommand() {
        hasCommand = MailBoxAPI.hasFileCommands(type, fileName);
        if(hasCommand) ;
    }

    // 获取指令列表
    @Override
    public void getFileCommandList() {
        commandList = MailBoxAPI.getFileCommands(type, fileName);
    }
    
    // 获取指令描述
    @Override
    public void getFileCommandDescription() {
        commandDescription = MailBoxAPI.getFileCommandsDescription(type, fileName);
    }

    // 执行指令
    @Override
    public boolean doCommand(Player p) {
        if(commandList!=null){
            for(int i=0;i<commandList.size();i++){
                String cs = commandList.get(i);
                try{
                    cs = cs.replace(GlobalConfig.fileCmdPlayer, p.getName());
                    if(Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cs)){
                        p.sendMessage(GlobalConfig.success+GlobalConfig.pluginPrefix+"第"+(i+1)+"条指令执行成功");
                    }else{
                        p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"第"+(i+1)+"条指令执行失败");
                    }
                } catch (CommandException e) {
                    p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+"第"+(i+1)+"条指令执行失败");
                }
            }
            return true;
        }else{
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+" 获取指令信息失败");
            return false;
        }
    }

    @Override
    public void getFileItemList() {
        itemList = MailBoxAPI.getFileItems(type, fileName);
        hasItem = !(itemList==null || itemList.isEmpty());
    }

    @Override
    public boolean giveItem(Player p) {
        // 检查背包空位够不够
        int x = 0;
        boolean hasBlank = false;
        for(int i = 0;i<36;i++){
            if(p.getInventory().getItem(i)==null)x++;
            if(x>=itemList.size()){
                hasBlank = true;
                break;
            }
        }
        if(hasBlank){
            ItemStack[] isa = {new ItemStack(Material.AIR),new ItemStack(Material.AIR),new ItemStack(Material.AIR),new ItemStack(Material.AIR),new ItemStack(Material.AIR)};
            for(int i = 0 ;i<itemList.size();i++){
                ItemStack is = itemList.get(i);
                isa[i] = is;
            }
            p.getInventory().addItem(isa);
            return true;
        }else{
            p.sendMessage(GlobalConfig.warning+GlobalConfig.pluginPrefix+" 领取失败，请在背包中留出"+itemList.size()+"个空位！");
            return false;
        }
    }
    
    // 判断玩家背包里是否有想要发送的物品
    public boolean hasItem(ArrayList<ItemStack> isl, Player p){
        for(int i=0;i<isl.size();i++){
            if(!p.getInventory().containsAtLeast(isl.get(i), isl.get(i).getAmount())) {
                return false;
            }
        }
        return true;
    }
    
    // 移除玩家背包里想要发送的物品
    private boolean removeItem(ArrayList<ItemStack> isl, Player p){
        if(p.hasPermission("mailbox.admin.send.noconsume"))return true;
        boolean success = true;
        ArrayList<Integer> clearList = new ArrayList();
        HashMap<Integer, ItemStack> reduceList = new HashMap();
        String error = GlobalConfig.normal+"[邮件预览]：从背包中移除以下物品失败";
        for(int i=0;i<isl.size();i++){
            ItemStack is1 = isl.get(i);
            int count = is1.getAmount();
            for(int j=0;j<36;j++){
                if(p.getInventory().getItem(j)!=null){
                    ItemStack is2 = p.getInventory().getItem(j).clone();
                    if(is1.isSimilar(is2)){
                        int amount = is2.getAmount();
                        if(count<=amount){
                            int temp = amount-count;
                            if(temp==0){
                                clearList.add(j);
                            }else{
                                is2.setAmount(temp);
                                reduceList.put(j, is2);
                            }
                            count = 0;
                            break;
                        }else{
                            clearList.add(j);
                            count -= amount;
                        }
                    }
                }
            }
            if(count!=0){
                success = false;
                error += "\n"+(i+1)+"号物品"+"缺少"+count+"个";
            }
        }
        if(success){
            if(!clearList.isEmpty()){
                clearList.forEach(k -> {
                    p.getInventory().clear(k);
                });
            }
            if(!reduceList.isEmpty()){
                reduceList.forEach((k, v) -> {
                    p.getInventory().setItem(k, v);
                });
            }
        }else{
            p.sendMessage(error);
        }
        return success;
    }
    
    private boolean giveCoin(Player p, double coin){
        return MailBoxAPI.addEconomy(p, coin);
    }
    
    private boolean removeCoin(Player p, double coin){
        return MailBoxAPI.reduceEconomy(p, coin);
    }
    
    public String getFileName(){
        return this.fileName;
    }
    
    public boolean getHasCommand(){
        return this.hasCommand;
    }
    
    public List<String> getCommandList(){
        return this.commandList;
    }
    public List<String> getCommandDescription(){
        return this.commandDescription;
    }
    
    public boolean getHasItem(){
        return this.hasItem;
    }
    
    public ArrayList<ItemStack> getItemList(){
        return this.itemList;
    }
    
    public double getCoin(){
        return this.coin;
    }
    
    @Override
    public String toString(){
        String str = typeName+"-"+id+"-"+topic+"-"+content+"-"+sender+"-"+date;
        if(!recipient.isEmpty()) {
            str += "-收件人：";
            for(String s:recipient) str += s+" ";
        }
        if(hasItem && !itemList.isEmpty()) str += "-含物品"+itemList.size()+"个";
        if(hasCommand && !commandList.isEmpty()) str += "-含指令"+commandList.size()+"条";
        if(coin!=0) str += "-含金钱："+coin;
        return str;
    }
    
}
