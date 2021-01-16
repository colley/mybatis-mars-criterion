/**
 * Copyright (C), 2018 store
 * Encoding: UTF-8
 * Date: 19-5-8 下午2:43
 * History:
 */
package com.mars.kit.exception;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

/**
 * ExceptionUtil.java
 * @author ColleyMa
 * @version 19-5-8 下午2:43
 */
public final class ExceptionUtil {
    public static void throwException(String e) throws ArchiveException {
        throw new ArchiveException(e);
    }
    
    public static void throwException(String format, Object... arguments) throws ArchiveException {
    	FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
        throw new ArchiveException(ft.getMessage(),ft.getThrowable());
    }
    

    public static void throwException(Throwable e) throws ArchiveException {
        throw new ArchiveException(e);
    }

    public static void throwException(String er, Throwable e)
        throws ArchiveException {
        throw new ArchiveException(er, e);
    }

    public static Throwable getRootCase(Throwable a) {
        Throwable r = null;

        for (r = a; r.getCause() != null; r = r.getCause()) {
            ;
        }

        return r;
    }

    public static String getExceptionStackTrace(Throwable e) {
        StringBuffer sb = new StringBuffer();
        sb.append(e);

        StackTraceElement[] trace = e.getStackTrace();

        for (int i = 0; i < trace.length; i++) {
            sb.append("\n " + trace[i]);
        }

        Throwable ourCause = e.getCause();

        if (ourCause != null) {
            getCauseStackTrace(sb, ourCause);
        }

        return sb.toString();
    }

    private static void getCauseStackTrace(StringBuffer sb, Throwable cause) {
        if (null != cause) {
            StackTraceElement[] trace = cause.getStackTrace();

            for (int i = 0; i < trace.length; i++) {
                sb.append("\n " + trace[i]);
            }

            Throwable ourCause = cause.getCause();

            if (ourCause != null) {
                getCauseStackTrace(sb, ourCause);
            }
        }
    }
}
