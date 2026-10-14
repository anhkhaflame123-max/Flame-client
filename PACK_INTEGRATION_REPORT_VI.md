# FLAME CLIENT 1.3 — báo cáo tích hợp pack

Bản này đóng gói riêng các pack do người dùng cung cấp vào `app/src/main/assets/packs/` và thêm danh sách xuất pack trong ứng dụng. Ứng dụng không tự cài pack vào Minecraft; người dùng chọn Xuất/Mở, lưu file, rồi nhập và kích hoạt trong Minecraft.

## Pack đi kèm
- **OS CLIENT • HUD / cosmetics / UI** (`os_client.mcpack`): Pack lớn; cần kiểm tra giấy phép và xung đột HUD.
- **GAVTI PvP Pack • BP + RP** (`gavti_pvp.mcaddon`): Có Behavior Pack và script; cần bật pack trong thế giới và thử tương thích.
- **Keystrokes + CPS Counter** (`keystrokes_cps.mcpack`): Có subpack Keystrokes + CPS hoặc CPS.
- **oSkullo's CPS Counter** (`osk_cps.mcpack`): HUD CPS dựa trên hoạt ảnh vung tay.
- **Bedrock ChunkFinder v1.2.0** (`chunkfinder.mcpack`): Pack ranh giới chunk/slime chunk; min engine 1.26.0.
- **Clear & Borderless Glass** (`clear_glass.mcpack`): Chọn subpack trong Minecraft.
- **Small Items** (`small_items.mcpack`): Chọn subpack kích thước vật phẩm trong Minecraft.
- **Low Fire** (`low_fire.mcpack`): Pack Low Fire đã gửi trước đó.

## Xung đột và giới hạn
- Không gộp các pack thành một resource pack, vì nhiều pack cùng sửa entity/player HUD hoặc tài nguyên chung.
- Keystrokes+CPS và oSkullo CPS Counter có thể xung đột vì cả hai sửa `entity/player.entity.json`. Chỉ bật một pack HUD tương ứng để thử.
- PvP Pack có Behavior Pack + script; cần bật Experimental/API phù hợp nếu Minecraft yêu cầu. Chưa xác minh runtime trên 1.26.52.
- OS CLIENT là pack bên thứ ba; giữ nguyên nội dung và ghi nhận tác giả. Kiểm tra giấy phép trước khi phân phối công khai.
- APK chưa được build hoặc kiểm thử trong Minecraft ở môi trường này. Đây là source project để build.
