package one.armelin.bpjlm.utils;

import java.util.HashMap;
import java.util.Map;

public class MessageFormatter {

    /**
     * Formats a message by replacing {parameter} with provided values
     * @param message Message with placeholders in {parameter} format
     * @param params Map with parameter values
     * @return Formatted message
     */
    public static String format(String message, Map<String, Object> params) {
        if (message == null || params == null) {
            return message;
        }

        String result = message;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, value);
        }

        return result;
    }

    // Overload to accept key-value pairs directly
    public static String format(String message, Object... params) {
        if (params.length % 2 != 0) {
            throw new IllegalArgumentException("Parameters must be in pairs (key, value)");
        }

        Map<String, Object> paramMap = new HashMap<>();
        for (int i = 0; i < params.length; i += 2) {
            paramMap.put(params[i].toString(), params[i + 1]);
        }

        return format(message, paramMap);
    }
}
