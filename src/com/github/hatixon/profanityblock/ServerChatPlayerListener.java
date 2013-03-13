package com.github.hatixon.profanityblock;

import java.io.IOException;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.logging.Level;
import java.util.regex.*;
import com.github.hatixon.profanityblock.*;

@SuppressWarnings({"unused", "rawtypes"})
public class ServerChatPlayerListener extends JavaPlugin implements Listener 
{
	public static ProfanityBlock plugin;
    public ServerChatPlayerListener(ProfanityBlock Instance)
    {
        plugin = Instance;
        
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent chat)
    {
    	Map blackList = plugin.getBlackMap();
		String type = plugin.getConfig().getString("PunishmentType");
    	Player player = chat.getPlayer();
    	String message = chat.getMessage().toLowerCase();
    	ChatColor RED = ChatColor.RED;
    	ChatColor YEL = ChatColor.YELLOW;
    	Date date = new Date();
    	String action;
		String pre = (new StringBuilder().append(RED).append("[ProfanityBlock]").append(YEL)).toString();
		if(plugin.isMuted(player.getName().toLowerCase()))
		{
			chat.setCancelled(true);
			player.sendMessage(new StringBuilder(pre).append(" You are muted and can not talk!").toString());
			return;
		}
		if(plugin.containsIP(message, player))
		{
			player.sendMessage(new StringBuilder(pre).append(" Do not advertise!").toString());
			chat.setCancelled(true);
			return;
		}
		if(!player.hasPermission("pb.bypass.spam"))
		{
			String verdict = plugin.redirect(player, message, new Date().getTime());
			if(verdict.isEmpty())
			{
				
			}else
			{
				plugin.logPlayerSwearing(player.getName(), message, "Spam");
				chat.setMessage("");
				chat.setCancelled(true);
				
			}
			
			if(verdict.equalsIgnoreCase("banned"))
			{
				if(plugin.getConfig().getString("SpamMuteOrBan").equalsIgnoreCase("mute"))
				{
					plugin.mutePlayer(player.getName());
					if(plugin.getNotifyOp())
					{
						plugin.notifyOp(player, "muted");
					}
					player.sendMessage(new StringBuilder(pre).append(" You have been muted!").toString());
				}else
				if(plugin.getConfig().getString("SpamMuteOrBan").equalsIgnoreCase("ban"))
				{
					plugin.spamBan(player);
					if(plugin.getNotifyOp())
					{
						plugin.notifyOp(player, "banned");
					}
				}else
				{
					plugin.logger.log(Level.SEVERE, new StringBuilder(pre).append(" Incorrect config option: MuteOrBan. Must be either \"mute\" or \"ban\"").toString());
				}
				chat.setCancelled(true);
			}else
			if(verdict.contains("warned"))
			{
				String pre2 = new StringBuilder().append("\247c").append("[ProfanityBlock]").append("\247e").toString();
				if(verdict.contains("repeated"))
				{
					plugin.logger.log(Level.INFO, new StringBuilder().append(pre2).append(player.getName()).append(" blocked for excessive letter repitition: ").append(message).toString());
				}else
				{
					plugin.logger.log(Level.INFO, new StringBuilder().append(pre2).append(player.getName()).append(" blocked by spam: ").append(message).toString());
				}
				player.sendMessage(new StringBuilder().append("<").append(player.getDisplayName()).append("> ").append(message).toString());
				chat.getFormat();
				plugin.addRepeated(player,chat.getMessage(), new Date().getTime());
				chat.setCancelled(true);
				return;
			}else
			{
				
			}
			plugin.addRepeated(player,chat.getMessage(), new Date().getTime());
		}
		
		if(player.hasPermission("pb.bypass.swear") || player.hasPermission("pb.*"))
		{
			return;
		}
		
		if(chat.getMessage() == "" || chat.isCancelled())
		{
			return;
		}
		
		if(plugin.instaBanCheck(message))
		{
    		if(player.hasPermission("pb.bypass.ban") || player.hasPermission("pb.*"))
    		{
    			player.sendMessage(new StringBuilder(pre).append(" ").append(plugin.getMessageWarn()).toString());
    			plugin.logPlayerSwearing(chat.getPlayer().getName(), chat.getMessage(), "Chat");
    			String message2 = plugin.instaCensorCheck(message);
    			if(message2.length() > 0)
    			{
    				chat.setMessage(message2);
    			}else
    			{
    				chat.setCancelled(true);
    			}
    		}else
    		{	
    			plugin.logPlayerSwearing(chat.getPlayer().getName(), chat.getMessage(), "Chat");
    			String message2 = plugin.instaCensorCheck(message);
    			if(message2.length() > 0)
    			{
    				chat.setCancelled(true);

    				plugin.playerCapsCheck.bypassMaps.add(chat.getPlayer().getName());
    			} else
    			{    				
    				chat.setMessage(message2);
    			}
    			if(plugin.getConfig().getString("MuteOrBan").equalsIgnoreCase("mute"))
    			{
    				plugin.mutePlayer(player.getName().toLowerCase());
    				if(plugin.getNotifyOp())
    				{
    					plugin.notifyOp(player, "muted");
    				}
    				player.sendMessage(new StringBuilder(pre).append(" You have been muted!").toString());
    			}else
    			if(plugin.getConfig().getString("MuteOrBan").equalsIgnoreCase("ban"))
    			{
    				if(plugin.getNotifyOp())
    				{
    					plugin.notifyOp(player, "banned");
    				}
    				plugin.instaBanPlayer(player);
    			}else
    			{
    				plugin.logger.log(Level.SEVERE, new StringBuilder(pre).append(" Incorrect config option: MuteOrBan. Must be either \"mute\" or \"ban\"").toString());
    			}
	            return;
    		}
		}else
		{
	    	if(plugin.didTheySwear(message))
	    	{

	    		plugin.logPlayerSwearing(chat.getPlayer().getName(), chat.getMessage(), "Chat");
				
	    		String uName = player.getName();
    			String message2 = plugin.censorCheck(message);
    			if(message2.length() > 0)
    			{
    				chat.setMessage(message2);
    				plugin.playerCapsCheck.bypassMaps.add(uName);
    			} else
    			{    				
    				chat.setCancelled(true);
    			}
	            if(plugin.getNotifyOp())
	            {
	            	plugin.notifyOp(player, "chat");
	            }
	            
	    		if(plugin.getMoneyEnabled())
	    		{
	    			if(player.hasPermission("pb.bypass.money") || player.hasPermission("pb.*"))
	    			{
	    				
	    			}
	    			else
	    			{
	    				plugin.executeMoneyRemoval(uName);
	    			}
	    		}
	    		if(plugin.getNotifyPlayer())
	    		{
	    			player.sendMessage(new StringBuilder(pre).append(" ").append(plugin.getMessageWarn()).toString());
	    		}
	    		plugin.addRepeated(player, chat.getMessage(), new Date().getTime());
	    		if(type.contains(("warnings").toLowerCase()))
	    		{
		    		if(plugin.getTotWarn().intValue() != -1)
		    		{
		    			Integer wBK = plugin.getWarnBKick();
		                Integer warnRemaining = plugin.getRemWarn(uName);
		                Integer warnRemainings = Integer.valueOf((warnRemaining.intValue()) - 1);
		                plugin.setRemWarn(uName, warnRemainings);
		                
		        		if(warnRemainings.intValue() > 0)
		        		{
		    				if(warnRemainings.intValue() < wBK)
		    				{
		    						if(player.hasPermission("pb.bypass.ban") || player.hasPermission("pb.*"))
		    						{
		    							return;
		    						}
		    						else
		    						{
		    							action = plugin.getMessageKick();
		    							player.kickPlayer(action);
		    							if(plugin.getNotifyOp())
		    							{
		    								plugin.notifyOp(player, "kick");
		    							}
		    							return;
		    						}
		        			}
		        		}
		        		else
		        		if(warnRemainings.intValue() == 0)
		        		{
			        		{
			        			if(player.hasPermission("pb.bypass.ban") || player.hasPermission("pb.*"))
			        			{
			        				player.sendMessage(new StringBuilder(pre).append(" Please stop using too many capitals.").toString());
			        				plugin.resetBanned(uName);
			        				return;
			        			}
		        				if(plugin.getResetOnBan())
		        				{
		        					plugin.resetBanned(uName);
		        				}
		        				if(plugin.getConfig().getString("MuteOrBan").equalsIgnoreCase("mute"))
		        				{
		        					plugin.mutePlayer(uName);
		        					if(plugin.getNotifyOp())
		        					{
		        						plugin.notifyOp(player, "muted");
		        					}
		        					player.sendMessage(new StringBuilder(pre).append(" You have been muted!").toString());
		        				}else
		        				if(plugin.getConfig().getString("MuteOrBan").equalsIgnoreCase("ban"))
		        				{
		        					plugin.bunnyRabbit(player);
		        					if(plugin.getNotifyOp())
		        					{
		        						plugin.notifyOp(player, "banned");
		        					}
		        				}else
		        				{
		        					plugin.logger.log(Level.SEVERE, new StringBuilder(pre).append(" Incorrect config option: MuteOrBan. Must be either \"mute\" or \"ban\"").toString());
		        				}
			        		}
		        		}
		    		}
	    		}
				if(type.contains(("Lightning").toLowerCase()))
				{
					player.getWorld().strikeLightning(player.getLocation());
				}
				if(type.contains(("damage").toLowerCase()))
				{
					player.damage(plugin.getConfig().getInt("Damage"));
				}
				return;
	    	}
	    	if(!plugin.messageLineup.containsKey(player.getName()))
	    	{
	    		plugin.addPlayerToQueue(player.getName());
	    	}
			Queueing mQueue = (Queueing)plugin.messageLineup.get(chat.getPlayer().getName());
	    	mQueue.addMessage(" " + chat.getMessage().toLowerCase());
			String queueMessage = mQueue.returnQueue();
			if(plugin.instaBanCheck(queueMessage))
			{
				chat.setCancelled(true);
				mQueue.clearAll();
				if(plugin.getQueuePunish())
				{
					plugin.instaBanPlayer(player);
					plugin.logPlayerSwearing(player.getName(), queueMessage, "Split across lines");
				}else
				if(plugin.getQueuePunish() == false)
				{
					plugin.mutePlayer(player.getName().toLowerCase());
    				if(plugin.getNotifyOp())
    				{
    					plugin.notifyOp(player, "muted");
    				}
    				player.sendMessage(new StringBuilder(pre).append(" You have been muted!").toString());
				}
			}else
			{
		    	if(plugin.didTheySwear(queueMessage))
		    	{
		    		mQueue.clearAll();
		    		plugin.logPlayerSwearing(chat.getPlayer().getName(), queueMessage, "Split across lines");
		    		String uName = player.getName();
		    		chat.setCancelled(true);
		    		
		            if(plugin.getNotifyOp())
		            {
		            	plugin.notifyOp(player, "Chat");
		            }
		            
		    		if(plugin.getMoneyEnabled())
		    		{
		    			if(player.hasPermission("pb.bypass.money") || player.hasPermission("pb.*"))
		    			{
		    				
		    			}
		    			else
		    			{
		    				plugin.executeMoneyRemoval(uName);
		    			}
		    		}
		    		
		    		if(plugin.getNotifyPlayer())
		    		{
		    			player.sendMessage(new StringBuilder(pre).append(" ").append(plugin.getMessageWarn()).toString());
		    		}
		    		
		    		plugin.addRepeated(player, chat.getMessage(), new Date().getTime());
		    		
		    		if(type.contains(("warnings").toLowerCase()))
		    		{
			    		if(plugin.getTotWarn().intValue() != -1)
			    		{
			    			Integer wBK = plugin.getWarnBKick();
			                Integer warnRemaining = plugin.getRemWarn(uName);
			                Integer warnRemainings = Integer.valueOf((warnRemaining.intValue()) - 1);
			                plugin.setRemWarn(uName, warnRemainings);
			                
			        		if(warnRemainings.intValue() > 0)
			        		{
			    				if(warnRemainings.intValue() < wBK)
			    				{
			    						if(player.hasPermission("pb.bypass.ban") || player.hasPermission("pb.*"))
			    						{
			    							return;
			    						}
			    						else
			    						{
			    							action = plugin.getMessageKick();
			    							player.kickPlayer(action);
			    							if(plugin.getNotifyOp())
			    							{
			    								plugin.notifyOp(player, "kick");
			    							}
			    							return;
			    						}
			        			}
			        		}
			        		else
			        		if(warnRemainings.intValue() == 0)
			        		{
				        		{
				        			if(player.hasPermission("pb.bypass.ban") || player.hasPermission("pb.*"))
				        			{
				        				player.sendMessage(new StringBuilder(pre).append(" Please stop using too many capitals.").toString());
				        				plugin.resetBanned(uName);
				        				return;
				        			}
			        				if(plugin.getResetOnBan())
			        				{
			        					plugin.resetBanned(uName);
			        				}
			        				if(plugin.getConfig().getString("MuteOrBan").equalsIgnoreCase("mute"))
			        				{
			        					plugin.mutePlayer(uName);
			        					if(plugin.getNotifyOp())
			        					{
			        						plugin.notifyOp(player, "muted");
			        					}
			        					player.sendMessage(new StringBuilder(pre).append(" You have been muted!").toString());
			        				}else
			        				if(plugin.getConfig().getString("MuteOrBan").equalsIgnoreCase("ban"))
			        				{
			        					plugin.bunnyRabbit(player);
			        					if(plugin.getNotifyOp())
			        					{
			        						plugin.notifyOp(player, "banned");
			        					}
			        				}else
			        				{
			        					plugin.logger.log(Level.SEVERE, new StringBuilder(pre).append(" Incorrect config option: MuteOrBan. Must be either \"mute\" or \"ban\"").toString());
			        				}
				        		}
			        		}
			    		}
		    		}
					if(type.contains(("Lightning").toLowerCase()))
					{
						player.getWorld().strikeLightning(player.getLocation());
					}
					if(type.contains(("damage").toLowerCase()))
					{
						player.damage(plugin.getConfig().getInt("Damage"));
					}
					return;
		    	}
			}
		}
    }
    
	@EventHandler
    public void onPlayJoin(PlayerJoinEvent join)
    {
    	Player p = join.getPlayer();
    	String uName = p.getName();
    	String userWarnings = (new StringBuilder("Warnings.Warned.").append(uName.toLowerCase())).toString();
    	ChatColor RED = ChatColor.RED;
    	ChatColor YEL = ChatColor.YELLOW;
		String pre = (new StringBuilder().append(RED).append("[ProfanityBlock]").append(YEL)).toString();
    	if(plugin.getWarnConfig().getString(userWarnings) == null)
    	{
    		plugin.setWarnJoin(uName);
    		p.sendMessage(new StringBuilder(pre).append(plugin.getJoinMessage()).toString());
    	}else
    	{
    		p.sendMessage(new StringBuilder(pre).append(" You have ").append(plugin.getRemWarn(uName.toLowerCase())).append(" warning(s) remaining.").toString());
    	}

    	if(plugin.getConfig().getBoolean("CheckForUpdates"))
    	{
    		if(p.hasPermission("pb.version") || p.hasPermission("pb.*"))
    		{
    			if(plugin.isUpdated())
    			{
    				p.sendMessage(new StringBuilder(pre).append(" There is an updated version of ProfanityBlock. Download at http://dev.bukkit.org/server-mods/profanityblock").toString());
    			}
    		}
    	}
    	SpamCheck.lastMessage.remove(p);
    	SpamCheck.lastMessageTimeStamp.remove(p);
    	SpamCheck.spamCount.remove(p);
    	plugin.addPlayerToQueue(join.getPlayer().getName());
    }
}