# TÀI LIỆU TỔNG HỢP KIẾN THỨC PHASE 3: EXCHANGE RATE SIMULATION
**Môn học:** Lập trình mạng với Java  
**Đề tài lớn:** Xây dựng chương trình ExchangeRate  
**Giai đoạn:** Phase 3 – Giả lập và Xử lý Tỷ giá 3 thị trường (Tokyo, New York, Hong Kong).

---

## 1. GIỚI HẠN PHASE 3

### ⛔ CHƯA ĐƯỢC LÀM TRONG PHASE NÀY:
* Ngày giờ (DateTime)
* Vòng lặp tự động cập nhật mỗi 1 giây
* Giao diện GUI (Swing/JavaFX)
* Cơ sở dữ liệu (Database)
* Phục vụ nhiều Client đồng thời (Multithreading)
* Chức năng ngoài đề bài

---

## 2. GIẢI THÍCH KIẾN THỨC NỀN TẢNG

### 💡 Các khái niệm cốt lõi:

* **`Random` trong Java là gì?**
  * Là lớp `java.util.Random` cung cấp khả năng sinh các số ngẫu nhiên (nguyên, thực, booleans,...). 
  * Trong Phase 3, chúng ta dùng `random.nextDouble()` để tạo giá trị tỷ giá ngẫu nhiên biến động trong một khoảng nhất định.

* **Vì sao đề bài cho phép dùng `Random`?**
  * Trong thực tế, tỷ giá tài chính lấy từ các API ngân hàng hoặc sàn chứng khoán thực. 
  * Tuy nhiên trong bài tập học tập, dùng `Random` giúp giả lập sự biến động liên tục của tỷ giá thị trường mà không phụ thuộc vào kết nối internet hay API bên thứ 3.

* **Vì sao Server sinh tỷ giá mà Client KHÔNG tự sinh tỷ giá?**
  * **Khái niệm Single Source of Truth (Nguồn dữ liệu duy nhất):** Trong kiến trúc Client-Server, Server đóng vai trò trung tâm quản lý và sở hữu dữ liệu gốc.
  * Nếu Client tự tạo tỷ giá thì các Client khác nhau sẽ có dữ liệu khác nhau, dẫn tới mất đồng bộ và sai bản chất mô hình Client-Server.

* **Phân định nhiệm vụ giữa Server và Client:**
  * **Server:** Nắm giữ logic nghiệp vụ (Business Logic), sinh tỷ giá, định dạng dữ liệu và phân phối cho Client.
  * **Client:** Chỉ gửi yêu cầu, nhận dữ liệu và hiển thị (Presentation Layer) cho người dùng xem.

---

## 3. CẤU TRÚC PROJECT (GIỮ NGUYÊN)

```text
d:/Lap_Trinh_Mang/Lab5_LapTrinhMang/
│
├── phase1.md                      <-- File tài liệu Phase 1
├── phase2.md                      <-- File tài liệu Phase 2
├── phase3.md                      <-- File tài liệu Phase 3
│
├── server/
│   └── ExchangeRateServer.java    <-- Code Server Phase 3
│
└── client/
    └── ExchangeRateTable.java     <-- Code Client Phase 3
```

---

## 4. SƠ ĐỒ LUỒNG DỮ LIỆU PHASE 3

```text
CLIENT                                      SERVER
 (ExchangeRateTable.java)              (ExchangeRateServer.java)
       │                                       │
       │ ── 1. Gửi lệnh Request "GET_RATE" ──> │
       │                                       │
       │                                       ├── 2. Sinh tỷ giá Tokyo
       │                                       ├── 3. Sinh tỷ giá New York
       │                                       ├── 4. Sinh tỷ giá Hong Kong
       │                                       └── 5. Đóng gói Response chuỗi:
       │                                              "Tokyo=154.32;NewYork=198.45;HongKong=176.21"
       │                                       │
       │ <─ 6. Trả về Response chứa 3 tỷ giá ─ │
       │                                       │
       ▼                                       ▼
 [Tách chuỗi & Hiển thị]               [Đóng kết nối]
```

---

## 5 & 6. MÃ NGUỒN CHƯƠNG TRÌNH

### 📄 1. File Server: `server/ExchangeRateServer.java`

```java
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
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
                // 4. GENERATE TOKYO RATE
                // ==============================
                double tokyoRate = 150.0 + (160.0 - 150.0) * random.nextDouble();

                // ==============================
                // 5. GENERATE NEW YORK RATE
                // ==============================
                double newYorkRate = 190.0 + (200.0 - 190.0) * random.nextDouble();

                // ==============================
                // 6. GENERATE HONG KONG RATE
                // ==============================
                double hongKongRate = 170.0 + (180.0 - 170.0) * random.nextDouble();

                // ==============================
                // 7. BUILD RESPONSE
                // ==============================
                response = String.format("Tokyo=%.2f;NewYork=%.2f;HongKong=%.2f",
                                          tokyoRate, newYorkRate, hongKongRate);
            } else {
                response = "ERROR: Unknown Command";
            }

            // ==============================
            // 8. SEND RESPONSE
            // ==============================
            writer.println(response);
            System.out.println("Server đã gửi Response tỷ giá: " + response);

            // Đóng socket kết nối ở Phase 3
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

        // ==============================
        // 1. CONNECT
        // ==============================
        System.out.println("Đang kết nối tới Server " + host + ":" + port + "...");

        try (Socket socket = new Socket(host, port)) {
            System.out.println("Kết nối đến ExchangeRateServer thành công!");

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            // ==============================
            // 2. SEND GET_RATE
            // ==============================
            String requestCommand = "GET_RATE";
            writer.println(requestCommand);
            System.out.println("Client đã gửi Request: " + requestCommand);

            // ==============================
            // 3. RECEIVE RESPONSE
            // ==============================
            String response = reader.readLine();

            // ==============================
            // 4. DISPLAY EXCHANGE RATE
            // ==============================
            System.out.println("\n--- BẢNG TỶ GIÁ NHẬN TỪ SERVER ---");
            System.out.println("Dữ liệu thô từ Server: " + response);

            if (response != null && !response.startsWith("ERROR")) {
                String[] markets = response.split(";");
                for (String market : markets) {
                    String[] kv = market.split("=");
                    if (kv.length == 2) {
                        System.out.println("Thị trường " + kv[0] + ": " + kv[1]);
                    }
                }
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

## 7. GIẢI THÍCH CHI TIẾT CÁC CÂU LỆNH TẠO TỶ GIÁ

### 1. Chỉ rõ các dòng code xử lý trên Server:

* **Tạo đối tượng Random:**  
  `Random random = new Random();`
* **Dòng sinh tỷ giá Tokyo:**  
  `double tokyoRate = 150.0 + (160.0 - 150.0) * random.nextDouble();`  
  *(Tạo số thực ngẫu nhiên trong khoảng từ 150.0 đến 160.0)*
* **Dòng sinh tỷ giá New York:**  
  `double newYorkRate = 190.0 + (200.0 - 190.0) * random.nextDouble();`  
  *(Tạo số thực ngẫu nhiên trong khoảng từ 190.0 đến 200.0)*
* **Dòng sinh tỷ giá Hong Kong:**  
  `double hongKongRate = 170.0 + (180.0 - 170.0) * random.nextDouble();`  
  *(Tạo số thực ngẫu nhiên trong khoảng từ 170.0 đến 180.0)*
* **Dòng đóng gói Response:**  
  `response = String.format("Tokyo=%.2f;NewYork=%.2f;HongKong=%.2f", tokyoRate, newYorkRate, hongKongRate);`  
  *(Định dạng lấy 2 chữ số thập phân và ghép các thị trường phân cách bằng dấu `;`)*
* **Dòng gửi Response về Client:**  
  `writer.println(response);`

---

### 2. Tại sao toàn bộ logic này phải nằm ở Server?

* **Đảm bảo tính nhất quán:** Tất cả các Client khi gửi `GET_RATE` vào cùng một thời điểm sẽ nhận dữ liệu do một Server tính toán ra.
* **Bảo mật và Dễ bảo trì:** Khi có sự thay đổi thuật toán tính tỷ giá hay kết nối Database thật sau này, chúng ta chỉ cần sửa trên Server mà không cần thay đổi hay cập nhật lại phần mềm phía Client.

---

## 8. HƯỚNG DẪN CHẠY & BẢNG TEST CASES

### Chạy kiểm thử nhiều lần bằng Terminal:

1. Biên dịch: `javac server/ExchangeRateServer.java client/ExchangeRateTable.java`
2. Lần 1: Bật Server (`java server.ExchangeRateServer`), Bật Client (`java client.ExchangeRateTable`).
3. Lần 2: Bật lại Server, Bật lại Client lần nữa để quan sát sự thay đổi giá trị ngẫu nhiên.

---

### Bảng Kịch bản Kiểm thử Phase 3:

| Test ID | Thao tác | Input | Kết quả mong đợi |
| :--- | :--- | :--- | :--- |
| **TC01** | Gửi lệnh `GET_RATE` chuẩn | `GET_RATE` | Response dạng `Tokyo=155.xx;NewYork=194.xx;HongKong=172.xx`. Client bóc tách và in đủ 3 thị trường. |
| **TC02** | Chạy Client nhiều lần | `GET_RATE` | Tỷ giá của Tokyo, New York, Hong Kong thay đổi ngẫu nhiên qua mỗi lần chạy. |
| **TC03** | Gửi sai lệnh | `GET_PRICE` | Server trả `ERROR: Unknown Command`. Client in ra thông báo lỗi từ Server. |
| **TC04** | Kiểm tra hiển thị | `GET_RATE` | Client hiển thị định dạng rõ ràng từng dòng: `Thị trường Tokyo: 155.xx`, `Thị trường New York: 194.xx`, `Thị trường Hong Kong: 172.xx`. |

---

## 9. CÂU HỎI TỰ KIỂM TRA PHASE 3 (10 CÂU HỎI)

> **Lưu ý:** Bạn hãy đọc kỹ và trả lời 10 câu hỏi này ra khung chat để tôi hỗ trợ chấm bài nhé!

1. **[Dễ]** Lớp `java.util.Random` trong Java được sử dụng để làm gì trong Phase này?
2. **[Dễ]** Tên của 3 thị trường tỷ giá mà đề bài yêu cầu Server tạo ra là gì?
3. **[Dễ]** Công thức `150.0 + (160.0 - 150.0) * random.nextDouble()` sinh ra giá trị nằm trong khoảng nào?
4. **[Trung bình]** Tại sao logic sinh tỷ giá ngẫu nhiên lại được đặt ở phía Server chứ không phải phía Client?
5. **[Trung bình]** Ý nghĩa của khái niệm "Single Source of Truth" trong kiến trúc Client-Server là gì?
6. **[Trung bình]** Tại sao chúng ta lại dùng ký tự `;` và `=` để đóng gói chuỗi Response (Ví dụ: `Tokyo=154.32;NewYork=198.45;HongKong=176.21`)?
7. **[Trung bình]** Hàm `String.format("%.2f", value)` có công dụng gì khi xử lý số thực tỷ giá?
8. **[Khó]** Phía Client đã sử dụng phương thức nào trong Java (`split`, `indexOf`,...) để bóc tách chuỗi Response nhận được từ Server thành từng thị trường?
9. **[Khó]** Nếu Client gửi `GET_RATE` nhưng Server không dùng `Random` mà trả về tỷ giá cố định (hardcode `Tokyo=150.00`), điều gì sẽ xảy ra và nó vi phạm mục tiêu nào của bài tập?
10. **[Khó]** Trong thực tế doanh nghiệp, thay vì dùng `Random`, Server sẽ lấy dữ liệu tỷ giá này từ đâu và gửi cho Client?

---

## 10. KIẾN THỨC TỔNG KẾT SAU PHASE 3

| Kiến thức | Tôi phải hiểu gì? |
| :--- | :--- |
| **Random Class** | Công cụ giả lập sự biến động dữ liệu thực tế trong lập trình. |
| **Server Data Processing** | Server chịu trách nhiệm tính toán, sở hữu và xử lý dữ liệu gốc. |
| **Data Formatting** | Cách đóng gói nhiều thông số (Tokyo, NY, HK) thành 1 chuỗi để truyền qua mạng. |
| **Client Parsing** | Client bóc tách chuỗi từ Server để hiển thị lên giao diện. |

---

## 11. CHECKLIST HOÀN THÀNH PHASE 3

- [ ] Hiểu cách sử dụng `Random` trong Java
- [ ] Hiểu lý do Server chịu trách nhiệm sinh dữ liệu tỷ giá
- [ ] Giả lập đủ 3 thị trường: Tokyo, New York, Hong Kong
- [ ] Đóng gói thành công Response chứa 3 tỷ giá
- [ ] Client gửi `GET_RATE` nhận được đúng tỷ giá của 3 thị trường
- [ ] Tỷ giá thay đổi khi thực hiện gửi yêu cầu nhiều lần
- [ ] Chạy thành công các test case của Phase 3
