# TÀI LIỆU TỔNG HỢP KIẾN THỨC PHASE 1: TCP SOCKET CƠ BẢN
**Môn học:** Lập trình mạng với Java  
**Đề tài lớn:** Xây dựng chương trình ExchangeRate  
**Giai đoạn:** Phase 1 – Thiết lập TCP Server và TCP Client kết nối cơ bản qua Port 2345.

---

## 1. GIẢI THÍCH KIẾN THỨC NỀN TẢNG

### 💡 Các khái niệm cốt lõi

* **Client–Server là gì?**
  * Là mô hình kiến trúc mạng phân chia công việc giữa hai bên:
    * **Server (Máy phục vụ):** Lắng nghe, chờ đợi yêu cầu từ phía Client và phục vụ dữ liệu/dịch vụ.
    * **Client (Máy khách):** Chủ động khởi tạo kết nối gửi yêu cầu tới Server để lấy thông tin.

* **TCP (Transmission Control Protocol) là gì?**
  * Là giao thức truyền tải ở tầng Transport của mô hình TCP/IP.
  * TCP đảm bảo dữ liệu được gửi đi **chắc chắn đến nơi**, **đúng thứ tự** và **không bị mất mát / nhiễu / lặp gói tin**.

* **Vì sao TCP là connection-oriented (hướng kết nối)?**
  * Vì trước khi Client và Server trao đổi bất kỳ dữ liệu thực sự nào, hai bên **bắt buộc phải thực hiện thiết lập một đường truyền kết nối logic** thông qua quá trình bắt tay (Handshake). Khi hoàn tất kết nối, đường truyền mới được sử dụng để truyền dữ liệu.

* **IP (Internet Protocol) là gì?**
  * Là địa chỉ định danh duy nhất cho từng thiết bị trong mạng (ví dụ: `192.168.1.10` hoặc `localhost` / `127.0.0.1` - địa chỉ đại diện cho chính máy hiện tại của bạn). IP giúp gói tin tìm đúng **máy tính cần tới**.

* **Port (Cổng) là gì?**
  * Một máy tính có thể chạy nhiều ứng dụng mạng cùng lúc (Web, Email, ExchangeRateServer,...).
  * Port là số nguyên từ `0` đến `65535` dùng để phân biệt các ứng dụng khác nhau trên cùng một máy tính. Dữ liệu đến đúng IP sẽ dựa vào Port để đi vào **đúng ứng dụng**.

* **ServerSocket là gì (trong Java)?**
  * Là lớp (`java.net.ServerSocket`) dùng riêng ở phía **Server**, đóng vai trò như một **"Tổng đài lắng nghe"** tại một cổng cố định (ví dụ port `2345`).
  * `ServerSocket` không tham gia trực tiếp truyền nhận dữ liệu với Client mà chỉ làm nhiệm vụ **chờ kết nối mới**.

* **Socket là gì (trong Java)?**
  * Là lớp (`java.net.Socket`) đại diện cho **kết nối 2 chiều (End-to-End communication)** giữa Client và Server.
  * Cả Client và Server đều dùng đối tượng `Socket` để gửi và nhận dữ liệu qua lại sau khi kết nối thành công.

* **`accept()` là gì?**
  * Là một phương thức của `ServerSocket`.
  * Khi gọi `serverSocket.accept()`, chương trình Server sẽ tạm dừng (rơi vào trạng thái **blocking**) để chờ đợi Client kết nối tới.
  * Khi có một Client tìm đến, `accept()` chấp nhận kết nối và trả về một đối tượng `Socket` mới đại diện riêng cho phiên làm việc với Client đó.

* **Vì sao Server phải chạy trước Client?**
  * Client đóng vai trò là bên chủ động phát tín hiệu tìm tới địa chỉ `IP:Port` của Server.
  * Nếu Server chưa bật, cổng `2345` chưa được mở để lắng nghe, hệ thống mạng sẽ từ chối kết nối và Client sẽ báo lỗi `ConnectException: Connection refused`.

---

### 🤝 Quá trình thiết lập kết nối TCP (Three-way Handshake)

Trước khi lệnh `accept()` của Server và lệnh `new Socket(...)` của Client hoàn thành, hai máy tính tự động trao đổi 3 gói tin bên dưới tầng giao thức:

```text
Client                                  Server
  │                                       │
  │ ─── 1. SYN (Xin chào, tôi muốn nối) ──>│
  │                                       │
  │ <── 2. SYN + ACK (Đã nghe, đồng ý) ───│
  │                                       │
  │ ─── 3. ACK (Xác nhận, đã sẵn sàng) ──>│
  │                                       │
  ▼                                       ▼
[Kết nối thành công - Tạo Socket giữa 2 bên]
```

---

### 🔄 Mối quan hệ giữa các thành phần Java

```text
       ServerSocket (Lắng nghe tại Port 2345)
            │
            ▼
      hàm accept() (Tạm dừng chờ Client kết nối)
            │
            ▼ (Khi có Client tới)
          Socket (Tạo ra ống truyền kết nối 2 chiều)
            │
            ▼
      TCP Connection (Sẵn sàng trao đổi dữ liệu)
```

---

## 2. CẤU TRÚC PROJECT

```text
d:/Lap_Trinh_Mang/Lab5_LapTrinhMang/
│
├── server/
│   └── ExchangeRateServer.java
│
└── client/
    └── ExchangeRateTable.java
```

### Nhiệm vụ từng file:
* **`server/ExchangeRateServer.java`**: Chịu trách nhiệm khởi tạo `ServerSocket(2345)`, chạy Server và lắng nghe kết nối từ Client.
* **`client/ExchangeRateTable.java`**: Chịu trách nhiệm khởi tạo `Socket("localhost", 2345)` để tìm và kết nối tới Server.

---

## 3. SƠ ĐỒ CHƯƠNG TRÌNH

```text
┌──────────────────────────────────────┐
│                SERVER                │
│                                      │
│  [server/ExchangeRateServer.java]    │
│                                      │
│    ServerSocket serverSocket         │
│          (Port : 2345)               │
└──────────────────┬───────────────────┘
                   │
                   │  TCP Connection 
                   │  (Port 2345)
                   │
┌──────────────────▼───────────────────┐
│                CLIENT                │
│                                      │
│   [client/ExchangeRateTable.java]    │
│                                      │
│            Socket socket             │
│      (Host: localhost:2345)          │
└──────────────────────────────────────┘
```

**Các bước diễn ra trên sơ đồ:**
1. Server khởi chạy `ServerSocket` mở cổng `2345` và gọi `accept()` đứng chờ.
2. Client khởi chạy `Socket` yêu cầu kết nối tới địa chỉ `localhost` cổng `2345`.
3. Hai bên thực hiện bắt tay TCP 3 bước thành công.
4. Server nhận được kết nối, `accept()` tạo đối tượng `Socket` đại diện cho Client và in ra thông báo thành công.

---

## 4. MÃ NGUỒN CÁC FILE

### 📄 File 1: `server/ExchangeRateServer.java`

```java
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ExchangeRateServer {
    public static void main(String[] args) {
        int port = 2345;

        // ==============================
        // 1. KHỞI ĐỘNG SERVER
        // ==============================
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("ExchangeRateServer đang lắng nghe tại cổng " + port + "...");

            // ==============================
            // 2. CHỜ CLIENT KẾT NỐI
            // ==============================
            System.out.println("Đang chờ Client kết nối tới...");

            // ==============================
            // 3. NHẬN CONNECTION
            // ==============================
            Socket socket = serverSocket.accept();
            System.out.println("Client đã kết nối thành công từ: " + socket.getInetAddress() + ":" + socket.getPort());

            // Đóng socket kết nối ở Phase 1
            socket.close();
            System.out.println("Đã đóng kết nối với Client.");
        } catch (IOException e) {
            System.err.println("Lỗi Server: " + e.getMessage());
        }
    }
}
```

---

### 📄 File 2: `client/ExchangeRateTable.java`

```java
package client;

import java.io.IOException;
import java.net.Socket;

public class ExchangeRateTable {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 2345;

        // ==============================
        // 1. KẾT NỐI SERVER
        // ==============================
        System.out.println("Đang kết nối tới Server " + host + ":" + port + "...");

        try (Socket socket = new Socket(host, port)) {

            // ==============================
            // 2. HIỂN THỊ TRẠNG THÁI
            // ==============================
            System.out.println("Kết nối đến ExchangeRateServer thành công!");
            System.out.println("Địa chỉ Local Client Socket: " + socket.getLocalSocketAddress());
            System.out.println("Địa chỉ Remote Server Socket: " + socket.getRemoteSocketAddress());
        } catch (IOException e) {
            System.err.println("Không thể kết nối tới Server: " + e.getMessage());
        }
    }
}
```

---

## 5. GIẢI THÍCH CHI TIẾT CÂU LỆNH CỐT LÕI

### 1. `ServerSocket serverSocket = new ServerSocket(2345);`
* **Làm gì?** Mở cổng số `2345` trên hệ điều hành của máy tính để lắng nghe các gói tin TCP kết nối tới.
* **Tại sao cần?** Tạo điểm tiếp nhận kết nối cho Server.
* **Nếu bỏ?** Không có cổng nào được mở, Client sẽ không thể tìm thấy Server.
* **Lỗi tiềm ẩn:** Bắn ra `java.net.BindException` nếu cổng 2345 đang bị phần mềm khác sử dụng.

### 2. `Socket socket = serverSocket.accept();`
* **Làm gì?** Đưa Server vào trạng thái tạm dừng (blocking), chờ có Client gọi tới. Ngay khi có Client kết nối, nó chấp nhận và tạo ra một kênh giao tiếp `Socket`.
* **Tại sao cần?** Để đón và tạo ra phiên làm việc riêng với Client.
* **Nếu bỏ?** Server sẽ đứng lắng nghe cổng nhưng không chấp nhận cho Client đi vào, chương trình sẽ kết thúc ngay lập tức.

### 3. `Socket socket = new Socket("localhost", 2345);`
* **Làm gì?** Phía Client chủ động gửi gói tin xin kết nối tới máy tính `localhost` (`127.0.0.1`) tại cổng `2345`.
* **Nếu bỏ?** Client sẽ không thực hiện hành động kết nối mạng nào.
* **Lỗi tiềm ẩn:** Bắn ra `java.net.ConnectException` nếu Server chưa được bật.

---

## 6. HƯỚNG DẪN CHẠY CHƯƠNG TRÌNH

### Chạy bằng Terminal (Command Prompt / PowerShell)
Đứng tại thư mục gốc `d:\Lap_Trinh_Mang\Lab5_LapTrinhMang`:

1. **Biên dịch cả 2 file:**
   ```bash
   javac server/ExchangeRateServer.java client/ExchangeRateTable.java
   ```
2. **Chạy Server (Terminal 1):**
   ```bash
   java server.ExchangeRateServer
   ```
   * *Màn hình hiện:* `ExchangeRateServer đang lắng nghe tại cổng 2345...`
3. **Chạy Client (Terminal 2 mới):**
   ```bash
   java client.ExchangeRateTable
   ```
   * *Màn hình hiện:* `Kết nối đến ExchangeRateServer thành công!`

---

## 7. BẢNG KỊCH BẢN KIỂM THỬ (TEST CASES)

| Test ID | Thao tác | Kết quả mong đợi |
| :--- | :--- | :--- |
| **TC01** | Bật Server trước. | Server báo đang lắng nghe cổng 2345 và chờ Client. |
| **TC02** | Bật Client sau khi Server đã bật. | Client báo kết nối thành công. Server in địa chỉ IP/Port của Client. |
| **TC03** | Đổi port Client thành `9999` rồi chạy. | Client báo lỗi `ConnectException: Connection refused`. |
| **TC04** | Tắt Server, chỉ bật Client. | Client báo lỗi `ConnectException: Connection refused`. |
| **TC05** | Quan sát sau khi kết nối xong. | Server in *"Đã đóng kết nối với Client"* và thoát chương trình an toàn. |

---

## 8. CÂU HỎI TỰ KIỂM TRA (10 CÂU HỎI)

1. **[Dễ]** Giao thức TCP có đặc điểm gì nổi bật? Tại sao gọi nó là giao thức "hướng kết nối"?
2. **[Dễ]** Địa chỉ `localhost` hay `127.0.0.1` có ý nghĩa là gì?
3. **[Dễ]** Tại sao chúng ta cần định nghĩa `port` khi viết chương trình lập trình mạng?
4. **[Trung bình]** Vai trò của lớp `ServerSocket` khác gì so với lớp `Socket` trong Java?
5. **[Trung bình]** Hàm `accept()` của `ServerSocket` trả về đối tượng thuộc kiểu dữ liệu gì? Mục đích của nó là gì?
6. **[Trung bình]** Điều gì sẽ xảy ra với luồng thực thi (thread) của chương trình Server tại dòng lệnh `serverSocket.accept()` khi chưa có Client nào kết nối tới?
7. **[Trung bình]** Nếu chạy Client trước khi bật Server, ngoại lệ (Exception) nào trong Java sẽ bắn ra? Giải thích lý do tại sao.
8. **[Khó]** Quá trình bắt tay 3 bước (Three-way Handshake) của TCP gồm những gói tin trao đổi nào giữa Client và Server?
9. **[Khó]** Khi Client kết nối tới cổng `2345` của Server, cổng phía Client mở ra để kết nối có bắt buộc phải là `2345` không? Tại sao?
10. **[Khó]** Tại sao trong đoạn code trên chúng ta lại sử dụng cú pháp `try-with-resources` (`try (Socket socket = ...)` )? Nếu không đóng Socket thì hậu quả là gì?

---

## 9. KIẾN THỨC TỔNG KẾT SAU PHASE 1

| Kiến thức | Tôi phải hiểu gì? |
| :--- | :--- |
| **Client–Server** | Server lắng nghe chờ đợi, Client tìm tới yêu cầu. |
| **TCP Protocol** | Truyền tin cậy, có bắt tay 3 bước trước khi truyền tin. |
| **IP & Port** | IP chọn đúng máy tính, Port chọn đúng phần mềm. |
| **ServerSocket** | Cổng lắng nghe phía Server tại port `2345`. |
| **Socket** | Kênh kết nối 2 chiều giữa 2 máy sau khi kết nối. |
| **`accept()`** | Hàm chờ duyệt và chấp nhận kết nối từ Client. |
| **`localhost`** | Địa chỉ tự trỏ về chính máy tính của mình. |

---

## 10. CHECKLIST HOÀN THÀNH PHASE 1

- [ ] Hiểu Client–Server
- [ ] Hiểu TCP
- [ ] Hiểu Connection-oriented
- [ ] Hiểu IP
- [ ] Hiểu Port
- [ ] Hiểu ServerSocket
- [ ] Hiểu Socket
- [ ] Hiểu accept()
- [ ] Hiểu localhost
- [ ] Server chạy port 2345
- [ ] Client kết nối Server
- [ ] Test thành công
