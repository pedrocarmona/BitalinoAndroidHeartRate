package com.iHealth;


public class MathTools {
	double k,  b;
	Boolean CalcLineKB(int[] arr, int len, int stInx )
	{
		k = b = 0;

		if (arr == null)
			return false;
		if (len < 2)
			return false;

		double mX, mY, mXX, mXY, n;

		mX	= mY = mXX = mXY = 0;
		n	= len;
		
		for (int i = 0; i < len; i++)
		{
			mX  += i; 
			mY  += arr[(stInx+i)];
			mXX += i * i;
			mXY += i * arr[(stInx+i)];
		}

		if (mX * mX - mXX * n == 0)
			return false;

		k = (mY * mX - mXY * n) / (mX * mX - mXX * n);
		b = (mY - mX * k) / n;
	 
		return true;
	}
	
	Boolean CalcLineKB(double[] arr, int len, int stInx )
	{
		k = b = 0;

		if (arr == null)
			return false;
		if (len < 2)
			return false;

		double mX, mY, mXX, mXY, n;

		mX	= mY = mXX = mXY = 0;
		n	= len;
		
		for (int i = 0; i < len; i++)
		{
			mX  += i; 
			mY  += arr[(stInx+i)];
			mXX += i * i;
			mXY += i * arr[(stInx+i)];
		}

		if (mX * mX - mXX * n == 0)
			return false;

		k = (mY * mX - mXY * n) / (mX * mX - mXX * n);
		b = (mY - mX * k) / n;
	 
		return true;
	}
	double GetSlope(int[] arr, int nIdx, int nLen)
	{
		k=b=0;
		if (CalcLineKB(arr, nLen,(nIdx - (nLen - 1) / 2)))
			return k;
		return 0;
	}
        double GetSlope(double[] arr, int nIdx, int nLen)
	{
		k=b=0;
		if (CalcLineKB(arr, nLen,(nIdx - (nLen - 1) / 2)))
			return k;
		return 0;
	}

	
	//----------------------------------------------------------------------
	double MeanMax(int[] arr)
	{
		int max,pos=0;
		max = arr[0];
		for(int i=1; i<arr.length;i++)
		{
			if(arr[i]>max)
			{
				max = arr[i];
				pos = i;
			}
		}
		return pos;
	}
	
	double	GetMeanMax(int[] buff, int nLength, int nParts, int stIndx)
	{
		if (buff == null || nLength <= 0)
			return 0;

		if (nParts <= 0)
			nParts = 1;

		int[]	max  = new int[nParts];
		int		count = nLength / nParts;
		double		sum = 0;
		
		for (int i = 0; i < nParts; i++)
		{
			max[i] = -0x7fffffff;
			for (int j = i * nParts, n = 0; n < count; j++, n++)
			{
				if (buff[stIndx+j] > max[i])
					max[i] = buff[stIndx+j];
			}
		}

		for (int i = 0; i < nParts; i++)
			sum += max[i];
		
		return sum / nParts;
	}
        
        double	GetMeanMax(double[] buff, int nLength, int nParts, int stIndx)
	{
		if (buff == null || nLength <= 0)
			return 0;

		if (nParts <= 0)
			nParts = 1;

		double[]	max  = new double[nParts];
		int		count = nLength / nParts;
		double		sum = 0;
		
		for (int i = 0; i < nParts; i++)
		{
			max[i] = -0x7fffffff;
			for (int j = i * nParts, n = 0; n < count; j++, n++)
			{
				if (buff[stIndx+j] > max[i])
					max[i] = buff[stIndx+j];
			}
		}

		for (int i = 0; i < nParts; i++)
			sum += max[i];
		
		return sum / nParts;
	}
	
	int GetMin(int[] buff, int nLength, int pos)
	{
		if (buff == null)
		{
			pos = -1;
			return 0;
		}

		int n = 0x7fffffff;

		for (int i = 0; i < nLength; i++)
		{
			if (buff[i] < n)
			{
				n = buff[i];
				pos = i;
			}
		}

		return n;
	}
	
	
	
	double	GetMeanMin(int[] buff, int nLength, int nParts, int stIndx )
	{
		if (buff == null || nLength <= 0)
			return 0;

		if (nParts <= 0)
			nParts = 1;

		int[] min  = new int[nParts];
		int    count = nLength / nParts;
		double sum   = 0;
		
		for (int i = 0; i < nParts; i++)
		{
			min[i] = 0x7fffffff;
			for (int j = i * nParts, n = 0; n < count; j++, n++)
			{
				if (buff[stIndx+j] < min[i])
					min[i] = buff[stIndx+j];
			}
		}

		for (int i = 0; i < nParts; i++)
			sum += min[i];


		return (int)sum / nParts;
	}
        double	GetMeanMin(double[] buff, int nLength, int nParts, int stIndx )
	{
		if (buff == null || nLength <= 0)
			return 0;

		if (nParts <= 0)
			nParts = 1;

		double [] min  = new double[nParts];
		int    count = nLength / nParts;
		double sum   = 0;
		
		for (int i = 0; i < nParts; i++)
		{
			min[i] = 0x7fffffff;
			for (int j = i * nParts, n = 0; n < count; j++, n++)
			{
				if (buff[stIndx+j] < min[i])
					min[i] = buff[stIndx+j];
			}
		}

		for (int i = 0; i < nParts; i++)
			sum += min[i];


		return sum / nParts;
	}
	
	public double GetGreatThanPercent(int[] buff, int nLength, double fRefValue, int stIndx)
	{
		int count = 0;
		for (int i = 0; i < nLength; i++)
		{
			if ((double)buff[stIndx+i-1] > fRefValue)
				count++;
		}
double k =  (100.0 * (double)count / nLength);
		return k;
	}
        
        public double GetGreatThanPercent(double[] buff, int nLength, double fRefValue, int stIndx)
	{
		int count = 0;
		for (int i = 0; i < nLength; i++)
		{
			if ((double)buff[stIndx+i-1] > fRefValue)
				count++;
		}
double k =  (100.0 * (double)count / nLength);
		return k;
	}
	
	
	int	GetMax(int[] buff, int nLength, int stInx)
	{
		int pos=-1;
		if (buff == null)
		{
			pos = -1;
			return 0;
		}

		int	n = -0x7fffffff;

		for (int i = 0; i < nLength; i++)
		{
			if (buff[stInx+i] > n)
			{
				n = buff[stInx+i];
				pos = stInx+i;
			}
		}

		return pos;
	}
int	GetMax(double[] buff, int nLength, int stInx)
	{
		int pos=-1;
		if (buff == null)
		{
			pos = -1;
			return 0;
		}

		double	n = -0x7fffffff;

		for (int i = 0; i < nLength; i++)
		{
			if (buff[stInx+i] > n)
			{
				n = buff[stInx+i];
				pos = stInx+i;
			}
		}

		return pos;
	}
	//--------------------------------------------------------------------------------------
	// Function name : 
	// Description   : 
	//--------------------------------------------------------------------------------------
	double GetMaxV(int[] buff, int nLength, int stInx)
	{
		int pos = -1;
		if (buff == null)
		{
			pos = -1;
			return 0;
		}

		double	n = -0x7fffffff;

		for (int i = 0; i < nLength; i++)
		{
			if (buff[stInx+i] > n)
			{
				n = buff[stInx+i];
				pos = stInx+i;
			}
		}

		return n;
	}
        double GetMaxV(double[] buff, int nLength, int stInx)
	{
		int pos = -1;
		if (buff == null)
		{
			pos = -1;
			return 0;
		}

		double	n = -0x7fffffff;

		for (int i = 0; i < nLength; i++)
		{
			if (buff[stInx+i] > n)
			{
				n = buff[stInx+i];
				pos = stInx+i;
			}
		}

		return n;
	}
	
	double GetMean(int[] buff, int nLength, int stInx)
	{
		if (buff == null)
			return 0;

		double	nSum = 0;

		for (int i = 0; i < nLength; i++)
			nSum += buff[stInx+i];

		if (nLength == 0)
			return 0;
		else
			return nSum / (double)nLength;
	}
	
	double GetMean(double[] buff, int nLength, int stInx)
	{
		if (buff == null)
			return 0;

		double	nSum = 0;

		for (int i = 0; i < nLength; i++)
			nSum += buff[stInx+i];

		if (nLength == 0)
			return 0;
		else
			return nSum / (double)nLength;
	}

	
	Boolean IsRisingEdgeCross(double[] arr, int nIdx, double fRef)
	{
		return (arr[nIdx - 1] < fRef && arr[nIdx + 1] >= fRef);
	}

	Boolean IsRisingEdgeCross(int[] arr, int nIdx, double nRef)
	{
		return ((double)arr[nIdx - 1] < nRef && (double)arr[nIdx + 1] >= nRef);
	}

	Boolean IsFallingEdgeCross(double[] arr, int nIdx, double fRef)
	{
		return (arr[nIdx - 1] > fRef && arr[nIdx + 1] <= fRef);
	}

	Boolean IsFallingEdgeCross(int[] arr, int nIdx, double nRef)
	{
		return ((double)arr[nIdx - 1] > nRef && (double)arr[nIdx + 1] <= nRef);
	}
}

