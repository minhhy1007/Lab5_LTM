# TÀI LIỆU TỔNG HỢP KIẾN THỨC PHASE 2: REQUEST / RESPONSE VIA TCP STREAM
**Môn học:** Lập trình mạng với Java  
**Đề tài lớn:** Xây dựng chương trình ExchangeRate  
**Giai đoạn:** Phase 2 – Cơ chế Gửi yêu cầu (Request: `GET_RATE`) và Nhận phản hồi (Response: `OK`).

---

## 1. GIỚI HẠN PHASE 2

### ⛔ CHƯA ĐƯỢC LÀM TRONG PHASE NÀY:
* Random dữ liệu
* 3 thị trường: Tokyo, New York, Hong Kong
* Ngày giờ (DateTime)
* Vòng lặp cập nhật mỗi 1 giây
* Giao diện GUI (Swing/JavaFX)
* Cơ sở dữ liệu (Database)
* Phục vụ nhiều Client đồng thời (Multithreading)

---

## 2. GIẢI THÍCH KIẾN THỨC NỀN TẢNG

### 💡 Các khái niệm cốt lõi:

* **TCP Byte Stream là gì?**
  * Giao thức TCP không truyền các câu lệnh chữ hay đối tượng (Object) trực tiếp.
  * TCP coi mọi dữ liệu gửi/nhận là **mọt dòng chảy các byte liên tục (Stream of bytes)**. Dữ liệu khi gửi đi sẽ được băm nhỏ thành các byte, truyền qua đường ống mạng và đến bên nhận dưới dạng dãy byte.

* **InputStream là gì?**
  * Là luồng dữ liệu **ĐỌC VÀO** chương trình Java từ mạng (`socket.getInputStream()`). 
  * Chương trình dùng `InputStream` để "hứng" các byte do bên kia gửi sang.

* **OutputStream là gì?**
  * Là luồng dữ liệu **GHI RA** mạng từ chương trình Java (`socket.getOutputStream()`). 
  * Chương trình dùng `OutputStream` để "bơm" các byte sang máy bên kia.

* **Client gửi dữ liệu như thế nào?**
  * Client lấy `OutputStream` từ `Socket`, biến chuỗi ký tự thành các byte (encode), rồi đẩy các byte này qua kết nối TCP sang Server.

* **Server nhận dữ liệu như thế nào?**
  * Server lấy `InputStream` từ `Socket`, "hứng" các byte truyền đến qua mạng, gom các byte lại và giải mã thành chuỗi ký tự (decode).

* **Request (Lệnh yêu cầu) là gì?**
  * Là thông điệp do **Client gửi cho Server** để yêu cầu Server thực hiện một hành động hoặc trả về thông tin nào đó (Ví dụ: `GET_RATE`).

* **Response (Thông điệp phản hồi) là gì?**
  * Là thông điệp do **Server gửi phản hồi lại cho Client** sau khi đã nhận và xử lý xong Request (Ví dụ: `OK` hoặc `ERROR: Unknown Command`).

* **Application Protocol (Giao thức tầng ứng dụng) là gì?**
  * Là tập hợp các **quy ước chung giữa Client và Server** về định dạng câu lệnh và cách thức giao tiếp.
  * **ĐẶC BIỆT LƯU Ý:** TCP chỉ cung cấp đường dẫn byte stream thô. Lệnh `GET_RATE` hay `OK` **KHÔNG PHẢI** là tính năng có sẵn của TCP, mà là do chính chúng ta quy định cho chương trình ExchangeRate của mình!

---

## 3. CẤU TRÚC PROJECT

Cấu trúc thư mục được giữ nguyên đơn giản và rõ ràng:

```text
d:/Lap_Trinh_Mang/Lab5_LapTrinhMang/
│
├── phase1.md                      <-- Tài liệu Phase 1
├── phase2.md                      <-- Tài liệu Phase 2
│
├── server/
│   └── ExchangeRateServer.java    <-- Mã nguồn Server Phase 2
│
└── client/
    └── ExchangeRateTable.java     <-- Mã nguồn Client Phase 2
```

---

## 4. SƠ ĐỒ LUỒNG DỮ LIỆU PHASE 2

```text
CLIENT                                      SERVER
 (ExchangeRateTable.java)              (ExchangeRateServer.java)
       │                                       │
       │ ── 1. Kết nối thành công (Socket) ──> │
       │                                       │
       │ ── 2. Request: "GET_RATE\n" ────────> │
       │                                       │ (Server đọc "GET_RATE",
       │                                       │  kiểm tra & xử lý)
       │                                       │
       │ <─ 3. Response: "OK\n" ────────────── │
       │                                       │
       ▼                                       ▼
 [Hiển thị "OK"]                       [Đóng kết nối]
```

**Giải thích từng bước:**
1. Client kết nối thành công tới Server qua cổng `2345`.
2. Client dùng `PrintWriter` đẩy chuỗi `"GET_RATE"` kèm ký tự xuống dòng `\n` qua `OutputStream`.
3. Server dùng `BufferedReader` đọc từ `InputStream` được câu lệnh `"GET_RATE"`.
4. Server kiểm tra nếu đúng chuỗi `"GET_RATE"` thì trả về `"OK"`, ngược lại trả về thông báo lỗi.
5. Client đọc được kết quả phản hồi `"OK"` từ Server và in ra màn hình.

---

## 5. MÃ NGUỒN VÀ TỔ CHỨC CODE

### 📄 1. File Server: `server/ExchangeRateServer.java`

```java
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ExchangeRateServer {
    public static void main(String[] args) {
        int port = 2345;

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

            // Khởi tạo luồng Đọc dữ liệu vào (InputStream) và Ghi dữ liệu ra (OutputStream)
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            // ==============================
            // 3. RECEIVE REQUEST
            // ==============================
            String request = reader.readLine();
            System.out.println("Server đã nhận Request: " + request);

            // ==============================
            // 4. PROCESS REQUEST
            // ==============================
            String response;
            if ("GET_RATE".equals(request)) {
                response = "OK";
            } else {
                response = "ERROR: Unknown Command";
            }

            // ==============================
            // 5. SEND RESPONSE
            // ==============================
            writer.println(response);
            System.out.println("Server đã gửi Response: " + response);

            // Đóng socket kết nối ở Phase 2
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
        // 1. CONNECT SERVER
        // ==============================
        try (Socket socket = new Socket(host, port)) {
            System.out.println("Kết nối đến ExchangeRateServer thành công!");

            // Khởi tạo luồng Đọc dữ liệu vào (InputStream) và Ghi dữ liệu ra (OutputStream)
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

            // ==============================
            // 4. DISPLAY RESPONSE
            // ==============================
            System.out.println("Client nhận phản hồi (Response) từ Server: " + response);

        } catch (IOException e) {
            System.err.println("Không thể kết nối tới Server: " + e.getMessage());
        }
    }
}
```

---

## 6. GIẢI THÍCH CHI TIẾT CÁC CÂU LỆNH ĐỌC/GHI DỮ LIỆU

### 1. Khởi tạo luồng Đọc và Ghi
```java
BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
```
* **`socket.getInputStream()`**: Lấy luồng byte đầu vào từ mạng.
* **`new InputStreamReader(...)`**: Chuyển đổi dãy byte nhận được thành các ký tự chữ (character).
* **`new BufferedReader(...)`**: Gom các ký tự vào bộ nhớ đệm để sử dụng được hàm `readLine()` (đọc trọn vẹn 1 dòng chữ cho đến dấu xuống dòng `\n`).
* **`socket.getOutputStream()`**: Lấy luồng byte đầu ra để truyền đi.
* **`new PrintWriter(..., true)`**: Giúp ghi dòng chữ dễ dàng bằng `println()`. Tham số `true` bật tính năng **auto-flush** (đẩy dữ liệu đi ngay lập tức qua mạng mà không bị giữ lại ở bộ nhớ đệm).

---

### 2. Dòng code thể hiện dữ liệu đi từ Client → Server

* **Phía Client (Gửi):**
  ```java
  // client/ExchangeRateTable.java dòng 25
  writer.println("GET_RATE");
  ```
  *Câu lệnh này chuyển chuỗi `"GET_RATE\n"` thành dãy byte và ghi ra `OutputStream` của Client.*

* **Phía Server (Nhận):**
  ```java
  // server/ExchangeRateServer.java dòng 28
  String request = reader.readLine();
  ```
  *Câu lệnh này đứng chờ đọc luồng byte từ `InputStream` của Server cho tới khi gặp ký tự xuống dòng `\n` và gán vào biến `request`.*

---

### 3. Dòng code thể hiện dữ liệu đi từ Server → Client

* **Phía Server (Gửi phản hồi):**
  ```java
  // server/ExchangeRateServer.java dòng 40
  writer.println(response);
  ```
  *Chuyển chuỗi `"OK\n"` thành dãy byte và ghi ra `OutputStream` của Server.*

* **Phía Client (Nhận phản hồi):**
  ```java
  // client/ExchangeRateTable.java dòng 31
  String response = reader.readLine();
  ```
  *Đọc dòng phản hồi từ `InputStream` của Client.*

---

## 7. HƯỚNG DẪN CHẠY CHƯƠNG TRÌNH

### Chạy bằng Terminal:
Đứng tại thư mục gốc `d:\Lap_Trinh_Mang\Lab5_LapTrinhMang`:

1. **Biên dịch:**
   ```bash
   javac server/ExchangeRateServer.java client/ExchangeRateTable.java
   ```
2. **Chạy Server (Terminal 1):**
   ```bash
   java server.ExchangeRateServer
   ```
   *Kết quả màn hình Server:*
   ```text
   ExchangeRateServer đang lắng nghe tại cổng 2345...
   ```
3. **Chạy Client (Terminal 2):**
   ```bash
   java client.ExchangeRateTable
   ```
   *Kết quả màn hình Client:*
   ```text
   Đang kết nối tới Server localhost:2345...
   Kết nối đến ExchangeRateServer thành công!
   Client đã gửi Request: GET_RATE
   Client nhận phản hồi (Response) từ Server: OK
   ```
4. **Kiểm tra màn hình Server (Terminal 1):**
   ```text
   Client đã kết nối thành công từ: /127.0.0.1
   Server đã nhận Request: GET_RATE
   Server đã gửi Response: OK
   Đã đóng kết nối với Client.
   ```

---

## 8. BẢNG KỊCH BẢN KIỂM THỬ (TEST CASES)

| Test ID | Input (Request của Client) | Thao tác | Kết quả mong đợi |
| :--- | :--- | :--- | :--- |
| **TC01** | `GET_RATE` | Bật Server, Client gửi lệnh chuẩn `GET_RATE`. | Server nhận `GET_RATE` và trả về `OK`. Client in: `Client nhận phản hồi (Response) từ Server: OK`. |
| **TC02** | `HELLO_SERVER` (Lệnh sai) | Sửa biến `requestCommand = "HELLO_SERVER"` trong Client rồi chạy. | Server nhận `HELLO_SERVER`, xử lý rẽ nhánh `else` và trả về `ERROR: Unknown Command`. |
| **TC03** | Không có Input | Chạy Client khi Server chưa được bật. | Client báo lỗi ngắt kết nối: `ConnectException: Connection refused`. |
| **TC04** | Client ngắt đột ngột | Client bị đóng/tắt giữa chừng trước khi gửi xong Request. | Server bắt ngoại lệ `IOException` hoặc đọc được `null`, thoát an toàn không crash. |
| **TC05** | Server đóng kết nối | Server gửi xong `OK` và gọi `socket.close()`. | Cả 2 bên giải phóng tài nguyên và kết thúc tiến trình sạch sẽ. |

---

## 9. CÂU HỎI TỰ KIỂM TRA PHASE 2 (10 CÂU HỎI)

> **Lưu ý:** Bạn hãy đọc kĩ và tự trả lời 10 câu hỏi này ra khung chat. Tôi sẽ chấm bài và giải đáp cho bạn!

1. **[Dễ]** TCP truyền nhận dữ liệu dưới dạng gì? Tại sao gọi là "byte stream"?
2. **[Dễ]** `InputStream` dùng để làm gì và `OutputStream` dùng để làm gì trong lập trình Socket Java?
3. **[Dễ]** Định nghĩa khái niệm `Request` và `Response` trong mô hình Client-Server.
4. **[Trung bình]** Lệnh `GET_RATE` là chức năng có sẵn của giao thức TCP hay do ứng dụng chúng ta tự quy định? Đây được gọi là tầng giao thức nào?
5. **[Trung bình]** Lớp `InputStreamReader` và `BufferedReader` đóng vai trò gì khi bọc quanh `socket.getInputStream()`?
6. **[Trung bình]** Tại sao trong lệnh `new PrintWriter(socket.getOutputStream(), true)` lại cần tham số `true` (autoFlush)? Nếu không có `true` thì chuyện gì có thể xảy ra khi gửi dữ liệu?
7. **[Trung bình]** Phương thức `reader.readLine()` làm thế nào để biết dữ liệu truyền tới đã kết thúc một câu lệnh?
8. **[Khó]** Nếu phía Client dùng `writer.print("GET_RATE")` (không có `ln` / không xuống dòng `\n`), phía Server gọi `reader.readLine()` thì Server có đọc được dữ liệu ngay không? Tại sao?
9. **[Khó]** Trình bày ngắn gọn luồng đi của dữ liệu từ dòng code `writer.println("GET_RATE")` ở Client tới biến `String request` ở Server.
10. **[Khó]** Nếu Server nhận được một câu lệnh khác không phải `"GET_RATE"` (ví dụ `"DELETE_ALL"`), chương trình Server ở Phase 2 sẽ xử lý như thế nào để không bị sập?

---

## 10. KIẾN THỨC TỔNG KẾT SAU PHASE 2

| Kiến thức | Tôi phải hiểu gì? |
| :--- | :--- |
| **Byte Stream** | Dữ liệu trên mạng TCP là dòng chảy của các byte. |
| **InputStream / OutputStream** | Luồng nhập dữ liệu VÀO và luồng xuất dữ liệu RA từ Socket. |
| **BufferedReader / PrintWriter** | Bộ công cụ giúp đọc/ghi chuỗi văn bản (String) theo dòng dễ dàng. |
| **Request & Response** | Client gửi yêu cầu (`GET_RATE`), Server phản hồi (`OK`). |
| **Application Protocol** | Quy ước chuỗi lệnh giao tiếp do chính người lập trình tạo ra. |
| **Auto-flush** | Đẩy dữ liệu ra khỏi bộ nhớ đệm lập tức để gửi qua mạng. |

---

## 11. CHECKLIST HOÀN THÀNH PHASE 2

- [ ] Hiểu khái niệm TCP Byte Stream
- [ ] Hiểu vai trò của `InputStream` và `OutputStream`
- [ ] Hiểu vai trò của `BufferedReader` và `PrintWriter`
- [ ] Hiểu bản chất Request và Response
- [ ] Hiểu Application Protocol (Giao thức ứng dụng)
- [ ] Client gửi thành công lệnh `GET_RATE`
- [ ] Server nhận và kiểm tra đúng lệnh `GET_RATE`
- [ ] Server gửi thành công phản hồi `OK` (hoặc báo lỗi nếu lệnh sai)
- [ ] Client nhận và hiển thị phản hồi `OK` lên màn hình
- [ ] Chạy thành công các test case của Phase 2
