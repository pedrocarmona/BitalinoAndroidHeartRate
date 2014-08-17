package com.iHealth;

public class ECG {

    int[] ecg, ecgD, ecgPeak;
    double[] ecgd1;
    int samplesize = 500, stIndx = 0, buffersize;
    int lastPeak;
    DataQueue buffer;
    final int E = 0, D = 1;
    MathTools mathtool = new MathTools();
    int nGap = 3 * samplesize / 10, sigLen, nPart, nA, nB, nPos = 0;
    double fMeanMax, fMeanMin, thresholdMax, threasholdMin, fPrevA;
    int FILTER_LEN = 10, m_nValidBufSize = 0;
    Boolean m_bRPeakExist;
    Boolean Full = false;
    int[] zeromem = new int[samplesize];
    private static final String TAG = "BITalinoServiceECG";

    public ECG() {
        buffersize = 4 * samplesize;
        ecg = new int[4 * samplesize];
        ecgd1 = new double[4 * samplesize];
        ecgPeak = new int[4 * samplesize];
        buffer = new DataQueue(samplesize);
        lastPeak = buffersize;

    }

    void Reset() {
        ecg = new int[buffersize];

        ecgd1 = new double[buffersize];
        ecgPeak = new int[buffersize];
        lastPeak = buffersize;
        m_bRPeakExist = false;
        m_nValidBufSize = 0;
    }

    public void pushbulk(int[] buff) {


        System.arraycopy(ecg, samplesize, ecg, 0, samplesize * 3);
        System.arraycopy(ecgPeak, samplesize, ecgPeak, 0, samplesize * 3);
        System.arraycopy(ecgd1, samplesize, ecgd1, 0, samplesize * 3);
        System.arraycopy(zeromem, 0, ecgPeak, samplesize * 3, samplesize);
        System.arraycopy(buff, 0, ecg, samplesize * 3, samplesize);
        FirstD();
//        FirstDSlope();
        if (m_nValidBufSize < 4) {
            m_nValidBufSize++;
        }

        lastPeak -= samplesize;

        if (lastPeak < 0) {
            lastPeak = 0;
        }
    }

    public void FirstD() {

        for (int i = samplesize * 3; i < buffersize - 3; i++) {
            ecgd1[i] = (-ecg[i + 2] + 8 * ecg[i + 1] - 8 * ecg[i - 1] + ecg[i - 2]) / 12.0;

        }
    }

    public void FirstDSlope() {
          for (int i = samplesize * 3; i < buffersize - 11; i++) {
            ecgd1[i] = mathtool.GetSlope(ecg, i, 20);

        }
    }

    public void DetectPeak() {
        try {
            stIndx = lastPeak >= 0 ? lastPeak + nGap : 0;

            sigLen = buffersize - stIndx;
            nPart = sigLen / samplesize;
            fMeanMax = mathtool.GetMeanMax(ecgd1, sigLen, nPart, stIndx);

            fMeanMin = mathtool.GetMeanMin(ecgd1, sigLen, nPart, stIndx);

            thresholdMax = fMeanMax * 0.65;
            threasholdMin = fMeanMin * 0.6;
            nA = nB = -1;

            if (mathtool.GetGreatThanPercent(ecgd1, (buffersize - stIndx + 1), thresholdMax, stIndx) < 5.0) {

                for (int i = stIndx + 1; i < buffersize - 1; i++) {
                    if (mathtool.IsFallingEdgeCross(ecgd1, i, thresholdMax)) // Locate point A
                    {
                        nA = i - FILTER_LEN;		// minus FILTER_LEN, because of the delay of the MA filter
                    }
                    if (nA > 0 && mathtool.IsFallingEdgeCross(ecgd1, i, threasholdMin)) // Locate point B
                    {
                        nB = i - FILTER_LEN;		// minus FILTER_LEN, because of the delay of the MA filter

                    }

                    if (nA > 0 && nB > 0 && nA < nB && nB < buffersize) // if both A & B found
                    {
                        // ensure that the peak is high enough to be an R-peak
                        if (fPrevA * 0.5 < ecgd1[nA + FILTER_LEN]) {
                            nPos = mathtool.GetMax(ecg, (nB - nA + 1), nA);
                            // Get back to Raw ECG to locate the
                            // maximum point ranging A to B as R peak
                            if ((nPos) < buffersize) {
                                ecgPeak[nPos] = 2;					// mark down the R peak
                                lastPeak = nPos;
                                fPrevA = ecgd1[nA + FILTER_LEN];
                                m_bRPeakExist = true;
                                nA = nB = -1;							// reset A & B to
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }

    }
    double hrsMax = 150.0 / 60.0;
    double hrsMin = 40.0 / 60.0;

    public Boolean PeaksOK() {
        int nPeakCount = 0;

        for (int i = 0; i < buffersize; i++) {
            if (ecgPeak[i] > 0) {
                nPeakCount++;
            }

        }
        if (0 == nPeakCount) {
            return false;
        }
        return (nPeakCount > hrsMin * m_nValidBufSize) && (nPeakCount < hrsMax * m_nValidBufSize);
    }

    public void ResetNewPeaks() {
        if (null == ecgPeak) {
            return;
        }

        for (int i = 0; i < buffersize; i++) {
            if (ecgPeak[i] == 2) {
                ecgPeak[i] = 1;
            }
        }
    }

    int GetPeakAt(int i) {
        if (i >= 0 && i < buffersize) {
            return ecgPeak[i];
        } else {
            return -1;
        }
    }
}
