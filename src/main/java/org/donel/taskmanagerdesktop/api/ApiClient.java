package org.donel.taskmanagerdesktop.api;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.donel.taskmanagerdesktop.services.ApiException;
import org.donel.taskmanagerdesktop.services.Session;

public class ApiClient {
    private static final String DEFAULT_BASE_URL = "http://localhost:8080";

    private final HttpClient httpClient;
    private final String baseUrl;

    public ApiClient() {
        this(DEFAULT_BASE_URL);
    }

    public ApiClient(String baseUrl) {
        this.httpClient = HttpClient.newHttpClient();
        this.baseUrl = baseUrl;
    }

    private void addAuthorizationHeader(HttpRequest.Builder builder) {
        String token = Session.getInstance().getAuthToken();
        if (token != null && !token.isBlank()) {
            builder.header("Authorization", "Bearer " + token);
        }
    }

    public <T> T get(String path, Class<T> responseType) throws IOException, InterruptedException, ApiException {
        return execute(path, "GET", null, responseType);
    }

    public <T> T post(String path, Object requestBody, Class<T> responseType) throws IOException, InterruptedException, ApiException {
        return execute(path, "POST", requestBody, responseType);
    }

    public <T> T put(String path, Object requestBody, Class<T> responseType) throws IOException, InterruptedException, ApiException {
        return execute(path, "PUT", requestBody, responseType);
    }

    public <T> T patch(String path, Object requestBody, Class<T> responseType) throws IOException, InterruptedException, ApiException {
        return execute(path, "PATCH", requestBody, responseType);
    }

    public <T> T uploadFile(String path, String fileName, String mimeType, byte[] fileBytes, Class<T> responseType)
            throws IOException, InterruptedException, ApiException {
        String boundary = "----TaskManagerDesktopBoundary" + System.nanoTime();
        byte[] multipartBody = buildMultipartBody(fileName, mimeType, fileBytes, boundary);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Accept", "application/json")
                .header("Content-Type", "multipart/form-data; boundary=" + boundary);

        addAuthorizationHeader(builder);

        builder.POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody));
        HttpRequest request = builder.build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            String errorMsg = "HTTP " + response.statusCode() + " for " + path;
            if (response.body() != null && !response.body().isBlank()) {
                errorMsg += ": " + response.body();
            }
            throw new ApiException(errorMsg);
        }

        String body = response.body();
        if (body == null || body.isBlank()) {
            return null;
        }

        return parseResponse(body, responseType);
    }

    private <T> T execute(String path, String method, Object requestBody, Class<T> responseType)
            throws IOException, InterruptedException, ApiException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json");

        addAuthorizationHeader(builder);

        if (requestBody != null) {
            builder.method(method, HttpRequest.BodyPublishers.ofString(bodyToJson(requestBody)));
        } else if ("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method)) {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            builder.GET();
        }

        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            String errorMsg = "HTTP " + response.statusCode() + " for " + path;
            if (response.body() != null && !response.body().isBlank()) {
                errorMsg += ": " + response.body();
            }
            throw new ApiException(errorMsg);
        }

        String body = response.body();
        if (body == null || body.isBlank()) {
            return null;
        }

        return parseResponse(body, responseType);
    }

    private <T> T parseResponse(String body, Class<T> responseType) {
        if (responseType.isArray()) {
            String trimmed = body.trim();
            if (trimmed.length() < 2 || trimmed.charAt(0) != '[' || trimmed.charAt(trimmed.length() - 1) != ']') {
                return responseType.cast(Array.newInstance(responseType.getComponentType(), 0));
            }

            String inner = trimmed.substring(1, trimmed.length() - 1).trim();
            if (inner.isEmpty()) {
                return responseType.cast(Array.newInstance(responseType.getComponentType(), 0));
            }

            String[] items = splitTopLevelObjects(inner);
            Object array = Array.newInstance(responseType.getComponentType(), items.length);
            for (int i = 0; i < items.length; i++) {
                Array.set(array, i, parseResponse(items[i], responseType.getComponentType()));
            }
            return responseType.cast(array);
        }

        if (responseType.isRecord()) {
            String trimmed = body.trim();
            if (trimmed.length() < 2 || trimmed.charAt(0) != '{' || trimmed.charAt(trimmed.length() - 1) != '}') {
                return null;
            }

            String inner = trimmed.substring(1, trimmed.length() - 1).trim();
            if (inner.isEmpty()) {
                return null;
            }

            String[] fields = splitTopLevelObjects(inner);
            RecordComponent[] components = responseType.getRecordComponents();
            Object[] args = new Object[components.length];
            for (int i = 0; i < components.length; i++) {
                RecordComponent component = components[i];
                Class<?> componentType = component.getType();
                String fieldName = component.getName();
                String fieldValue = findFieldValue(fields, fieldName);
                args[i] = parseValue(fieldValue, componentType);
            }

            try {
                Constructor<T> ctor = responseType.getDeclaredConstructor(componentTypes(components));
                ctor.setAccessible(true);
                return ctor.newInstance(args);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Unable to instantiate response record " + responseType.getName(), e);
            }
        }

        if (responseType == String.class) {
            return responseType.cast(unquote(body.trim()));
        }
        if (responseType == Long.class || responseType == long.class) {
            return responseType.cast(Long.parseLong(body.trim()));
        }
        if (responseType == Integer.class || responseType == int.class) {
            return responseType.cast(Integer.parseInt(body.trim()));
        }
        if (responseType == Boolean.class || responseType == boolean.class) {
            return responseType.cast(Boolean.parseBoolean(body.trim()));
        }

        return null;
    }

    private String[] splitTopLevelObjects(String inner) {
        int depth = 0;
        int start = 0;
        java.util.List<String> parts = new java.util.ArrayList<>();
        for (int i = 0; i < inner.length(); i++) {
            char ch = inner.charAt(i);
            if (ch == '{' || ch == '[') {
                depth++;
            } else if (ch == '}' || ch == ']') {
                depth--;
            } else if (ch == ',' && depth == 0) {
                parts.add(inner.substring(start, i).trim());
                start = i + 1;
            }
        }
        if (start < inner.length()) {
            parts.add(inner.substring(start).trim());
        }
        return parts.toArray(String[]::new);
    }

    private String findFieldValue(String[] fields, String fieldName) {
        for (String field : fields) {
            if (field.startsWith("\"" + fieldName + "\":")) {
                return field.substring(("\"" + fieldName + "\":").length()).trim();
            }
        }
        return null;
    }

    private Object parseValue(String raw, Class<?> type) {
        if (raw == null || raw.equals("null")) {
            return null;
        }
        if (type == String.class) {
            return unquote(raw);
        }
        if (type == long.class || type == Long.class) {
            return Long.parseLong(raw);
        }
        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(raw);
        }
        if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(raw);
        }
        if (type.isEnum()) {
            return Enum.valueOf((Class<? extends Enum>) type, raw.replace("\"", ""));
        }
        if (type.isRecord()) {
            return parseResponse(raw, type);
        }
        return null;
    }

    private Class<?>[] componentTypes(RecordComponent[] components) {
        Class<?>[] types = new Class<?>[components.length];
        for (int i = 0; i < components.length; i++) {
            types[i] = components[i].getType();
        }
        return types;
    }

    private String unquote(String raw) {
        if (raw.length() >= 2 && raw.charAt(0) == '"' && raw.charAt(raw.length() - 1) == '"') {
            return raw.substring(1, raw.length() - 1).replace("\\\"", "\"").replace("\\\\", "\\");
        }
        return raw;
    }

    private byte[] buildMultipartBody(String fileName, String mimeType, byte[] fileBytes, String boundary) {
        String boundaryLine = "--" + boundary + "\r\n";
        String closingBoundary = "\r\n--" + boundary + "--\r\n";

        StringBuilder builder = new StringBuilder();
        builder.append(boundaryLine)
                .append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                .append(fileName)
                .append("\"\r\n")
                .append("Content-Type: ")
                .append(mimeType)
                .append("\r\n\r\n");

        byte[] headerBytes = builder.toString().getBytes(StandardCharsets.UTF_8);
        byte[] footerBytes = closingBoundary.getBytes(StandardCharsets.UTF_8);

        byte[] finalBody = new byte[headerBytes.length + fileBytes.length + footerBytes.length];
        System.arraycopy(headerBytes, 0, finalBody, 0, headerBytes.length);
        System.arraycopy(fileBytes, 0, finalBody, headerBytes.length, fileBytes.length);
        System.arraycopy(footerBytes, 0, finalBody, headerBytes.length + fileBytes.length, footerBytes.length);
        return finalBody;
    }

    private String bodyToJson(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String string) {
            return "\"" + string.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof Enum<?> enumValue) {
            return "\"" + enumValue.name() + "\"";
        }
        if (value.getClass().isRecord()) {
            StringBuilder json = new StringBuilder();
            json.append('{');
            RecordComponent[] components = value.getClass().getRecordComponents();
            for (int i = 0; i < components.length; i++) {
                RecordComponent component = components[i];
                String name = component.getName();
                Object componentValue;
                try {
                    componentValue = component.getAccessor().invoke(value);
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException("Unable to serialize record component: " + name, e);
                }
                if (i > 0) {
                    json.append(',');
                }
                json.append('"').append(name).append('"').append(':').append(bodyToJson(componentValue));
            }
            json.append('}');
            return json.toString();
        }
        return "{}";
    }
}
