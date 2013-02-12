package com.github.hatixon.profanityblock;

import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.*;
import org.bukkit.entity.Player;
@SuppressWarnings("unused")
public class SignPlaceListener implements Listener
{

	public static ProfanityBlock plugin;
	
	public SignPlaceListener(ProfanityBlock instance)
	{
		plugin = instance;
	}
	@EventHandler
	public void onSignPlace(SignChangeEvent e)
	{			
		ChatColor RED = ChatColor.RED;
	    ChatColor YEL = ChatColor.YELLOW;
	    String pre = (new StringBuilder().append(RED).append("[ProfanityBlock]").append(YEL)).toString();
		String type = plugin.getConfig().getString("PunishmentType");
		if(plugin.getConfig().getBoolean("Check.Signs"))
		{
			Player player = e.getPlayer();
			String uName = player.getName();
			if(player.hasPermission("pb.bypass.signcheck") || player.hasPermission("pb.*"))
			{
				return;
			}
			if(plugin.isMuted(player.getName().toLowerCase()))
			{
				e.setCancelled(true);
				player.sendMessage(new StringBuilder(pre).append(" You are muted and can not talk!").toString());
				return;
			}

	    	String action;

			StringBuilder messageSB = new StringBuilder();
			for(int c = 0; c < 4; c++)
			{
				if(c == 3)
				{
					messageSB.append(e.getLine(c));
					break;
				}
				if(e.getLine(c + 1).isEmpty())
				{
					messageSB.append(e.getLine(c));
				}else{
					messageSB.append(e.getLine(c)).append(" ");
				}
			}
			String message = messageSB.toString();
    		if(player.hasPermission("pb.bypass.swear") || player.hasPermission("pb.bypass.*"))
    		{
    			return;
    		}
			if(plugin.instaBanCheck(message))
			{

	    		if(player.hasPermission("pb.bypass.ban") || player.hasPermission("pb.bypass.*"))
	    		{
	    			plugin.logPlayerSwearing(e.getPlayer().getName(), messageSB.toString(), "Sign");
		    		if(plugin.getConfig().getBoolean("BreakSignsOnSwear"))
		    		{
		    			e.getBlock().breakNaturally();
		    		}else
		    		{
		    			e.setLine(0, "ProfanityBlock");
		    			e.setLine(1, "Sign message");
		    			e.setLine(2, "has been");
		    			e.setLine(3, "cancelled!");
		    			e.getBlock().getState().update();
		    		}
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
	    			plugin.logPlayerSwearing(e.getPlayer().getName(), message, "Signs");
		    		if(plugin.getConfig().getBoolean("BreakSignsOnSwear"))
		    		{
		    			e.getBlock().breakNaturally();
		    		}else
		    		{
		    			e.setLine(0, "ProfanityBlock");
		    			e.setLine(1, "Sign message");
		    			e.setLine(2, "has been");
		    			e.setLine(3, "cancelled!");
		    			e.getBlock().getState().update();
			            return;
		    		}
	    		}
			}else
			{
		    	if(plugin.didTheySwear(message))
		    	{
					plugin.logPlayerSwearing(e.getPlayer().getName(), message, "Signs");
					
		    		if(plugin.getConfig().getBoolean("BreakSignsOnSwear"))
		    		{
		    			e.getBlock().breakNaturally();
		    		}else
		    		{
		    			e.setLine(0, "ProfanityBlock");
		    			e.setLine(1, "Sign message");
		    			e.setLine(2, "has been");
		    			e.setLine(3, "cancelled!");
		    			e.getBlock().getState().update();
		    		}
		            
		            if(plugin.getNotifyOp())
		            {
		            	plugin.notifyOp(player, "sign");
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
			    						if(player.hasPermission("pb.bypass.ban") || player.hasPermission("pb.bypass.*"))
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
				        				player.sendMessage(new StringBuilder(pre).append(" Please stop swearing. Action may be taken if you continue.").toString());
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
			        						plugin.notifyOp(player, "muted");
			        					player.sendMessage(new StringBuilder(pre).append(" You have been muted!").toString());
			        				}else
			        				if(plugin.getConfig().getString("MuteOrBan").equalsIgnoreCase("ban"))
			        				{
			        					plugin.bunnyRabbit(player);
			        					if(plugin.getNotifyOp())
			        						plugin.notifyOp(player, "banned");
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
		    	}
			}
		}
	}
}
