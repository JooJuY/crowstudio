package com.example.goldencrow.editor;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.example.goldencrow.common.Constants.*;

/**
 * 코드의 스타일(포맷팅), 문법검사(린트)를 처리하는 서비스
 */
@Service
public class EditorsService {
    public static final String PATH = BASE_URL + "temp/";

    /**
     * 포맷팅을 처리하는 내부 로직
     *
     * @param language  해당 파일의 언어 종류 ex. python, text
     * @param code      해당 파일의 내용
     * @return 포맷팅 처리를 한 temp 파일의 제목, 성패에 따른 result 반환
     */
    public Map<String, String> Formatting(String language, String code) {
        long now = new Date().getTime();
        Map<String, String> serviceRes = new HashMap<>();
        String type;
        // 해당 파일의 언어 확인
        switch (language) {
            case "python":
                type = ".py";
                break;
            default:
                type = ".txt";
                break;
        }

        try {
            // 현재 시간을 활용해 temp 파일명
            String name = "format" + now + type;
            // temp 파일 생성
            File file = new File(PATH + name);
            FileOutputStream ffw = new FileOutputStream(file);
            PrintWriter writer = new PrintWriter(ffw);
            // temp 파일에 code를 입력
            writer.print(code);

            // FileWriter 닫기
            writer.flush();
            writer.close();

            // ubuntu에서 포맷팅을 위해 black을 실행시키는 명령어
            String command = "black " + PATH + name;

            // Black 작동
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            serviceRes.put("result", SUCCESS);
            serviceRes.put("data", now + "");
        } catch (Exception e) {
            serviceRes.put("result", UNKNOWN);
        }
        return serviceRes;
    }

    /**
     * 포맷팅 결과를 읽어오는 내부 로직
     *
     * @param language  해당 파일의 언어 종류 ex. python, text
     * @param fileName  포맷팅한 결과가 저장되어있는 파일의 이름
     * @return          포맷팅한 결과 코드를 반환, 성패에 따른 result 반환
     */
    public Map<String, String> FormatRead(String language, String fileName) {
        Map<String, String> serviceRes = new HashMap<>();
        String type;
        // 파일의 언어 종류
        if (language.equals("python")) {
            type = ".py";
        } else {
            type = ".txt";
        }
        // 파일의 절대 경로
        String absolutePath = PATH + "format" + fileName + type;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(absolutePath));
            StringBuilder sb = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                String temp = str + "\n";
                sb.append(temp);
            }
            serviceRes.put("data", sb.toString());
            reader.close();
        } catch (Exception e) {
            // 파일의 경로가 틀렸거나 그 파일이 없는 경우
            serviceRes.put("result", NO_SUCH);
            return serviceRes;
        }

        // 사용한 temp파일 삭제
        Path filePath = Paths.get(absolutePath);
        try {
            Files.deleteIfExists(filePath);
            // 파일 삭제까지 모두 수행한 경우
            serviceRes.put("result", SUCCESS);
        } catch (Exception e) {
            // 파일 삭제가 제대로 수행되지 않은 경우
            serviceRes.put("result", UNKNOWN);
        }
        return serviceRes;
    }

    public HashMap<String, Object> Linting(String language, String code) {
        HashMap<String, Object> result = new HashMap<>();
        if (language.equals("python")) {
            try {
                File file = new File(PATH + "lint.py");
                System.out.println("lint.py 생성");
                FileOutputStream lfw = new FileOutputStream(file);
                PrintWriter writer = new PrintWriter(lfw);
                // temp.py에 code를 입력
                writer.print(code);

                // FileWriter 닫기(안 하면 오류)
                writer.flush();
                writer.close();

                // windows cmd를 가리키는 변수
                // 나중에 Ubuntu할 때 맞는 변수로 바꿀 것
                String filePath = PATH + "lint.py";
                String command = "pylint " + filePath;
                System.out.println(command);

                Process process = Runtime.getRuntime().exec(command);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;

                LinkedList<String> response = new LinkedList<>();
                ArrayList<Integer> index = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    if (line.contains("lint.py")) {
                        String[] letters = line.split(":");
                        int number = Integer.parseInt(letters[1]);
                        index.add(number);
                        response.add(letters[4].trim());
                    }
                }

                reader.close();
                result.put("data", response);
                result.put("index", index);

                try {
                    File deleteFile = new File(filePath);
                    if (deleteFile.delete()) {
                        System.out.println("파일이 정상적으로 삭제되었습니다");
                    } else {
                        System.out.println("파일이 삭제되지 않았습니다");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
                result.put("data", "null");
            }

        } else {
            return null;
        }
        return result;
    }

    public HashMap<String, Object> autoComplete(String text) {
        LinkedList<String> response = new LinkedList<>();
        response.add(text);
        response.add("통신");
        response.add("일단");
        response.add("성공");
        HashMap<String, Object> result = new HashMap<>();
        result.put("data", response);
        return result;
    }
}
