package com.iHealth;

public class Filter {
	
	DataQueue filter = new DataQueue(50);
	
	public int Filt(int n)
	{
		filter.push(n);
		return filter.IsFull()? filter.GetMean():n;
	} 

}
