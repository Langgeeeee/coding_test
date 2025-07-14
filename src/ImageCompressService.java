import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageCompressService {

    private static final ConcurrentHashMap<String, ImageRecord> imageStore = new ConcurrentHashMap<String, ImageRecord>();
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws IOException {

        // This class implements a simple HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // handle image upload and compression functions
        server.createContext("/api/upload", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("POST".equals(exchange.getRequestMethod())) {
                    threadPool.submit(new Runnable() {
                        @Override
                        public void run() {
                            handleImageUpload(exchange);
                        }
                    });
                } else {
                    sendResponse(exchange, 405, "Method not allowed, please use POST");
                }
            }
        });

        // handle image compress history
        server.createContext("/api/history", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("GET".equals(exchange.getRequestMethod())) {
                    handleHistoryRequest(exchange);
                } else {
                    sendResponse(exchange, 405, "Method not allowed, please use GET");
                }
            }
        });

        server.setExecutor(threadPool);
        server.start();
        System.out.println("Server started successfully......");
    }

    private static void handleImageUpload(HttpExchange exchange) {
        try {
            // Parse multipart form data
            System.out.println("------handleImageUpload------");
            System.out.println(exchange);
            String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
            System.out.println(contentType);
            if (contentType == null || !contentType.startsWith("multipart/form-data")) {
                sendResponse(exchange, 400, "Content-Type must be multipart/form-data");
                return;
            }

            InputStream requestBody = exchange.getRequestBody();
            System.out.println(requestBody);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int readLength;
            while ((readLength = requestBody.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, readLength);
            }
            buffer.flush();
            byte[] requestData = buffer.toByteArray();

            // Extract image
            byte[] imageData = extractImageFromMultipart(requestData, contentType);
            if (imageData == null) {
                sendResponse(exchange, 400, "No image found in the request");
                return;
            }

            // Compress image
            byte[] compressedImage = compressImage(imageData);
            if (compressedImage == null) {
                sendResponse(exchange, 500, "Failed to process image");
                return;
            }

            // if the image is compressed successfully， store the compressed record
            String imageId = UUID.randomUUID().toString();
            long timestamp = System.currentTimeMillis();
            imageStore.put(imageId, new ImageCompressService.ImageRecord(imageId, imageData.length, compressedImage.length, timestamp));

            // Return compressed image
            exchange.getResponseHeaders().set("Content-Type", "image/jpeg");
            exchange.sendResponseHeaders(200, compressedImage.length);

            OutputStream os = exchange.getResponseBody();
            os.write(compressedImage);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                sendResponse(exchange, 500, "Internal Server Error");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] responseBytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
    }

    private static byte[] extractImageFromMultipart(byte[] data, String contentType) throws IOException {

        System.out.println("data-------------------");
        // [B@77db0d8a
        System.out.println(data);
        System.out.println(contentType);
        // multipart/form-data; boundary=------------------------0XfvVfudasdOip7Xt6Ocdz
        String boundary = contentType.split("=")[1];
        System.out.println(boundary);
        System.out.println("--" + boundary);
        byte[] boundaryBytes = ("--" + boundary).getBytes(StandardCharsets.UTF_8);
        System.out.println(boundaryBytes);

        // Find the start of the image data
        int startIndex = indexOf(data, boundaryBytes, 0) + boundaryBytes.length;
        if (startIndex < boundaryBytes.length) return null;
        System.out.println();

        // Skip http headers
        int headerEnd = indexOf(data, "\r\n\r\n".getBytes(StandardCharsets.UTF_8), startIndex);
        if (headerEnd == -1) return null;
        int imageStart = headerEnd + 4;

        // Find the end of the image data
        int endIndex = indexOf(data, boundaryBytes, imageStart);
        if (endIndex == -1) return null;

        // Extract image bytes
        byte[] imageData = new byte[endIndex - imageStart - 2]; // -2 for the \r\n before boundary
        System.arraycopy(data, imageStart, imageData, 0, imageData.length);
        return imageData;
    }

    private static int indexOf(byte[] array, byte[] target, int startIndex) {
        outer:
        for (int i = startIndex; i < array.length - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    private static byte[] compressImage(byte[] inputImage) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(inputImage);
            BufferedImage image = ImageIO.read(bis);

            // Simple compression: resize to half dimensions
            int newWidth = image.getWidth() / 2;
            int newHeight = image.getHeight() / 2;
            if (newWidth < 1) newWidth = 1;
            if (newHeight < 1) newHeight = 1;

            BufferedImage compressedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = compressedImage.createGraphics();
            g.drawImage(image, 0, 0, newWidth, newHeight, null);
            g.dispose();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(compressedImage, "jpg", bos);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static class ImageRecord {
        String id;
        int originalSize;
        int compressedSize;
        long timestamp;

        ImageRecord(String id, int originalSize, int compressedSize, long timestamp) {
            this.id = id;
            this.originalSize = originalSize;
            this.compressedSize = compressedSize;
            this.timestamp = timestamp;
        }
    }

    private static void handleHistoryRequest(HttpExchange exchange) throws IOException {
        List<Map<String, Object>> history = new ArrayList<>();
        for (ImageCompressService.ImageRecord record : imageStore.values()) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("id", record.id);
            entry.put("timestamp", record.timestamp);
            entry.put("originalSize", record.originalSize);
            entry.put("compressedSize", record.compressedSize);
            history.add(entry);
        }

        // Sort by timestamp, Decending
        history.sort(new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> a, Map<String, Object> b) {
                return Long.compare(
                        (Long) b.get("timestamp"),
                        (Long) a.get("timestamp")
                );
            }
        });

        String response = toJson(history);
        sendResponse(exchange, 200, response);
    }

    private static String toJson(List<Map<String, Object>> data) {
        StringBuilder sb = new StringBuilder("[");
        for (Map<String, Object> entry : data) {
            sb.append("{");
            sb.append("\"id\":\"").append(entry.get("id")).append("\",");
//            sb.append("\"timestamp\":").append(entry.get("timestamp")).append(",");
            sb.append("\"originalSize\":").append(entry.get("originalSize")).append(",");
            sb.append("\"compressedSize\":").append(entry.get("compressedSize")).append(",");
            sb.append("\"timestamp\":").append(entry.get("timestamp"));
            sb.append("},");
        }
        if (!data.isEmpty()) {
            // delete last ','
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
        return sb.toString();
    }
}