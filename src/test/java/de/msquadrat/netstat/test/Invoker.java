package de.msquadrat.netstat.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

public class Invoker {
    public static final Object invokeMethodOn(final Object obj, final String name, final Class<?>[] parameterTypes, final Object... args) {    
        try {
            Method method;
            method = obj.getClass().getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);
            return method.invoke(obj, args); 
        }
        catch (InvocationTargetException e) {
            // Unwrap the exception
            try {
                Throwable cause = e.getCause();
                if (cause == null) throw e;
                throw cause;
            }
            catch (RuntimeException | Error cause) {
                throw cause;
            }
            catch (Throwable cause) {
                throw new UndeclaredThrowableException(cause);
            }
        }
        catch (ReflectiveOperationException e) {
            throw new UndeclaredThrowableException(e, e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
