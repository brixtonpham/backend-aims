package Project_ITSS.controller;


import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

@Repository
@CrossOrigin(origins = "http://localhost:3000")
public class ImageUploadController {
    //    @PostMapping("/upload-image")
//    public Map<String, Object> uploadImage(@RequestParam("file") MultipartFile file) {
//        Map<String, Object> json = new HashMap<>();

//        try {
//            // Kiểm tra file có tồn tại không
//            if (file.isEmpty()) {
//                json.put("status", 0);
//                json.put("message", "Please select a file to upload");
//                return json;
//            }

//            // Kiểm tra loại file (chỉ cho phép ảnh)
//            String contentType = file.getContentType();
//            if (contentType == null || !contentType.startsWith("image/")) {
//                json.put("status", 0);
//                json.put("message", "Only image files are allowed");
//                return json;
//            }

//            // Tạo thư mục lưu trữ nếu chưa tồn tại
//            String uploadDir = "D:\\frontend-aims-main(5)\\frontend-aims-main(2)\\frontend-aims-main(2)\\frontend-aims-main\\public";
//            File directory = new File(uploadDir);
//            if (!directory.exists()) {
//                directory.mkdirs();
//            }

//            // Tạo tên file unique để tránh trùng lặp
//            String originalFilename = file.getOriginalFilename();
//            String fileExtension = "";
//            if (originalFilename != null && originalFilename.contains(".")) {
//                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
//            }
//            String filename = UUID.randomUUID().toString() + fileExtension;

//            // Lưu file
//            Path filePath = Paths.get(uploadDir + filename);
//            Files.copy(file.getInputStream(), filePath);

//            json.put("status", 1);
//            json.put("message", "File uploaded successfully");
//            json.put("filename", filename);
//            json.put("filepath", filePath.toString());

//        } catch (IOException e) {
//            e.printStackTrace();
//            json.put("status", 0);
//            json.put("message", "Failed to upload file: " + e.getMessage());
//        }

//        return json;
//    }
}
