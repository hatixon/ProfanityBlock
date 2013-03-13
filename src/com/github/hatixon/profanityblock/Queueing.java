package com.github.hatixon.profanityblock;

import java.util.*;

public class Queueing
{
	private static ArrayList<String> queue;
	private static int allowedLength;
	
	public Queueing()
	{
		queue = new ArrayList<String>();
		allowedLength = 10;
	}
	
	public void addMessage(String msg)
	{
		queue.add(msg);
		if(queue.size() > allowedLength)
		{
			queue.remove(0);
		}
	}
	
	public String returnQueue()
	{
		StringBuilder sb = new StringBuilder();
		for(Iterator<String> i = queue.iterator(); i.hasNext();)
		{
			sb.append(i.next());
		}
		return sb.toString();
	}
	
	public void clearAll()
	{
		queue.clear();
	}
}
