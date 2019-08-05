package com.raonsecure.rslogger.contract;

import android.content.Context;
import android.view.View;

import com.raonsecure.rslogger.RSLogger;
import com.raonsecure.rslogger.model.Configuration;
import com.raonsecure.rslogger.model.CrashReport;
import com.raonsecure.rslogger.util.RSCrashHandler;
import com.raonsecure.rslogger.util.RSErrorCode;
import com.raonsecure.rslogger.util.mail.BackgroundMail;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;

public class RSLoggerManager {
    private static final Logger logger = Logger.getLogger("RSLog");

    private RSLoggerManager() { }

    private static class RSLoggerManagerHolder {
        public static final RSLoggerManager INSTANCE = new RSLoggerManager();
    }

    public static RSLoggerManager getInstance() {
        return RSLoggerManagerHolder.INSTANCE;
    }

    public void v(String tag ,String msg) {
        String tagMsg = String.format("%s: %s", tag, msg);
        logger.debug(tagMsg);
    }

    public void d(String tag ,String msg) {
        String tagMsg = String.format("%s: %s", tag, msg);
        logger.debug(tagMsg);
    }

    public void i(String tag ,String msg) {
        String tagMsg = String.format("%s: %s", tag, msg);
        logger.info(tagMsg);
    }

    public void w(String tag ,String msg) {
        String tagMsg = String.format("%s: %s", tag, msg);
        logger.warn(tagMsg);
    }

    public void e(String tag ,String msg) {
        String tagMsg = String.format("%s: %s", tag, msg);
        logger.error(tagMsg);
    }

    public void setCrashHandler(Context context, final boolean enable) {
        if (enable == false) {
            return;
        }

        new RSCrashHandler.Builder(context)
                .setRSCHandler(true)
                .setCallback(callback)
                .build();
    }

    private final RSLoggerContract.OnCrashResponseCallback callback = new RSLoggerContract.OnCrashResponseCallback() {
        @Override
        public void onCrash(Context context, String stackTraceString) {
            CrashReport.saveLastCrashReport(stackTraceString);
        }
    };

    public void onUI(JoinPoint joinPoint) {
        RSLoggerManager.getInstance().i(Configuration.getInstance().getApplicationName(), "[UI] Front-most activity :" + joinPoint.getTarget());
    }

    public void onInteraction (JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            View view = (View) args[0];
            Context context = view.getContext();
            int viewResId = view.getId();
            if (context != null && viewResId > 0) {
                String entryName = context.getResources().getResourceEntryName(view.getId());
                RSLoggerManager.getInstance().i(Configuration.getInstance().getApplicationName(), "[Interaction] Action [" + view.getContext() + "] by sender view (resourceId:\"" + entryName + "\"" + ")");
            } else {
                RSLoggerManager.getInstance().i(Configuration.getInstance().getApplicationName(), "[Interaction] Action currentTitle:\"" + joinPoint.getTarget() + "\"");
            }
        } else {
            RSLoggerManager.getInstance().i(Configuration.getInstance().getApplicationName(), "[Interaction] Action currentTitle:\"" + joinPoint.getTarget() + "\"");
        }
    }

    public void sendMailOnBackground(RSLogger.MailBuilder builder) {
        BackgroundMail.newBuilder(Configuration.getInstance().getRsContext())
                .withUsername(builder.getUserName())
                .withPassword(builder.getPassword())
                .withMailTo(builder.getMailTo())
                .withMailCc(builder.getMailCc())
                .withMailBcc(builder.getMailBcc())
                .withSubject(builder.getSubject())
                .withBody(builder.getBody())
                .withAttachments(Configuration.getInstance().getFileNameForAdmin(), Configuration.getInstance().getFileNameForBase())
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        RSLoggerManager.getInstance().i(Configuration.getInstance().getApplicationName(), "[MailSender] Message was sent successfully.");
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        RSLoggerManager.getInstance().i(Configuration.getInstance().getApplicationName(), "[MailSender] Error sending email, check your connection");
                    }
                })
                .send();
    }
}
