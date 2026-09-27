# TÀI LIỆU HOÀN THIỆN PHASE 7: KIỂM THỬ TỔNG HỢP VÀ ÔN TẬP VẤN ĐÁP
**Môn học:** Lập trình mạng với Java  
**Đề tài lớn:** Xây dựng chương trình ExchangeRate  
**Giai đoạn:** Phase 7 – Hoàn thiện dự án, kiểm thử toàn diện và bộ 30 câu hỏi vấn đáp.

---

## 1. CẤU TRÚC PROJECT CUỐI CÙNG

```text
d:/Lap_Trinh_Mang/Lab5_LapTrinhMang/
│
├── phase1.md / phase2.md / phase3.md / phase4.md / phase5.md / phase6.md / phase7.md
│
├── server/
│   └── ExchangeRateServer.java    <-- Mã nguồn Máy chủ Server (Mở port 2345, lắng nghe & phát tỷ giá)
│
└── client/
    └── ExchangeRateTable.java     <-- Mã nguồn Máy khách Client (Kết nối, gửi GET_RATE, hiển thị & lặp 1s)
```

### 🎯 Giải thích vai trò từng thư mục và file:
* **Thư mục `server/`**: Chứa toàn bộ xử lý nghiệp vụ của Máy chủ (Business Logic & Data Provider).
  * [ExchangeRateServer.java](file:///d:/Lap_Trinh_Mang/Lab5_LapTrinhMang/server/ExchangeRateServer.java): Lắng nghe cổng `2345`, nhận lệnh `GET_RATE`, tính toán tỷ giá ngẫu nhiên cho 3 thị trường, lấy mốc thời gian hệ thống `LocalDateTime`, đóng gói bản tin Response và phản hồi cho Client.
* **Thư mục `client/`**: Chứa xử lý hiển thị giao diện phía Máy khách (Presentation Layer).
  * [ExchangeRateTable.java](file:///d:/Lap_Trinh_Mang/Lab5_LapTrinhMang/client/ExchangeRateTable.java): Khởi tạo kết nối Socket tới địa chỉ `localhost:2345`, tự động gửi yêu cầu `GET_RATE` định kỳ mỗi 1 giây, hứng bản tin Response, bóc tách dữ liệu và hiển thị Bảng Tỷ Giá giao diện Console.

---

## 2. SƠ ĐỒ TOÀN BỘ HỆ THỐNG

```text
                TCP Port: 2345
                     │
    ┌────────────────▼────────────────┐
    │             SERVER              │
    │                                 │
    │     ExchangeRateServer.java     │
    │                                 │
    │  - TCP Server (ServerSocket)    │
    │  - Client Connection (accept)   │
    │  - Receive Request (readLine)   │
    │  - Generate Rate (Random)       │
    │    + Tokyo                      │
    │    + New York                   │
    │    + Hong Kong                  │
    │  - DateTime (LocalDateTime)     │
    │  - Build & Send Response        │
    └────────────────▲────────────────┘
                     │
                     │ Response: "Tokyo=...;NewYork=...;HongKong=...;Time=..."
                     │
    ┌────────────────┴────────────────┐
    │             CLIENT              │
    │                                 │
    │     ExchangeRateTable.java      │
    │                                 │
    │  - Connect (Socket)             │
    │  - Send Request ("GET_RATE")    │
    │  - Receive Response             │
    │  - Parse & Display Table        │
    │  - Sleep 1 second (1000ms)      │
    │  - Repeat Loop (while true)     │
    └─────────────────────────────────┘
```

---

## 3. KIỂM TRA VÀ RÀ SOÁT CODE (CODE REVIEW)

Toàn bộ mã nguồn ở hai file `server/ExchangeRateServer.java` và `client/ExchangeRateTable.java` đã được rà soát kỹ lưỡng:
- **`ServerSocket(2345)`**: Mở cổng 2345 an toàn, có khối `try-with-resources` tự dọn dẹp tài nguyên.
- **`accept()`**: Đưa Server vào trạng thái lắng nghe chờ Client kết nối.
- **`BufferedReader` & `PrintWriter`**: Khởi tạo đúng luồng đọc/ghi với thuộc tính `autoFlush = true`.
- **Luồng xử lý `while((request = reader.readLine()) != null)`**: Giúp Server duy trì kết nối để phục vụ nhiều lần Request liên tục từ Client.
- **Tạo tỷ giá `Random` & `LocalDateTime`**: Tạo số thực chuẩn xác và định dạng mốc thời gian `dd/MM/yyyy HH:mm:ss`.
- **Phía Client `while(true)` & `Thread.sleep(1000)`**: Tạo nhịp cập nhật tự động chuẩn 1 giây/lần không làm tràn CPU.

✅ **Kết luận:** Mã nguồn chuẩn mực, không có lỗi cú pháp hay rò rỉ tài nguyên (Memory Leak / Socket Leak).

---

## 4. BẢN ĐỒ MÃ NGUỒN (CODE MAP TABLE)

| Chức năng | File | Khu vực / Câu lệnh | Kiến thức mạng & Java |
| :--- | :--- | :--- | :--- |
| **Lắng nghe cổng TCP** | Server | `new ServerSocket(2345)` | TCP Transport Layer, Port Allocation |
| **Chấp nhận kết nối** | Server | `serverSocket.accept()` | Blocking Socket, Connection Acceptance |
| **Khởi tạo kết nối Client** | Client | `new Socket("localhost", 2345)` | Three-way Handshake, Localhost IP |
| **Gửi Request** | Client | `writer.println("GET_RATE")` | Application Protocol, OutputStream |
| **Nhận Request** | Server | `reader.readLine()` | InputStream, Text Encoding |
| **Sinh tỷ giá ngẫu nhiên** | Server | `random.nextDouble()` | Java Utility, Business Logic Simulation |
| **Lấy ngày giờ Server** | Server | `LocalDateTime.now().format(...)` | Java Time API, Date Formatting |
| **Gửi Response** | Server | `writer.println(response)` | OutputStream, Auto-flush |
| **Nhận Response** | Client | `reader.readLine()` | InputStream, Response Parsing |
| **Cập nhật định kỳ 1s** | Client | `Thread.sleep(1000)` | Java Multithreading, Process Delay |

---

## 5. BẢNG KỊCH BẢN KIỂM THỬ TỔNG HỢP (15 TEST CASES)

| Test ID | Điều kiện | Thao tác kiểm thử | Kết quả mong đợi |
| :--- | :--- | :--- | :--- |
| **TC01** | Server khởi động | Bật file `ExchangeRateServer.java`. | Server báo đang lắng nghe cổng 2345 thành công. |
| **TC02** | Client kết nối | Bật file `ExchangeRateTable.java`. | Client báo kết nối thành công, Server hiện log nhận kết nối. |
| **TC03** | Lệnh `GET_RATE` | Quan sát Request phát đi từ Client. | Server nhận đúng chuỗi `"GET_RATE"`. |
| **TC04** | Nhận tỷ giá Tokyo | Kiểm tra console Client. | Thị trường Tokyo hiển thị tỷ giá (khoảng 150.xx - 160.xx). |
| **TC05** | Nhận tỷ giá New York| Kiểm tra console Client. | Thị trường New York hiển thị tỷ giá (khoảng 190.xx - 200.xx). |
| **TC06** | Nhận tỷ giá Hong Kong| Kiểm tra console Client. | Thị trường Hong Kong hiển thị tỷ giá (khoảng 170.xx - 180.xx). |
| **TC07** | Nhận DateTime | Kiểm tra dòng thời gian cập nhật. | Hiển thị chuỗi dạng `dd/MM/yyyy HH:mm:ss` khớp giờ máy tính. |
| **TC08** | Cập nhật mỗi 1s | Cho Client chạy trong 10 giây. | Client tự động in ra 10 bảng tỷ giá, số giây ở `Time` tăng dần. |
| **TC09** | Tắt Server | Đóng cửa sổ chạy Server. | Server giải phóng cổng 2345 an toàn. |
| **TC10** | Tắt Client | Tắt nút Stop ở Client. | Socket ngắt kết nối, Server nhận `null` và dừng luồng. |
| **TC11** | Sai Port | Sửa port Client thành `9999` rồi chạy. | Client báo lỗi `ConnectException: Connection refused`. |
| **TC12** | Server chưa chạy | Chỉ chạy duy nhất Client. | Client báo lỗi `ConnectException: Connection refused`. |
| **TC13** | Request không hợp lệ | Sửa lệnh Client thành `"GET_FOO"`. | Server phản hồi `ERROR: Unknown Command`. |
| **TC14** | Connection bị ngắt | Tắt Server khi Client đang lặp 1s. | Client in thông báo `[WARNING] Server đã đóng kết nối!` và ngắt. |
| **TC15** | Client chạy lâu | Để Client chạy tự động 5 phút. | Hệ thống chạy ổn định, không bị văng lỗi đệm hay rò rỉ bộ nhớ. |

---

## 6. GIẢI THÍCH TOÀN BỘ CODE (DÀNH CHO VẤN ĐÁP)

| Đoạn code quan trọng | Chức năng chi tiết | Tại sao lại cần câu lệnh này? | Nếu bỏ câu lệnh này thì chuyện gì xảy ra? |
| :--- | :--- | :--- | :--- |
| `ServerSocket serverSocket = new ServerSocket(2345)` | Đăng ký mở cổng 2345 lắng nghe TCP. | Để máy chủ có một cổng chờ sẵn cho Client tìm tới. | Client không thể kết nối tới Server, báo lỗi `Connection refused`. |
| `Socket socket = serverSocket.accept()` | Chấp nhận kết nối từ Client. | Chờ và tạo kênh kết nối Socket 2 chiều với Client. | Server không bao giờ đón Client vào làm việc, chương trình kết thúc ngay. |
| `Socket socket = new Socket("localhost", 2345)` | Khởi tạo kết nối từ Client tới Server. | Tìm đúng IP và Port của Server để thiết lập bắt tay TCP. | Client không thực hiện bất kỳ hành động mạng nào. |
| `new InputStreamReader(socket.getInputStream())` | Chuyển luồng byte thành ký tự. | Đọc được các ký tự văn bản truyền qua mạng. | Chỉ đọc được các byte thô không thể ghép thành chuỗi chữ. |
| `new BufferedReader(...)` | Đệm ký tự để đọc theo dòng. | Cung cấp hàm `readLine()` đọc trọn vẹn 1 câu lệnh. | Phải tự viết thuật toán gom từng ký tự rất phức tạp. |
| `new PrintWriter(getOutputStream(), true)` | Ghi chuỗi văn bản ra mạng. | Gửi dòng chữ đi nhanh chóng với tính năng auto-flush. | Dữ liệu bị giữ lại ở bộ nhớ đệm, bên kia không nhận được ngay. |
| `Random random = new Random()` | Sinh số ngẫu nhiên. | Giả lập sự biến động của tỷ giá thị trường tài chính. | Tỷ giá bị cố định (hardcode), không phản ánh tính biến động. |
| `LocalDateTime.now()` | Lấy mốc thời gian hệ thống. | Tạo mốc thời gian chuẩn xác khi Server phát hành dữ liệu. | Người dùng không biết dữ liệu được cập nhật vào lúc nào. |
| `DateTimeFormatter.ofPattern(...)` | Định dạng chuỗi ngày giờ. | Đưa mốc thời gian về dạng chuẩn `dd/MM/yyyy HH:mm:ss`. | Ngày giờ hiển thị dạng mặc định ISO rất rườm rà, khó đọc. |
| `Thread.sleep(1000)` | Tạm dừng luồng 1 giây. | Tạo nhịp cập nhật định kỳ 1s/lần cho Client. | CPU vọt lên 100%, gửi hàng triệu request/s làm sập hệ thống. |

---

## 7. BỘ 30 CÂU HỎI VẤN ĐÁP KIỂM TRA KIẾN THỨC (KHÔNG CÓ ĐÁP ÁN NAY)

> **Bạn hãy tự ôn tập và thử tự trả lời 30 câu hỏi được phân loại theo 7 nhóm dưới đây:**

### 🔹 Nhóm A – TCP Cơ bản
1. TCP là gì và tại sao gọi nó là giao thức "hướng kết nối" (connection-oriented)?
2. Quá trình bắt tay 3 bước (Three-way Handshake) trong TCP diễn ra như thế nào?
3. IP Address và Port Number có vai trò phân biệt điều gì trong kết nối mạng?
4. Tại sao lại chọn số cổng 2345 mà không chọn các cổng như 80 hay 443?

### 🔹 Nhóm B – Java Socket
5. Sự khác biệt căn bản về chức năng giữa `ServerSocket` và `Socket` trong Java là gì?
6. Hàm `accept()` hoạt động theo cơ chế Blocking hay Non-blocking? Giải thích ý nghĩa.
7. Tại sao phải bọc `socket.getInputStream()` qua `InputStreamReader` và `BufferedReader`?
8. Tham số `true` trong lệnh `new PrintWriter(outputStream, true)` có ý nghĩa gì?
9. Cú pháp `try-with-resources` mang lại lợi ích gì cho việc quản lý Socket trong Java?

### 🔹 Nhóm C – Mô hình Client–Server
10. Nguyên lý hoạt động cơ bản của mô hình Client-Server trong bài tập này là gì?
11. Tại sao Server bắt buộc phải khởi động và chạy trước Client?
12. Tại sao lại đặt logic sinh tỷ giá ngẫu nhiên ở Server mà không đặt ở Client?
13. Khái niệm "Single Source of Truth" áp dụng như thế nào trong bài tập này?

### 🔹 Nhóm D – Request / Response
14. Request là gì và Response là gì trong kiến trúc ứng dụng mạng?
15. Khái niệm "Application Protocol" (Giao thức tầng ứng dụng) có nghĩa là gì? Lệnh `GET_RATE` thuộc giao thức nào?
16. Dữ liệu thô từ Server được đóng gói theo định dạng chuỗi nào trước khi truyền qua Socket?
17. Phía Client dùng phương thức nào của Java để phân tách chuỗi Response nhận được?

### 🔹 Nhóm E – ExchangeRate & DateTime
18. Công thức `150.0 + (160.0 - 150.0) * random.nextDouble()` hoạt động ra sao?
19. Lớp `LocalDateTime` khác gì so với lớp `java.util.Date` cũ trong Java?
20. Ý nghĩa của các ký tự `dd`, `MM`, `yyyy`, `HH`, `mm`, `ss` trong mẫu định dạng ngày giờ?
21. Tại sao mốc thời gian cập nhật lại phải lấy giờ từ máy Server?

### 🔹 Nhóm F – Xử lý lỗi & Luồng (Threading)
22. Hàm `Thread.sleep(1000)` nhận tham số tính bằng đơn vị thời gian gì?
23. Tại sao chạy vòng lặp `while(true)` mà không có `Thread.sleep()` lại gây quá tải CPU?
24. Khi Client đột ngột ngắt kết nối, hàm `reader.readLine()` phía Server sẽ trả về giá trị gì?
25. Ngoại lệ `ConnectException` nảy sinh trong những tình huống nào?
26. Ngoại lệ `BindException` xuất hiện khi nào trong quá trình Server khởi động?

### 🔹 Nhóm G – Tình huống thực tế & Mở rộng
27. Nếu muốn nâng cấp bài tập này phục vụ 100 Client cùng lúc, Server cần bổ sung kiến thức gì (Multithreading/ThreadPool)?
28. Nhược điểm của việc dùng `Thread.sleep()` trên luồng chính (Main Thread) nếu phát triển giao diện đồ họa GUI sau này là gì?
29. Làm thế nào để đảm bảo tính an toàn dữ liệu nếu truyền thông tin qua mạng Internet công cộng thay vì `localhost`?
30. Tóm tắt ngắn gọn toàn bộ luồng đời (Lifecycle) của 1 chu kỳ cập nhật dữ liệu ở Phase 7.

---

## 8. 10 TÌNH HUỐNG THỰC TẾ & BẢNG XỬ LÝ LỖI

| STT | Tình huống lỗi | Hiện tượng nhận biết | Nguyên nhân có thể | Vị trí code kiểm tra | Cách khắc phục |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **1** | Server chưa chạy | Client báo `ConnectException: Connection refused`. | Cổng 2345 chưa mở. | `ExchangeRateTable.java` dòng khởi tạo Socket. | Chạy `ExchangeRateServer.java` trước. |
| **2** | Nhập sai Port | Client báo `ConnectException: Connection refused`. | Client trỏ tới nhầm port. | Biến `port = 2345` ở Client. | Sửa biến `port` Client về đúng `2345`. |
| **3** | Server mất kết nối | Client nhận `null` hoặc báo `SocketException`. | Server bị tắt/sập giữa chừng. | Khối `while(true)` Client kiểm tra `response == null`. | Bắt điều kiện `null` để ngắt vòng lặp an toàn. |
| **4** | Client đóng đột ngột| Server in log ngắt kết nối hoặc bắt `IOException`. | Client bấm Stop/tắt tab. | Vòng lặp `while((req = reader.readLine()) != null)` Server. | Server thoát vòng lặp đóng Socket an toàn. |
| **5** | Request sai | Server trả về `ERROR: Unknown Command`. | Gửi sai chuỗi `"GET_RATE"`. | Biến `requestCommand` ở Client. | Đảm bảo gửi đúng chuỗi `"GET_RATE"`. |
| **6** | Server không phản hồi| Client bị nghẽn (Freeze) dừng vô tận ở `readLine()`. | Quên gọi `writer.println()` hoặc quên auto-flush ở Server. | `new PrintWriter(..., true)` Server. | Thêm tham số `true` (autoFlush) vào PrintWriter. |
| **7** | Gửi request quá nhanh| CPU máy tính tăng lên 100%. | Quên lệnh `Thread.sleep(1000)`. | Trong vòng lặp `while(true)` Client. | Thêm `Thread.sleep(1000)` vào cuối vòng lặp. |
| **8** | Tỷ giá không đổi | Giá trị Tokyo, NY, HK đứng yên qua các lần. | Đặt đối tượng `Random` cố định seed hoặc hardcode. | Dòng sinh tỷ giá Server. | Dùng `random.nextDouble()` chuẩn. |
| **9** | DateTime không nhảy | Giờ đứng yên ở mốc cũ. | Gọi `LocalDateTime.now()` ngoài vòng lặp. | Trong vòng lặp `while` Server. | Đặt `LocalDateTime.now()` bên trong khối xử lý `GET_RATE`. |
| **10**| Không bóc tách được | Client in chuỗi thô rườm rà. | Sai ký tự phân cách `;` hoặc `=`. | Đoạn code `response.split(";")` Client. | Khớp chuẩn ký tự phân cách ở 2 bên. |

---

## 9. SƠ ĐỒ TỔNG KẾT MỐI QUAN HỆ KIẾN THỨC

```text
       ┌───────────────┐
       │      TCP      │  (Giao thức truyền tải tin cậy)
       └───────┬───────┘
               │
               ▼
       ┌───────────────┐
       │    SOCKET     │  (Cổng giao tiếp 2 chiều: ServerSocket & Socket)
       └───────┬───────┘
               │
               ▼
       ┌───────────────┐
       │ CLIENT-SERVER │  (Mô hình phân định Server quản lý, Client yêu cầu)
       └───────┬───────┘
               │
               ▼
       ┌───────────────┐
       │REQUEST/RESPONSE│ (Giao thức ứng dụng: "GET_RATE" ──> Response Data)
       └───────┬───────┘
               │
       ┌───────┴───────┐
       ▼               ▼
┌──────────────┐ ┌──────────────┐
│    RANDOM    │ │   DATETIME   │ (Server xử lý logic sinh Tỷ giá + mốc Thời gian)
└──────┬───────┘ └──────┬───────┘
       │                │
       └───────┬────────┘
               │
               ▼
       ┌───────────────┐
       │  AUTO UPDATE  │ (Cập nhật định kỳ 1s bằng Thread.sleep(1000))
       └───────┬───────┘
               │
               ▼
       ┌───────────────┐
       │ SYSTEM TESTED │ (Hoàn thành dự án & bảo vệ vấn đáp thành công)
       └───────────────┘
```

---

## 10. CHECKLIST HOÀN THÀNH TOÀN BỘ BÀI TẬP

- [x] Hiểu bản chất giao thức TCP
- [x] Hiểu tính chất Connection-oriented (bắt tay 3 bước)
- [x] Hiểu vai trò địa chỉ IP và Port number
- [x] Nắm vững lớp `ServerSocket` phía Server
- [x] Nắm vững lớp `Socket` phía Client và Server
- [x] Hiểu cơ chế hoạt động phương thức `accept()`
- [x] Đọc/ghi dữ liệu chữ qua `BufferedReader` và `PrintWriter`
- [x] Hiểu khái niệm Request và Response
- [x] Định nghĩa giao thức ứng dụng `GET_RATE`
- [x] Sử dụng `Random` tạo giả lập 3 thị trường Tokyo, New York, Hong Kong
- [x] Sử dụng `LocalDateTime` và `DateTimeFormatter`
- [x] Thực hiện tự động cập nhật định kỳ mỗi 1s bằng `Thread.sleep(1000)`
- [x] Hiểu rõ cấu trúc Project tiêu chuẩn `server/` và `client/`
- [x] Chạy thử nghiệm thành công 15/15 Test Cases
- [x] Tự tin giải thích từng dòng lệnh trước Giáo viên bảo vệ vấn đáp
