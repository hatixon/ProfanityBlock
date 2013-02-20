package com.github.hatixon.profanityblock;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

@SuppressWarnings({"rawtypes", "unused"})
public class CommandListener implements Listener
{
	public static ProfanityBlock plugin;
	
	public CommandListener(ProfanityBlock instance)
	{
		plugin = instance;
	}
	
	@EventHandler
	public void cmdPreProcess(PlayerCommandPreprocessEvent e)
	{
		Map blackList = plugin.getBlackMap();
		String type = plugin.getConfig().getString("PunishmentType");
    	ChatColor RED = ChatColor.RED;
    	ChatColor YEL = ChatColor.YELLOW;
		String pre = (new StringBuilder().append(RED).append("[ProfanityBlock]").append(YEL)).toString();
		if(plugin.getCommandCheck())
		{
			Player player = e.getPlayer();
			String uName = player.getName();
			String splits[] = e.getMessage().split(" ");
			String cmd = splits[0].replace("/", "");
			if(player.hasPermission("pb.bypass.commandcheck") || player.hasPermission("pb.*"))
			{
				return;
			}
			if(plugin.commandSwear(cmd))
			{
				if(plugin.isMuted(player.getName().toLowerCase()))
				{
					e.setCancelled(true);
					player.sendMessage(new StringBuilder(pre).append(" You are muted and can not talk!").toString());
					return;
				}
				StringBuilder sb = new StringBuilder();
				int l;
				for(l = 1; l < splits.length; l++)
				{
					sb.append(splits[l]).append(" ");
				}
		    	String message = sb.toString();
		    	String action;
	    		if(player.hasPermission("pb.bypass.swear") || player.hasPermission("pb.*"))
	    		{
	    			return;
	    		}
				if(plugin.instaBanCheck(message))
				{

		    		if(player.hasPermission("pb.bypass.ban") || player.hasPermission("pb.*"))
		    		{
		    			e.setCancelled(true);
		    			plugin.logPlayerSwearing(e.getPlayer().getName(), e.getMessage(), "Commands");
		    			player.sendMessage(new StringBuilder(pre).append(" Please stop swearing. If this continues, punishment may occur").toString());
		    			return;
		    		}
		    		else
		    		{
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
		    			e.setCancelled(true);
			            plugin.logPlayerSwearing(e.getPlayer().getName(), e.getMessage(), "Commands");
			            return;
		    		}
	    	
				}else
				{

			    	if(plugin.didTheySwear(message))
			    	{

		    			e.setCancelled(true);
			            if(plugin.getNotifyOp())
			            {
			            	plugin.notifyOp(player, "command");
			            }
			    		if(plugin.getMoneyEnabled())
			    		{
			    			if(player.hasPermission("pb.bypass.money") || player.hasPermission("pb.bypass.*"))
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
			    		plugin.logPlayerSwearing(e.getPlayer().getName(), e.getMessage(), "Commands");
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
				        			if(player.hasPermission("pb.bypass.ban") || player.hasPermission("pb.*"))
				        			{
				        				player.sendMessage(new StringBuilder(pre).append(" Please stop swearing. If this continues, punishment may occur").toString());
			        					plugin.resetBanned(uName);
				        			}else
				        			{
				        				action = plugin.getMessageBanned();
				        				if(plugin.getResetOnBan())
				        				{
				        					plugin.resetBanned(uName);
				        				}
				        					plugin.bunnyRabbit(player);
				        					if(plugin.getNotifyOp())
				        					{
				        						plugin.notifyOp(player, "banned");
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
					if(!player.hasPermission("pb.bypass.spam"))
					{
						String verdict = plugin.redirect(player, message, new Date().getTime());
						if(verdict.isEmpty())
						{
							
						}else
						{
							e.setCancelled(true);
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
							e.setCancelled(true);
						}else
						if(verdict.equalsIgnoreCase("warned"))
						{
							plugin.logger.log(Level.INFO, new StringBuilder().append("Player ").append(player.getName()).append(" blocked by spam: ").append(e.getMessage()).toString());
							plugin.addRepeated(player,e.getMessage(), new Date().getTime());
							e.setCancelled(true);
							return;
						}else
						{
						}
						plugin.addRepeated(player,e.getMessage(), new Date().getTime());
						return;
					}
				}
			}
		}
	}
}
