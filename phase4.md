# TÀI LIỆU TỔNG HỢP KIẾN THỨC PHASE 4: INTEGRATING DATE TIME
**Môn học:** Lập trình mạng với Java  
**Đề tài lớn:** Xây dựng chương trình ExchangeRate  
**Giai đoạn:** Phase 4 – Nhúng Thời gian Cập nhật (DateTime) từ Server vào Response.

---

## 1. GIẢI THÍCH KIẾN THỨC NỀN TẢNG

### 💡 Các khái niệm cốt lõi:

* **Vì sao cần DateTime trong hệ thống tỷ giá?**
  * Tỷ giá tài chính biến động liên tục theo từng giây/phút. Thêm mốc thời gian (Timestamp) giúp người xem **xác minh được tính mới (Freshness)** của dữ liệu và biết chính xác tỷ giá được chốt vào thời điểm nào.

* **Vì sao thời gian BẮT BUỘC phải lấy từ Server chứ không lấy từ Client?**
  * **Đảm bảo tính trung thực (Single Source of Truth):** Giờ trên máy tính của Client có thể bị đặt sai, lệch giờ hoặc bị người dùng cố tình chỉnh sửa. Server là máy chủ trung tâm có thời gian chuẩn, do đó mốc thời gian phát hành dữ liệu phải lấy từ hệ thống của Server.

* **`LocalDateTime` trong Java là gì?**
  * Là một lớp nằm trong gói `java.time` (ra đời từ Java 8), đại diện cho mốc ngày và giờ (Năm, Tháng, Ngày, Giờ, Phút, Giây, Nanosecond) mà không cần quan tâm đến múi giờ (Timezone).

* **`DateTimeFormatter` trong Java là gì?**
  * Là lớp giúp chuyển đổi đối tượng `LocalDateTime` thành một chuỗi ký tự (String) theo đúng mẫu định dạng mong muốn (và ngược lại).

* **Định dạng mẫu `dd/MM/yyyy HH:mm:ss` có ý nghĩa gì?**
  * `dd`: Ngày dạng 2 chữ số (Ví dụ: `05`, `27`).
  * `MM`: Tháng dạng 2 chữ số (Ví dụ: `09`, `12`).
  * `yyyy`: Năm dạng 4 chữ số (Ví dụ: `2026`).
  * `HH`: Giờ ở chế độ 24 tiếng dạng 2 chữ số (Ví dụ: `09`, `15`).
  * `mm`: Phút dạng 2 chữ số (Ví dụ: `20`, `45`).
  * `ss`: Giây dạng 2 chữ số (Ví dụ: `10`, `59`).

---

## 2. CẤU TRÚC PROJECT (GIỮ NGUYÊN)

```text
d:/Lap_Trinh_Mang/Lab5_LapTrinhMang/
│
├── phase1.md                      <-- Tài liệu Phase 1
├── phase2.md                      <-- Tài liệu Phase 2
├── phase3.md                      <-- Tài liệu Phase 3
├── phase4.md                      <-- Tài liệu Phase 4
│
├── server/
│   └── ExchangeRateServer.java    <-- Code Server Phase 4
│
└── client/
    └── ExchangeRateTable.java     <-- Code Client Phase 4
```

---

## 3. SƠ ĐỒ LUỒNG DỮ LIỆU PHASE 4

```text
CLIENT                                      SERVER
 (ExchangeRateTable.java)              (ExchangeRateServer.java)
       │                                       │
       │ ── 1. Gửi lệnh Request "GET_RATE" ──> │
       │                                       │
       │                                       ├── 2. Sinh tỷ giá (Tokyo, NY, HK)
       │                                       ├── 3. Lấy giờ hệ thống: LocalDateTime.now()
       │                                       ├── 4. Format giờ: "dd/MM/yyyy HH:mm:ss"
       │                                       └── 5. Đóng gói Response:
       │                                              "Tokyo=154.32;...;Time=27/09/2026 15:20:10"
       │                                       │
       │ <─ 6. Trả về Response đầy đủ ──────── │
       │                                       │
       ▼                                       ▼
 [Hiển thị Tỷ giá & Thời gian cập nhật] [Đóng kết nối]
```

---

## 4 & 5. MÃ NGUỒN CHƯƠNG TRÌNH

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

        // ==============================
        // 1. SERVER START
        // ==============================
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("ExchangeRateServer đang lắng nghe tại cổng " + port + "...");

            // ==============================
            // 2. ACCEPT CLIENT
            // ==============================
            Socket socket = serverSocket.accept();
            System.out.println("Client đã kết nối thành công từ: " + socket.getInetAddress());

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            // ==============================
            // 3. RECEIVE REQUEST
            // ==============================
            String request = reader.readLine();
            System.out.println("Server đã nhận Request: " + request);

            String response;
            if ("GET_RATE".equals(request)) {
                // ==============================
                // 4. GENERATE EXCHANGE RATE
                // ==============================
                double tokyoRate = 150.0 + (160.0 - 150.0) * random.nextDouble();
                double newYorkRate = 190.0 + (200.0 - 190.0) * random.nextDouble();
                double hongKongRate = 170.0 + (180.0 - 170.0) * random.nextDouble();

                // ==============================
                // 5. GET CURRENT DATETIME (MỚI Ở PHASE 4)
                // ==============================
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                String formattedDateTime = now.format(formatter);

                // ==============================
                // 6. BUILD RESPONSE (MỚI Ở PHASE 4)
                // ==============================
                response = String.format("Tokyo=%.2f;NewYork=%.2f;HongKong=%.2f;Time=%s",
                                          tokyoRate, newYorkRate, hongKongRate, formattedDateTime);
            } else {
                response = "ERROR: Unknown Command";
            }

            // ==============================
            // 7. SEND RESPONSE
            // ==============================
            writer.println(response);
            System.out.println("Server đã gửi Response: " + response);

            // Đóng socket kết nối ở Phase 4
            socket.close();
            System.out.println("Đã đóng kết nối với Client.");

        } catch (IOException e) {
            System.err.println("Lỗi Server: " + e.getMessage());
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

        System.out.println("Đang kết nối tới Server " + host + ":" + port + "...");

        // ==============================
        // 1. CONNECT
        // ==============================
        try (Socket socket = new Socket(host, port)) {
            System.out.println("Kết nối đến ExchangeRateServer thành công!");

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            // ==============================
            // 2. SEND REQUEST
            // ==============================
            String requestCommand = "GET_RATE";
            writer.println(requestCommand);
            System.out.println("Client đã gửi Request: " + requestCommand);

            // ==============================
            // 3. RECEIVE RESPONSE
            // ==============================
            String response = reader.readLine();

            System.out.println("\n--- BẢNG TỶ GIÁ TÀI CHÍNH ---");
            System.out.println("Dữ liệu thô từ Server: " + response);

            if (response != null && !response.startsWith("ERROR")) {
                String[] parts = response.split(";");
                String updateTime = "Chưa rõ";

                System.out.println("\n--- TỶ GIÁ THỊ TRƯỜNG ---");
                for (String part : parts) {
                    String[] kv = part.split("=");
                    if (kv.length == 2) {
                        if ("Time".equals(kv[0])) {
                            updateTime = kv[1];
                        } else {
                            // ==============================
                            // 4. DISPLAY RATE
                            // ==============================
                            System.out.println("Thị trường " + kv[0] + ": " + kv[1]);
                        }
                    }
                }

                // ==============================
                // 5. DISPLAY LAST UPDATE TIME (MỚI Ở PHASE 4)
                // ==============================
                System.out.println("---------------------------------");
                System.out.println("Thời gian cập nhật mới nhất: " + updateTime);
            } else {
                System.out.println("Lỗi từ Server: " + response);
            }

        } catch (IOException e) {
            System.err.println("Không thể kết nối tới Server: " + e.getMessage());
        }
    }
}
```

---

## 6. GIẢI THÍCH CHI TIẾT CÁC CÂU LỆNH MỚI VỀ DATETIME

### 1. `LocalDateTime now = LocalDateTime.now();`
* **Lấy cái gì?** Lấy thời gian hiện tại của hệ thống máy chủ (năm, tháng, ngày, giờ, phút, giây).
* **Tại sao cần?** Tạo ra đối tượng lưu trữ thời điểm Server xử lý xong bản tin tỷ giá.

### 2. `DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");`
* **Làm gì?** Định nghĩa mẫu (pattern) định dạng ngày giờ chuẩn: `ngày/tháng/năm giờ:phút:giây`.
* **Tại sao cần?** Đối tượng `LocalDateTime` mặc định xuất ra dạng chuỗi ISO phức tạp (ví dụ: `2026-09-27T15:20:10.12345`). Dùng `formatter` giúp đưa về dạng thân thiện với người dùng Việt Nam.

### 3. `String formattedDateTime = now.format(formatter);`
* **Làm gì?** Áp khuôn mẫu `formatter` lên đối tượng `now` để thu được một chuỗi ký tự đẹp mắt, ví dụ: `"27/09/2026 15:20:10"`.

---

## 7. BẢNG NGUỒN GỐC DỮ LIỆU CỦA BẢN TIN

| Thành phần | Được tạo ở đâu? | Ý nghĩa & Vai trò |
| :--- | :--- | :--- |
| **Tokyo** | **Server** (`ExchangeRateServer`) | Tỷ giá giả lập thị trường Tokyo. |
| **New York** | **Server** (`ExchangeRateServer`) | Tỷ giá giả lập thị trường New York. |
| **Hong Kong** | **Server** (`ExchangeRateServer`) | Tỷ giá giả lập thị trường Hong Kong. |
| **DateTime** | **Server** (`ExchangeRateServer`) | Mốc thời gian thực mà Server sinh bản tin tỷ giá. |

---

## 8. HƯỚNG DẪN CHẠY & BẢNG TEST CASES

### Bảng Kịch bản Kiểm thử Phase 4:

| Test ID | Thao tác | Input | Kết quả mong đợi |
| :--- | :--- | :--- | :--- |
| **TC01** | Gửi Request lần 1 | `GET_RATE` | Server trả về đủ 3 tỷ giá và mốc thời gian thực hiện tại (ví dụ: `Time=27/09/2026 15:20:10`). |
| **TC02** | Gửi Request lần 2 (sau 10 giây) | `GET_RATE` | Mốc thời gian `Time` ở Response lần 2 tăng lên đúng 10 giây so với lần 1. |
| **TC03** | Kiểm tra định dạng giờ | `GET_RATE` | Chuỗi `Time` hiển thị chính xác mẫu `dd/MM/yyyy HH:mm:ss`. |
| **TC04** | Client phân tách dữ liệu | `GET_RATE` | Client hiển thị tách riêng phần tỷ giá và dòng *"Thời gian cập nhật mới nhất: ..."* ở cuối. |

---

## 9. CÂU HỎI TỰ KIỂM TRA PHASE 4 (10 CÂU HỎI)

> **Lưu ý:** Bạn hãy đọc kỹ và tự trả lời 10 câu hỏi này ra khung chat để tôi chấm bài nhé!

1. **[Dễ]** Lớp `LocalDateTime` nằm trong package nào của Java?
2. **[Dễ]** Ký hiệu `HH` trong mẫu định dạng `"dd/MM/yyyy HH:mm:ss"` đại diện cho giờ 12 tiếng hay 24 tiếng?
3. **[Dễ]** Sự khác biệt giữa `MM` (tháng viết hoa) và `mm` (phút viết thường) trong mẫu định dạng ngày giờ là gì?
4. **[Trung bình]** Tại sao mốc thời gian `Time` bắt buộc phải lấy ở máy Server chứ không nên lấy `LocalDateTime.now()` ở máy Client?
5. **[Trung bình]** Hàm `LocalDateTime.now()` hoạt động như thế nào? Nó lấy thời gian từ đâu?
6. **[Trung bình]** Mục đích của phương thức `format()` trong lớp `LocalDateTime` là gì?
7. **[Trung bình]** Chuỗi Response ở Phase 4 có cấu trúc tổng thể như thế nào khi Server phát đi?
8. **[Khó]** Làm thế nào Client phân biệt được đâu là thông tin Tỷ giá và đâu là thông tin Thời gian khi nhận chuỗi Response từ Server?
9. **[Khó]** Nếu không dùng `DateTimeFormatter` mà nối trực tiếp `now.toString()` vào Response thì chuỗi nhận được ở Client sẽ có dạng thế nào?
10. **[Khó]** Giả sử Server và Client ở hai múi giờ khác nhau (Ví dụ: Server ở Tokyo, Client ở Hà Nội), việc dùng `LocalDateTime` không kèm múi giờ có thể gây ra vấn đề gì và giải pháp là gì?

---

## 10. KIẾN THỨC TỔNG KẾT SAU PHASE 4

| Kiến thức | Tôi phải hiểu gì? |
| :--- | :--- |
| **`LocalDateTime`** | Lớp biểu diễn ngày giờ hiện tại hệ thống. |
| **`DateTimeFormatter`** | Công cụ biến đổi ngày giờ thành dạng chuỗi văn bản tùy biến. |
| **Server Timestamp** | Luôn lấy thời gian tạo dữ liệu từ chính Server để bảo đảm tin cậy. |
| **Combined Response** | Kỹ thuật đóng gói cả dữ liệu số (Tỷ giá) và dữ liệu chuỗi (Thời gian) vào cùng 1 bản tin. |

---

## 11. CHECKLIST HOÀN THÀNH PHASE 4

- [ ] Hiểu lớp `LocalDateTime`
- [ ] Hiểu lớp `DateTimeFormatter`
- [ ] Hiểu cách sử dụng hàm `format()`
- [ ] Server lấy thời gian chuẩn từ hệ thống máy chủ
- [ ] Response đóng gói đầy đủ Tỷ giá + DateTime
- [ ] Client bóc tách và hiển thị đẹp mắt dòng thời gian cập nhật
- [ ] Chạy thành công các test case của Phase 4
