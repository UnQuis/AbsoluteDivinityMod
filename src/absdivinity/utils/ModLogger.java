import java.util.logging.Level;
import java.util.logging.Logger;

public class ModLogger {
    private static final Logger LOGGER = Logger.getLogger(ModLogger.class.getName());

    public static void info(String message) {
        LOGGER.log(Level.INFO, message);
    }

    public static void warn(String message) {
        LOGGER.log(Level.WARNING, message);
    }

    public static void error(String message) {
        LOGGER.log(Level.SEVERE, message);
    }

    public static void debug(String message) {
        LOGGER.log(Level.FINE, message);
    }
}