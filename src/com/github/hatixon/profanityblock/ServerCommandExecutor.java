package com.github.hatixon.profanityblock;

import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

@SuppressWarnings("rawtypes")
public class ServerCommandExecutor implements CommandExecutor
{
	
	public static ProfanityBlock plugin;
	
	public ServerCommandExecutor(ProfanityBlock Instance)
	{
		plugin = Instance;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[])
	{

		ChatColor RED = ChatColor.RED;
		ChatColor YEL = ChatColor.YELLOW;
		Map blackList = plugin.getBlackMap();
		Map whiteList = plugin.getWhiteMap();
		Map userList = plugin.getUserMap();
		Map instaBanList = plugin.getBanList();
		String pre = (new StringBuilder().append(RED).append("[ProfanityBlock]").append(YEL)).toString();
		String cantUse = new StringBuilder(pre).append(" You can not use this command!").toString();
		if(sender instanceof ConsoleCommandSender)
		{
			ConsoleCommandSender ccs = (ConsoleCommandSender)sender;
			if(cmd.getName().equalsIgnoreCase("pb"))
	        {
				if(args.length < 1)
				{
					ccs.sendMessage(new StringBuilder(pre).append("pb help").toString());
					return true;
				}
				if(args.length > 0)
				{
					String param = args[0].toLowerCase();
	            	if(param.equalsIgnoreCase("mute"))
	            	{
						if(args.length == 2)
						{
							String uName = args[1];
							if(Bukkit.getPlayer(args[1].toLowerCase()).hasPermission("pb.*") || Bukkit.getPlayer(args[1].toLowerCase()).hasPermission("pb.bypass.ban"))
							{
								ccs.sendMessage(new StringBuilder(pre).append(" This player can not be muted!").toString());
								return true;
							}
							if(plugin.mutePlayer(uName))
							{
								ccs.sendMessage(new StringBuilder(pre).append(" ").append(uName).append(" was muted!").toString());
							}else
							{
								ccs.sendMessage(new StringBuilder(pre).append(" ").append(uName).append(" is already muted!").toString());
							}
						}else
						{
							ccs.sendMessage(new StringBuilder(pre).append(" Please specify the player you are muting.").toString());
						}
	            	}
	            	if(param.equalsIgnoreCase("unmute"))
	            	{
						if(args.length == 2)
						{
							String uName = args[1];
							if(plugin.unMutePlayer(uName))
							{
								ccs.sendMessage(new StringBuilder(pre).append(" ").append(uName).append(" was unmuted!").toString());
							}else
							{
								ccs.sendMessage(new StringBuilder(pre).append(" ").append(uName).append(" is not muted!").toString());
							}
						}else
						{
							ccs.sendMessage(new StringBuilder(pre).append(" Please specify the player you are unmuting.").toString());
						}
	            	}
	            	if(param.equalsIgnoreCase("download"))
	            	{
	            		if(plugin.getOnlineList())
	            		{
	            			ccs.sendMessage(new StringBuilder(pre).append(" A preconfigured word list has been downloaded!").toString());
	            		}else
	            		{
	            			ccs.sendMessage(new StringBuilder(pre).append(" Failed to download word list!").toString());
	            		}
	            	}
					if(param.equalsIgnoreCase("resetall"))
					{
						String thisPlayer;

							if(args.length > 1)
							{
								String amount = args[1];
								for(Iterator iterator = userList.entrySet().iterator(); iterator.hasNext();)
		        	            {
									java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
									thisPlayer = (String)entry.getKey();
									plugin.editWarn(thisPlayer, amount);
		        	            }
								ccs.sendMessage(new StringBuilder(pre).append(" Players warnings have been reset to ").append(amount).toString());
							}else
							{
								for(Iterator iterator = userList.entrySet().iterator(); iterator.hasNext();)
		        	            {
								java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
								thisPlayer = (String)entry.getKey();
								plugin.resetWarn(thisPlayer);
		        	            }
								ccs.sendMessage(new StringBuilder(pre).append(" Players warnings have been reset to default").toString());
							}
						
						return true;
					}
					if(param.equalsIgnoreCase("warnings") || param.equalsIgnoreCase("w"))
					{
						if(args.length > 2)
						{
							ccs.sendMessage(new StringBuilder(pre).append(" Too many players!").toString());
							return true;
						}
						if(args.length < 2)
						{
							ccs.sendMessage(new StringBuilder(pre).append(" Please specify a player!").toString());
							return true;
						}
						String uName = args[1];
            			int remWarn = plugin.getRemWarn(uName.toLowerCase());
            			if(remWarn != 0)
            			{
            				ccs.sendMessage(new StringBuilder(pre).append(" ").append(uName.toUpperCase()).append(" has ").append(remWarn).append(" warning(s) left.").toString());
            			}
            			else
            			{
            				ccs.sendMessage(new StringBuilder(pre).append(" ").append(uName.toUpperCase()).append(" does not exist. Make sure you spelt the name right.").toString());
            			}
            			return true;
					}
					if(!param.equalsIgnoreCase("whitelist") 
							&& !param.equalsIgnoreCase("blacklist") 
							&& !param.equalsIgnoreCase("reset") 
							&& !param.equalsIgnoreCase("warnings") 
							&& !param.equalsIgnoreCase("help") 
							&& !param.equalsIgnoreCase("instaban") 
							&& !param.equalsIgnoreCase("resetall") 
							&& !param.equalsIgnoreCase("info")
							&& !param.equalsIgnoreCase("set")
							&& !param.equalsIgnoreCase("wl")
							&& !param.equalsIgnoreCase("bl")
							&& !param.equalsIgnoreCase("ib")
							&& !param.equalsIgnoreCase("w")
							&& !param.equalsIgnoreCase("unmute")
							&& !param.equalsIgnoreCase("mute")
							&& !param.equalsIgnoreCase("download"))
						
	                {
	                	ccs.sendMessage(new StringBuilder(pre).append(" No such command. Use /pb help").toString());
	                	return true;
	                }

	            	if(param.equalsIgnoreCase("whitelist") || param.equalsIgnoreCase("wl"))
	            	{
	            		if(args.length < 2)
	            		{
	            			ccs.sendMessage(new StringBuilder(pre).append(" /pb whitelist help").toString());
	            			return true;
	            		}
	            		
	            		if(!args[1].equalsIgnoreCase("list")
	            				&& !args[1].equalsIgnoreCase("add") 
	            				&& !args[1].equalsIgnoreCase("delete")
	            				&& !args[1].equalsIgnoreCase("help")
	            				&& !args[1].equalsIgnoreCase("del"))
	            		{
	            			ccs.sendMessage(new StringBuilder(pre).append(" '").append(args[1]).append("' is not a valid parameter /pb whitelist help").toString());
	            			return true;
	            		}
	            		
	            		if(args[1].equalsIgnoreCase("list"))
		            	{

		        	            String thisWord;
		        	            String replaceAppend;
		        	            ccs.sendMessage((new StringBuilder(pre).append(" Whitelisted words:")).toString());
		            			for(Iterator iterator = whiteList.entrySet().iterator(); iterator.hasNext(); sender.sendMessage((new StringBuilder(String.valueOf(thisWord).replace("\\w*", "")/*.replace("+?", "")*/)).append(replaceAppend).toString()))
		        	            {
		        	                java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
		        	                thisWord = (String)entry.getKey();
		        	                String thisReplace = (String)entry.getValue();
		        	                replaceAppend = thisReplace.length() <= 0 ? "" : (new StringBuilder(":")).append(thisReplace).toString();
		        	            }
		            			return true;
	            		}
	            		

		            	if(args[1].equalsIgnoreCase("add"))
		            	{

			            		if(args.length > 3)
			            		{
			            			ccs.sendMessage(new StringBuilder(pre).append(" Too many arguments!").toString());
			            			return true;
			            		}
		            			if(args.length > 2)
		                        {
		                            String whiteWord = args[2].toLowerCase().replace("\\w*", "").replace("+?", "");
	                            	if(plugin.addWhiteWord(whiteWord))
	                            	{
	                            		ccs.sendMessage(new StringBuilder(pre).append(" ").append(whiteWord).append(" was added to the whitelist.").toString());
	                            	} else	
	                            	{
	                            		ccs.sendMessage(new StringBuilder(pre).append(" ").append(whiteWord).append(" is already in the whitelist.").toString());
	                            	}
	                            	return true;
		                        }else
		                        if(args.length < 3)
			            		{
			            			ccs.sendMessage(new StringBuilder(pre).append(" Not enough arguments /pb whitelist add <word>").toString());
			            			return true;
			            		}

		            	}
		            	if(args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("del"))
		            	{
	
			            		if(args.length > 3)
			            		{
			            			ccs.sendMessage(new StringBuilder(pre).append(" Too many arguments!").toString());
			            			return true;
			            		}else
		                        if(args.length > 2)
		                        {
		                            String whiteWord = args[2].toLowerCase().replace("\\w*", "").replace("+?", "");

		                            if(plugin.delWhiteWord(whiteWord))
		                            {
		                                ccs.sendMessage(new StringBuilder(pre).append(" ").append(whiteWord).append(" was deleted from the whitelist.").toString());
		                            } else
		                            {
		                                ccs.sendMessage(new StringBuilder(pre).append(" ").append(whiteWord).append(" is not in the whitelist.").toString());
		                            }
		                            return true;
		                            
		                        }else
		                        	
		                        if(args.length < 3)
			            		{
			            			ccs.sendMessage(new StringBuilder(pre).append(" Not enough arguments /pb whitelist delete <words>").toString());
			            			return true;
			            		}
		            		
	            		}
		            	if(args[1].equalsIgnoreCase("help"))
		            	{
		            		ccs.sendMessage(new StringBuilder(pre).append(" /pb whitelist parameters are:").toString());
		            		ccs.sendMessage(new StringBuilder().append(" help - this command").toString());
		            		ccs.sendMessage(new StringBuilder().append(" list - lists all the whitelisted words").toString());
		            		ccs.sendMessage(new StringBuilder().append(" add word - add an allowed word to the whitelist").toString());
		            		ccs.sendMessage(new StringBuilder().append(" delete [word] - delete a word from the whitelist").toString());
	        			}
		            	return true;
	            	}
	            	if(param.equalsIgnoreCase("blacklist") || param.equalsIgnoreCase("bl"))
		            {

						if(args.length == 1)
						{
							ccs.sendMessage(new StringBuilder(pre).append(" /pb blacklist help").toString());
							return true;
						}
						
						if(!args[1].equalsIgnoreCase("list")
								&& !args[1].equalsIgnoreCase("add")
								&& !args[1].equalsIgnoreCase("delete")
								&& !args[1].equalsIgnoreCase("help")
								&& args[1].equalsIgnoreCase("del"))
						{
							ccs.sendMessage(new StringBuilder(pre).append(" '").append(args[1]).append("' is not a valid parameter /pb blacklist help").toString());			            		
							return true;
						}		            			

						if(args[1].equalsIgnoreCase("list"))
						{

							String thisWord;
							String replaceAppend;
							ccs.sendMessage((new StringBuilder(pre).append(" Blacklisted words:")).toString());
							for(Iterator iterator = blackList.entrySet().iterator(); iterator.hasNext(); ccs.sendMessage((new StringBuilder(String.valueOf(thisWord).replace("\\w*", "")/*.replace("+?", "")*/)).append(replaceAppend).toString()))
							{
								java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
								thisWord = (String)entry.getKey();
								String thisReplace = (String)entry.getValue();
								replaceAppend = thisReplace.length() <= 0 ? "" : (new StringBuilder(":")).append(thisReplace).toString();
							}
							return true;

						}
						if(args[1].equalsIgnoreCase("add"))
						{

							if(args.length > 2)
							{
								String blackWord = "";
								if(args.length > 3)
								{
									if(args.length > 4)
									{
										ccs.sendMessage(new StringBuilder(pre).append("Too many arguments - /pb blacklist help").toString());
									}else
									{
										StringBuilder sb = new StringBuilder();
										for(int i = 2; i < args.length; i++)
										{
											sb.append(args[i]);
											if(i < args.length - 1)
											{
												sb.append(":");
											}
										}
										blackWord = sb.toString().toLowerCase().replace("\\w*", "").replace("+?", "");
									}
								} else
								{
									blackWord = args[2].toLowerCase().replace("\\w*", "").replace("+?", "");
								}
								if(plugin.addBlackWord(blackWord))
								{
									ccs.sendMessage(new StringBuilder(pre).append(" ").append(blackWord).append(" was added to the blacklist.").toString());
								} else
								{
									String blackwords[] = blackWord.split(":");
									ccs.sendMessage(new StringBuilder(pre).append(" ").append(blackwords[0]).append(" is already in the blacklist.").toString());
								}
								return true;
							}

						}
						if(args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("del"))
						{

							if(args.length > 2)
							{
								
								String blackWord = "";

								if(args.length > 3)
								{
									if(args.length > 4)
									{
										ccs.sendMessage(new StringBuilder(pre).append("Too many arguments - /pb blacklist help").toString());
										return true;
									}else
									{
										StringBuilder sb = new StringBuilder();
										for(int i = 2; i < args.length; i++)
										{
											sb.append(args[i]);
											if(i < args.length - 1)
											{
												sb.append(":");
											}
										}
										blackWord = sb.toString().toLowerCase().replace("\\w*", "").replace("+?", "");
									}
								}else
								{
									blackWord = args[2].toLowerCase().replace("\\w*", "").replace("+?", "");
								}
								if(plugin.delBlackWord(blackWord))
								{
									String blackwords[] = blackWord.split(":");
									ccs.sendMessage(new StringBuilder(pre).append(" ").append(blackwords[0]).append(" was deleted from the blacklist.").toString());
								} else
								{
									String blackwords[] = blackWord.split(":");
									ccs.sendMessage(new StringBuilder(pre).append(" ").append(blackwords[0]).append(" is not in the blacklist.").toString());
								}
								return true;
	                            
							}

						}
						if(args[1].equalsIgnoreCase("help"))
						{
							ccs.sendMessage(new StringBuilder(pre).append(" /pb blacklist parameters are:").toString());
							ccs.sendMessage(new StringBuilder().append("help - this command").toString());
							ccs.sendMessage(new StringBuilder().append("add:").toString());
							ccs.sendMessage(new StringBuilder().append("    - to add a banned word to the blacklist use add [word]").toString());
							ccs.sendMessage(new StringBuilder().append("    - to add a replacement use add [wordtoreplace] [wordtoreplacewith]").toString());
							ccs.sendMessage(new StringBuilder().append("#see config for formatting styles#").toString());
							ccs.sendMessage(new StringBuilder().append("delete:").toString());
							ccs.sendMessage(new StringBuilder().append("       - to delete a single word delete [word]").toString());	
							ccs.sendMessage(new StringBuilder().append("       - to delete a word and its replacement delete [replacedword] [replacingword]").toString());
							ccs.sendMessage(new StringBuilder().append("list - lists all the blacklisted words").toString());
						}
						return true;
	        		}

		            if(param.equalsIgnoreCase("reset"))
		            {	   
						if(args.length < 2)
						{
							ccs.sendMessage(new StringBuilder(pre).append(" Not enough arguments /pb reset <player>").toString());
						}
						else
						if(args.length > 2)
						{
							ccs.sendMessage(new StringBuilder(pre).append(" Too many arguments /pb reset <player>").toString());
						}
						else
						if(args.length == 2)
						{
							String uName = args[1];
							if(plugin.getWarnConfig().getString(new StringBuilder("Warnings.Warned.").append(uName.toLowerCase()).toString()) == null)
							{
								ccs.sendMessage(new StringBuilder(pre).append(" ").append(uName.toUpperCase()).append(" does not exist. Check config if you believe this is a mistake.").toString());
							}else
							{
								plugin.resetWarn(uName);
								ccs.sendMessage(new StringBuilder(pre).append(" ").append(uName.toUpperCase()).append(" has had their warnings reset to ").append(plugin.getTotWarn()).toString());
							}
						}
						return true;
	        		}
	            	if(param.equalsIgnoreCase("info"))
	            	{
	            		ccs.sendMessage(new StringBuilder(pre).append("\nVersion: ").append(Bukkit.getServer().getPluginManager().getPlugin("ProfanityBlock").getDescription().getVersion()).append("\nAuthor: Hatixon\n").toString());
	            		if(plugin.getConfig().getBoolean("CheckForUpdates"))
	            		{
	            			if(plugin.isUpdated())
	            			{
	            				ccs.sendMessage(new StringBuilder(pre).append(" There is an updated version of ProfanityBlock. Download at http://dev.bukkit.org/server-mods/profanityblock/").toString());
	            			}
	            			return true;
	            		}
	            	}
		            if(param.equalsIgnoreCase("instaban") || param.equalsIgnoreCase("ib"))
	            	{
	            		if(args.length < 2)
	            		{
	            			ccs.sendMessage(new StringBuilder(pre).append(" /pb instaban help").toString());
	            			return true;
	            		}
	            		
	            		if(!args[1].equalsIgnoreCase("list")
	            				&& !args[1].equalsIgnoreCase("add")
	            				&& !args[1].equalsIgnoreCase("delete")
	            				&& !args[1].equalsIgnoreCase("help")
	            				&& args[1].equalsIgnoreCase("del"))
	            		{
	            			ccs.sendMessage(new StringBuilder(pre).append(" '").append(args[1]).append("' is not a valid parameter /pb instaban help").toString());
	            			return true;
	            		}
	            		
	            		if(args[1].equalsIgnoreCase("list"))
		            	{

		        	            String thisWord;
		        	            String replaceAppend;
		        	            ccs.sendMessage((new StringBuilder(pre).append(" Instaban words:")).toString());
		            			for(Iterator iterator = instaBanList.entrySet().iterator(); iterator.hasNext(); sender.sendMessage((new StringBuilder(String.valueOf(thisWord.replace("\\w*", "")/*.replace("+?", "")*/))).append(replaceAppend).toString()))
		        	            {
		        	                java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
		        	                thisWord = (String)entry.getKey();
		        	                String thisReplace = (String)entry.getValue();
		        	                replaceAppend = thisReplace.length() <= 0 ? "" : (new StringBuilder(":")).append(thisReplace).toString();
		        	            }
		            			return true;

	            		}
	            		

		            	if(args[1].equalsIgnoreCase("add"))
		            	{

			            		if(args.length > 3)
			            		{
			            			ccs.sendMessage(new StringBuilder(pre).append(" Too many arguments!").toString());
			            			return true;
			            		}
		            			if(args.length > 2)
		                        {
		                            String banWord = args[2].toLowerCase().replace("\\w*", "").replace("+?", "");
	                            	if(plugin.addBanWord(banWord))
		                            {
		                                ccs.sendMessage(new StringBuilder(pre).append(" ").append(banWord).append(" was added to the instaban.").toString());
		                            } else
		                            {
		                                ccs.sendMessage(new StringBuilder(pre).append(" ").append(banWord).append(" is already in the instaban list.").toString());
		                            }
		                            return true;
		                        }else
		                        if(args.length < 3)
			            		{
			            			ccs.sendMessage(new StringBuilder(pre).append(" Not enough arguments /pb instaban add <word>").toString());
			            			return true;
			            		}

		            	}
		            	if(args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("del"))
		            	{
			            		if(args.length > 3)
			            		{
			            			ccs.sendMessage(new StringBuilder(pre).append(" Too many arguments!").toString());
			            			return true;
			            		}else
		                        if(args.length > 2)
		                        {
		                            String banWord = args[2].toLowerCase().replace("\\w*", "").replace("+?", "");
		                            if(plugin.delBanWord(banWord))
		                            {
		                                ccs.sendMessage(new StringBuilder(pre).append(" ").append(banWord).append(" was deleted from the instaban list.").toString());
		                            } else
		                            {
		                                ccs.sendMessage(new StringBuilder(pre).append(" ").append(banWord).append(" is not in the instaban list.").toString());
		                            }
		                            return true;
		                            
		                        }else
		                        	
		                        if(args.length < 3)
			            		{
			            			ccs.sendMessage(new StringBuilder(pre).append(" Not enough arguments /pb instaban delete <words>").toString());
			            			return true;
			            		}
		            		
	            		}
		            	if(args[1].equalsIgnoreCase("help"))
		            	{
		            		ccs.sendMessage(new StringBuilder(pre).append(" /pb instaban parameters are:").toString());
		            		ccs.sendMessage(new StringBuilder().append(" help - this command").toString());
		            		ccs.sendMessage(new StringBuilder().append(" list - lists all the instaban listed instaban list").toString());
		            		ccs.sendMessage(new StringBuilder().append(" delete [word] - delete a word from the instaban list").toString());
		            		ccs.sendMessage(new StringBuilder().append(" add [word] - add a word to the instaban list").toString());
	        			}
	            	return true;
	            	}
	            	if(param.equalsIgnoreCase("set"))
	            	{
	            		if(args.length < 3)
	            		{
	            			ccs.sendMessage(new StringBuilder(pre).append(" Not enough arguments /pb set <player> <amount>").toString());
	            		}
	            		else
	            		if(args.length > 3)
	            		{
	            			ccs.sendMessage(new StringBuilder(pre).append(" Too many arguments /pb set <player> <amount>").toString());
	            		}
	            		else
	            		if(args.length == 3)
	            		{
	            			String uName = args[1];
	            			String amount = args[2];
	            			if(amount.length() > 9)
	            			{
	            				ccs.sendMessage(new StringBuilder(pre).append(" Number is too high. 999999999 is the max.").toString());
	            				return true;
	            			}
	            			if(plugin.getWarnConfig().getString(new StringBuilder("Warnings.Warned.").append(uName.toLowerCase()).toString()) == null)
	            			{
	            				ccs.sendMessage(new StringBuilder(pre).append(" ").append(uName.toUpperCase()).append(" does not exist. Check config if you believe this is a mistake.").toString());
	            			}else
	            			{
	            				plugin.editWarn(uName, amount);
		            			ccs.sendMessage(new StringBuilder(pre).append(" ").append(uName.toUpperCase()).append(" has had their warnings set to ").append(amount).toString());
	            			}
	            		}
	            		return true;
	            	}
	            	
		            if(param.equalsIgnoreCase("help"))
		            {
		            	ccs.sendMessage(new StringBuilder(pre).append(" List of pb's available commands:").toString());
		            	ccs.sendMessage(new StringBuilder().append("/pb whitelist - use /pb whitelist help for more").toString());
		            	ccs.sendMessage(new StringBuilder().append("/pb blacklist - use /pb blacklist help for more").toString());
		            	ccs.sendMessage(new StringBuilder().append("/pb instaban - use /pb instaban help for more").toString());
		            	ccs.sendMessage(new StringBuilder().append("/pb warnings [player] - checks a players warnings").toString());
		            	ccs.sendMessage(new StringBuilder().append("/pb reset [player] - resets a players warnings to default").toString());
		            	ccs.sendMessage(new StringBuilder().append("/pb resetall - resets all players to default warnings").toString());
		            	ccs.sendMessage(new StringBuilder().append("/pb set [player] [amount] - sets player to specified amount").toString());
		            	ccs.sendMessage(new StringBuilder().append("/pb info - displays plugin info").toString());
		            	ccs.sendMessage(new StringBuilder().append("/pb help - this command").toString());
		            	return true;
		            }
				}
	        }
		}
		else
		{
			Player p = (Player)sender;
			if(cmd.getName().equalsIgnoreCase("pb"))
	        {
				if(args.length < 1)
				{
					p.sendMessage(new StringBuilder(pre).append(" /pb help").toString());
					return true;
				}
	            if(args.length > 0)
	            {
	                String param = args[0].toLowerCase();
					if(!param.equalsIgnoreCase("whitelist") 
							&& !param.equalsIgnoreCase("blacklist") 
							&& !param.equalsIgnoreCase("reset") 
							&& !param.equalsIgnoreCase("warnings") 
							&& !param.equalsIgnoreCase("help") 
							&& !param.equalsIgnoreCase("instaban") 
							&& !param.equalsIgnoreCase("info")
							&& !param.equalsIgnoreCase("set")
							&& !param.equalsIgnoreCase("wl")
							&& !param.equalsIgnoreCase("bl")
							&& !param.equalsIgnoreCase("ib")
							&& !param.equalsIgnoreCase("w")
							&& !param.equalsIgnoreCase("unmute")
							&& !param.equalsIgnoreCase("mute")
							&& !param.equalsIgnoreCase("download")
							//&& !param.equalsIgnoreCase("reload")
							)
	                {
	                	p.sendMessage(new StringBuilder(pre).append(" No such command. Use /pb help").toString());
	                	return true;
	                }
	            	if(p.hasPermission("pb.download"))
	            	{
						if(param.equalsIgnoreCase("download"))
	            		{
	            			if(plugin.getOnlineList())
	            			{
	            				p.sendMessage(new StringBuilder(pre).append(" A preconfigured word list has been downloaded!").toString());
	            			}else
		            		{
		            			p.sendMessage(new StringBuilder(pre).append(" Failed to download word list!").toString());
		            		}
	            		}
	            	}else
	            	{
	            		p.sendMessage(cantUse);
	            	}
					if(p.hasPermission("pb.mute"))
					{
		            	if(param.equalsIgnoreCase("mute"))
		            	{
							if(args.length == 2)
							{
								String uName = args[1];
								if(Bukkit.getPlayer(args[1].toLowerCase()).isOp())
								{
									p.sendMessage(new StringBuilder(pre).append(" This player can not be muted!").toString());
									return true;
								}
								if(plugin.mutePlayer(uName))
								{
									p.sendMessage(new StringBuilder(pre).append(" ").append(uName).append(" was muted!").toString());
								}else
								{
									p.sendMessage(new StringBuilder(pre).append(" ").append(uName).append(" is already muted!").toString());
								}
							}else
							{
								p.sendMessage(new StringBuilder(pre).append(" Please specify the player you are muting.").toString());
							}
		            	}
		            	if(param.equalsIgnoreCase("unmute"))
		            	{
							if(args.length == 2)
							{
								String uName = args[1];
								if(plugin.unMutePlayer(uName))
								{
									p.sendMessage(new StringBuilder(pre).append(" ").append(uName).append(" was unmuted!").toString());
								}else
								{
									p.sendMessage(new StringBuilder(pre).append(" ").append(uName).append(" is not muted!").toString());
								}
							}else
							{
								p.sendMessage(new StringBuilder(pre).append(" Please specify the player you are unmuting.").toString());
							}
		            	}
					}
					/*if(param.equalsIgnoreCase("reload"))
					{
						if(plugin.reloadChanges())
						{
							Bukkit.getServer().broadcastMessage("worked");
						}
					}*/
					if(p.hasPermission("pb") || p.hasPermission("pn.*"))
	            	{
		            	if(param.equalsIgnoreCase("whitelist") || param.equalsIgnoreCase("wl"))
		            	{
		            		if(args.length < 2)
		            		{
		            			p.sendMessage(new StringBuilder(pre).append(" /pb whitelist help").toString());
		            			return true;
		            		}
		            		
		            		if(!args[1].equalsIgnoreCase("list")
		            				&& !args[1].equalsIgnoreCase("add")
		            				&& !args[1].equalsIgnoreCase("delete")
		            				&& !args[1].equalsIgnoreCase("help")
		            				&& args[1].equalsIgnoreCase("del"))
		            		{
		            			p.sendMessage(new StringBuilder(pre).append(" '").append(args[1]).append("' is not a valid parameter /pb whitelist help").toString());
		            			return true;
		            		}
		            		
		            		if(args[1].equalsIgnoreCase("list"))
			            	{
			            		if(p.hasPermission("pb.list") || p.hasPermission("pb.*"))
			            		{
			        	            String thisWord;
			        	            String replaceAppend;
			        	            p.sendMessage((new StringBuilder(pre).append(" Whitelisted words:")).toString());
			            			for(Iterator iterator = whiteList.entrySet().iterator(); iterator.hasNext(); sender.sendMessage((new StringBuilder(String.valueOf(thisWord).replace("\\w*", "")/*.replace("+?", "")*/)).append(replaceAppend).toString()))
			        	            {
			        	                java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next(); 
			        	                thisWord = (String)entry.getKey();
			        	                String thisReplace = (String)entry.getValue();
			        	                replaceAppend = thisReplace.length() <= 0 ? "" : (new StringBuilder(":")).append(thisReplace).toString();
			        	            }
			            			return true;
			            		}else
			            		{
			            			p.sendMessage(cantUse);
			            			return true;
			            		}
		            		}
		            		

			            	if(args[1].equalsIgnoreCase("add"))
			            	{
			            		if(p.hasPermission("pb.edit") || p.hasPermission("pb.*"))
			            		{
				            		if(args.length > 3)
				            		{
				            			p.sendMessage(new StringBuilder(pre).append(" Too many arguments!").toString());
				            			return true;
				            		}
			            			if(args.length > 2)
			                        {
			                            String whiteWord = args[2].toLowerCase().replace("\\w*", "");
			                            if(plugin.addWhiteWord(whiteWord))
			                            {
			                                p.sendMessage(new StringBuilder(pre).append(" ").append(whiteWord).append(" was added to the whitelist.").toString());
			                            } else
			                            {
			                                p.sendMessage(new StringBuilder(pre).append(" ").append(whiteWord).append(" is already in the whitelist.").toString());
			                            }
			                            return true;
			                        }else
			                        if(args.length < 3)
				            		{
				            			p.sendMessage(new StringBuilder(pre).append(" Not enough arguments /pb whitelist add <word>").toString());
				            			return true;
				            		}
			            		}else
			            		{
			            			p.sendMessage(cantUse);
			            			return true;
			            		}
			            	}
			            	if(args[1].equalsIgnoreCase("delete"))
			            	{
			            		if(p.hasPermission("pb.edit") || p.hasPermission("pb.*"))
			            		{		
				            		if(args.length > 3)
				            		{
				            			p.sendMessage(new StringBuilder(pre).append(" Too many arguments!").toString());
				            			return true;
				            		}else
			                        if(args.length > 2)
			                        {
			                            String whiteWord = args[2].toLowerCase().replace("\\w*", "").replace("+?", "");
			                            if(plugin.delWhiteWord(whiteWord))
			                            {
			                                p.sendMessage(new StringBuilder(pre).append(" ").append(whiteWord).append(" was deleted from the whitelist.").toString());
			                            } else
			                            {
			                                p.sendMessage(new StringBuilder(pre).append(" ").append(whiteWord).append(" is not in the whitelist.").toString());
			                            }
			                            return true;
			                        }else
			                        if(args.length < 3)
				            		{
				            			p.sendMessage(new StringBuilder(pre).append(" Not enough arguments /pb whitelist delete <words>").toString());
				            			return true;
				            		}
			            		}else
			            		{
			            			p.sendMessage(cantUse);
			            			return true;
			            		}
		            		}
			            	if(args[1].equalsIgnoreCase("help"))
			            	{
			            		p.sendMessage(new StringBuilder(pre).append(" /pb whitelist parameters are:").toString());
			            		p.sendMessage(new StringBuilder().append(" help - this command").toString());
			            		p.sendMessage(new StringBuilder().append(" list - lists all the whitelisted words").toString());
			            		p.sendMessage(new StringBuilder().append(" add [word] - add an allowed word to the whitelist").toString());
			            		p.sendMessage(new StringBuilder().append(" delete [word] - delete a word from the whitelist").toString());
		        			}
			            	return true;
		            	}
	            	}else
	        		{
	        			p.sendMessage(cantUse);
	        			return true;
	        		}

	            	if(param.equalsIgnoreCase("blacklist") || param.equalsIgnoreCase("bl"))
		            {
		        		if(p.hasPermission("pb") || p.hasPermission("pb.*"))
		        		{
		            		if(args.length == 1)
		            		{
		            			p.sendMessage(new StringBuilder(pre).append(" /pb blacklist help").toString());
		            			return true;
		            		}
		            		
		            		if(!args[1].equalsIgnoreCase("list") && !args[1].equalsIgnoreCase("add") && !args[1].equalsIgnoreCase("delete") && !args[1].equalsIgnoreCase("help"))
		            		{
		            			p.sendMessage(new StringBuilder(pre).append(" '").append(args[1]).append("' is not a valid parameter /pb blacklist help").toString());			            		
		            			return true;
		            		}		            			

			            	if(args[1].equalsIgnoreCase("list"))
			            	{
			            		if(p.hasPermission("pb.list") || p.hasPermission("pb.*"))
			            		{
			        	            String thisWord;
			        	            String replaceAppend;
			        	            p.sendMessage((new StringBuilder(pre).append(" Blacklisted words:")).toString());
			            			for(Iterator iterator = blackList.entrySet().iterator(); iterator.hasNext(); sender.sendMessage((new StringBuilder(String.valueOf(thisWord).replace("\\w*", "")/*.replace("+?", "")*/)).append(replaceAppend).toString()))
			        	            {
			        	                java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
			        	                thisWord = (String)entry.getKey();
			        	                String thisReplace = (String)entry.getValue();
			        	                replaceAppend = thisReplace.length() <= 0 ? "" : (new StringBuilder(":")).append(thisReplace).toString();
			        	            }
			            			return true;
			            		}else
			            		{
			            			p.sendMessage(cantUse);
			            			return true;
			            		}
		            		}
			            	if(args[1].equalsIgnoreCase("add"))
			                {
			            		if(p.hasPermission("pb.edit") || p.hasPermission("pb.*"))
			            		{
			            			if(args.length > 2)
									{
										String blackWord = "";
										if(args.length > 3)
										{
											if(args.length > 4)
											{
												p.sendMessage(new StringBuilder(pre).append("Too many arguments - /pb blacklist help").toString());
											}else
											{
												StringBuilder sb = new StringBuilder();
												for(int i = 2; i < args.length; i++)
												{
													sb.append(args[i]);
													if(i < args.length - 1)
													{
														sb.append(":");
													}
												}
												blackWord = sb.toString().toLowerCase().replace("\\w*", "").replace("+?", "");
											}
										} else
										{
											blackWord = args[2].toLowerCase().replace("\\w*", "").replace("+?", "");
										}
										if(plugin.addBlackWord(blackWord))
										{
											p.sendMessage(new StringBuilder(pre).append(" ").append(blackWord).append(" was added to the blacklist.").toString());
										} else
										{
											String blackWords[] = blackWord.split(":");
											p.sendMessage(new StringBuilder(pre).append(" ").append(blackWords[0]).append(" is already in the blacklist.").toString());
										}
										return true;
									}
			                    }
			            		else
			            		{
			            			p.sendMessage(cantUse);
			            			return true;
			            		}
			                }
			            	if(args[1].equalsIgnoreCase("delete"))
			            	{
			            		if(p.hasPermission("pb.edit") || p.hasPermission("pb.*"))
				            	{
			            			if(args.length > 2)
									{
										
										String blackWord = "";

										if(args.length > 3)
										{
											if(args.length > 4)
											{
												p.sendMessage(new StringBuilder(pre).append("Too many arguments - /pb blacklist help").toString());
												return true;
											}else
											{
												StringBuilder sb = new StringBuilder();
												for(int i = 2; i < args.length; i++)
												{
													sb.append(args[i]);
													if(i < args.length - 1)
													{
														sb.append(":");
													}
												}
												blackWord = sb.toString().toLowerCase().replace("\\w*", "").replace("+?", "");
											}
										}else
										{
											blackWord = args[2].toLowerCase().replace("\\w*", "").replace("+?", "");
										}

										if(plugin.delBlackWord(blackWord))
										{
											String blackwords[] = blackWord.split(":");
											p.sendMessage(new StringBuilder(pre).append(" ").append(blackwords[0]).append(" was deleted from the blacklist.").toString());
										} else
										{
											String blackwords[] = blackWord.split(":");
											p.sendMessage(new StringBuilder(pre).append(" ").append(blackwords[0]).append(" is not in the blacklist.").toString());
										}
										return true;
		                            
									}

		            			}else
			            		{
			            			p.sendMessage(cantUse);
			            			return true;
			            		}
			            	}
			            	if(args[1].equalsIgnoreCase("help"))
			            	{
			            		p.sendMessage(new StringBuilder(pre).append(" /pb blacklist parameters are:").toString());
			            		p.sendMessage(new StringBuilder().append("help - this command").toString());
			            		p.sendMessage(new StringBuilder().append("add:").toString());
			            		p.sendMessage(new StringBuilder().append("    - to add a banned word to the blacklist use add [word]").toString());
			            		p.sendMessage(new StringBuilder().append("    - to add a replacement use add [wordtoreplace] [wordtoreplacewith]").toString());
			            		p.sendMessage(new StringBuilder().append("#see config for formatting styles#").toString());
			            		p.sendMessage(new StringBuilder().append("delete:").toString());
			            		p.sendMessage(new StringBuilder().append("       - to delete a single word delete [word]").toString());	
			            		p.sendMessage(new StringBuilder().append("       - to delete a word and its replacement delete [replacedword] [replacingword]").toString());
			            		p.sendMessage(new StringBuilder().append("list - lists all the blacklisted words").toString());
		        			}
		        		}else
		        		{
		        			p.sendMessage(cantUse);
		        			return true;
		        		}
		        		return true;
	        		}

		            if(param.equalsIgnoreCase("reset"))
		            {	    
		        		if(p.hasPermission("pb.reset") || p.hasPermission("pb.*"))
		        		{
		            		if(args.length < 2)
		            		{
		            			p.sendMessage(new StringBuilder(pre).append(" Not enough arguments /pb reset <player>").toString());
		            		}
		            		else
		            		if(args.length > 2)
		            		{
		            			p.sendMessage(new StringBuilder(pre).append(" Too many arguments /pb reset <player>").toString());
		            		}
		            		else
		            		if(args.length == 2)
		            		{
		            			String uName = args[1];
		            			if(plugin.getWarnConfig().getString(new StringBuilder("Warnings.Warned.").append(uName.toLowerCase()).toString()) == null)
		            			{
		            				p.sendMessage(new StringBuilder(pre).append(" ").append(uName.toUpperCase()).append(" does not exist. Check config if you believe this is a mistake.").toString());
		            			}else
		            			{
		            				plugin.resetWarn(uName);
			            			p.sendMessage(new StringBuilder(pre).append(" ").append(uName.toUpperCase()).append(" has had their warnings reset to ").append(plugin.getTotWarn()).toString());
		            			}
		            		}
		            		return true;
		            	}else
		        		{
		        			p.sendMessage(cantUse);
		        			return true;
		        		}
	        		}
		            
	            	if(param.equalsIgnoreCase("warnings") || param.equalsIgnoreCase("w"))
	            	{	
		        		if(p.hasPermission("pb.warnings") || p.hasPermission("pb.*"))
		        		{
		            		String uName = "";
		            		int remWarn;
		            		if(args.length == 1)
		            		{
		            			uName = p.getName().toLowerCase();
		            			remWarn = plugin.getRemWarn(uName);
		            			p.sendMessage(new StringBuilder(pre).append(" You have ").append(remWarn).append(" warning(s) left.").toString());
		            			return true;
		            		}
		            		if(args.length > 1)
		            		{
		            			uName = args[1].toLowerCase();
		            			String uNameMatch = p.getName().toLowerCase();
		            			if(uName.equalsIgnoreCase(uNameMatch))
		            			{
			            			remWarn = plugin.getRemWarn(uName);
			            			p.sendMessage(new StringBuilder(pre).append(" You have ").append(remWarn).append(" warning(s) left.").toString());
		            			}else
		            			{
		            				if(p.hasPermission("pb.warnings.others") || p.hasPermission("pb.*"))
		            				{
				            			remWarn = plugin.getRemWarn(uName.toLowerCase());
				            			if(remWarn != 0)
				            			{
				            				p.sendMessage(new StringBuilder(pre).append(" ").append(uName.toUpperCase()).append(" has ").append(remWarn).append(" warning(s) left.").toString());
				            			}
				            			else
				            			{
				            				p.sendMessage(new StringBuilder(pre).append(" ").append(uName.toUpperCase()).append(" does not exist. Make sure you spelt the name right.").toString());
				            			}
		            				}else
		            				{
		            					p.sendMessage(new StringBuilder(pre).append(" You do not have the permission to view another users warnings").toString());
		            				}
		            			}
		            			return true;
		            		}
		            	}else
		        		{
		        			p.sendMessage(cantUse);
		        			return true;
		        		}
		        		return true;
	        		}
	            	
	            	if(param.equalsIgnoreCase("info"))
	            	{
	            		p.sendMessage(new StringBuilder(pre).append("\nVersion: ").append(Bukkit.getServer().getPluginManager().getPlugin("ProfanityBlock").getDescription().getVersion()).append("\nAuthor: Hatixon\n").toString());
	            		if(plugin.getConfig().getBoolean("CheckForUpdates"))
	            		{
	            			if(plugin.isUpdated())
	            			{
	            				if(p.hasPermission("pb.version") || p.hasPermission("pb.*"))
	            				{
	            					p.sendMessage(new StringBuilder(pre).append(" There is an updated version of ProfanityBlock. Download at http://dev.bukkit.org/server-mods/profanityblock").toString());
	            				}
	            				
	            			}
	            			return true;
	            		}
	            	}
	            	
		            if(param.equalsIgnoreCase("instaban") || param.equalsIgnoreCase("ib"))
	            	{
	            		if(args.length < 2)
	            		{
	            			p.sendMessage(new StringBuilder(pre).append(" /pb instaban help").toString());
	            			return true;
	            		}
	            		if(!args[1].equalsIgnoreCase("list") && !args[1].equalsIgnoreCase("add") && !args[1].equalsIgnoreCase("delete")&& !args[1].equalsIgnoreCase("help"))
	            		{
	            			p.sendMessage(new StringBuilder(pre).append(" '").append(args[1]).append("' is not a valid parameter /pb instaban help").toString());
	            			return true;
	            		}
	            		if(args[1].equalsIgnoreCase("list"))
		            	{
	            			if(p.hasPermission("pb.list") || p.hasPermission("pb.*"))
	            			{
		        	            String thisWord;
		        	            String replaceAppend;
		        	            p.sendMessage((new StringBuilder(pre).append(" Instaban words:")).toString());
		            			for(Iterator iterator = instaBanList.entrySet().iterator(); iterator.hasNext(); sender.sendMessage((new StringBuilder(String.valueOf(thisWord).replace("\\w*", "")/*.replace("+?", "")*/)).append(replaceAppend).toString()))
		        	            {
		        	                java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
		        	                thisWord = (String)entry.getKey();
		        	                String thisReplace = (String)entry.getValue();
		        	                replaceAppend = thisReplace.length() <= 0 ? "" : (new StringBuilder(":")).append(thisReplace).toString();
		        	            }
		            			return true;
	            			}
	            		}
		            	if(args[1].equalsIgnoreCase("add"))
		            	{
		            		if(p.hasPermission("pb.edit") || p.hasPermission("pb.*"))
		            		{
			            		if(args.length > 3)
			            		{
			            			p.sendMessage(new StringBuilder(pre).append(" Too many arguments!").toString());
			            			return true;
			            		}
		            			if(args.length > 2)
		                        {
		                            String banWord = args[2].toLowerCase().replace("\\w*", "").replace("+?", "");
		                            if(plugin.addBanWord(banWord))
		                            {
		                                p.sendMessage(new StringBuilder(pre).append(" ").append(banWord).append(" was added to the instaban.").toString());
		                            }else
		                            {
		                                p.sendMessage(new StringBuilder(pre).append(" ").append(banWord).append(" was already in the instaban list.").toString());
		                            }
		                            return true;
		                        }else
		                        if(args.length < 3)
			            		{
			            			p.sendMessage(new StringBuilder(pre).append(" Not enough arguments /pb instaban add <word>").toString());
			            			return true;
			            		}
		            		}else
		            		{
		            			p.sendMessage(cantUse);
		            		}
		            	}
		            	if(args[1].equalsIgnoreCase("delete"))
		            	{
		            		if(p.hasPermission("pb.edit") || p.hasPermission("pb.*"))
		            		{
			            		if(args.length > 3)
			            		{
			            			p.sendMessage(new StringBuilder(pre).append(" Too many arguments!").toString());
			            			return true;
			            		}else
		                        if(args.length > 2)
		                        {
		                            String banWord = args[2].toLowerCase().replace("\\w*", "").replace("+?", "");
		                            if(plugin.delBanWord(banWord))
		                            {
		                                p.sendMessage(new StringBuilder(pre).append(" ").append(banWord).append(" was deleted from the instaban list.").toString());
		                            } else
		                            {
		                                p.sendMessage(new StringBuilder(pre).append(" ").append(banWord).append(" is not in the instaban list.").toString());
		                            }
		                            return true;
		                        }else
		                        	
		                        if(args.length < 3)
			            		{
			            			p.sendMessage(new StringBuilder(pre).append(" Not enough arguments /pb instaban delete <words>").toString());
			            			return true;
			            		}
		            		}else
		            		{
		            			p.sendMessage(cantUse);
		            		}
		            		
	            		}
		            	if(args[1].equalsIgnoreCase("help"))
		            	{
		            		p.sendMessage(new StringBuilder(pre).append(" /pb instaban parameters are:").toString());
		            		p.sendMessage(new StringBuilder().append(" help - this command").toString());
		            		p.sendMessage(new StringBuilder().append(" list - lists all the instaban listed instaban list").toString());
		            		p.sendMessage(new StringBuilder().append(" delete [word] - delete a word from the instaban list").toString());
		            		p.sendMessage(new StringBuilder().append(" add [word] - add a word to the instaban list").toString());
	        			}
		            	return true;
	            	}
	            	if(param.equalsIgnoreCase("set"))
	            	{
		        		if(p.hasPermission("pb.set") || p.hasPermission("pb.*"))
		        		{
		            		if(args.length < 3)
		            		{
		            			p.sendMessage(new StringBuilder(pre).append(" Not enough arguments /pb set <player> <amount>").toString());
		            		}
		            		else
		            		if(args.length > 3)
		            		{
		            			p.sendMessage(new StringBuilder(pre).append(" Too many arguments /pb set <player> <amount>").toString());
		            		}
		            		else
		            		if(args.length == 3)
		            		{
		            			String uName = args[1];
		            			String amount = args[2];
		            			if(amount.length() > 9)
		            			{
		            				p.sendMessage(new StringBuilder(pre).append(" Number is too high. 999999999 is the max.").toString());
		            				return true;
		            			}
		            			if(plugin.getWarnConfig().getString(new StringBuilder("Warnings.Warned.").append(uName.toLowerCase()).toString()) == null)
		            			{
		            				p.sendMessage(new StringBuilder(pre).append(" ").append(uName.toUpperCase()).append(" does not exist. Check config if you believe this is a mistake.").toString());
		            			}else
		            			{
		            				plugin.editWarn(uName, amount);
			            			p.sendMessage(new StringBuilder(pre).append(" ").append(uName.toUpperCase()).append(" has had their warnings set to ").append(amount).toString());
		            			}
		            		}
		            		return true;
		            	}else
		        		{
		        			p.sendMessage(cantUse);
		        			return true;
		        		}
	            	}
		            if(param.equalsIgnoreCase("help"))
		            {
		            	p.sendMessage(new StringBuilder(pre).append(" List of pb's available commands:").toString());
		            	p.sendMessage(new StringBuilder().append(RED).append("/pb whitelist - use /pb whitelist help for more").toString());
		            	p.sendMessage(new StringBuilder().append(RED).append("/pb blacklist - use /pb blacklist help for more").toString());
		            	p.sendMessage(new StringBuilder().append(RED).append("/pb warnings - checks your own warnings").toString());
		            	p.sendMessage(new StringBuilder().append(RED).append("/pb warnings [player] - checks a players warnings including your own").toString());
		            	p.sendMessage(new StringBuilder().append(RED).append("/pb reset [player] - resets a players warnings to default").toString());
		            	p.sendMessage(new StringBuilder().append(RED).append("/pb set [player] [amount] - sets player to specified amount").toString());
		            	p.sendMessage(new StringBuilder().append(RED).append("/pb help - this command").toString());
		            	return true;
		            }
	            }
	        }
		}
		return true;
	}	
}
