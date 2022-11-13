package com.lodong.spring.golfreservation.controller;

import com.lodong.spring.golfreservation.responseentity.StatusEnum;
import com.lodong.spring.golfreservation.responseentity.service.MainService;
import com.lodong.spring.golfreservation.util.MakeResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("rest/v1/main")
public class MainController {
    private final ResourceLoader resourceLoader;
    private final MainService mainService;

    public MainController(@Autowired ResourceLoader resourceLoader, MainService mainService) {
        this.resourceLoader = resourceLoader;
        this.mainService = mainService;
    }

    @GetMapping("/get/advertisement")
    public ResponseEntity<?> getAdvertisementUrl() {
        List<String> advertisementList = mainService.getAdverTisementList();

        StatusEnum statusEnum = StatusEnum.OK;
        String message = "광고 사진 이름 목록";
        return MakeResponseEntity.getResponseMessage(statusEnum, message, advertisementList);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<?> instructorFileDownload(@PathVariable String fileName) {
        Resource resource = new ClassPathResource("static" + File.separator + "images" + File.separator + "advertisement" + File.separator + fileName);
        try {
            InputStream is = resource.getInputStream();
            File tempFile = File.createTempFile(String.valueOf(is.hashCode()), ".png");
            tempFile.deleteOnExit();
            copyInputStreamToFile(is, tempFile);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, tempFile.getName())    //다운 받아지는 파일 명 설정
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(tempFile.length()))    //파일 사이즈 설정
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM.toString())    //바이너리 데이터로 받아오기 설정
                    .body(resource);    //파일 넘기기
        } catch (IOException e) {
            StatusEnum statusEnum = StatusEnum.NOT_FOUND;
            String message = "광고 사진이 존재하지 않습니다.";
            return MakeResponseEntity.getResponseMessage(statusEnum, message);
        }
    }

    @GetMapping("/update/holiday")
    public boolean updateHoliday(String year, String month) {
        try {
            mainService.updateHoliday(year, month);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
    }
}
