package com.github.hatixon.profanityblock;

import java.util.*;
import java.util.Map.*;
import org.bukkit.entity.*;
@SuppressWarnings({"rawtypes", "unchecked"})
public class SpamCheck
{
	static Map<Player, String> lastMessage = new HashMap();
	static Map<Player, Long> lastMessageTimeStamp = new HashMap();
	static Map<Player, Integer> spamCount = new HashMap();
	
	public static Map getLastMessage() 
	{
		return lastMessage;
	}
	
	public static Map getLastMTime()
	{
		return lastMessageTimeStamp;
	}
	
	public static String spamCheck(Player player, String message, Long time)
	{
		Boolean repeated = false;
		String lstMessage = null;
		Long lstTime = null;
		if(player.hasPermission("pb.bypass.spam"))
		{
			return "";
		}
		for(Iterator i = lastMessage.entrySet().iterator(); i.hasNext();)
		{	
			Entry entry = (Entry)i.next();
			Player player2 = (Player)entry.getKey();
			if(player2 == player)
			{
				lstMessage = (String)entry.getValue();
			}
		}
		if(lstMessage == null)
		{
			return "";
		}
		
		for(Iterator i = lastMessageTimeStamp.entrySet().iterator(); i.hasNext();)
		{
			Entry entry = (Entry)i.next();
			Player player2 = (Player)entry.getKey();
			if(player2 == player)
			{
				lstTime = (Long)entry.getValue();
			}
		}
		if(message.equalsIgnoreCase(lstMessage))
		{
			repeated = true;
		}
		
		if(lstTime == null)
		{
			return "";
		}
		if(!spamCount.containsKey(player))
		{
			spamCount.put(player, 0);
		}

		if((time - lstTime) < 1000L || (repeated == true && (time - lstTime) < 3000L))
		{
			for(Iterator i = spamCount.entrySet().iterator(); i.hasNext();)
			{	
				Entry entry = (Entry)i.next();
				Player player2 = (Player)entry.getKey();
				Integer int2 = (Integer) entry.getValue();
				if(player2 == player)
				{
					if(int2 >= 6)
					{
						lastMessage.remove(player);
						lastMessageTimeStamp.remove(player);
						spamCount.remove(player);
						return "banned";
					}else
					{
						spamCount.remove(player);
						spamCount.put(player, (int2 + 1));
						return "warned";	
					}
				}
			}
		}
		return "";
	}
}
