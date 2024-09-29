package dev.cassis2310.falloutmc.util.exceptions;

import com.machinezoo.noexception.throwing.ThrowingRunnable;
import com.machinezoo.noexception.throwing.ThrowingSupplier;

public class ExceptionHandlingHelpers {
    /**
     * Executes the specified action without checking for exceptions, suppressing any Throwables that occur.
     * Use with caution, as this can lead to unexpected behavior if not properly handled.
     *
     * @param action the action to execute without checks
     * @param <T>    the return type of the action
     * @return the result of the action, or null if an exception occurs
     */
    @SuppressWarnings("unchecked")
    public static <T> T uncheck(ThrowingSupplier<?> action) {
        try {
            return (T) action.get();
        } catch (Throwable e) {
            return throwAsUnchecked(e);
        }
    }

    /**
     * Executes the specified action without checking for exceptions, suppressing any Throwables that occur.
     * Use with caution, as this can lead to unexpected behavior if not properly handled.
     *
     * @param action the action to execute without checks
     */
    public static void uncheck(ThrowingRunnable action) {
        try {
            action.run();
        } catch (Throwable e) {
            throwAsUnchecked(e);
        }
    }

    /**
     * Throws an exception as an unchecked exception.
     * This method is useful for rethrowing checked exceptions in places where only unchecked exceptions are allowed.
     *
     * @param exception The exception to throw.
     * @param <E>       The type of the exception.
     * @param <T>       A generic return type to facilitate its use in various contexts.
     * @throws E The exception being thrown.
     */
    @SuppressWarnings("unchecked")
    public static <E extends Throwable, T> T throwAsUnchecked(Throwable exception) throws E {
        throw (E) exception;
    }
}
