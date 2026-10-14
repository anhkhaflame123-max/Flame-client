# Trạng thái build FLAME CLIENT 1.3

- Dự án Android Kotlin chứa màn hình quản lý pack và nút overlay Android.
- Đã sửa chuỗi nhiều dòng Kotlin để dùng raw triple-quoted string.
- Các pack được nhúng dưới `app/src/main/assets/packs/` và được xuất riêng, không tự gộp.
- Chưa biên dịch APK và chưa kiểm thử trong Minecraft PE 1.26.52.
- Máy build hiện tại không có Gradle/Android SDK; cần build bằng AndroidIDE hoặc môi trường Android build phù hợp.
- Overlay chỉ là giao diện nổi Android; không truy cập bộ nhớ nội bộ Minecraft và không đảm bảo các module như Freelook, Ping hay FPS counter trong game.
