package com.khs.spcmeasure.library;

import android.content.Context;

import com.khs.spcmeasure.R;

/**
 * Created by Mark on 08/10/2015.
 * see:
 * http://stackoverflow.com/questions/20969848/passcode-on-resume-from-background
 */
public class SecurityUtils {

    private static final String TAG = "SecurityUtils";

    static final String SECURITY = "security";
    static final String IS_LOGGED_IN = "is_logged_in";
    static final String CAN_MEASURE = "can_meassure";
    static final String USERNAME = "username";

    public static void setIsLoggedIn(Context context, boolean status) {
        context.getApplicationContext().getSharedPreferences(SECURITY, Context.MODE_PRIVATE).edit().putBoolean(IS_LOGGED_IN, status).commit();
    }

    public static boolean getIsLoggedIn(Context context) {
        return context.getApplicationContext().getSharedPreferences(SECURITY, Context.MODE_PRIVATE).getBoolean(IS_LOGGED_IN, true);
    }

    public static void setCanMeasure(Context context, boolean status) {
        context.getApplicationContext().getSharedPreferences(SECURITY, Context.MODE_PRIVATE).edit().putBoolean(CAN_MEASURE, status).commit();
    }

    public static boolean getCanMeasure(Context context) {
        return context.getApplicationContext().getSharedPreferences(SECURITY, Context.MODE_PRIVATE).getBoolean(CAN_MEASURE, false);
    }

    public static void setUsername(Context context, String username) {
        context.getApplicationContext().getSharedPreferences(SECURITY, Context.MODE_PRIVATE).edit().putString(USERNAME, username).commit();
    }

    public static String getUsername(Context context) {
        return context.getApplicationContext().getSharedPreferences(SECURITY, Context.MODE_PRIVATE).getString(USERNAME, "");
    }

    // checks whether security is okay
    public static boolean checkSecurity(Context context, boolean showMess) {
        boolean securityOkay = true;

        if (!SecurityUtils.getIsLoggedIn(context)) {
            // ensure user is logged in
            if (showMess) {
                AlertUtils.errorDialogShow(context, context.getString(R.string.sec_not_logged_in));
            }
            securityOkay = false;
        } else if (!SecurityUtils.getCanMeasure(context)) {
            // ensure user has access rights
            if (showMess) {
                AlertUtils.errorDialogShow(context, context.getString(R.string.sec_cannot_measure));
            }
            securityOkay = false;
        }

        return securityOkay;
    }
}