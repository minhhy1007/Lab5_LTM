package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ExchangeRateTable {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 2345;

        System.out.println("[CLIENT START] Đang kết nối tới Server " + host + ":" + port + "...");

        // ==========================================
        // 1. KẾT NỐI SERVER
        // ==========================================
        try (Socket socket = new Socket(host, port)) {
            System.out.println("[CONNECTED] Kết nối đến ExchangeRateServer thành công!");

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            int updateCount = 0;

            // ==========================================
            // 2. VÒNG LẶP CẬP NHẬT TỰ ĐỘNG MỖI 1 GIÂY
            // ==========================================
            while (true) {
                updateCount++;

                // A. Gửi Request
                String requestCommand = "GET_RATE";
                writer.println(requestCommand);

                // B. Nhận Response
                String response = reader.readLine();

                // Nếu Server ngắt kết nối (readLine() trả về null)
                if (response == null) {
                    System.out.println("\n[WARNING] Server đã đóng kết nối!");
                    break;
                }

                // C. Hiển thị thông tin
                System.out.println("\n=================================");
                System.out.println("   BẢNG TỶ GIÁ THỊ TRƯỜNG (Lần " + updateCount + ")");
                System.out.println("=================================");

                if (!response.startsWith("ERROR")) {
                    String[] parts = response.split(";");
                    String updateTime = "Chưa rõ";

                    for (String part : parts) {
                        String[] kv = part.split("=");
                        if (kv.length == 2) {
                            if ("Time".equals(kv[0])) {
                                updateTime = kv[1];
                            } else {
                                System.out.printf(" - Thị trường %-10s : %s\n", kv[0], kv[1]);
                            }
                        }
                    }
                    System.out.println("---------------------------------");
                    System.out.println("Mốc thời gian cập nhật : " + updateTime);
                    System.out.println("=================================");
                } else {
                    System.out.println("[ERROR] Lỗi nhận từ Server: " + response);
                }

                // D. Tạm dừng 1 giây (1000 miligiây) trước lần cập nhật tiếp theo
                Thread.sleep(1000);
            }

        } catch (InterruptedException e) {
            System.out.println("[CLIENT INFO] Luồng bị ngắt dừng chương trình.");
        } catch (IOException e) {
            System.err.println("[CLIENT ERROR] Lỗi kết nối tới Server: " + e.getMessage());
        }
    }
}
