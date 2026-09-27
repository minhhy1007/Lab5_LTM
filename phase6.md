# TÀI LIỆU TỔNG HỢP KIẾN THỨC PHASE 6: AUTO UPDATE MỖI 1 GIÂY
**Môn học:** Lập trình mạng với Java  
**Đề tài lớn:** Xây dựng chương trình ExchangeRate  
**Giai đoạn:** Phase 6 – Tự động gửi Request `GET_RATE` và cập nhật dữ liệu định kỳ mỗi 1 giây (Auto Update Loop).

---

## 1. GIẢI THÍCH KIẾN THỨC NỀN TẢNG

### 💡 Các khái niệm cốt lõi:

* **Vòng lặp (Loop - `while(true)`) là gì?**
  * Là cấu trúc điều khiển cho phép lặp đi lặp lại một khối lệnh không giới hạn số lần cho đến khi gặp điều kiện dừng (`break`) hoặc bị ngắt tiến trình.

* **`Thread.sleep(1000)` là gì?**
  * Là phương thức của lớp `java.lang.Thread` giúp tạm dừng luồng thực thi (thread) hiện tại của chương trình trong một khoảng thời gian tính bằng miligiây (milliseconds).
  * `Thread.sleep(1000)` tạm dừng đúng **1000ms = 1 giây**.

* **Tại sao cần có thời gian chờ (delay/sleep) giữa các lần Request?**
  * **Tránh quá tải CPU (CPU Spike):** Nếu chạy vòng lặp vô hạn `while(true)` không có `sleep()`, CPU của máy tính sẽ bị chiếm tới 100% công suất chỉ để liên tục gửi hàng triệu Request/giây.
  * **Tránh nghẽn băng thông mạng (Network Flooding/Spamming):** Gửi dữ liệu quá dồn dập khiến đường truyền mạng bị quá tải và Server không kịp xử lý.

* **`sleep()` ảnh hưởng Client như thế nào?**
  * Tạo nhịp điệu cập nhật dữ liệu **đều đặn, nhịp nhàng (1 giây/lần)** giúp người dùng theo dõi được sự biến đổi của tỷ giá thị trường một cách rõ ràng.

---

## 2. CẤU TRÚC PROJECT (GIỮ NGUYÊN)

```text
d:/Lap_Trinh_Mang/Lab5_LapTrinhMang/
│
├── phase1.md / phase2.md / phase3.md / phase4.md / phase5.md / phase6.md
│
├── server/
│   └── ExchangeRateServer.java    <-- Code Server Phase 6
│
└── client/
    └── ExchangeRateTable.java     <-- Code Client Phase 6
```

---

## 3. BẢN ĐỒ LUỒNG THỰC THI CLIENT (CODE MAP)

```text
ExchangeRateTable.java (Client)
│
├── 1. CONNECT SERVER  (Kết nối Socket tới localhost:2345)
│
└── 2. UPDATE LOOP     (Vòng lặp while(true))
    │
    ├── SEND REQUEST   (Gửi lệnh "GET_RATE\n")
    ├── RECEIVE        (Đọc chuỗi Response)
    ├── DISPLAY        (In Bảng tỷ giá + DateTime)
    └── SLEEP 1000ms   (Thread.sleep(1000) tạm dừng 1s)
    │
    └──────► REPEAT (Lặp lại bước 2)
```

---

## 4. TIẾN TRÌNH THỜI GIAN (TIMELINE DIAGRAM)

```text
t = 0s ───► Client gửi GET_RATE ───► Server gửi Tokyo, NY, HK, Time (16:15:00) ───► Sleep 1s
t = 1s ───► Client gửi GET_RATE ───► Server gửi Tokyo, NY, HK, Time (16:15:01) ───► Sleep 1s
t = 2s ───► Client gửi GET_RATE ───► Server gửi Tokyo, NY, HK, Time (16:15:02) ───► Sleep 1s
t = 3s ───► Client gửi GET_RATE ───► Server gửi Tokyo, NY, HK, Time (16:15:03) ───► Sleep 1s
...
```

---

## 5 & 6. MÃ NGUỒN CHƯƠNG TRÌNH PHASE 6

### 📄 1. File Server: `server/ExchangeRateServer.java`

```java
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
```

---

### 📄 2. File Client: `client/ExchangeRateTable.java`

```java
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
```

---

## 7. XỬ LÝ CÁC TÌNH HUỐNG LỖI MẠNG (FAILURE MODES)

1. **Tình huống 1: Server bị tắt đột ngột giữa chừng**
   * Phía Client: Phương thức `reader.readLine()` sẽ nhận giá trị `null` hoặc ném `SocketException: Connection reset`. Vòng lặp `while(true)` phát hiện `response == null` và thoát an toàn.
2. **Tình huống 2: Client ngắt kết nối / Tắt cửa sổ Client**
   * Phía Server: Vòng lặp `while ((request = reader.readLine()) != null)` sẽ kết thúc vì `readLine()` nhận `null`. Server thoát vòng lặp và gọi `socket.close()` giải phóng cổng.
3. **Tình huống 3: Client muốn dừng chương trình**
   * Người dùng nhấn `Ctrl + C` hoặc nút **Stop** trên IDE. Luồng thực thi bị chấm dứt, Socket được tự động dọn dẹp nhờ `try-with-resources`.

---

## 8. BẢNG KỊCH BẢN KIỂM THỬ (TEST CASES)

| Test ID | Thao tác | Kịch bản kiểm thử | Kết quả mong đợi |
| :--- | :--- | :--- | :--- |
| **TC01** | Quan sát lần 1 đến lần 5 | Để Client chạy liên tục trong 5 giây. | Màn hình in ra 5 Bảng tỷ giá (Lần 1, Lần 2, Lần 3, Lần 4, Lần 5). |
| **TC02** | Kiểm tra nhịp thời gian | Quan sát chuỗi `Time` giữa các lần. | Giây tăng dần đều đặn (+1 giây ở mỗi lần in). |
| **TC03** | Kiểm tra sự biến động tỷ giá | Quan sát số liệu Tokyo/NY/HK. | Tỷ giá có sự thay đổi giữa các lần cập nhật. |
| **TC04** | Tắt Server giữa chừng | Đóng cửa sổ Server khi Client đang chạy. | Client in thông báo `[WARNING] Server đã đóng kết nối!` và ngắt vòng lặp an toàn. |

---

## 9. CÂU HỎI TỰ KIỂM TRA PHASE 6 (15 CÂU HỎI)

> **Lưu ý:** Bạn hãy đọc kỹ và tự trả lời 15 câu hỏi này ra khung chat để tôi hỗ trợ chấm bài nhé!

1. **[Dễ]** 1000 miligiây (ms) bằng bao nhiêu giây?
2. **[Dễ]** Phương thức nào trong Java được sử dụng để cho luồng chương trình tạm dừng trong 1 giây?
3. **[Dễ]** Vòng lặp nào được sử dụng trong `ExchangeRateTable.java` để thực hiện việc gửi Request liên tục?
4. **[Trung bình]** Điều gì sẽ xảy ra với CPU của máy tính nếu chúng ta bỏ dòng `Thread.sleep(1000)` trong vòng lặp `while(true)` của Client?
5. **[Trung bình]** Khái niệm "Network Flooding" hay "Spamming" xảy ra khi nào trong lập trình Socket?
6. **[Trung bình]** Tại sao Server cũng cần một vòng lặp `while ((request = reader.readLine()) != null)` ở Phase 6?
7. **[Trung bình]** Khi Client ngắt kết nối, hàm `reader.readLine()` ở phía Server sẽ trả về giá trị gì?
8. **[Trung bình]** Ngoại lệ `InterruptedException` xảy ra khi nào khi gọi `Thread.sleep()`?
9. **[Khó]** Sự khác biệt giữa việc mở lại `new Socket()` mỗi 1 giây và việc giữ nguyên 1 `Socket` duy nhất để lặp lại việc gửi Request là gì? Phương pháp nào tối ưu hơn về mặt hiệu năng mạng?
10. **[Khó]** Làm thế nào Client biết được Server đã đóng kết nối để thoát khỏi vòng lặp `while(true)`?
11. **[Khó]** Nếu mạng bị trễ (latency 200ms), tổng thời gian của một chu kỳ cập nhật (gửi + nhận + hiển thị + sleep 1s) sẽ là bao nhiêu?
12. **[Khó]** Trong thực tế, làm thế nào để người dùng bấm một nút "Stop" trên giao diện để dừng vòng lặp cập nhật 1s một cách an toàn mà không làm treo ứng dụng?
13. **[Khó]** Việc sử dụng `Thread.sleep()` trên luồng chính (Main Thread) có nhược điểm gì nếu phát triển giao diện đồ họa GUI sau này?
14. **[Khó]** Giải pháp thay thế `Thread.sleep()` chuyên nghiệp hơn trong Java để lên lịch cập nhật định kỳ 1s là gì (`ScheduledExecutorService`, `Timer`,...)?
15. **[Khó]** Tóm tắt toàn bộ luồng dữ liệu 1 chu kỳ cập nhật ở Phase 6 từ lúc Client gửi Request cho đến khi quay lại bước chờ.

---

## 10. CHECKLIST HOÀN THÀNH PHASE 6

- [ ] Hiểu cơ chế vòng lặp `while(true)`
- [ ] Hiểu tác dụng của `Thread.sleep(1000)`
- [ ] Quy đổi thành thạo 1000ms = 1s
- [ ] Client tự động gửi `GET_RATE` định kỳ mỗi 1s
- [ ] Server phản hồi liên tục dữ liệu tỷ giá mới
- [ ] Mốc thời gian `Time` nhảy số giây chính xác (+1s)
- [ ] Tỷ giá biến đổi linh hoạt qua các lần cập nhật
- [ ] Xử lý an toàn khi Server hoặc Client ngắt kết nối
- [ ] Test thành công ít nhất 5 chu kỳ cập nhật liên tục

---

📁 Tài liệu tổng hợp kiến thức Phase 6 chi tiết đã được tạo tại file **[phase6.md](file:///d:/Lap_Trinh_Mang/Lab5_LapTrinhMang/phase6.md)**.
