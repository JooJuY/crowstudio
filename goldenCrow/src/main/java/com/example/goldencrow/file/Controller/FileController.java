package com.example.goldencrow.file.Controller;

import com.example.goldencrow.file.FileDto.FileCreateDto;
import com.example.goldencrow.file.Service.FileService;
import com.example.goldencrow.user.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import static java.lang.System.out;

@RestController
@RequestMapping(value = "/api/files")
public class FileController {

    private final FileService fileService;
    private final JwtService jwtService;
    private String baseUrl = "/home/ubuntu/crow_data/";
    public FileController(FileService fileService, JwtService jwtService) {
        this.fileService = fileService;
        this.jwtService = jwtService;
    }
    /**
     *
     * @param fileCreateDto
     * @return Message
     * 파일 생성 포스트 요청
     * 기본 경로를 잡아줄 지? 아니면 그냥 보내줄지?
     */

    @PostMapping("/{teamSeq}")
    public ResponseEntity<String> userFileCreate(@RequestHeader("Authorization") String jwt,@RequestParam Integer type,@PathVariable Long teamSeq, @RequestBody FileCreateDto fileCreateDto) {
        if (fileService.createFile(fileCreateDto, type, teamSeq)) {
            String newFilePath = fileCreateDto.getFilePath()  + "/" + fileCreateDto.getFileTitle();
            return new ResponseEntity<>(newFilePath, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("파일 생성에 실패했습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @param teamSeq
     * 파일 삭제 요청
     */
    @DeleteMapping("/{teamSeq}")
    public ResponseEntity<String> userFileDelete(@RequestHeader("Authorization") String jwt,@PathVariable Long teamSeq, @RequestParam Integer type,@RequestBody HashMap<String, String> filePath) {
        if (fileService.deleteFile(filePath.get("filePath"), type,teamSeq)) {
            return new ResponseEntity<>("파일 삭제를 성공했습니다.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("파일 삭제를 실패했습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 파일 이름 변경 요청
     * @param
     * @return
     */
    @PutMapping("/{teamSeq}/files")
    public ResponseEntity<String> fileNameUpdate(@PathVariable Long teamSeq, @RequestBody HashMap<String, String> filePath) {
        String newFilePath = filePath.get("filePath");
        String renameFilePath = filePath.get("filePath") + "\\" + filePath.get("newFileName");
        File targetFile = new File(newFilePath);
        File reNameFile = new File(renameFilePath);

        boolean result = targetFile.renameTo(reNameFile);

        if (result) {
            return new ResponseEntity<>("파일 이름 변경 성공!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("파일 이름 변경 실패!", HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/{teamSeq}")
    public ResponseEntity<String> getDirectory(@RequestHeader("Authorization") String jwt, @PathVariable Long teamSeq) {
        out.println(jwtService.JWTtoUserSeq(jwt));
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @PutMapping("/{teamSeq}/files")
    public ResponseEntity<String> saveFile(@RequestHeader("Authorization") String jwt,@PathVariable Long teamSeq,@RequestBody HashMap<String, String> fileContent){

        String content = fileContent.get("fileContent");
        String filePath = fileContent.get("filePath");

        boolean result = fileService.saveFile(filePath,content);
        boolean result2 = fileService.updateFileUpdatedAt(teamSeq,filePath);
        if (result && result2) {
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/files")
    public ResponseEntity<String> readFile(@RequestHeader("Authorization") String jwt, @RequestBody String filePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("/home/ubuntu/crow_data/10000/helloWorld/helloWorld/settings.py"));
            out.println(lines);
        } catch (IOException e) {
            out.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Hello",HttpStatus.OK);
    }

}
