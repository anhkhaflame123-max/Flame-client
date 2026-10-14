# Cách tạo APK FLAME CLIENT trên GitHub

> ZIP này là mã nguồn Android, chưa phải APK đã biên dịch. Workflow dưới đây giúp GitHub build APK tự động.

1. Giải nén `FLAME_CLIENT_Android_1.2_Source.zip`.
2. Tạo một repository GitHub mới và tải toàn bộ nội dung thư mục `FLAME_CLIENT_Android_1.1` lên repository (bao gồm thư mục `.github/workflows`).
3. Mở tab **Actions**, chọn **Build FLAME CLIENT APK**, rồi bấm **Run workflow** nếu workflow chưa tự chạy.
4. Khi workflow hoàn tất màu xanh, mở lần chạy đó, kéo xuống **Artifacts** và tải `FLAME_CLIENT-debug-apk`.
5. Giải nén artifact ZIP để lấy `app-debug.apk`, sau đó cài trên điện thoại Android. Có thể cần cho phép cài ứng dụng từ nguồn này.

## Giới hạn hiện tại
- Đây là debug APK, chưa ký phát hành và chưa được kiểm thử trên điện thoại thật.
- Overlay cần người dùng cấp quyền hiển thị trên ứng dụng khác.
- Tích hợp file ChunkFinder vào ứng dụng chỉ đóng gói tài nguyên; chưa chứng minh pack hoạt động trên Minecraft 1.26.45 hoặc tự điều khiển được trong game.
