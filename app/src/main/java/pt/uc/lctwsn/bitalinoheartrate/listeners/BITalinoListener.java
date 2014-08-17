package pt.uc.lctwsn.bitalinoheartrate.listeners;

/**
 * Created by pedrocarmona on 31/07/14.
 */
public interface BITalinoListener {


    //public void didFinishListening();

    //public void willStartListening();

    /**
     * Called when the thread receives the information from the shirt
     *
     * @param hr contains HeartRate of the user
     */
    public void didGotHeartRate(final int hr);


    /**
     * Called when the thread receives the information  to log
     *
     * @param log contains LOG
     */
    public void didGotLogInfo(final String log);


    //public void didGotAcceleration(float x, float y, float z);

    //public void didSmartShirtFailWithException(Exception e);

    //public void didNotConnect();


    }