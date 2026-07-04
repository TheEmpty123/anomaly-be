### Một lưu ý về version

Bạn đang có 3 nơi chứa version:

* `pom.xml`
* `Chart.yaml`
* `values.yaml` (`image.tag`)

Để tránh quên cập nhật, quy trình release của mình là:

1. Đổi `<version>` trong `pom.xml` (ví dụ `1.0.1`).
2. Đổi `Chart.yaml`:

   * `version: 1.0.1`
   * `appVersion: "1.0.1"`
3. Đổi `values.yaml`:

   * `image.tag: "1.0.1"`

Sau này nếu muốn tự động hóa, Jenkins có thể cập nhật `image.tag` khi deploy, nhưng giai đoạn đầu mình khuyên giữ thủ công để dễ kiểm soát.
