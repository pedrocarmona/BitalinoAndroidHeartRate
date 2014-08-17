package com.iHealth;

import java.io.*;
import java.util.Vector;

public class DataQueue {
	public int[] queue;
	int len;
	int pointer;
	public Vector<Integer> q;
	public DataQueue(int len)
	{
		queue = new int[len];
		this.len = len-1;
		pointer = len;
		
	}
	
	public synchronized void fillQueue(ByteArrayInputStream bs)
	{	
		 
	}
	
	public synchronized int getHead()
	{
		return queue[0];
	}
	
	public synchronized void Clear()
	{
		
		queue = new int[len+1];
		pointer = len+1;
	}
	public synchronized void add(int b)
	{
		q.add(b);
		
	}
	public synchronized void push(int b)
	{
		for(int i =1; i <= len ; i++)
		{
			try{
			queue[i-1]=queue[i];
			
			}
			catch(Exception e){}
		}
		queue[len]=b;
		if(pointer>0){
		pointer -=1;}
		
	}
	public synchronized  Boolean IsFull()
	{
		if(pointer==0){return true;}
		else{return false;}
	}
	
	public int GetMean()
	{int sum=0;
		for(int i=0;i<queue.length;i++)
		{
			sum+= queue[i];
		}
		return sum/queue.length;
	}
	
}
