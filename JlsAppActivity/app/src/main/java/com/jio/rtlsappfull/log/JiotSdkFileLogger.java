package com.jio.rtlsappfull.log;


import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import static com.jio.rtlsappfull.config.Config.logEnable;

public class JiotSdkFileLogger {
    public static JiotSdkFileLogger m_jiotSdkFileLoggerInstance = null;
    public static File m_jiotLoggerFile;
    private static Context context;
    private static final String FILE_NAME = "logs.txt";

    public static File getRtlsLogFile(){
        return m_jiotLoggerFile;
    }

    private static void JiotCreateLogger(Context context) {
        JiotSdkFileLogger.context = context;
        try {
            m_jiotLoggerFile = new File(context.getExternalFilesDir(null), "JiotRtlsLogs.txt");
            Log.d("LOGGER", "PATH OF LOGGER FILE " + m_jiotLoggerFile.getAbsolutePath().toString());
        } catch (Exception e) {
            Log.d("LOGGER", "EXCEPTION IN JiotSdkFileLogger createLogger CREATION");
        }
    }

    public static synchronized JiotSdkFileLogger JiotGetFileLoggerInstance(Context context) {
        if (m_jiotSdkFileLoggerInstance == null) {
            m_jiotSdkFileLoggerInstance = new JiotSdkFileLogger();
            JiotCreateLogger(context);
        }
        return m_jiotSdkFileLoggerInstance;
    }


    public static synchronized int JiotWriteLogDataToFile(String data){
        int ret = 0;
        if(logEnable) {
            data = data + "\n";
            try {
                FileOutputStream fileOutputData = context.openFileOutput(FILE_NAME, Context.MODE_APPEND);
                fileOutputData.write(data.getBytes());
                fileOutputData.close();
                ret = data.length();
                Log.d("LOGGER", "Wrote Data to file: " + data);
            } catch (Exception e) {
                Log.d("LOGGER", "EXCEPTION IN LOGGER CREATION");
            }
        }
        return ret;
    }
}
