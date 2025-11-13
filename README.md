"Bai tap game"
TÀI LIỆU PHÂN TÍCH KỸ THUẬT DỰ ÁN 
1. MÔ HÌNH VÀ KIẾN TRÚC TỔNG THỂ
Dự án được xây dựng trên kiến trúc Lập trình Hướng đối tượng (OOP) của JavaFX, đảm bảo sự tách biệt giữa Model (logic game), View (giao diện), và Logic Điều khiển.
1.1. Ứng dụng Mẫu Thiết kế
• Mẫu Factory Method: Được sử dụng trong các lớp Level (Level1.java...) để đọc cấu hình từ file .txt và khởi tạo các subclass Brick cụ thể (ví dụ: new GreenBrick, new FastBrick).
• Mẫu Strategy: Class BrickGroup.java áp dụng mẫu này, nơi các hành vi chuyển động khác nhau ("circle", "wave", "zigzag") được định nghĩa và áp dụng thông qua các công thức toán học (Math.sin(), Math.cos()) trong updatePattern().
• Mẫu Observer/Listener: Các giao diện Callback (Client.OnMessageListener, BossFireBall.OnHitListener) cho phép các module giao tiếp sự kiện mà không cần phụ thuộc trực tiếp.
1.2. Chiến lược Đa Luồng (Multithreading)
• Networking: Các hoạt động I/O chặn (như đọc/ghi Socket) trong Client.java và Server.java được thực thi trong một luồng riêng (new Thread()).
• Định thời Power-Up: Các Power-Up sử dụng một luồng phụ để thực hiện Thread.sleep() cho thời gian hiệu lực (5-10 giây).
• Đồng bộ UI: Mọi cập nhật giao diện (từ luồng mạng hoặc luồng Power-Up) đều sử dụng Platform.runLater() để chuyển giao tác vụ về Luồng JavaFX chính.
• Game Loop: Sử dụng AnimationTimer để cung cấp vòng lặp game ổn định (~100 FPS).
2. PHÂN TÍCH CHUYÊN SÂU CÁC ĐỐI TƯỢNG GAME (MODEL)
2.1. Logic Vật lý và Hiệu ứng Cốt lõi
Ball.java (Bóng): Va chạm cứng: Phương thức bounceOff() tính toán độ chồng chéo (overlapX, overlapY) để xác định trục va chạm và điều chỉnh vị trí để tránh lỗi kẹt vật lý. Hiệu ứng: createTrail() dùng FadeTransition trên các Circle để tạo vệt mờ.
Brick.java (Gạch): Hiệu ứng Neon: Kết hợp DropShadow để phát sáng và Timeline điều khiển opacityProperty() để tạo hiệu ứng nhấp nháy. Phá hủy: playDestroyEffect() kích hoạt Timeline rung lắc (Shake) trên trục X và FadeTransition để gạch biến mất. Rơi: Brick.fall() sử dụng Interpolator.EASE_IN trên yProperty() để mô phỏng gia tốc trọng trường và rotateProperty() cho hiệu ứng xoay.
CollisionHandler: Tính toán góc nảy Paddle bằng cách biến đổi độ lệch so với tâm vợt thành góc nảy tối đa 45°.
2.2. Boss Level 6 và Hệ thống Power-Up
Boss.java: Trạng thái Enraged: Kích hoạt khi HP ≤ 50%. Sử dụng ColorAdjust để đổi màu và TranslateTransition để tạo hiệu ứng rung lắc (Shake).
Đạn Boss: BossFireBall.java sử dụng ParallelTransition kết hợp RotateTransition và hiệu ứng Particle/Flame. Bảo vệ Sát thương: takeDamage() giới hạn tần suất Boss bị trừ máu (Cooldown 300ms).
Power-Up Logic: Các Power-Up (ví dụ: ExpandPaddlePowerUp) chứa logic định thời bằng luồng phụ (Thread.sleep()) để đảo ngược hiệu ứng (ví dụ: thu nhỏ vợt lại) sau thời gian quy định.
3. LUỒNG GAMEPLAY VÀ GIAO DIỆN NGƯỜI DÙNG (UI/UX)
3.1. Cấu trúc Level và Quản lý Game
Level Loading: Các lớp Level (ví dụ: Level1.java) chịu trách nhiệm đọc dữ liệu cấu trúc từ file tài nguyên và khởi tạo gạch theo tọa độ cứng.
Quản lý Game Loop: GameManager.update() kiểm tra level.getBricks().isEmpty(), nếu đúng thì kích hoạt nextLevel().
Xóa Gạch An toàn: GameManager sử dụng danh sách tạm thời (bricksToRemove) để xóa gạch sau khi vòng lặp for kết thúc, ngăn ngừa lỗi ConcurrentModificationException.
3.2. Hiệu ứng UI/UX Nâng cao
GamePanel.java (Menu): Hiệu ứng Neon: Sử dụng Blend và DropShadow kép trên tiêu đề để tạo hiệu ứng Glitch và phát sáng đa màu. Tương tác: Áp dụng BoxBlur cho Menu nền khi hộp thoại phụ (Username, Settings) bật lên. Các nút có hiệu ứng trượt (TranslateTransition) và ScaleTransition mượt mà.
Màn hình Kết thúc: showGameOver và showWinScreen sử dụng kết hợp nhiều Transition và Particle Effects (vòng tròn/hạt) để tạo hiệu ứng kịch tính và thị giác mạnh mẽ.
HUDPanel.java: Có hiệu ứng trượt vào (slideIn) bằng TranslateTransition khi game bắt đầu, đồng thời hiển thị trạng thái và thanh máu Boss HP.
3.3. Luồng Mạng (Client/Server)
Đồng bộ Vị trí: Vị trí Y của vợt được truyền liên tục giữa Client và Server qua Socket (ví dụ: MOVE:Y hoặc OPPONENT_POS:Y).
Ứng dụng Vị trí: Luồng mạng đối phương phân tích cú pháp chuỗi và sử dụng Platform.runLater() để áp dụng vị trí cho vợt đối thủ: gm.getPaddle().setY(y).
Kết thúc Mạng An toàn: Biến boolean gameEndSent được sử dụng để đảm bảo chỉ có một thông điệp kết thúc (GAME_OVER:WIN/LOSE) được gửi qua Socket, ngăn chặn lỗi đồng bộ.
