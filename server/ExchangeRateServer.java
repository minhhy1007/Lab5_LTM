package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class ExchangeRateServer {
    public static void main(String[] args) {
        int port = 2345;
        Random random = new Random();

        // ==========================================
        // 1. KHỞI ĐỘNG SERVER
        // ==========================================
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[SERVER START] ExchangeRateServer đang lắng nghe tại cổng " + port + "...");

            // ==========================================
            // 2. CHẤP NHẬN KẾT NỐI CLIENT
            // ==========================================
            Socket socket = serverSocket.accept();
            System.out.println("[CLIENT CONNECTED] Client đã kết nối từ địa chỉ: " + socket.getInetAddress());

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            // ==========================================
            // 3. VÒNG LẶP XỬ LÝ NHIỀU REQUEST TỪ CLIENT
            // ==========================================
            String request;
            while ((request = reader.readLine()) != null) {
                System.out.println("[RECEIVE REQUEST] Đã nhận lệnh từ Client: " + request);

                String response;
                if ("GET_RATE".equals(request)) {
                    // Sinh tỷ giá giả lập cho 3 thị trường
                    double tokyoRate = 150.0 + (160.0 - 150.0) * random.nextDouble();
                    double newYorkRate = 190.0 + (200.0 - 190.0) * random.nextDouble();
                    double hongKongRate = 170.0 + (180.0 - 170.0) * random.nextDouble();

                    // Lấy ngày giờ hệ thống mới nhất
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    String formattedDateTime = now.format(formatter);

                    // Đóng gói Response
                    response = String.format("Tokyo=%.2f;NewYork=%.2f;HongKong=%.2f;Time=%s",
                                              tokyoRate, newYorkRate, hongKongRate, formattedDateTime);
                } else {
                    response = "ERROR: Unknown Command";
                }

                // Gửi phản hồi về cho Client
                writer.println(response);
                System.out.println("[SEND RESPONSE] Đã phản hồi tới Client: " + response);
            }

            // Đóng socket khi Client ngắt kết nối
            socket.close();
            System.out.println("[SERVER END] Client đã đóng kết nối. Server kết thúc an toàn.");

        } catch (IOException e) {
            System.err.println("[SERVER ERROR] Lỗi truyền thông hoặc Client ngắt kết nối: " + e.getMessage());
        }
    }
}
