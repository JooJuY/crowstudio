package com.example.goldencrow.compile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class SchedulerService {

    @Autowired
    private CompileService compileService;

    @Scheduled(cron = "0 * * * * *")
    public void run() {
        System.out.println("hi i'm working");
        // 모든 컨테이너 닫기
        /*
        * 우리가 사용하는 포트(컨테이너)를 제외하고 !
        * 특정 이름을 가진 것만 필터링해서 조회 (crowstudio_)
        * docker stop $(docker container ls --filter='name=crowstudio_' -q)
        * crowstudio_로 시작하는 이미지 모두 삭제
        * docker rmi $(docker images crowstudio_* -q)
        * <none>인 이미지 삭제
        * docker rmi $(docker images -f "dangling=true" -q)
        * */
        String filteringName = "crowstudio_";
//        String[] containerCmd = {"docker", "container", "ls", "--filter=\"name=crowstudio\"", "-q"}; // filtering이 문제임 왜 안돼 진자ㅠㅠㅠㅠㅠㅠㅠㅠ
        String[] containerCmd = {"/bin/sh", "-c", "docker", "container", "ls", "| grep crowstudio"};
        String containerList = compileService.resultString(containerCmd);
        if (containerList == null) {
            System.out.println("container 못찾음"); return; }
        System.out.println(containerList);
//        String[] stopCmd = {"docker", "stop", containerList};
//        System.out.println("docker stop 시작 !");
//        compileService.resultString(stopCmd);
//
//        System.out.println("docker stop 됐다 !");
//        String[] rmImgCmd = {"/bin/sh", "-c", "docker", "rmi", "$(docker", "images", filteringName +"*", "-q)"};
//        System.out.println("docker images 삭제 시작 !");
//        compileService.resultString(rmImgCmd);
//        System.out.println("docker images 삭제 됐다 !");
//        String[] rmNoneCmd = {"/bin/sh", "-c", "docker", "rmi", "$(docker", "images", "-f" ,"\"dangling=true\"", "-q)"};
//        System.out.println("none image 삭제 시작 !");
//        compileService.resultString(rmNoneCmd);
//        System.out.println("none image 삭제 됐다 !");
    }
}
