package com.github.hatixon.profanityblock;

import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.ChatColor;


public class CapsCheck implements Listener
{
	public static ProfanityBlock plugin;	
	public CapsCheck(ProfanityBlock instance)
	{
		plugin = instance;
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerChat(AsyncPlayerChatEvent e)
	{
		ChatColor RED = ChatColor.RED;
		ChatColor YEL = ChatColor.YELLOW;
		String pre = (new StringBuilder().append(RED).append("[ProfanityBlock]").append(YEL)).toString();
		String type = plugin.getConfig().getString("PunishmentType");
		if(!plugin.getCapsOn())
		{
			return;
		}
		if(e.getMessage().length() < plugin.getConfig().getInt("Caps.MinimumLength"))
		{
			return;
		}
		if(e.isCancelled())
		{
			return;
		}
		String message = e.getMessage();
		int BypassCode = plugin.getConfig().getInt("BypassCode.Caps");
		String contains1 = new StringBuilder().append("bypasscode").append(BypassCode).toString();
		int BypassCodeCancel = plugin.getConfig().getInt("BypassCode.CapsCancel");
		String contains2 = new StringBuilder().append("bypasscode").append(BypassCodeCancel).toString();
		if(message.contains(contains1))
		{
			e.setMessage(message.replaceAll(contains1, "").toLowerCase());
			return;
		}
		if(message.contains(contains2))
		{
			e.setCancelled(true);
			return;
		}
		Player p = e.getPlayer();
		int capsCount = 0;
		for(int c = 0; c < message.length(); c++)
		{
			int value = message.charAt(c);
			
            if(value >= 65 && value <= 90)
            {
                capsCount++;
            }
		}
		
		double percentCaps = (double)capsCount / (double)message.length();
		if(percentCaps > plugin.getCapsPercent())
		{
			if(p.hasPermission("pb.bypass.caps") || p.hasPermission("pb.*"))
			{
				return;
			}
			else
			{
				plugin.logPlayerSwearing(e.getPlayer().getName(), e.getMessage(), "Caps");
                if(plugin.getNotifyPlayer())
                {
                	p.sendMessage(new StringBuilder(pre).append(" ").append(plugin.getMessageCaps()).toString());
                }
				if(plugin.getCapsPunishment())
				{	
					String action;
					String uName = p.getName();


	                if(plugin.getNotifyOp())
	                {
	                	plugin.notifyOp(p, "caps");
	                }
					e.setCancelled(true);
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
		    						if(p.hasPermission("pb.bypass.ban") || p.hasPermission("pb.*"))
		    						{
		    							return;
		    						}
		    						else
		    						{
		    							action = plugin.getMessageKick();
		    							p.kickPlayer(action);
		    							if(plugin.getNotifyOp())
		    							{
		    								plugin.notifyOp(p, "kick");
		    							}
		    							return;
		    						}
			        			}
			        		}
			        		else
			        		if(warnRemainings.intValue() == 0)
			        		{
			        			if(p.hasPermission("pb.bypass.ban") || p.hasPermission("pb.*"))
			        			{
			        				p.sendMessage(new StringBuilder(pre).append(" Please stop using too many capitals.").toString());
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
		        						plugin.notifyOp(p, "muted");
		        					}
		        					p.sendMessage(new StringBuilder(pre).append(" You have been muted!").toString());
		        				}else
		        				if(plugin.getConfig().getString("MuteOrBan").equalsIgnoreCase("banned"))
		        				{
		        					plugin.capsBunnyRabbit(p);
		        					if(plugin.getNotifyOp())
		        					{
		        						plugin.notifyOp(p, "banned");
		        					}
		        				}else
		        				{
		        					plugin.logger.log(Level.SEVERE, new StringBuilder(pre).append(" Incorrect config option: MuteOrBan. Must be either \"mute\" or \"ban\"").toString());
		        				}
			        		}
		                }
		    		}
					if(type.contains(("lightning").toLowerCase()))
					{
						p.getWorld().strikeLightning(p.getLocation());
					}
					if(type.contains(("damage").toLowerCase()))
					{
						p.damage(plugin.getConfig().getInt("Damage"));
					}
				}else
				{
					e.setCancelled(true);
				}
			}
		}
	}
}
