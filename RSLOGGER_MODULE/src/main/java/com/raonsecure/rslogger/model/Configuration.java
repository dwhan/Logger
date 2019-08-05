package com.raonsecure.rslogger.model;

import android.content.Context;

import androidx.annotation.NonNull;

import com.raonsecure.rslogger.RSLogger;


public class Configuration {
    //user configuration
    private Context rsContext;
    private RSLogger.LogLevel logLevel = RSLogger.LogLevel.VERBOSE;
    private final long RSMB = 1024 * 1024;  //1MB
    private final long RSGB = RSMB * 1024;  //1GB
    private long maxFileSizeForBase = 10 * RSMB;
    private long maxFileSizeForAdmin = 10 * RSMB;
    private int maxPeriodDay = 30;
    private boolean isEnabledAdminLog;
    private boolean isEnabledUIEventLog;
    private boolean isEnabledCrashLog;

    //core default configuration
    private String filePattern = "%d - [%p::%c::%C] - %m%n";
    private String logCatPattern = "%m%n";
    private String fileNameForBase;
    private String fileNameForAdmin;
    private String applicationName = "RSLoggerSDK";
    private int maxBackupSize = 1;
    private boolean immediateFlush = true;
    private boolean useLogCatAppender = true;
    private boolean useFileAppender = true;
    private boolean resetConfiguration = true;
    private boolean internalDebugging = false;

    private Configuration() { }

    private static class ConfigurationHolder {
        public static final Configuration INSTANCE = new Configuration();
    }

    public static Configuration getInstance() {
        return ConfigurationHolder.INSTANCE;
    }

    public static class ConfigBuilder {

        private static Configuration instance = Configuration.getInstance();
        private Context context;
        private RSLogger.LogLevel logLevel;
        private long maxFileSizeForBase;
        private long maxFileSizeForAdmin;
        private int maxPeriodDay;
        private boolean isEnabledAdminLog;
        private boolean isEnabledUIEventLog;
        private boolean isEnabledCrashLog;

        public ConfigBuilder(Context context) {
            this.context = context;
        }

        public ConfigBuilder setLogLevel(RSLogger.LogLevel level) {
            this.logLevel = level;
            return this;
        }

        public ConfigBuilder setMaxFileSizeForBase(long maxFileSize) {
            this.maxFileSizeForBase = maxFileSize;
            return this;
        }

        public ConfigBuilder setMaxFileSizeForAdmin(long maxFileSize) {
            this.maxFileSizeForAdmin = maxFileSize;
            return this;
        }

        public ConfigBuilder setMaxPeriodDay(int maxPeriodDay) {
            this.maxPeriodDay = maxPeriodDay;
            return this;
        }

        public ConfigBuilder setEnableAdminLog(boolean enabled) {
            this.isEnabledAdminLog = enabled;
            return this;
        }

        public ConfigBuilder setEnableUIEventLog(boolean enabled) {
            this.isEnabledUIEventLog = enabled;
            return this;
        }

        public ConfigBuilder setEnableCrashLog(boolean enabled) {
            this.isEnabledCrashLog = enabled;
            return this;
        }

        public Configuration build() {
            //return new Configuration(this);
            instance.rsContext = context;
            instance.logLevel = logLevel;
            if (maxFileSizeForBase > 0) {
                instance.maxFileSizeForBase = maxFileSizeForBase * instance.RSMB;
            }
            if (maxFileSizeForAdmin > 0) {
                instance.maxFileSizeForAdmin = maxFileSizeForAdmin * instance.RSMB;
            }
            if (maxPeriodDay > 0) {
                instance.maxPeriodDay = maxPeriodDay;
            }
            instance.isEnabledAdminLog = isEnabledAdminLog;
            instance.isEnabledUIEventLog = isEnabledUIEventLog;
            instance.isEnabledCrashLog = isEnabledCrashLog;

            String applicationName = context.getApplicationInfo().labelRes == 0 ? context.getApplicationInfo().nonLocalizedLabel.toString() : context.getString(context.getApplicationInfo().labelRes);
            instance.applicationName = applicationName;
            instance.fileNameForBase = context.getFilesDir() + "/LOG_FILE/" + applicationName + ".log";
            instance.fileNameForAdmin = context.getFilesDir() + "/LOG_FILE/" + applicationName + "_Admin.log";

            return instance;
        }
    }
    public Context getRsContext() {
        return rsContext;
    }

    public void setRsContext(Context rsContext) {
        this.rsContext = rsContext;
    }

    public RSLogger.LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(RSLogger.LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public long getMaxFileSizeForBase() {
        return maxFileSizeForBase;
    }

    public void setMaxFileSizeForBase(long maxFileSizeForBase) {
        this.maxFileSizeForBase = maxFileSizeForBase;
    }

    public long getMaxFileSizeForAdmin() {
        return maxFileSizeForAdmin;
    }

    public void setMaxFileSizeForAdmin(long maxFileSizeForAdmin) {
        this.maxFileSizeForAdmin = maxFileSizeForAdmin;
    }

    public int getMaxPeriodDay() {
        return maxPeriodDay;
    }

    public void setMaxPeriodDay(int maxPeriodDay) {
        this.maxPeriodDay = maxPeriodDay;
    }

    public boolean isEnabledAdminLog() {
        return isEnabledAdminLog;
    }

    public void setEnabledAdminLog(boolean enabledAdminLog) {
        isEnabledAdminLog = enabledAdminLog;
    }

    public boolean isEnabledUIEventLog() {
        return isEnabledUIEventLog;
    }

    public void setEnabledUIEventLog(boolean enabledUIEventLog) {
        isEnabledUIEventLog = enabledUIEventLog;
    }

    public boolean isEnabledCrashLog() {
        return isEnabledCrashLog;
    }

    public void setEnabledCrashLog(boolean enabledCrashLog) {
        isEnabledCrashLog = enabledCrashLog;
    }

    public String getFileNameForBase() {
        return fileNameForBase;
    }

    public String getFileNameForAdmin() {
        return fileNameForAdmin;
    }

    public String getFilePattern() {
        return filePattern;
    }

    public int getMaxBackupSize() {
        return maxBackupSize;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public boolean isImmediateFlush() {
        return immediateFlush;
    }

    public boolean isUseLogCatAppender() {
        return useLogCatAppender;
    }

    public boolean isUseFileAppender() {
        return useFileAppender;
    }

    public boolean isResetConfiguration() {
        return resetConfiguration;
    }

    public boolean isInternalDebugging() {
        return internalDebugging;
    }

    @NonNull
    @Override
    public String toString() {
        return "LogLevel : '" + logLevel + '\''
                + " maxFileSizeForBase : '" + maxFileSizeForBase + '\''
                + " maxFileSizeForAdmin : '" + maxFileSizeForAdmin + '\''
                + " maxPeriodDay : '" + maxPeriodDay + '\''
                + " isEnabledAdminLog : '" + isEnabledAdminLog + '\''
                + " isEnabledUIEventLog : '" + isEnabledUIEventLog + '\''
                + " isEnabledCrashLog : '" + isEnabledCrashLog + '\'';
    }


}
