# TÀI LIỆU TỔNG HỢP KIẾN THỨC PHASE 5: GHÉP CHƯƠNG TRÌNH HOÀN CHỈNH
**Môn học:** Lập trình mạng với Java  
**Đề tài lớn:** Xây dựng chương trình ExchangeRate  
**Giai đoạn:** Phase 5 – Ghép nối hoàn chỉnh TCP Connection + Request/Response (`GET_RATE`) + Random (Tokyo, New York, Hong Kong) + DateTime.

---

## 1. GIỚI HẠN PHASE 5

### ⛔ CHƯA ĐƯỢC LÀM TRONG PHASE NÀY:
* Vòng lặp tự động cập nhật dữ liệu mỗi 1 giây (Auto Update)
* Giao diện đồ họa GUI (Swing / JavaFX)
* Lưu trữ CSDL (Database)
* Phục vụ nhiều Client đồng thời (Multithreading / ThreadPool)

---

## 2. CẤU TRÚC PROJECT VÀ BẢN ĐỒ CODE (CODE MAP)

```text
d:/Lap_Trinh_Mang/Lab5_LapTrinhMang/
│
├── phase1.md / phase2.md / phase3.md / phase4.md / phase5.md
│
├── server/
│   └── ExchangeRateServer.java    <-- Mã nguồn Server hoàn chỉnh Phase 5
│
└── client/
    └── ExchangeRateTable.java     <-- Mã nguồn Client hoàn chỉnh Phase 5
```

### 🗺️ Bản đồ Code chi tiết:

#### **SERVER: `ExchangeRateServer.java`**
* **1. TCP Server:** Khởi tạo `ServerSocket(2345)` lắng nghe mạng.
* **2. Client Connection:** Gọi `accept()` chấp nhận Client kết nối.
* **3. Receive Request:** Đọc câu lệnh từ Client qua `BufferedReader`.
* **4. Exchange Rate:** Tạo đối tượng `Random` và sinh giá trị cho **Tokyo**, **New York**, **Hong Kong**.
* **5. DateTime:** Lấy mốc thời gian `LocalDateTime.now()` và format `dd/MM/yyyy HH:mm:ss`.
* **6. Build Response:** Đóng gói chuỗi `"Tokyo=...;NewYork=...;HongKong=...;Time=..."`.
* **7. Send Response:** Đẩy chuỗi qua `PrintWriter` gửi về cho Client.

#### **CLIENT: `ExchangeRateTable.java`**
* **1. TCP Connection:** Khởi tạo `Socket("localhost", 2345)` nối tới Server.
* **2. Send Request:** Gửi chuỗi lệnh `"GET_RATE"` qua `PrintWriter`.
* **3. Receive Response:** Hứng chuỗi phản hồi bằng `BufferedReader`.
* **4. Display:** Tách chuỗi theo dấu `;` và `=`, hiển thị Bảng tỷ giá 3 thị trường cùng mốc thời gian cập nhật.

---

## 3. SƠ ĐỒ LUỒNG THỰC THI (SEQUENCE DIAGRAM)

```text
CLIENT                                              SERVER
(ExchangeRateTable.java)                      (ExchangeRateServer.java)
       │                                               │
       │ ──── 1. Khởi tạo TCP Connection (Port 2345) ─> │
       │                                               │
       │ ──── 2. Gửi Request command "GET_RATE\n" ───> │
       │                                               │ (Server xử lý:
       │                                               │  - Random Tokyo, NY, HK
       │                                               │  - Lấy LocalDateTime.now()
       │                                               │  - Format & Build Response)
       │                                               │
       │ <─── 3. Trả về Response chuỗi Tỷ giá + Time ─ │
       │                                               │
       ▼                                               ▼
 [Tách chuỗi & In Bảng Tỷ Giá]                [Đóng Socket an toàn]
```

---

## 4 & 5. MÃ NGUỒN CHƯƠNG TRÌNH HOÀN CHỈNH

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
        // 1. TCP SERVER
        // ==========================================
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[SERVER START] ExchangeRateServer đang lắng nghe tại cổng " + port + "...");

            // ==========================================
            // 2. CLIENT CONNECTION
            // ==========================================
            Socket socket = serverSocket.accept();
            System.out.println("[CLIENT CONNECTED] Client đã kết nối từ địa chỉ: " + socket.getInetAddress());

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            // ==========================================
            // 3. RECEIVE REQUEST
            // ==========================================
            String request = reader.readLine();
            System.out.println("[RECEIVE REQUEST] Đã nhận lệnh từ Client: " + request);

            String response;
            if ("GET_RATE".equals(request)) {
                // ==========================================
                // 4. EXCHANGE RATE (TOKYO, NEW YORK, HONG KONG)
                // ==========================================
                double tokyoRate = 150.0 + (160.0 - 150.0) * random.nextDouble();
                double newYorkRate = 190.0 + (200.0 - 190.0) * random.nextDouble();
                double hongKongRate = 170.0 + (180.0 - 170.0) * random.nextDouble();

                // ==========================================
                // 5. DATETIME
                // ==========================================
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                String formattedDateTime = now.format(formatter);

                // ==========================================
                // 6. BUILD RESPONSE
                // ==========================================
                response = String.format("Tokyo=%.2f;NewYork=%.2f;HongKong=%.2f;Time=%s",
                                          tokyoRate, newYorkRate, hongKongRate, formattedDateTime);
            } else {
                response = "ERROR: Unknown Command";
            }

            // ==========================================
            // 7. SEND RESPONSE
            // ==========================================
            writer.println(response);
            System.out.println("[SEND RESPONSE] Đã phản hồi dữ liệu tới Client: " + response);

            // Đóng kết nối socket
            socket.close();
            System.out.println("[SERVER END] Đã đóng kết nối với Client an toàn.");

        } catch (IOException e) {
            System.err.println("[SERVER ERROR] " + e.getMessage());
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
        // 1. TCP CONNECTION
        // ==========================================
        try (Socket socket = new Socket(host, port)) {
            System.out.println("[CONNECTED] Kết nối đến ExchangeRateServer thành công!");

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            // ==========================================
            // 2. SEND REQUEST
            // ==========================================
            String requestCommand = "GET_RATE";
            writer.println(requestCommand);
            System.out.println("[SEND REQUEST] Đã gửi lệnh: " + requestCommand);

            // ==========================================
            // 3. RECEIVE RESPONSE
            // ==========================================
            String response = reader.readLine();

            // ==========================================
            // 4. DISPLAY
            // ==========================================
            System.out.println("\n=================================");
            System.out.println("   BẢNG TỶ GIÁ THỊ TRƯỜNG CẬP NHẬT");
            System.out.println("=================================");
            System.out.println("Dữ liệu gốc (Raw Data): " + response);

            if (response != null && !response.startsWith("ERROR")) {
                String[] parts = response.split(";");
                String updateTime = "Chưa rõ";

                System.out.println("\n[CHI TIẾT TỶ GIÁ]");
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
                System.out.println("=================================\n");
            } else {
                System.out.println("[ERROR] Lỗi nhận từ Server: " + response);
            }

            System.out.println("[CLIENT END] Hoàn thành phiên giao tiếp.");

        } catch (IOException e) {
            System.err.println("[CLIENT ERROR] Không thể kết nối tới Server: " + e.getMessage());
        }
    }
}
```

---

## 5. GIẢI THÍCH CHI TIẾT 9 BƯỚC CỦA TOÀN BỘ LUỒNG

1. **Bước 1: Client kết nối** -> Client khởi tạo `new Socket("localhost", 2345)` để phát tín hiệu kết nối TCP tới Server.
2. **Bước 2: Client gửi `GET_RATE`** -> Client ghi chuỗi `"GET_RATE\n"` vào `OutputStream` đẩy qua đường dẫn mạng.
3. **Bước 3: Server nhận lệnh** -> Hàm `reader.readLine()` ở Server nhận được chuỗi `"GET_RATE"`.
4. **Bước 4: Server sinh tỷ giá** -> Server gọi `random.nextDouble()` tính toán giá ngẫu nhiên cho 3 thị trường Tokyo, New York, Hong Kong.
5. **Bước 5: Server lấy thời gian** -> Server gọi `LocalDateTime.now()` và định dạng thành chuỗi `dd/MM/yyyy HH:mm:ss`.
6. **Bước 6: Server tạo Response** -> Ghép chuỗi tỷ giá + thời gian theo cấu trúc `Tokyo=...;NewYork=...;HongKong=...;Time=...`.
7. **Bước 7: Server gửi Response** -> Ghi chuỗi Response ra `OutputStream` phản hồi cho Client.
8. **Bước 8: Client nhận dữ liệu** -> Client gọi `reader.readLine()` để lấy dòng chữ kết quả từ Server.
9. **Bước 9: Client hiển thị** -> Client dùng `split()` tách thông tin và in Bảng Tỷ Giá đẹp mắt ra Console.

---

## 6. BẢNG MÃ NGUỒN (CODE MAP TABLE)

| Chức năng | File | Khu vực code | Kiến thức sử dụng |
| :--- | :--- | :--- | :--- |
| **Lắng nghe kết nối** | `ExchangeRateServer.java` | Khu vực 1 | `ServerSocket`, `port 2345` |
| **Chấp nhận Client** | `ExchangeRateServer.java` | Khu vực 2 | `accept()`, `Socket` |
| **Nhận Request** | `ExchangeRateServer.java` | Khu vực 3 | `BufferedReader`, `readLine()` |
| **Sinh tỷ giá ngẫu nhiên**| `ExchangeRateServer.java` | Khu vực 4 | `java.util.Random` |
| **Lấy ngày giờ Server** | `ExchangeRateServer.java` | Khu vực 5 | `LocalDateTime`, `DateTimeFormatter` |
| **Đóng gói dữ liệu** | `ExchangeRateServer.java` | Khu vực 6 | `String.format()` |
| **Gửi phản hồi** | `ExchangeRateServer.java` | Khu vực 7 | `PrintWriter`, `println()`, `autoFlush` |
| **Khởi tạo kết nối Client**| `ExchangeRateTable.java` | Khu vực 1 | `Socket`, `localhost` |
| **Gửi lệnh `GET_RATE`** | `ExchangeRateTable.java` | Khu vực 2 | `PrintWriter` |
| **Nhận dữ liệu phản hồi** | `ExchangeRateTable.java` | Khu vực 3 | `BufferedReader` |
| **Bóc tách & Hiển thị** | `ExchangeRateTable.java` | Khu vực 4 | `String.split()`, `System.out.printf()` |

---

## 7. HƯỚNG DẪN CHẠY BẰNG INTELLIJ IDEA TỪ ĐẦU

1. **Bước 1:** Mở dự án `Lab5_LapTrinhMang` trong IntelliJ IDEA.
2. **Bước 2:** Định vị thư mục `server/ExchangeRateServer.java`.
3. **Bước 3:** Nhấn nút **Run** (hình tam giác màu xanh bên trái dòng `public class ExchangeRateServer`).
   * *Console xuất hiện:* `[SERVER START] ExchangeRateServer đang lắng nghe tại cổng 2345...`
4. **Bước 4:** Định vị thư mục `client/ExchangeRateTable.java`.
5. **Bước 5:** Nhấn nút **Run** phía Client.
   * *Console xuất hiện:* In toàn bộ Bảng tỷ giá tài chính 3 thị trường kèm mốc thời gian cập nhật.
6. **Bước 6:** Chuyển lại tab **Console Server** để xem log kết nối đã hoàn thành.

---

## 8. BẢNG KỊCH BẢN KIỂM THỬ TỔNG HỢP (TEST CASES)

| Test ID | Thao tác | Input | Kết quả mong đợi |
| :--- | :--- | :--- | :--- |
| **TC01** | Khởi động Server | Chạy `ExchangeRateServer`. | Server báo lắng nghe cổng 2345 và đi vào trạng thái chờ. |
| **TC02** | Kết nối Client chuẩn | Chạy `ExchangeRateTable`. | Client gửi `GET_RATE`, nhận đủ dữ liệu Tokyo, New York, Hong Kong và Time. |
| **TC03** | Kiểm tra Tokyo | Quan sát Console Client. | Thị trường Tokyo hiển thị tỷ giá trong khoảng 150.xx - 160.xx. |
| **TC04** | Kiểm tra New York | Quan sát Console Client. | Thị trường New York hiển thị tỷ giá trong khoảng 190.xx - 200.xx. |
| **TC05** | Kiểm tra Hong Kong | Quan sát Console Client. | Thị trường Hong Kong hiển thị tỷ giá trong khoảng 170.xx - 180.xx. |
| **TC06** | Kiểm tra DateTime | Quan sát mốc thời gian. | Chuỗi `Time` khớp với thời gian thực trên máy tính. |
| **TC07** | Gửi nhiều Request | Chạy lại Client nhiều lần. | Mỗi lần chạy cho ra tỷ giá ngẫu nhiên mới và mốc thời gian mới. |
| **TC08** | Server chưa chạy | Chỉ chạy Client. | Client báo lỗi ngắt kết nối `ConnectException`. |
| **TC09** | Tắt Server | Đóng tiến trình Server. | Cổng 2345 được giải phóng an toàn. |
| **TC10** | Tắt Client | Client chạy xong và kết thúc `main()`. | Socket phía Client tự động đóng không rò rỉ tài nguyên. |

---

## 9. 15 CÂU HỎI VẤN ĐÁP KIỂM TRA KIẾN THỨC (CÓ ĐÁP ÁN ĐỂ TỰ ÔN)

> **Dưới đây là 15 câu hỏi thường gặp khi bảo vệ bài tập Lập trình mạng:**

1. **Câu 1:** Mô hình Client-Server hoạt động theo nguyên lý nào trong bài tập này?
   * *Đáp án:* Server lắng nghe cổng 2345, Client chủ động gửi Request `GET_RATE`, Server xử lý và gửi Response chứa Tỷ giá + Time cho Client.
2. **Câu 2:** Lớp `ServerSocket` và `Socket` khác nhau ở điểm nào?
   * *Đáp án:* `ServerSocket` dùng ở Server để mở cổng lắng nghe kết nối; `Socket` đại diện cho kênh truyền thông 2 chiều giữa Client và Server.
3. **Câu 3:** Tại sao phải gọi phương thức `accept()`?
   * *Đáp án:* ĐểServer tạm dừng chờ và chấp nhận kết nối từ Client, trả về đối tượng `Socket` làm việc.
4. **Câu 4:** Giao thức TCP mang lại ưu điểm gì cho bài tập này?
   * *Đáp án:* Đảm bảo dữ liệu chuỗi tỷ giá được gửi đi chính xác, không mất mát và đúng thứ tự.
5. **Câu 5:** Lớp `BufferedReader` và `PrintWriter` dùng để làm gì?
   * *Đáp án:* `BufferedReader` bọc `InputStream` giúp đọc dòng văn bản bằng `readLine()`; `PrintWriter` bọc `OutputStream` giúp ghi văn bản bằng `println()`.
6. **Câu 6:** Kỹ thuật Auto-flush trong `PrintWriter` là gì?
   * *Đáp án:* Bật tham số `true` để tự động đẩy dữ liệu đi qua mạng ngay sau lệnh `println()` mà không bị nghẽn ở bộ nhớ đệm.
7. **Câu 7:** Tại sao lệnh `GET_RATE` lại được gọi là Application Protocol (Giao thức ứng dụng)?
   * *Đáp án:* Vì `GET_RATE` không thuộc giao thức TCP mà là quy ước riêng do chính chúng ta định nghĩa cho chương trình này.
8. **Câu 8:** Tại sao logic tạo số ngẫu nhiên tỷ giá nằm ở Server mà không phải Client?
   * *Đáp án:* Đảm bảo Server là nguồn dữ liệu chuẩn duy nhất (Single Source of Truth), tất cả Client đều nhận dữ liệu đồng bộ từ Server.
9. **Câu 9:** Ý nghĩa của công thức `150.0 + (160.0 - 150.0) * random.nextDouble()` là gì?
   * *Đáp án:* Sinh số thực ngẫu nhiên nằm trong khoảng từ 150.0 đến 160.0.
10. **Câu 10:** Tại sao ngày giờ cập nhật lại được lấy từ Server bằng `LocalDateTime.now()`?
    * *Đáp án:* Tránh trường hợp giờ máy Client bị cài đặt sai, đảm bảo mốc thời gian phát hành là thời gian chuẩn của Server.
11. **Câu 11:** Mẫu định dạng `"dd/MM/yyyy HH:mm:ss"` của `DateTimeFormatter` có ý nghĩa thế nào?
    * *Đáp án:* Chuyển đổi ngày giờ thành dạng `ngày/tháng/năm giờ:phút:giây` dễ đọc.
12. **Câu 12:** Chuỗi Response được Server đóng gói theo định dạng nào?
    * *Đáp án:* Dạng chuỗi ghép `Tokyo=...;NewYork=...;HongKong=...;Time=...`.
13. **Câu 13:** Phương thức nào ở phía Client dùng để phân tách chuỗi Response?
    * *Đáp án:* Dùng `response.split(";")` để tách các trường và `split("=")` để lấy tên - giá trị.
14. **Câu 14:** Ngoại lệ nào xảy ra nếu chạy Client khi Server chưa bật?
    * *Đáp án:* `java.net.ConnectException: Connection refused`.
15. **Câu 15:** Tại sao chúng ta lại dùng cú pháp `try-with-resources` (`try (Socket socket = ...)` )?
    * *Đáp án:* Giúp tự động đóng Socket và giải phóng cổng mạng khi chương trình kết thúc, tránh rò rỉ tài nguyên hệ thống.

---

## 10. CHECKLIST HOÀN THÀNH PHASE 5

- [ ] Hiểu toàn bộ luồng kết nối TCP Connection
- [ ] Gửi và nhận thành công Request `GET_RATE`
- [ ] Trả về thành công Response đóng gói dữ liệu
- [ ] Sử dụng `Random` tạo đúng 3 thị trường: Tokyo, New York, Hong Kong
- [ ] Lấy ngày giờ hệ thống bằng `LocalDateTime`
- [ ] Định dạng ngày giờ bằng `DateTimeFormatter`
- [ ] Phía Client bóc tách và hiển thị Bảng Tỷ Giá đẹp mắt
- [ ] Server xử lý và quản lý toàn bộ dữ liệu gốc
- [ ] Thực hiện test hoàn chỉnh toàn bộ chương trình thành công

---

📁 File tài liệu tổng hợp hoàn chỉnh cũng đã được lưu tại **[phase5.md](file:///d:/Lap_Trinh_Mang/Lab5_LapTrinhMang/phase5.md)**.
