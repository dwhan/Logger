package com.raonsecure.rslogger.contract;

import android.content.Context;

import com.raonsecure.rslogger.RSLogger;
import com.raonsecure.rslogger.model.Configuration;

import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.helpers.LogLog;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.mindpipe.android.logging.log4j.LogCatAppender;


public class RSLoggerConfigurator {
    private static final String SHARED_PREFERENCES_FILE = "rsch_preferences";
    private static final String SHARED_PREFERENCES_FILE_TIMESTAMP = "create_file_timestamp";

    private RSLogger.LogLevel logLevel = RSLogger.LogLevel.VERBOSE;
    private Level rootLevel = Level.TRACE;
    private String filePattern = "%d - [%p::%c::%C] - %m%n";
    private String logCatPattern = "%m%n";
    private String fileName = "android-rslog.log";
    private String fileNameForce = "forceAll-rslog.log";

    private int maxBackupSize = 1;
    private final long RSMB = 1024 * 1024;  //1MB
    private long maxFileSizeForGeneric = 10 * RSMB;
    private long maxFileSizeForForce = 10 * RSMB;
    private int maxPeriodDay = 30;

    private boolean immediateFlush = true;
    private boolean useLogCatAppender = true;
    private boolean useFileAppender = true;
    private boolean resetConfiguration = true;
    private boolean internalDebugging = false;
    private Context rsContext;

    public RSLoggerConfigurator() {
    }

    /**
     * @param fileName Name of the log file
     */
    public RSLoggerConfigurator(final String fileName) {
        setFileName(fileName);
    }

    /**
     * @param fileName  Name of the log file
     * @param rootLevel Log level for the root logger
     */
    public RSLoggerConfigurator(final String fileName, final Level rootLevel) {
        this(fileName);
        setRootLevel(rootLevel);
    }

    /**
     * @param fileName Name of the log file
     * @param rootLevel Log level for the root logger
     * @param filePattern Log pattern for the file appender
     */
    public RSLoggerConfigurator(final String fileName, final Level rootLevel, final String filePattern) {
        this(fileName);
        setRootLevel(rootLevel);
        setFilePattern(filePattern);
    }

    /**
     * @param fileName Name of the log file
     * @param maxBackupSize Maximum number of backed up log files
     * @param maxFileSize Maximum size of log file until rolling
     * @param filePattern  Log pattern for the file appender
     * @param rootLevel Log level for the root logger
     */
    public RSLoggerConfigurator(final String fileName, final int maxBackupSize,
                                final long maxFileSize, final String filePattern, final Level rootLevel) {
        this(fileName, rootLevel, filePattern);
        setMaxBackupSize(maxBackupSize);
        setMaxFileSize(maxFileSize, RSLogger.LoggerType.BASE);
        setMaxFileSize(maxFileSize, RSLogger.LoggerType.ADMIN);
    }

    public void configure() {
        final Logger root = Logger.getRootLogger();

        if(isResetConfiguration()) {
            LogManager.getLoggerRepository().resetConfiguration();
        }

        LogLog.setInternalDebugging(isInternalDebugging());

        if(isUseFileAppender()) {
            configureFileAppender();
        }
        if (Configuration.getInstance().isEnabledAdminLog()) {
            configureForceFileAppender();
        }

        if(isUseLogCatAppender()) {
            configureLogCatAppender();
        }

        root.setLevel(Level.ALL);
    }

    /**
     * Sets the level of logger with name <code>loggerName</code>.
     * Corresponds to log4j.properties <code>log4j.logger.org.apache.what.ever=ERROR</code>
     * @param loggerName
     * @param level
     */
    public void setLevel(final String loggerName, final Level level) {
        Logger.getLogger(loggerName).setLevel(level);
    }

    public void setLevel(final RSLogger.LogLevel level) {

        this.logLevel = level==null ? RSLogger.LogLevel.VERBOSE : level;

        if (level == RSLogger.LogLevel.VERBOSE) {
            setRootLevel(Level.ALL);
        } else if (level == RSLogger.LogLevel.DEBUG) {
            setRootLevel(Level.DEBUG);
        } else if (level == RSLogger.LogLevel.INFO) {
            setRootLevel(Level.INFO);
        } else if (level == RSLogger.LogLevel.WARN) {
            setRootLevel(Level.WARN);
        } else if (level == RSLogger.LogLevel.ERROR) {
            setRootLevel(Level.ERROR);
        } else {
            setRootLevel(Level.ALL);
        }
    }

    public RSLogger.LogLevel getLevel() {
        return logLevel;
    }


    private void configureFileAppender() {
        checkPeriodDayOver();

        final Logger root = Logger.getRootLogger();
        final RollingFileAppender rollingFileAppender;
        final Layout fileLayout = new PatternLayout(getFilePattern());

        try {
            rollingFileAppender = new RollingFileAppender(fileLayout, getFileName());
        } catch (final IOException e) {
            throw new RuntimeException("Exception configuring log system", e);
        }

        rollingFileAppender.setMaxBackupIndex(getMaxBackupSize());
        rollingFileAppender.setMaximumFileSize(getMaxFileSize(RSLogger.LoggerType.BASE));
        rollingFileAppender.setImmediateFlush(isImmediateFlush());
        rollingFileAppender.setThreshold(rootLevel);
        root.addAppender(rollingFileAppender);
    }

    private void configureForceFileAppender() {
        final Logger root = Logger.getRootLogger();
        final RollingFileAppender rollingFileAppender;
        final Layout fileLayout = new PatternLayout(getFilePattern());

        try {
            rollingFileAppender = new RollingFileAppender(fileLayout, getFileNameForForce());
        } catch (final IOException e) {
            throw new RuntimeException("Exception configuring log system", e);
        }

        rollingFileAppender.setMaxBackupIndex(getMaxBackupSize());
        rollingFileAppender.setMaximumFileSize(getMaxFileSize(RSLogger.LoggerType.ADMIN));
        rollingFileAppender.setImmediateFlush(isImmediateFlush());
        root.addAppender(rollingFileAppender);
    }



    private void configureLogCatAppender() {
        final Logger root = Logger.getRootLogger();
        final Layout logCatLayout = new PatternLayout(getLogCatPattern());
        final LogCatAppender logCatAppender = new LogCatAppender(logCatLayout);
        logCatAppender.setThreshold(rootLevel);

        root.addAppender(logCatAppender);
    }

    /**
     * Return the log level of the root logger
     * @return Log level of the root logger
     */
    public Level getRootLevel() {
        return rootLevel;
    }

    /**
     * Sets log level for the root logger
     * @param level Log level for the root logger
     */
    public void setRootLevel(final Level level) {
        this.rootLevel = level;
    }

    public String getFilePattern() {
        return filePattern;
    }

    public void setFilePattern(final String filePattern) {
        this.filePattern = filePattern;
    }

    public String getLogCatPattern() {
        return logCatPattern;
    }

    public void setLogCatPattern(final String logCatPattern) {
        this.logCatPattern = logCatPattern;
    }

    /**
     * Returns the name of the log file
     * @return the name of the log file
     */
    public String getFileName() {
        return fileName;
    }


    public String getFileNameForForce() {
        return fileNameForce;
    }

    /**
     * Sets the name of the log file
     * @param fileName Name of the log file
     */
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public void setFileNameForForce(final String fileName) {
        this.fileNameForce = fileName;
    }


    /**
     * Returns the maximum number of backed up log files
     * @return Maximum number of backed up log files
     */
    public int getMaxBackupSize() {
        return maxBackupSize;
    }

    /**
     * Sets the maximum number of backed up log files
     * @param maxBackupSize Maximum number of backed up log files
     */
    public void setMaxBackupSize(final int maxBackupSize) {
        this.maxBackupSize = maxBackupSize;
    }



    /**
     * Sets the maximum size of log file until rolling
     * @param maxFileSize Maximum size of log file until rolling
     */
    public void setMaxFileSize(final long maxFileSize, final RSLogger.LoggerType type) {
        if (maxFileSize <= 0) {
            return;
        }

        if (RSLogger.LoggerType.BASE == type) {
            this.maxFileSizeForGeneric = maxFileSize * RSMB;
        } else {
            this.maxFileSizeForForce = maxFileSize * RSMB;
        }
    }


    /**
     * Returns the maximum size of log file until rolling
     * @return Maximum size of log file until rolling
     */
    public long getMaxFileSize(final RSLogger.LoggerType type) {

        if (RSLogger.LoggerType.BASE == type) {
            return maxFileSizeForGeneric;
        } else {
            return maxFileSizeForForce;
        }
    }


    public void setMaxPeriodDay(final int maxPeriodDay) {
        if (maxPeriodDay <= 0) {
            return;
        }

        this.maxPeriodDay = maxPeriodDay;
    }

    public int getMaxPeriodDay() {
        return maxPeriodDay;
    }



    public boolean isImmediateFlush() {
        return immediateFlush;
    }

    public void setImmediateFlush(final boolean immediateFlush) {
        this.immediateFlush = immediateFlush;
    }

    /**
     * Returns true, if FileAppender is used for logging
     * @return True, if FileAppender is used for logging
     */
    public boolean isUseFileAppender() {
        return useFileAppender;
    }

    /**
     * @param useFileAppender the useFileAppender to set
     */
    public void setUseFileAppender(final boolean useFileAppender) {
        this.useFileAppender = useFileAppender;
    }

    /**
     * Returns true, if LogcatAppender should be used
     * @return True, if LogcatAppender should be used
     */
    public boolean isUseLogCatAppender() {
        return useLogCatAppender;
    }

    /**
     * If set to true, LogCatAppender will be used for logging
     * @param useLogCatAppender If true, LogCatAppender will be used for logging
     */
    public void setUseLogCatAppender(final boolean useLogCatAppender) {
        this.useLogCatAppender = useLogCatAppender;
    }

    public void setResetConfiguration(boolean resetConfiguration) {
        this.resetConfiguration = resetConfiguration;
    }

    /**
     * Resets the log4j configuration before applying this configuration. Default is true.
     * @return True, if the log4j configuration should be reset before applying this configuration.
     */
    public boolean isResetConfiguration() {
        return resetConfiguration;
    }

    public void setInternalDebugging(boolean internalDebugging) {
        this.internalDebugging = internalDebugging;
    }

    public boolean isInternalDebugging() {
        return internalDebugging;
    }


    public Context getRsContext() {
        return rsContext;
    }

    public void setRsContext(final Context context) {
        this.rsContext = context;
    }



    //PeriodDay
    private void checkPeriodDayOver() {
        File file = new File(getFileName());
        if (file.exists()) {
            long createdTimestamp = getCreateFileTimestamp();
            long currentTimestamp = new Date().getTime();
            //두날짜 사이의 시간 차이(ms)를 하루 동안의 ms(24시*60분*60초*1000밀리초) 로 나눈다.
            long diffTimestamp = (currentTimestamp - createdTimestamp)/(24*60*60*1000);

            //Log.i("dwhan", "diff day : "+ diffTimestamp + "일");
            if ((createdTimestamp <= currentTimestamp &&  getMaxPeriodDay() < diffTimestamp)) {
                folderFlushing();
            }
        } else {
            setCreateFileTimestamp(new Date().getTime());
        }
    }

    private void setCreateFileTimestamp(long timestamp) {
        getRsContext().getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).edit().putLong(SHARED_PREFERENCES_FILE_TIMESTAMP, timestamp).commit();
    }

    private long getCreateFileTimestamp () {
        return getRsContext().getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).getLong(SHARED_PREFERENCES_FILE_TIMESTAMP, -1);
    }

    private void folderFlushing() {
        File dir = new File(getRsContext().getFilesDir() + "/LOG_FILE/");
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }

            setCreateFileTimestamp(new Date().getTime());
        }
    }
}
