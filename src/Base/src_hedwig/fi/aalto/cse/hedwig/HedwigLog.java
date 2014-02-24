package fi.aalto.cse.hedwig;

import android.util.Log;

/**
 * Advance logging
 * @author Long
 *
 */
public class HedwigLog {
    
    public static void logFunction(Object callingClass, String methodName) {
	Log.d(Constant.HEDWIGFUNCTIONTAG, callingClass.getClass().getSimpleName()
		+ "." + methodName);
    }
    
    public static void log(String message){
	Log.d(Constant.HEDWIGTAG, message);
    }
    
}
