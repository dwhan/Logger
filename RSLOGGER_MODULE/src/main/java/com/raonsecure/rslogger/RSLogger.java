package com.raonsecure.rslogger;

import android.content.Context;
import android.util.Log;

import com.raonsecure.rslogger.contract.RSLoggerConfigurator;
import com.raonsecure.rslogger.contract.RSLoggerManager;
import com.raonsecure.rslogger.model.Configuration;
import com.raonsecure.rslogger.model.CrashReport;
import com.raonsecure.rslogger.util.RSErrorCode;

import java.io.File;


public class RSLogger {
    private static RSLoggerConfigurator loggerConfigurator = new RSLoggerConfigurator();
    private static Configuration configuration;
    private static RSLoggerManager loggerManager;

    public enum LoggerType {BASE, ADMIN}
    public enum LogLevel {
        VERBOSE (0),
        DEBUG   (1),
        INFO    (2),
        WARN    (3),
        ERROR   (4);

        private final int code;

        LogLevel(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

    RSLogger(Builder builder) {
        if (builder == null) {
            Log.w(RSErrorCode.TAG, "WARNING: The RSLogger builder is not instance. The RSLogger.Builder is null.");
            return;
        }
        new Configuration.ConfigBuilder(builder.context)
                .setLogLevel(builder.logLevel)
                .setMaxFileSizeForBase(builder.maxFileSizeForBase)
                .setMaxFileSizeForAdmin(builder.maxFileSizeForAdmin)
                .setMaxPeriodDay(builder.maxPeriodDay)
                .setEnableAdminLog(builder.isEnabledAdminLog)
                .setEnableUIEventLog(builder.isEnabledUIEventLog)
                .setEnableCrashLog(builder.isEnabledCrashLog)
                .build();
        setRSLoggerConfig(builder.context);
    }

    public static class Builder {
        private Context context;
        private RSLogger.LogLevel logLevel = LogLevel.VERBOSE;
        private long maxFileSizeForBase = 0;
        private long maxFileSizeForAdmin = 0;
        private int maxPeriodDay = 0;
        private boolean isEnabledAdminLog = false;
        private boolean isEnabledUIEventLog = false;
        private boolean isEnabledCrashLog = false;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setLogLevel(RSLogger.LogLevel level) {
            this.logLevel = level;
            return this;
        }

        public Builder setMaxFileSizeForBase(long maxFileSize) {
            if (maxFileSize <= 0) {
                Log.w(RSErrorCode.TAG, "WARNING: RSLogger maximum file(BASE) size is invalid. You should set to a value greater than zero.");
            }

            this.maxFileSizeForBase = maxFileSize;
            return this;
        }

        public Builder setMaxFileSizeForAdmin(long maxFileSize) {
            if (maxFileSize <= 0) {
                Log.w(RSErrorCode.TAG, "WARNING: RSLogger maximum file(ADMIN) size is invalid. You should set to a value greater than zero.");
            }

            this.maxFileSizeForAdmin = maxFileSize;
            return this;
        }

        public Builder setMaxPeriodDay(int maxPeriodDay) {
            if (maxPeriodDay <= 0) {
                Log.w(RSErrorCode.TAG, "WARNING: RSLogger maximum period value is invalid. You should set to a value greater than zero.");
            }

            this.maxPeriodDay = maxPeriodDay;
            return this;
        }

        public Builder setEnableAdminLog(boolean enabled) {
            this.isEnabledAdminLog = enabled;
            return this;
        }

        public Builder setEnableUIEventLog(boolean enabled) {
            this.isEnabledUIEventLog = enabled;
            return this;
        }

        public Builder setEnableCrashLog(boolean enabled) {
            this.isEnabledCrashLog = enabled;
            return this;
        }

        public RSLogger build() {
            return new RSLogger(this);
        }
    }

    private static synchronized void setRSLoggerConfig(Context context) {
        Log.i(RSErrorCode.TAG, Configuration.getInstance().toString());

        if (context != null) {
            try {
                configuration = Configuration.getInstance();
                loggerManager = RSLoggerManager.getInstance();
                loggerConfigurator.setRsContext(configuration.getRsContext());
                loggerConfigurator.setFileName(configuration.getFileNameForBase());
                loggerConfigurator.setFileNameForForce(configuration.getFileNameForAdmin());
                loggerConfigurator.setFilePattern(configuration.getFilePattern());
                loggerConfigurator.setMaxBackupSize(configuration.getMaxBackupSize());
                loggerConfigurator.setLevel(configuration.getLogLevel());
                loggerConfigurator.setMaxFileSize(configuration.getMaxFileSizeForBase(), LoggerType.BASE);
                loggerConfigurator.setMaxFileSize(configuration.getMaxFileSizeForAdmin(), LoggerType.ADMIN);
                loggerConfigurator.setMaxPeriodDay(configuration.getMaxPeriodDay());
                loggerManager.setCrashHandler(context, configuration.isEnabledCrashLog());
                loggerConfigurator.configure();
            } catch (SecurityException exception) {
            }
        } else {
            Log.w(RSErrorCode.TAG, "WARNING: The RSLogger sdk is not initialized. The context provided is null.");
        }
    }

    public static RSLogger.LogLevel getLogLevel() {
        return configuration.getLogLevel();
    }

    public static String getFileName(LoggerType type) {
        if (LoggerType.BASE == type) {
            return configuration.getFileNameForBase();
        } else {
            return configuration.getFileNameForAdmin();
        }
    }

    public static long getMaxFileSize(final LoggerType type) {
        if (LoggerType.BASE == type) {
            return configuration.getMaxFileSizeForBase();
        } else {
            return configuration.getMaxFileSizeForAdmin();
        }
    }

    public static int getMaxPeriodDay() {
        return configuration.getMaxPeriodDay();
    }

    public static boolean isHasLastCrashed() {
        return CrashReport.isHasLastCrashReport();
    }

    public static String getCrashReportWithFlush() {
        return CrashReport.loadLastCrashReportWithFlush();
    }

    public static File getLogFile(LoggerType type) {
        if (LoggerType.BASE == type) {
            File logFile = new File(loggerConfigurator.getFileName());
            return logFile;
        } else if (LoggerType.ADMIN == type) {
            File logFile = new File(loggerConfigurator.getFileNameForForce());
            return logFile;
        } else {
            return null;
        }
    }

    public static void sendMail(MailBuilder mailBuilder) {
        loggerManager.sendMailOnBackground(mailBuilder);
    }

    public static void e(final String tag, final String msg) {
        if (isRSLoggerInitialized() && isLevelOptimization(LogLevel.ERROR)) {
            loggerManager.e(tag, msg);
        }
    }

    public static void w(final String tag, final String msg) {
        if (isRSLoggerInitialized() && isLevelOptimization(LogLevel.WARN)) {
            loggerManager.w(tag, msg);
        }
    }

    public static void i(final String tag, final String msg) {
        if (isRSLoggerInitialized() && isLevelOptimization(LogLevel.INFO)) {
            loggerManager.i(tag, msg);
        }
    }

    public static void d(final String tag, final String msg) {
        if (isRSLoggerInitialized() && isLevelOptimization(LogLevel.DEBUG)) {
            loggerManager.d(tag, msg);
        }
    }

    public static void v(final String tag, final String msg) {
        if (isRSLoggerInitialized() && isLevelOptimization(LogLevel.VERBOSE)) {
            loggerManager.v(tag, msg);
        }
    }

    private static boolean isLevelOptimization(LogLevel level) {
        if (Configuration.getInstance().getLogLevel().getCode() > level.getCode()) {
            //Log.w(TAG, "WARNING: RSLogger current Level is skipped. (SettingLevel:"+ Configuration.getInstance().getLogLevel().getCode() + ", CurrentLevel:" + level.getCode() + ")");
            return false;
        } else {
            return true;
        }
    }

    private static boolean isRSLoggerInitialized() {
        if (loggerManager == null || configuration == null) {
            Log.w(RSErrorCode.TAG, "WARNING: RSLogger SDK is not initialized. You should call first to the method RSLogger.init()");
            return false;
        } else {
            return true;
        }
    }

    public static class MailBuilder {

        private String userName;
        private String password;
        private String mailTo;
        private String mailCc;
        private String mailBcc;
        private String subject;
        private String body;

        public MailBuilder() { }

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
        }

        public String getMailTo() {
            return mailTo;
        }

        public String getMailCc() {
            return mailCc;
        }

        public String getMailBcc() {
            return mailBcc;
        }

        public String getSubject() {
            return subject;
        }

        public String getBody() {
            return body;
        }

        public MailBuilder withSenderAccountId(String id) {
            this.userName = id;
            return this;
        }

        public MailBuilder withSenderAccounPassword(String password) {
            this.password = password;
            return this;
        }

        public MailBuilder withMailTo(String mailTo) {
            this.mailTo = mailTo;
            return this;
        }

        public MailBuilder withMailCc(String mailCc) {
            this.mailCc = mailCc;
            return this;
        }

        public MailBuilder withMailBcc(String mailBcc) {
            this.mailBcc = mailBcc;
            return this;
        }

        public MailBuilder withSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public MailBuilder withBody(String body) {
            this.body = body;
            return this;
        }

        public MailBuilder build() {
            return this;
        }
    }
}
