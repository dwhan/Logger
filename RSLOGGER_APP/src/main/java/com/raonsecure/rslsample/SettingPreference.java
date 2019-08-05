package com.raonsecure.rslsample;

import android.os.Bundle;
import android.util.Log;

import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

import com.raonsecure.rslogger.RSLogger;

public class SettingPreference  extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private static final String TAG = "RSLoggerSample";
    private static final String API_INIT = "key_init";
    private static final String API_SET_LEVEL = "key_level";
    private static final String API_SET_ADMIN_LOGGER = "key_admin_logger_on";
    private static final String API_SET_CRASH_LOGGER = "key_crash_report_on";
    private static final String API_SET_UIEVENT_LOGGER = "key_uievent_on";
    private static final String API_SET_FILESIZE_SEEK = "key_file_size_seek";
    private static final String API_SET_PERIOD_SEEK = "key_period_day_seek";
    private static final String BTN_CONFIGURE = "key_configure";
    private static final String LOG_LEVEL = "key_log_level";
    private static final String LOG_BTN_LOGGING = "key_logging";
    private static final String LOG_BTN_CRASH = "key_force_crash";
    private static final String LOG_BTN_CHECK_FILELOG = "key_check_filelog";
    private static final String LOG_BTN_CHECK_ADMINLOG = "key_check_adminlog";
    private static final String LOG_BTN_CHECK_CRASHLOG = "key_check_crashlog";
    private static final String LOG_BTN_GET_CRASHLOG = "key_get_crashlog";
    private static final String LOG_BTN_SEND_MAIL = "key_send_mail";
    private static final String LOG_BTN_PRINT_LOG = "key_print_view";


    private PreferenceScreen screen;
    private CheckBoxPreference mInitPreference;
    private ListPreference mLevelPreference;
    private SeekBarPreference mFileSizeSeekPreference;
    private SeekBarPreference mPeriodDaySeekPreference;
    private SwitchPreference mAdminOnPreference;
    private SwitchPreference mCrashOnPreference;
    private SwitchPreference mUIEventOnPreference;
    private Preference mConfigureButton;
    private ListPreference mLogLevelPreference;
    private Preference mLoggingButton;
    private Preference mCrashButton;
    private Preference mCheckFileLogButton;
    private Preference mCheckAdminLogButton;
    private Preference mCheckCrashButton;
    private Preference mGetCrashButton;
    private Preference mSendMailButton;
    private Preference mPrintLogButton;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.setting_preferences);

        screen = getPreferenceScreen();
        mInitPreference = (CheckBoxPreference) findPreference(API_INIT);
        mLevelPreference = (ListPreference) findPreference(API_SET_LEVEL);
        mAdminOnPreference = (SwitchPreference) findPreference(API_SET_ADMIN_LOGGER);
        mCrashOnPreference = (SwitchPreference) findPreference(API_SET_CRASH_LOGGER);
        mUIEventOnPreference = (SwitchPreference) findPreference(API_SET_UIEVENT_LOGGER);
        mFileSizeSeekPreference = (SeekBarPreference) findPreference(API_SET_FILESIZE_SEEK);
        mPeriodDaySeekPreference = (SeekBarPreference) findPreference(API_SET_PERIOD_SEEK);
        mConfigureButton = (Preference) findPreference(BTN_CONFIGURE);
        mLogLevelPreference = (ListPreference)findPreference(LOG_LEVEL);
        mLoggingButton = (Preference) findPreference(LOG_BTN_LOGGING);
        mCrashButton = (Preference) findPreference(LOG_BTN_CRASH);
        mCheckFileLogButton = (Preference) findPreference(LOG_BTN_CHECK_FILELOG);
        mCheckAdminLogButton = (Preference) findPreference(LOG_BTN_CHECK_ADMINLOG);
        mCheckCrashButton = (Preference) findPreference(LOG_BTN_CHECK_CRASHLOG);
        mGetCrashButton = (Preference) findPreference(LOG_BTN_GET_CRASHLOG);
        mSendMailButton = (Preference) findPreference(LOG_BTN_SEND_MAIL);
        mPrintLogButton = (Preference) findPreference(LOG_BTN_PRINT_LOG);

        mInitPreference.setOnPreferenceChangeListener(this);
        mLevelPreference.setOnPreferenceChangeListener(this);
        mFileSizeSeekPreference.setOnPreferenceChangeListener(this);
        mPeriodDaySeekPreference.setOnPreferenceChangeListener(this);
        mAdminOnPreference.setOnPreferenceChangeListener(this);
        mCrashOnPreference.setOnPreferenceChangeListener(this);
        mUIEventOnPreference.setOnPreferenceChangeListener(this);
        mConfigureButton.setOnPreferenceClickListener(this);
        mLogLevelPreference.setOnPreferenceChangeListener(this);
        mLoggingButton.setOnPreferenceClickListener(this);
        mCrashButton.setOnPreferenceClickListener(this);
        mCheckFileLogButton.setOnPreferenceClickListener(this);
        mCheckAdminLogButton.setOnPreferenceClickListener(this);
        mCheckCrashButton.setOnPreferenceClickListener(this);
        mGetCrashButton.setOnPreferenceClickListener(this);
        mSendMailButton.setOnPreferenceClickListener(this);
        mPrintLogButton.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        //Log.i(TAG, "preference: " + preference + ", newValue: " + newValue);

        if (preference == mInitPreference) {
            //RSLogger.init(getContext());
            new RSLogger.Builder(getContext())
                    .setLogLevel(RSLogger.LogLevel.INFO)
                    .setMaxFileSizeForBase(1)
                    .setMaxFileSizeForAdmin(1)
                    .setMaxPeriodDay(4)
                    .setEnableAdminLog(true)
                    .setEnableUIEventLog(false)
                    .setEnableCrashLog(false)
                    .build();

        } else if (preference == mLevelPreference) {
            String level = (String)newValue;
            preference.setSummary(level);

        } else if (preference == mFileSizeSeekPreference) {
            int maxFileSize = (int)newValue;
            //RSLogger.setMaxFileSize(maxFileSize, RSLogger.Type.BaseLogger);
            StringBuilder sb = new StringBuilder(Integer.toString(maxFileSize)).append("MB");
            preference.setSummary(sb);

        } else if (preference == mPeriodDaySeekPreference) {
            int maxPeriodDay = (int)newValue;
            //RSLogger.setMaxPeriodDay(maxPeriodDay);
            StringBuilder sb = new StringBuilder(Integer.toString(maxPeriodDay)).append("일");
            preference.setSummary(sb);

        } else if (preference == mAdminOnPreference) {
            //RSLogger.set

        } else if (preference == mCrashOnPreference) {
            new RSLogger.Builder(getContext())
                    .setEnableCrashLog((boolean)newValue)
                    .build();

        } else if (preference == mUIEventOnPreference) {
            //RSLogger.setUIEventLogging(true);

        } else if (preference == mLogLevelPreference) {
            preference.setSummary((String)newValue);
        }
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        //Log.i(TAG, "preference: " + preference);

        if (preference == mConfigureButton) {
            RSLogger.LogLevel logLevel = RSLogger.LogLevel.VERBOSE;
            String level = mLevelPreference.getValue();

            if (level.equals("RSLogLevelVerbose")) {
                //RSLogger.setLogLevel(RSLogger.RSLogLevel.RSLogLevelVerbose);
                logLevel = RSLogger.LogLevel.VERBOSE;
            } else if (level.equals("RSLogLevelDebug")) {
                //RSLogger.setLogLevel(RSLogger.RSLogLevel.RSLogLevelDebug);
                logLevel = RSLogger.LogLevel.DEBUG;
            } else if (level.equals("RSLogLevelInfo")) {
                //RSLogger.setLogLevel(RSLogger.RSLogLevel.RSLogLevelInfo);
                logLevel = RSLogger.LogLevel.INFO;
            } else if (level.equals("RSLogLevelWarning")) {
                //RSLogger.setLogLevel(RSLogger.RSLogLevel.RSLogLevelWarning);
                logLevel = RSLogger.LogLevel.WARN;
            } else if (level.equals("RSLogLevelError")) {
                //RSLogger.setLogLevel(RSLogger.RSLogLevel.RSLogLevelError);
                logLevel = RSLogger.LogLevel.ERROR;
            }

            new RSLogger.Builder(getContext())
                    .setLogLevel(logLevel)
                    .setMaxFileSizeForBase(mFileSizeSeekPreference.getValue())
                    .setMaxFileSizeForAdmin(mFileSizeSeekPreference.getValue())
                    .setMaxPeriodDay(mPeriodDaySeekPreference.getValue())
                    .setEnableAdminLog(mAdminOnPreference.isEnabled())
                    .setEnableUIEventLog(mUIEventOnPreference.isEnabled())
                    .setEnableCrashLog(mCrashOnPreference.isEnabled())
                    .build();


        } else if (preference == mLoggingButton) {
//            String level = mLogLevelPreference.getValue();
//            if (level.equals("RSLogLevelVerbose")) {
//                RSLogger.v(TAG, "This is verbose message.");
//            } else if (level.equals("RSLogLevelDebug")) {
//                RSLogger.d(TAG, "This is debug message.");
//            } else if (level.equals("RSLogLevelInfo")) {
//                RSLogger.i(TAG, "This is info message.");
//            } else if (level.equals("RSLogLevelWarning")) {
//                RSLogger.w(TAG, "This is warning message.");
//            } else if (level.equals("RSLogLevelError")) {
//                RSLogger.e(TAG, "This is error message.");
//            }

            for (int i=0; i < 1; i++) {
                RSLogger.v(TAG, "<< This is verbose message >>");
                RSLogger.d(TAG, "<< This is debug message >>");
                RSLogger.i(TAG, "<< This is info message >>");
                RSLogger.w(TAG, "<< This is warning message >>");
                RSLogger.e(TAG, "<< This is error message >>");
            }

        } else if (preference == mCrashButton) {
            throw new RuntimeException("This is a crash");
        } else if (preference == mCheckFileLogButton) {

        } else if (preference == mCheckAdminLogButton) {

        } else if (preference == mCheckCrashButton) {
            Log.d(TAG, "isHasLastCrashed : " + RSLogger.isHasLastCrashed());
        } else if (preference == mGetCrashButton) {
            Log.i(TAG, "crash : " + RSLogger.getCrashReportWithFlush());
        } else if (preference == mSendMailButton) {
            //RSLogger.sendMail();
            RSLogger.sendMail(new RSLogger.MailBuilder()
                    .withSenderAccountId("raonClient")
                    .withSenderAccounPassword("12qwaszx!@")
                    .withMailTo("dwhan@raonsecure.com")
                    .withMailCc("dwhan@raonsecure.com")
                    .withMailBcc("dwhan@raonsecure.com")
                    .withSubject("RSLogger mail send")
                    .withBody("this is mail body, ok!")
                    .build()
            );
        } else if (preference == mPrintLogButton) {

        }

        return true;
    }}
