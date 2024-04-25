package com.example.sbb.image;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/image")
public class ImageController {

    @PostMapping("/change")
    public String imageform(Model model, @RequestParam("file") MultipartFile file){
        if (!file.isEmpty()) {
            try {
                // 이미지 저장할 경로 설정
                String uploadDir = "경로를 설정하세요"; // 예: "/uploads/"
                Path uploadPath = Paths.get(uploadDir);

                // 디렉토리가 존재하지 않으면 생성
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // 파일 이름 생성
                String filename = file.getOriginalFilename();
                Path filePath = uploadPath.resolve(filename);

                // 이미지 파일 저장
                Files.copy(file.getInputStream(), filePath);

                // 이미지 저장 후, 이미지 경로를 모델에 추가
                model.addAttribute("imageURL", "/" + uploadDir + filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return "";
    }
}
