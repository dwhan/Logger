package com.raonsecure.rslogger.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.raonsecure.rslogger.contract.RSLoggerContract;
import com.raonsecure.rslogger.contract.RSLoggerManager;
import com.raonsecure.rslogger.model.Configuration;
import com.raonsecure.rslogger.model.CrashReport;

import org.apache.log4j.jmx.LoggerDynamicMBean;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public class RSCrashHandler {
    private static final String RS_HANDLER_PACKAGE_NAME = "com.raonsecure.rslogger.util";
    private static final String DEFAULT_HANDLER_PACKAGE_NAME = "com.android.internal.os";
    private static final int MAX_STACK_TRACE_SIZE = 131071; //128 KB - 1
    private static boolean isEnabledCrashLog = false;
    private static Application application;

    RSCrashHandler(Builder builder) {
        isEnabledCrashLog = builder.isEnabledCrashLog;
        setRSCrashHandler(builder.context, builder.crashCallback);
    }

    public static class Builder {
        private Context context;
        private RSLoggerContract.OnCrashResponseCallback crashCallback;
        private boolean isEnabledCrashLog = true;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setRSCHandler (boolean enable) {
            //this.isEnabledCrashLog = enable;
            return this;
        }

        public Builder setCallback (RSLoggerContract.OnCrashResponseCallback callback) {
            this.crashCallback = callback;
            return this;
        }

        public RSCrashHandler build() {
            return new RSCrashHandler(this);
        }
    }

    private static void setRSCrashHandler(final Context context, final RSLoggerContract.OnCrashResponseCallback callback) {
        try {
            if (context != null) {
                final Thread.UncaughtExceptionHandler exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
                if (exceptionHandler != null && exceptionHandler.getClass().getName().startsWith(RS_HANDLER_PACKAGE_NAME)) {
                    RSLoggerManager.getInstance().e(Configuration.getInstance().getApplicationName(), "RSHandler was already installed, doing nothing!");
                } else {
                    if (exceptionHandler != null && !exceptionHandler.getClass().getName().startsWith(DEFAULT_HANDLER_PACKAGE_NAME)) {
                        RSLoggerManager.getInstance().e(Configuration.getInstance().getApplicationName(), "You already have an UncaughtExceptionHandler. If you use a custom UncaughtExceptionHandler, it should be initialized after RSCrashHandler! Installing anyway, but your original handler will not be called.");

                    }
                }
                application = (Application) context.getApplicationContext();

                //Setup RSCrash Handler
                Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread thread, Throwable throwable) {
                        Log.e(Configuration.getInstance().getApplicationName(), "App crashed, executing RSCrash Handler's UncaughtExceptionHandler", throwable);

                        if (isEnabledCrashLog == false) {
                            Log.e(Configuration.getInstance().getApplicationName(), "App crashed recently. but, disabled crashLog. Not also logging anything.", throwable);
                            return;
                        }

                        if (hasCrashedinTheLastSeconds(application)) {
                            Log.e(Configuration.getInstance().getApplicationName(), "App already crashed recently, not starting custom error activity because we could enter a restart loop.", throwable);
                            if (exceptionHandler != null) {
                                exceptionHandler.uncaughtException(thread, throwable);
                                return;
                            }
                        } else {
                            CrashReport.setLastCrashTimestamp(new Date().getTime());

                            //stackTraceString
                            StringWriter stringWriter = new StringWriter();
                            PrintWriter printWriter = new PrintWriter(stringWriter);
                            throwable.printStackTrace(printWriter);
                            String stackTraceString = stringWriter.toString();
                            if (stackTraceString.length() > MAX_STACK_TRACE_SIZE) {
                                String discalaimer = " [stack trace too large]";
                                stackTraceString = stackTraceString.substring(0, MAX_STACK_TRACE_SIZE - discalaimer.length()) + discalaimer;
                            }

                            //저장하지 않고 callback 으로 처리
                            //setLastCrashData(context, getAllErrorDataDetailsFormCrash(context, stackTraceString));

                            //callback.onCrash(getAllErrorDataDetailsFormCrash(context, stackTraceString));
                            callback.onCrash(context, stackTraceString);

                            //killCurrentProcess();
                        }
                    }
                });
            } else {
                RSLoggerManager.getInstance().e(Configuration.getInstance().getApplicationName(), "Context can not be null");
            }
        } catch (Throwable throwable) {
            Log.e(Configuration.getInstance().getApplicationName(), "RSCrashHandler can not be initialized.", throwable);
        }
    }


    private static boolean hasCrashedinTheLastSeconds (Context context) {
        long lastTimestamp = CrashReport.getLastCrashTimestamp();
        long currentTimestamp = new Date().getTime();
        return (lastTimestamp <= currentTimestamp && currentTimestamp - lastTimestamp < 3000);
    }

    private static void killCurrentProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    static void closeApplication(Activity activity) {
        activity.finish();
        killCurrentProcess();
    }
}
