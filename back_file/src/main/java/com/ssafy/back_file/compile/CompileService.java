package com.ssafy.back_file.compile;

import com.ssafy.back_file.File.FileDto.FileCreateDto;
import com.ssafy.back_file.File.Service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;

@Service
public class CompileService {

    @Autowired
    private FileService fileService;

    // 실행 결과 반환 로직
    public String resultString(String cmd) {
        String result = "";
        StringBuffer sb = new StringBuffer();
        try{
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String cl = null;
            while((cl = in.readLine()) != null){
                sb.append(cl);
                sb.append("\n");
            }
            result = sb.toString();
            in.close();
            return result;
        }catch(IOException e){
            e.printStackTrace();
            return "";
        }

    }

    // 도커파일 생성
    public boolean createDockerfile(String filePath, Long teamSeq, String type) {
        int filePathIndex = filePath.lastIndexOf("/");
        String projectName = filePath.substring(filePathIndex);
//        String dockerfilePath = filePath + "/Dockerfile";
        String content = "";
        if (Objects.equals(type, "django")) {
            content = "FROM python:3.10\n" +
                    "RUN pip3 install django\n" +
                    "WORKDIR" + filePath + "\n" +
                    "COPY . .\n" +
                    "WORKDIR ./" + projectName +
                    "CMD [\"python3\", \"manage.py\", \"runserver\", \"0.0.0.0:0\"]\n" +
                    "EXPOSE 0";
        }

        return saveDockerfile(filePath, teamSeq, content);
    }

    // 파일 저장
    public boolean saveDockerfile(String filePath, Long teamSeq, String content) {
        // create
        FileCreateDto fileCreateDto = new FileCreateDto();
        fileCreateDto.setFilePath(filePath);
        fileCreateDto.setFileTitle("Dockerfile");
        boolean created = fileService.createFile(fileCreateDto, teamSeq);
        if (!created) { return false; }
        // save
        return fileService.saveFile(filePath, content);
    }


    public String pyCompile(Map<String, String> req, Long teamSeq) {
        String filePath = req.get("filePath");
        int filePathIndex = filePath.lastIndexOf("/");
        String projectName = filePath.substring(filePathIndex);
        // 퓨어파이썬일 때
        if (Objects.equals(req.get("type"), "pure")) {
            String command = String.format("python3 %s", filePath);
            return resultString(command);
        }
        // Django 프로젝트일 때
        else if (Objects.equals(req.get("type"), "django")) {
            // 도커파일 추가
            boolean dockerfile = createDockerfile(filePath, teamSeq, req.get("type"));
            if (!dockerfile) { return "Can't make dockerfile"; }
            // 도커 이미지 빌드
            String image = String.format("docker build -t %s .", projectName);
            try {
                Process p = Runtime.getRuntime().exec(image);
                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            } catch(IOException e){
                e.printStackTrace();
            }

            // 도커 런
            String command = String.format("docker run -d --name %s -p 0:0 %s", projectName, projectName);
            return resultString(command);

        }

        else {
            return "haha";
        }


//        try {
//            String cmd = String.format("python %s", req.get("filePath"));
//            Process p = Runtime.getRuntime().exec("cmd /c " + cmd);
//
//            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            String l = null;
//            StringBuffer sb = new StringBuffer();
//            while ((l = r.readLine()) != null) {
//                sb.append(l);
//                sb.append("\n");
//            }
//            return sb.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
    }
}
