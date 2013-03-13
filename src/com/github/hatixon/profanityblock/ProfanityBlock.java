package com.github.hatixon.profanityblock;
//    /\    /\
//   /  \  /  \
//  /    \/    \
//  \          /
//   \        /
//    \      /
//     \    /
//      \  /
//       \/
// Was told to add love to my plugin. So there you have it.

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipException;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.*;
import org.bukkit.potion.*;

import com.github.hatixon.profanityblock.logging.ProLogger;

@SuppressWarnings({"unused", "rawtypes", "unchecked"})
public class ProfanityBlock extends JavaPlugin
{
	public final ProfanityBlock plugin = this;
    public final Logger logger = Logger.getLogger("Minecraft");
	public final ServerChatPlayerListener playerChatListener = new ServerChatPlayerListener(this);
	public final CapsCheck playerCapsCheck = new CapsCheck(this);
	public final CommandListener commandCheck = new CommandListener(this);
	public final SignPlaceListener signPlace = new SignPlaceListener(this);
	private FileConfiguration usersConfig = null;
	private File usersConfigFile = null;
	private FileConfiguration whiteConfig = null;
	private File whiteFile = null;
	private FileConfiguration blackConfig = null;
	private File blackFile = null;
	private Map<String, Boolean> mutedList = new HashMap();
	private FileConfiguration instantConfig = null;
	private File instantFile = null;
	private FileConfiguration warnConfig = null;
	private File warnFile = null;
	private String pre = (new StringBuilder().append(ChatColor.RED).append("[ProfanityBlock]").append(ChatColor.YELLOW)).toString();
	private Map blackWordList;
	private Map<Player, String> repeatList = new HashMap();
	private Map whiteWordList;
	private Map instaBanList;
	private Map userList;
	private Map commandsList;  
	Map<String, Queueing> messageLineup = new HashMap();
	
	public Map getUserMap()
	{
		return userList;
	}
	
	public void addPlayerToQueue(String name)
	{
		messageLineup.put(name, new Queueing());
		
	}
	
	public boolean CheckQueue(String name)
	{
		
		return false;
	}
	
	public void getUserList()
	{
		List blackW = getUserConfig().getStringList("UserList");
		for(Iterator i = blackW.iterator(); i.hasNext();)
		{
            String thisLine = (String)i.next();
            userList.put(thisLine, "");
		}
	}
	
	public void reloadWarnConfig()
	{
		if(warnConfig == null)
		{
			warnFile = new File(getDataFolder(), "warnings.yml");
		}
		warnConfig = YamlConfiguration.loadConfiguration(warnFile);
		
	    InputStream defConfigStream = this.getResource("warnings.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        warnConfig.setDefaults(defConfig);
	    }
	}
	
	public void reloadUserConfig()
	{
		if(usersConfig == null)
		{
			usersConfigFile = new File(getDataFolder(), "users.yml");
		}
		usersConfig = YamlConfiguration.loadConfiguration(usersConfigFile);
		
	    InputStream defConfigStream = this.getResource("users.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        usersConfig.setDefaults(defConfig);
	    }
	}
	
	public void reloadWhiteConfig()
	{
		if(whiteConfig == null)
		{
			whiteFile = new File(getDataFolder(), "whitelist.yml");
		}
		whiteConfig = YamlConfiguration.loadConfiguration(whiteFile);
		
	    InputStream defConfigStream = this.getResource("whitelist.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        whiteConfig.setDefaults(defConfig);
	    }
	}

	public void reloadBlackConfig()
	{
		if(blackConfig == null)
		{
			blackFile = new File(getDataFolder(), "blacklist.yml");
		}
		blackConfig = YamlConfiguration.loadConfiguration(blackFile);
		
	    InputStream defConfigStream = this.getResource("blacklist.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        blackConfig.setDefaults(defConfig);
	    }
	}
	
	public void notifyOp(Player player, String neededMessage)
	{
		ChatColor RED = ChatColor.RED;
		ChatColor YEL = ChatColor.YELLOW;

		String message = null;
		if(neededMessage.equalsIgnoreCase("caps"))
		{
			message = getConfig().getString("Message.Notification.Caps");
		}else
		if(neededMessage.equalsIgnoreCase("sign"))
		{
			message = getConfig().getString("Message.Notification.Sign");
		}else
		if(neededMessage.equalsIgnoreCase("command"))
		{
			message = getConfig().getString("Message.Notification.Command");
		}else
		if(neededMessage.equalsIgnoreCase("chat"))
		{
			message = getConfig().getString("Message.Notification.Chat");
		}else
		if(neededMessage.equalsIgnoreCase("muted"))
		{
			message = getConfig().getString("Message.Notification.Mute");
		}else
		if(neededMessage.equalsIgnoreCase("banned"))	
		{
			message = getConfig().getString("Message.Notification.Ban");
		}else
		if(neededMessage.equalsIgnoreCase("kick"))	
		{
			message = getConfig().getString("Message.Notification.Kick");
		}
		if(message == null)
		{
			message = new StringBuilder(pre).append(" Configuration error!").toString();
		}
		message = message.replace("/player/", player.getName());
		String pre = (new StringBuilder().append(RED).append("[ProfanityBlock] ").append(YEL)).append(message).toString();
        Player arr[] = getServer().getOnlinePlayers();
        int len = arr.length;
    	for(int i = 0; i < len; i++)
    	{
    		Player player2 = arr[i];
    		if(player2.hasPermission("pb.notify") || player2.hasPermission("pb.*"))
    		{
    			player2.sendMessage(pre);
    		}
    	}
    	logger.info(pre);
		
	}
	public void reloadInstantConfig()
	{
		if(instantConfig == null)
		{
			instantFile = new File(getDataFolder(), "instaban.yml");
		}
		instantConfig = YamlConfiguration.loadConfiguration(instantFile);
		
	    InputStream defConfigStream = this.getResource("instaban.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        instantConfig.setDefaults(defConfig);
	    }
	}
	
	public void addUserL(String uName)
	{
        userList.put(uName, "");
        saveUserList();
	}
	
	public void saveUserList()
	{
		
        ArrayList userListA = new ArrayList();
        String printLine;
        for(Iterator iterator = userList.entrySet().iterator(); iterator.hasNext(); userListA.add(printLine))
        {
        	Map.Entry entry = (Map.Entry)iterator.next();
            String thisEntry = (String)entry.getKey();
            String thisReplacementment = (String)entry.getValue();
            printLine = thisReplacementment.length() <= 0 ? thisEntry : (new StringBuilder(String.valueOf(thisEntry))).append(":").append(thisReplacementment).toString();
        }
        getUserConfig().set("UserList", ((Object) (userListA.toArray())));
        saveUserConfig();
	}
	
    public FileConfiguration getUserConfig() {
        if (usersConfig == null) {
            this.reloadUserConfig();
        }
        return usersConfig;
    }
    
    public void saveUserConfig() {
        if (usersConfig == null || usersConfigFile == null) {
        return;
        }
        try {
            getUserConfig().save(usersConfigFile);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + usersConfigFile, ex);
        }
    }
    
	
    public FileConfiguration getWhiteConfig() {
        if (whiteConfig == null) {
            this.reloadWhiteConfig();
        }
        return whiteConfig;
    }
    
    public void saveWhiteConfig() {
        if (whiteConfig == null || whiteFile == null) {
        return;
        }
        try {
            getWhiteConfig().save(whiteFile);
        } catch (IOException ex) 
        {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + whiteFile, ex);        
        }
    }
    
	
    public FileConfiguration getBlackConfig() {
        if (blackConfig == null) {
            this.reloadBlackConfig();
        }
        return blackConfig;
    }
    
    public void saveBlackConfig() {
        if (blackConfig == null || blackFile == null) {
        return;
        }
        try {
            getBlackConfig().save(blackFile);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + blackFile, ex);
        }
    }
    
	
    public FileConfiguration getInstantConfig() {
        if (instantConfig == null) {
            this.reloadInstantConfig();
        }
        return instantConfig;
    }
    
    public void saveInstaConfig() {
        if (instantConfig == null || instantFile == null) {
        return;
        }
        try {
            getInstantConfig().save(instantFile);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + instantFile, ex);
        }
    }
    
    public FileConfiguration getWarnConfig() {
        if (warnConfig == null) {
            this.reloadWarnConfig();
        }
        return warnConfig;
    }
    
    public void saveWarnConfig() {
        if (warnConfig == null || warnFile == null) {
        return;
        }
        try {
            getWarnConfig().save(warnFile);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + warnFile, ex);
        }
    }
	
	public void executeMoneyRemoval(String uName)
	{
		getServer().dispatchCommand(Bukkit.getConsoleSender(), new StringBuilder().append("eco take ").append(uName).append(" ").append(getMonPen()).toString());
	}

	public Integer getMonPen()
	{
		return getConfig().getInt("Money.Penalty");
	}
	
	public boolean getMoneyEnabled()
	{
		return getConfig().getBoolean("Money.Enabled");
	}
	
	public Map getWhiteMap()
	{
		return whiteWordList;
		
	}
	
	public Map getBlackMap()
	{
		return blackWordList;
	}
	
	public Map getBanList()
	{
		return instaBanList;
	}
	
	public void onDisable()
	{
        PluginDescriptionFile pdffile = getDescription();
		String pre = (new StringBuilder().append("[ProfanityBlock] ")).toString();
		logger.info(new StringBuilder(pre).append("ProfanityBlock v").append(pdffile.getVersion()).append(" has been disabled!").toString());
		ProLogger.cleanup();
	}
	
	public void logPlayerSwearing(String uName, String message, String pType)
	{
		Calendar clf = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-YYYY");
		SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
		String logging = new StringBuilder().append("[").append(sdf1.format(clf.getTime())).append("]")
				.append(sdf2.format(clf.getTime())).append(" - ")
				.append(uName.toUpperCase()).append(" said ").append(message)
				.append(". Type of message: ").append(pType).toString();
		ProLogger.fileLogger.info(logging);
	}
	
	public void onEnable()
	{
		ProLogger.setupLogger(new File(getDataFolder(), "records.log"));
		String pre = (new StringBuilder().append("[ProfanityBlock] ")).toString();
        PluginDescriptionFile pdffile = getDescription();
		loadConfiguration();
		logger.info(new StringBuilder(pre).append("ProfanityBlock v").append(pdffile.getVersion()).append(" has been enabled!").toString());
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(playerChatListener, this);
		pm.registerEvents(commandCheck, this);
		pm.registerEvents(playerCapsCheck, this);
		pm.registerEvents(signPlace, this);
        getCommand("pb").setExecutor(new ServerCommandExecutor(this));
		getBlackList();
		getWhiteList();
		getInstaBan();
		getUserList();
		getCommandsList();
		loadMuteList();
		if(getConfig().getBoolean("CheckForUpdates"))
		{
			if(isUpdated())
			{
				logger.info(new StringBuilder(pre).append("There is an updated version of ProfanityBlock. Download at http://dev.bukkit.org/server-mods/profanityblock/").toString());
			}
		}
		getConfig().addDefault("QueuedMessagePunishment", "ban");
		saveConfig();
	}
	
    public String getLatest()
    {
        StringBuilder responseData = new StringBuilder();
    	try
    	{
        String uri = "http://pastebin.com/raw.php?i=Tsaqvs1s";
        URL url = new URL(uri);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = null;
        while((line = in.readLine()) != null) 
        {
            responseData.append(line);
        }
    	}catch(Exception e)
    	{
    		PluginDescriptionFile pdffile = getDescription();
    		return pdffile.getVersion();
    	}
        return responseData.toString();
    }
    
    public boolean isUpdated()
    {
    	PluginDescriptionFile pdffile = getDescription();
    	String current = pdffile.getVersion();
    	String latest = getLatest();
    	boolean updated = false;
    	if(!current.equals(latest))
    	{
    		updated = true;
    	}
    	return updated;
    }
	
	public String getMessageInstaBan()
	{
		return getConfig().getString("Message.InstaBan");
	}
	
	public void instaBanPlayer(Player player)
	{
		String uName = player.getName();
		String banCom = getConfig().getString("BanCommands.InstaBan");
		if(banCom.indexOf(" ") < 1)
		{
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), new StringBuilder(banCom).append(" ").append(uName).append(" ").append(getMessageInstaBan()).toString());
		}
		else
		{
			String comSplit[] = banCom.split(" ");
			if(comSplit.length == 2)
			{
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), new StringBuilder(comSplit[0]).append(" ").append(uName).append(" ").append(comSplit[1]).append(getMessageInstaBan()).toString());
			}else
			if(comSplit.length == 3)
			{
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), new StringBuilder(comSplit[0]).append(" ").append(uName).append(" ").append(comSplit[1]).append(" ").append(comSplit[2]).append(" ").append(getMessageInstaBan()).toString());
			}
		}
	}
	
    public boolean getOnlineList()
    {
    	Boolean done= false;
        StringBuilder responseData = new StringBuilder();
        if(!getServer().getOnlineMode())
        {
        	logger.log(Level.SEVERE, "This can not be done in offline mode!");
        	done = false;
        	return done;
        }
    	try
    	{
	        String uri = "http://pastebin.com/raw.php?i=v70791kD";
	        URL url = new URL(uri);
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	        conn.setRequestMethod("GET");
	        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        String line = null;
	        while((line = in.readLine()) != null) 
	        {
	            if(line.startsWith("bLw"))
	            {
	            	addBlackWord(line.replaceFirst("bLw", ""));
	            }
	            if(line.startsWith("wLw"))
	            {
	            	addWhiteWord(line.replaceFirst("wLw", ""));
	            }
	            done = true;
	        }
    	}catch(Exception e)
    	{
    		logger.log(Level.SEVERE, "Online word list download failed!");
    		done = false;
    	}
    	return done;
    }
    
	public String getMessageCapsBan()
	{
		return getConfig().getString("Message.Caps.Ban");
	}
	
	public void capsBunnyRabbit(Player player)
	{
		String uName = player.getName();
		String banCom = getConfig().getString("BanCommands.CapsBan");
		if(banCom.indexOf(" ") < 1)
		{
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), new StringBuilder(banCom).append(" ").append(uName).append(" ").append(getMessageCaps()).toString());
		}
		else
		{
			String comSplit[] = banCom.split(" ");
			if(comSplit.length == 2)
			{
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), new StringBuilder(comSplit[0]).append(" ").append(uName).append(" ").append(comSplit[1]).append(getMessageCaps()).toString());
			}else
			if(comSplit.length == 3)
			{
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), new StringBuilder(comSplit[0]).append(" ").append(uName).append(" ").append(comSplit[1]).append(" ").append(comSplit[2]).append(" ").append(getMessageCaps()).toString());
			}
		}
	}
	
	public String getMessageCaps()
	{
		return getConfig().getString("Message.Caps.Warn");
	}
    
    public boolean getCapsPunishment()
    {
    	return getConfig().getBoolean("Caps.Enforced");
    }
	
	public void bunnyRabbit(Player player)
	{
		String uName = player.getName();
		String banCom = getConfig().getString("BanCommands.Ban");
		if(banCom.indexOf(" ") < 1)
		{
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), new StringBuilder(banCom).append(" ").append(uName).append(" ").append(getMessageBanned()).toString());
		}
		else
		{
			String comSplit[] = banCom.split(" ");
			if(comSplit.length == 2)
			{
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), new StringBuilder(comSplit[0]).append(" ").append(uName).append(" ").append(comSplit[1]).append(" ").append(getMessageBanned()).toString());
			}else
			if(comSplit.length == 3)
			{
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), new StringBuilder(comSplit[0]).append(" ").append(uName).append(" ").append(comSplit[1]).append(" ").append(comSplit[2]).append(" ").append(getMessageBanned()).toString());
			}
		}
	}
	
	public void spamBan(Player player)
	{
		String uName = player.getName();
		String banCom = getConfig().getString("BanCommands.Spam");
		if(banCom.indexOf(" ") < 1)
		{
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), new StringBuilder(banCom).append(" ").append(uName).append(" ").append(getConfig().getString("Message.SpamBan")).toString());
		}
		else
		{
			String comSplit[] = banCom.split(" ");
			if(comSplit.length == 2)
			{
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), new StringBuilder(comSplit[0]).append(" ").append(uName).append(" ").append(comSplit[1]).append(" ").append(getConfig().getString("Message.SpamBan")).toString());
			}else
			if(comSplit.length == 3)
			{
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), new StringBuilder(comSplit[0]).append(" ").append(uName).append(" ").append(comSplit[1]).append(" ").append(comSplit[2]).append(" ").append(getConfig().getString("Message.SpamBan")).toString());
			}
		}
	}
	
	public void loadConfiguration()
	{
		getConfig().options().copyDefaults(true);
		getUserConfig().options().copyDefaults(true);
		getWhiteConfig().options().copyDefaults(true);
		getBlackConfig().options().copyDefaults(true);
		getInstantConfig().options().copyDefaults(true);
		getWarnConfig().options().copyDefaults(true);
		saveDefaultConfig();
		saveUserConfig();
		saveWhiteConfig();
		saveBlackConfig();
		saveInstaConfig();
		saveWarnConfig();
	}
	
	 
	public ProfanityBlock()
	{
		blackWordList = new HashMap();
		whiteWordList = new HashMap();
		instaBanList = new HashMap();
		userList = new HashMap();
		commandsList = new HashMap();
	}
	
	public String comPact(String message)
	{
        for(Iterator iterator = blackWordList.entrySet().iterator(); iterator.hasNext();)
        {
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
            String thisEntry = (String)entry.getKey();
            String thisNewEntry = "";
            if(thisEntry.indexOf("\\w*") > 0)
            {
            	thisNewEntry = thisEntry.replace("\\w*", "");
            }
            String thisRegex = (new StringBuilder("\\b")).append(thisNewEntry).append("\\b").toString();
            Pattern patt = Pattern.compile(thisRegex, 2);
            Matcher m = patt.matcher(message);
            if(m.find())
            {
                    message = message.replace(thisRegex, "");
            }
        }
        return message;
	}
	
	public double getCapsPercent()
	{
		int percentA = getConfig().getInt("Caps.Percentage");
		double percentB = (double)percentA / 100;
		return percentB;
	}
	
	public boolean getNotifyOp()
	{
		return getConfig().getBoolean("Notify.Op");
	}
	public boolean getResetOnBan()
	{
		return getConfig().getBoolean("ResetWarningsOnBan");
	}
	
	public Integer getRemWarn(String uName)
	{
		return getWarnConfig().getInt((new StringBuilder().append("Warnings.Warned.").append((uName).toLowerCase())).toString());
	}
	
	public String getMessageKick()
	{
		return getConfig().getString("Message.Swear.Kick");
	}
	
	public String getMessageBanned()
	{
		return getConfig().getString("Message.Swear.Ban");	
	}
	
	public void resetBanned(String uName)
	{
		getWarnConfig().set(((new StringBuilder().append("Warnings.Warned.").append(uName.toLowerCase())).toString()), getTotWarn());
		saveWarnConfig();
	}
	
	public Integer getTotWarn()
	{
		return getConfig().getInt("TotalWarnings");		
	}
	
    public void setRemWarn(String uName, Integer remWarn)
    {
        getWarnConfig().set((new StringBuilder("Warnings.Warned.")).append(uName.toLowerCase()).toString(), remWarn);
        saveWarnConfig();
    }
    
    public Integer getWarnBKick()
    {
    	return getConfig().getInt("RemainingWarningsBeforeKick");
    }
    
	public void getBlackList()
	{
		List blackW = getBlackConfig().getStringList("Blacklist");
		for(Iterator i = blackW.iterator(); i.hasNext();)
		{
            String thisLine = (String)i.next();
            if(thisLine.indexOf(":") > 0)
            {
                String thisSplit[] = thisLine.split(":", 2);
                blackWordList.put(thisSplit[0], thisSplit[1]);
            } else
            {
                blackWordList.put(thisLine, "");
            }
		}
	}
	
	public void getCommandsList()
	{
		List blackW = getConfig().getStringList("commandsList");
		for(Iterator i = blackW.iterator(); i.hasNext();)
		{
            String thisLine = (String)i.next();
            commandsList.put(thisLine, "");
		}
	}
	
	public void getInstaBan()
	{
		List banW = getInstantConfig().getStringList("InstaBanList");
		for(Iterator i = banW.iterator(); i.hasNext();) 
		{
            String thisLine = (String)i.next();
            if(thisLine.indexOf(":") > 0)
            {
                String thisSplit[] = thisLine.split(":", 2);
                instaBanList.put(thisSplit[0], thisSplit[1]);
            } else
            {
                instaBanList.put(thisLine, "");
            }
		}
	}
	
	public void setWarnJoin(String uName)
	{
		getWarnConfig().set((new StringBuilder("Warnings.Warned.")).append(uName.toLowerCase()).toString(), getTotWarn());
		saveWarnConfig();
	}
	
	public String getJoinMessage()
	{
		return getConfig().getString("Message.FirstJoin");
	}
	
	public void getWhiteList()
	{
		List whiteW = getWhiteConfig().getStringList("Whitelist");
		for(Iterator i = whiteW.iterator(); i.hasNext();)
		{
            String thisLine = (String)i.next();
            if(thisLine.indexOf(":") > 0)
            {
                String thisSplit[] = thisLine.split(":", 2);
                whiteWordList.put(thisSplit[0], thisSplit[1]);
            } else
            {
                whiteWordList.put(thisLine, "");
            }
		}
	}
	
	public String censorCheck(String message)
	{
		String input = isItAllowed(message);
	    boolean censored = false;
	    boolean uncensored = false;
	    for(Iterator iterator = blackWordList.entrySet().iterator(); iterator.hasNext();)
	    {
	        java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
	        String thisEntry = (String)entry.getKey();
	        String thisValue = (String)entry.getValue();
	        String thisRegex = (new StringBuilder("\\b")).append(thisEntry).append("\\b").toString().toLowerCase();
	        Pattern patt = Pattern.compile(thisRegex, 2);
	        Matcher m = patt.matcher(input);
	        if(m.find())
	        {
	        	if(getCompact() == true)
	        	{
	        		int c;
	        		String comRepE = thisEntry.replace("\\w*", "").replace("+?", "");
	        		StringBuilder comRep = new StringBuilder();
	        		String charC = String.valueOf(getCharacter());
	        		for(c = 0; c < comRepE.length(); c++)
	        		{
	        			comRep.append(charC);
	        		}
	                message = message.replaceAll(thisRegex, comRep.toString());
	                censored = true;
	        	}else
	        	{
	        		if(thisValue.length() > 0)
	        		{
	        			message = message.replaceAll(thisRegex, thisValue);
	        			censored = true;
	        		} else
	        		{
	        			uncensored = true;
	        		}
	        	}
	        }
	    }
	    
	    if(spaceCheck(message))
	    {
	    	uncensored = true;
	    	censored = false;
	    }
	
	    if(!censored && !uncensored || !uncensored && censored)
	    {
	        return message;
	    }else
	    {
	        return "";
	    }
	}
	
	public String instaCensorCheck(String message)
	{
		String input = isItAllowed(message);
	    boolean censored = false;
	    boolean uncensored = false;
	    for(Iterator iterator = instaBanList.entrySet().iterator(); iterator.hasNext();)
	    {
	        java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
	        String thisEntry = (String)entry.getKey();
	        String thisValue = (String)entry.getValue();
	        String thisRegex = (new StringBuilder("\\b")).append(thisEntry).append("\\b").toString().toLowerCase();
	        Pattern patt = Pattern.compile(thisRegex, 2);
	        Matcher m = patt.matcher(input);
	        if(m.find())
	        {
	        	if(getCompact() == true)
	        	{
	        		int c;
	        		String comRepE = thisEntry.replace("\\w*", "").replace("+?", "");
	        		StringBuilder comRep = new StringBuilder();
	        		String charC = String.valueOf(getCharacter());
	        		for(c = 0; c < comRepE.length(); c++)
	        		{
	        			comRep.append(charC);
	        		}
	                message = message.replaceAll(thisRegex, comRep.toString());
	                censored = true;
	        	}else
	        	{
	        		if(thisValue.length() > 0)
	        		{
	        			message = message.replaceAll(thisRegex, thisValue);
	        			censored = true;
	        		} else
	        		{
	        			uncensored = true;
	        		}
	        	}
	        }
	    }
	    
	    if(instaSpaceCheck(message))
	    {
	    	uncensored = true;
	    	censored = false;
	    }
	
	    if(!censored && !uncensored || !uncensored && censored)
	    {
	        return message;
	    }else
	    {
	        return "";
	    }
	}

	public boolean spaceCheck(String message)
    {
		int x;
		StringBuilder message1 = new StringBuilder();
		String split[] = message.split(" "); 
		for(x = 0; x < split.length; x++)
		{
			message1.append(split[x].replace("-", "")
					.replace(".", "").replace(",", "")
					.replace("\\/", "v").replace("|<", "k")
					.replace("0", "o").replace("1", "i")
					.replace("3", "e")
					.replace("/\\", "a").replace("/-\\", "a")
					.replace("4", "a").replace("@", "a")
					.replace("\\", "").replace("/", "")
					.replace("!", "i").replace("$", "s")
					.replace("8", "b").replace("<", "c")
					.replace(";", "").replace("_", "")
					.replace("+",  "").replace("=", "")
					.replace("{", "").replace("}", "")
					.replace("[", "").replace("]", "")
					.replace("|", "l").replace("?", "")
					.replace("*", "").replace("&", "")
					.replace("^", "").replace("#", "")
					.replace("`", "").replace("~", "")
					);
		}
		message = message1.toString();
		boolean swore = false;
        for(Iterator iterator = blackWordList.entrySet().iterator(); iterator.hasNext();)
        {
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
            String theWord = (String)entry.getKey();
            String theRegex = (new StringBuilder("\\b")).append(theWord).append("\\b").toString();
            Pattern patt = Pattern.compile(theRegex, 2);
            Matcher m = patt.matcher(message);
            if(m.find())
            {
                swore = true;
            }
        }
        return swore;
    }
	
	public boolean instaSpaceCheck(String message)
    {
		int x;
		StringBuilder message1 = new StringBuilder();
		String split[] = message.split(" "); 
		for(x = 0; x < split.length; x++)
		{
			message1.append(split[x].replace("-", "")
					.replace(".", "").replace(",", "")
					.replace("\\/", "v").replace("|<", "k")
					.replace("0", "o").replace("1", "i")
					.replace("3", "e")
					.replace("/\\/\\", "m").replace("/\\/", "n")
					.replace("4", "a").replace("@", "a")
					.replace("\\", "").replace("/", "")
					.replace("!", "i").replace("$", "s")
					.replace("8", "b").replace("<", "c")
					.replace(";", "").replace("_", "")
					.replace("+",  "").replace("=", "")
					.replace("{", "").replace("}", "")
					.replace("[", "").replace("]", "")
					.replace("|", "l").replace("?", "")
					.replace("*", "").replace("&", "")
					.replace("^", "").replace("#", "")
					.replace("`", "").replace("~", "")
					);
		}
		
		message = message1.toString();
		boolean swore = false;
        for(Iterator iterator = instaBanList.entrySet().iterator(); iterator.hasNext();)
        {
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
            String theWord = (String)entry.getKey();
            String theRegex = (new StringBuilder("\\b")).append(theWord).append("\\b").toString();
            Pattern patt = Pattern.compile(theRegex, 2);
            Matcher m = patt.matcher(message);
            if(m.find())
            {
                swore = true;
            }
        }
        return swore;
    }
	
	public boolean getCompact()
	{
		return getConfig().getBoolean("Compact.Replacement");
	}
	
	public String getCharacter()
	{
		return getConfig().getString("Compact.Char");
	}
	 
	public boolean didTheySwear(String input)
	{
		String message = isItAllowed(input);
		boolean swore = false;
        for(Iterator iterator = blackWordList.entrySet().iterator(); iterator.hasNext();)
        {
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
            String theWord = (String)entry.getKey();
            String theRegex = (new StringBuilder("\\b")).append(theWord).append("\\b").toString();
            Pattern patt = Pattern.compile(theRegex, 2);
            Matcher m = patt.matcher(message);
            if(m.find())
            {
                swore = true;
            }
        }
        if(spaceCheck(message))
        {
        	swore = true;
        }
        return swore;
	}
	
	public boolean commandSwear(String message)
	{
		boolean found = false;
        for(Iterator iterator = commandsList.entrySet().iterator(); iterator.hasNext();)
        {
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
            String theWord = (String)entry.getKey();
            String theRegex = (new StringBuilder("\\b")).append(theWord).append("\\b").toString();
            Pattern patt = Pattern.compile(theRegex, 2);
            Matcher m = patt.matcher(message);
            if(m.find())
            {
                found = true;
            }
        }

        return found;
    }
	
	public boolean getCommandCheck()
	{
		return getConfig().getBoolean("Check.Commands");
	}
	
	public boolean instaBanCheck(String message)
	{
		boolean instaBan = false;
		message = isItAllowed(message);
        for(Iterator iterator = instaBanList.entrySet().iterator(); iterator.hasNext();)
        {
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
            String theWord = (String)entry.getKey();
            String theRegex = (new StringBuilder("\\b")).append(theWord).append("\\b").toString();
            Pattern patt = Pattern.compile(theRegex, 2);
            Matcher m = patt.matcher(message);
            if(m.find())
            {
                instaBan = true;
            }
        }
        if(instaSpaceCheck(message))
        {
        	instaBan = true;
        }

        return instaBan;
    }	
	
	public boolean getNotifyPlayer()
	{
		return getConfig().getBoolean("Notify.Player");
	}
	
	public String getMessageWarn()
	{
		return getConfig().getString("Message.Swear.Warning");
	}

	 
	public String isItAllowed(String message)
	{
        for(Iterator iterator = whiteWordList.entrySet().iterator(); iterator.hasNext();)
        {
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
            String theWord = (String)entry.getKey();
            String theRegex = (new StringBuilder("\\b")).append(theWord).append("\\b").toString();
            Pattern patt = Pattern.compile(theRegex, 2);
            Matcher m = patt.matcher(message);
            if(m.find())
            {
                message = message.replaceAll(theRegex, "WhItEliSTeDReMoVeD");
            }
        }
        return message;
    }
	
	public void resetWarn(String uName)
	{
		getWarnConfig().set((new StringBuilder("Warnings.Warned.").append(uName).toString()), getTotWarn());
		saveWarnConfig();
	}
	
	public void editWarn(String uName, String amount)
	{
		getWarnConfig().set((new StringBuilder("Warnings.Warned.").append(uName).toString()), Integer.valueOf(amount));
		saveWarnConfig();
	}
	
	public void saveWhiteList()
	{
        ArrayList whiteListA = new ArrayList();
        String printLine;
        for(Iterator iterator = whiteWordList.entrySet().iterator(); iterator.hasNext(); whiteListA.add(printLine))
        {
            Map.Entry entry = (Map.Entry)iterator.next();
            String thisEntry = (String)entry.getKey();
            String thisReplacementment = (String)entry.getValue();
            printLine = thisReplacementment.length() <= 0 ? thisEntry : (new StringBuilder(String.valueOf(thisEntry))).append(":").append(thisReplacementment).toString();
        }

        getWhiteConfig().set("Whitelist", ((Object) (whiteListA.toArray())));
        saveWhiteConfig();
    }
	
	public void saveBlackList()
	{
        ArrayList blackListA = new ArrayList();
        String printLine;
        for(Iterator iterator = blackWordList.entrySet().iterator(); iterator.hasNext(); blackListA.add(printLine))
        {
            Map.Entry entry = (Map.Entry)iterator.next();
            String thisEntry = (String)entry.getKey();
            String thisReplacement = (String)entry.getValue();
            printLine = thisReplacement.length() <= 0 ? thisEntry : (new StringBuilder(String.valueOf(thisEntry))).append(":").append(thisReplacement).toString();
        }

        getBlackConfig().set("Blacklist", ((Object) (blackListA.toArray())));
        saveBlackConfig();
    }
	
    public boolean addWhiteWord(String whiteWord)
    {
    	StringBuilder sb = new StringBuilder();
    	String arr[] = whiteWord.split("");
    	for(int x = 1; x < arr.length; x++)
    	{
    		if(x == (arr.length - 1))
    		{
    			sb.append(arr[x]);
    		}else
    		{
    			sb.append(arr[x]).append("+?");
    		}
    	}
    	whiteWord = new StringBuilder().append("\\w*").append(sb.toString()).append("\\w*").toString();
    	if(whiteWordList.containsKey(whiteWord))
    	{
    		return false;
    	}
    	else
        {
    		if(whiteWord.indexOf(":") > 0)
	        {
	            String thisSplit[] = whiteWord.split(":", 2);
	            whiteWordList.put(thisSplit[0], thisSplit[1]);
	            saveWhiteList();
	        } else
	        {
	            whiteWordList.put(whiteWord, "");
	            saveWhiteList();
	        }

        return true;
        }
    }
    
    public boolean delWhiteWord(String whiteWord)
    {
        if(whiteWord.indexOf(":") > 0)
        {
            String thisSplit[] = whiteWord.split(":", 2);
            whiteWord = thisSplit[0];
        }
    	StringBuilder sb = new StringBuilder();
    	String arr[] = whiteWord.split("");
    	for(int x = 1; x < arr.length; x++)
    	{
    		if(x == (arr.length - 1))
    		{
    			sb.append(arr[x]);
    		}else
    		{
    			sb.append(arr[x]).append("+?");
    		}
    	}
    	whiteWord = new StringBuilder().append("\\w*").append(sb.toString()).append("\\w*").toString();
        if(whiteWordList.containsKey(whiteWord))
        {
            whiteWordList.remove(whiteWord);
            saveWhiteList();
            return true;
        } else
        {
            return false;
        }
    }
    
    public boolean addBlackWord(String blackWord)
    {
		if(blackWord.indexOf(":") > 0)
        {
            String thisSplit[] = blackWord.split(":", 2);
        	StringBuilder sb = new StringBuilder();
        	String arr[] = thisSplit[0].split("");
        	for(int x = 1; x < arr.length; x++)
        	{
        		if(x == (arr.length - 1))
        		{
        			sb.append(arr[x]);
        		}else
        		{
        			sb.append(arr[x]).append("+?");
        		}
        	}
        	String split1Word = new StringBuilder().append("\\w*").append(sb.toString()).append("\\w*").toString();
            if(blackWordList.containsKey(split1Word))
            {
            	return false;
            }else
            {
            	blackWordList.put(split1Word, thisSplit[1]);
                saveBlackList();
            }
        } else
        {
        	StringBuilder sb = new StringBuilder();
        	String arr[] = blackWord.split("");
        	for(int x = 1; x < arr.length; x++)
        	{
        		if(x == (arr.length - 1))
        		{
        			sb.append(arr[x]);
        		}else
        		{
        			sb.append(arr[x]).append("+?");
        		}
        	}
        	blackWord = new StringBuilder().append("\\w*").append(sb.toString()).append("\\w*").toString();
        	if(!blackWordList.containsKey(blackWord))
        	{
        		blackWordList.put(blackWord, "");
                saveBlackList();
        		return true;
        	}else
        	{
        		return false;
        	}
        }

        return true;
    }

    public boolean delBlackWord(String blackWord)
    {
        if(blackWord.indexOf(":") > 0)
        {
        	String thisSplit[] = blackWord.split(":", 2);
        	StringBuilder sb = new StringBuilder();
        	String arr[] = thisSplit[0].split("");
        	for(int x = 1; x < arr.length; x++)
        	{
        		if(x == (arr.length - 1))
        		{
        			sb.append(arr[x]);
        		}else
        		{
        			sb.append(arr[x]).append("+?");
        		}
        	}
        	blackWord = new StringBuilder().append("\\w*").append(sb.toString()).append("\\w*").toString();
        }else
        {
	    	StringBuilder sb = new StringBuilder();
	    	String arr[] = blackWord.split("");
	    	for(int x = 1; x < arr.length; x++)
	    	{
	    		if(x == (arr.length - 1))
	    		{
	    			sb.append(arr[x]);
	    		}else
	    		{
	    			sb.append(arr[x]).append("+?");
	    		}
	    	}
	    	blackWord = new StringBuilder().append("\\w*").append(sb.toString()).append("\\w*").toString();
        }
        if(blackWordList.containsKey(blackWord))
        {
            blackWordList.remove(blackWord);
            saveBlackList();
            return true;
        } else
        {
            return false;
        }
    }
    
	
    public boolean addBanWord(String banWord)
    {
    	StringBuilder sb = new StringBuilder();
    	String arr[] = banWord.split("");
    	for(int x = 1; x < arr.length; x++)
    	{
    		if(x == (arr.length - 1))
    		{
    			sb.append(arr[x]);
    		}else
    		{
    			sb.append(arr[x]).append("+?");
    		}
    	}
    	banWord = new StringBuilder().append("\\w*").append(sb.toString()).append("\\w*").toString();
    	if(instaBanList.containsKey(banWord))
    	{
    		return false;
    	}
    	else
        {
    		instaBanList.put(banWord, "");
        }
        saveBanList();
        return true;
    }
    
    public boolean delBanWord(String banWord)
    {
    	StringBuilder sb = new StringBuilder();
    	String arr[] = banWord.split("");
    	for(int x = 1; x < arr.length; x++)
    	{
    		if(x == (arr.length - 1))
    		{
    			sb.append(arr[x]);
    		}else
    		{
    			sb.append(arr[x]).append("+?");
    		}
    	}
    	banWord = new StringBuilder().append("\\w*").append(sb.toString()).append("\\w*").toString();
        if(instaBanList.containsKey(banWord))
        {
            instaBanList.remove(banWord);
            saveBanList();
            return true;
        } else
        {
            return false;
        }
    }
    
	public void saveBanList()
	{
        ArrayList banListA = new ArrayList();
        String printLine;
        for(Iterator iterator = instaBanList.entrySet().iterator(); iterator.hasNext(); banListA.add(printLine))
        {
            Map.Entry entry = (Map.Entry)iterator.next();
            String thisEntry = (String)entry.getKey();
            String thisReplacementment = (String)entry.getValue();
            printLine = thisReplacementment.length() <= 0 ? thisEntry : (new StringBuilder(String.valueOf(thisEntry))).append(":").append(thisReplacementment).toString();
        }

        getInstantConfig().set("InstaBanList", ((Object) (banListA.toArray())));
        saveInstaConfig();
    }

	public boolean getCapsOn()
	{
		return getConfig().getBoolean("Caps.Enabled");
	}

	public String getMessageCapsKick() 
	{
		return getConfig().getString("Message.Caps.Kick");
	}

	public boolean mutePlayer(String player)
	{
		if(mutedList.containsKey(player.toLowerCase()))
		{
			return false;
		}
		else
		{
			mutedList.put(player.toLowerCase(), true);
			saveMuteList();
			return true;
		}
	}
	
	public boolean unMutePlayer(String player)
	{
		if(mutedList.containsKey(player.toLowerCase()))
		{
			mutedList.remove(player.toLowerCase());
			saveMuteList();
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean isMuted(String player)
	{
		if(mutedList.containsKey(player.toLowerCase()))
		{
			return true;
		}else
		{
			return false;
		}
	}
	
	public void saveMuteList()
	{
        ArrayList muteList = new ArrayList();
        String thisEntry;
        for(Iterator iterator = mutedList.entrySet().iterator(); iterator.hasNext(); muteList.add(thisEntry.toLowerCase()))
        {
            Map.Entry entry = (Map.Entry)iterator.next();
            thisEntry = (String)entry.getKey();
        }
        getUserConfig().set("MuteList", ((Object) (muteList.toArray())));
        saveUserConfig();
    }
	
	public void loadMuteList()
	{
        for(Iterator iterator = getUserConfig().getStringList("MuteList").iterator(); iterator.hasNext();)
        {
        	String thisEntry = (String)iterator.next();
            mutePlayer(thisEntry);
        }
	}
	
	public void addRepeated(Player p, String message, Long time)
	{
		if(SpamCheck.lastMessage.containsKey(p))
		{
			SpamCheck.lastMessage.remove(p);
		}
		if(SpamCheck.lastMessageTimeStamp.containsKey(p))
		{
			SpamCheck.lastMessageTimeStamp.remove(p);
		}
		SpamCheck.lastMessage.put(p, message);
		SpamCheck.lastMessageTimeStamp.put(p, time);
	}

	public String redirect(Player player, String message, Long time)
	{
		if(!getConfig().getBoolean("SpamEnabled"))
		{
			return "";
		}else
		{
			return SpamCheck.spamCheck(player, message, time);
		}
	}
	
	public boolean containsIP(String message, Player p)
	{
		if(message.startsWith("http://") || message.startsWith("www") || p.hasPermission("pb.bypass.advertise") || p.hasPermission("pb.*"))
		{
			return false;
		}
		Pattern pattern = Pattern.compile("\\d{1,4}\\D{1,3}\\d{1,4}\\D{1,3}\\d{1,4}\\D{1,3}\\d{1,4}");
		Pattern pattern2 = Pattern.compile("[a-zA-Z]\\w*\\.[a-zA-Z]{4}\\w*\\.[a-zA-Z]\\w*");
		Pattern localc = Pattern.compile("192\\.168\\.[0-9]\\w*\\.[0-9]\\w*");
		Pattern locala = Pattern.compile("10\\.[0-9]\\w*\\.[0-9]\\w*\\.[0-9]\\w*");
		Pattern localb = Pattern.compile("172\\.[1-3][0-9]\\.[0-9]\\w*\\.[0-9]\\w*");
		if((pattern.matcher(message).find() || pattern2.matcher(message).find()) && !(locala.matcher(message).find() || localb.matcher(message).find() || localc.matcher(message).find()))
		{
			return true;
		}
		return false;
	}
	
public boolean getQueuePunish()
{
	if(getConfig().getString("QueuedMessagePunishment").equalsIgnoreCase("ban"))
	{
		return true;
	}else
	if(getConfig().getString("QueuedMessagePunishment").equalsIgnoreCase("mute"))
	{
		return false;
	}
	return true;
}
	
	/*public boolean reloadChanges()
	{
		try
		{
			getServer().getPluginManager().disablePlugin(plugin);
			getServer().getPluginManager().enablePlugin(plugin);
			return true;
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	*/
	
	
}
	