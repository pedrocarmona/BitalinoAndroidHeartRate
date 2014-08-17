package com.iHealth;
import java.io.ByteArrayInputStream;
import java.util.Vector;

public class DoubleQ {
	private int count;
	public double[] queue;
	public int len;
	int pointer;
	public Vector q;
	public DoubleQ(int len)
	{
		queue = new double[len];
		this.len = len-1;
		pointer = len;
		
	}
	
	public synchronized void fillQueue(ByteArrayInputStream bs)
	{	
		 
	}
	
	public synchronized double getHead()
	{
		return queue[0];
	}
	
	public synchronized void Clear()
	{
		
		queue = new double[len+1];
		pointer = len+1;
	}
	public synchronized void add(int b)
	{
		q.add(b);
		
	}
	public synchronized void push(double m_nRT)
	{
		for(int i =1; i <= len ; i++)
		{
			try{
			queue[i-1]=queue[i];
			
			}
			catch(Exception e){}
		}
		queue[len]=m_nRT;
		if(pointer>0){
		pointer -=1;}
		
	}
	public synchronized  Boolean IsFull()
	{
		if(pointer==0){return true;}
		else{return false;}
	}
	
	public double GetMean()
	{
		double sum = 0;
		for(double d : queue)
		{
			sum+=d;
		}
		return sum/queue.length;
	}
}
