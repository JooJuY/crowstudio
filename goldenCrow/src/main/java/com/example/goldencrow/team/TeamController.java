package com.example.goldencrow.team;

import com.example.goldencrow.team.dto.TeamDto;
import com.example.goldencrow.user.UserService;
import com.example.goldencrow.user.dto.UserInfoDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/api/team")
public class TeamController {

    private final String SUCCESS = "SUCCESS";
    private final String FAILURE = "FAILURE";

    private final UserService userService;

    private final TeamService teamService;

    public TeamController(UserService userService, TeamService teamService) {
        this.userService = userService;
        this.teamService = teamService;
    }

    // 팀 목록 조회 GET
    // -
    @GetMapping("")
    public ResponseEntity<List<TeamDto>> teamListGet(@RequestHeader("jwt") String jwt){

        // 내가 속한 것만 골라서 반환

        if(jwt==null){
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        List<TeamDto> teamDtoList = new ArrayList<>();

        TeamDto teamDto = new TeamDto();
        teamDto.setTeamSeq(new Long(1));
        teamDto.setTeamName("team Name Sample 1");
        teamDto.setTeamLeaderSeq(new Long(1));
        teamDto.setTeamLeaderNickname("team Leader Nickname Sample 1");
        teamDtoList.add(teamDto);

        teamDto.setTeamSeq(new Long(2));
        teamDto.setTeamName("team Name Sample 2");
        teamDto.setTeamLeaderSeq(new Long(2));
        teamDto.setTeamLeaderNickname("team Leader Nickname Sample 2");
        teamDtoList.add(teamDto);

        // 일단 성공하면 이렇게 반환될 겁니다
        return new ResponseEntity<>(teamDtoList, HttpStatus.OK);

    }

    // 팀 생성 POST
    // create
    @PostMapping("/create")
    public ResponseEntity<String> teamCreatePost(@RequestHeader("jwt") String jwt, @RequestBody Map<String, String> req) {

        // 팀장이 본인으로 자동 설정될 예정임

        if(jwt==null){
            return new ResponseEntity<>(FAILURE, HttpStatus.UNAUTHORIZED);
        }

        if(req.get("teamName")==null){
            return new ResponseEntity<>(FAILURE, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(SUCCESS, HttpStatus.OK);

    }
    // 팀 수정 PUT
    // modify
    @PutMapping("/modify/{teamSeq}")
    public ResponseEntity<String> teamModifyPut(@RequestHeader("jwt") String jwt, @PathVariable Long teamSeq, @RequestBody Map<String, String> req){

        // 리더 권한 없으면 터질 예정임
        // 이전 이름과 같아도 수정함

        if(jwt==null){
            return new ResponseEntity<>(FAILURE, HttpStatus.UNAUTHORIZED);
        }

        if(req.get("teamName")==null){
            return new ResponseEntity<>(FAILURE, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(SUCCESS, HttpStatus.OK);

    }

    // 팀 삭제 DELETE
    // delete/{teamSeq}
    @DeleteMapping("/delete/{teamSeq}")
    public ResponseEntity<String> teamDelete(@RequestHeader("jwt") String jwt, @PathVariable Long teamSeq){

        // 리더 권한 없으면 터질 예정임
        // 이후 팀 파일과 이것저것 싹 날아감

        if(jwt==null){
            return new ResponseEntity<>(FAILURE, HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(SUCCESS, HttpStatus.OK);

    }

    // 팀원 목록 조회 GET
    // member/{teamSeq}
    @GetMapping("/member/{teamSeq}")
    public ResponseEntity<List<UserInfoDto>> memberListGet(@RequestHeader("jwt") String jwt, @PathVariable Long teamSeq){

        // 자기 팀이면 ㄱㅊ

        if(jwt==null){
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        List<UserInfoDto> userInfoDtoList = new ArrayList<>();

        // 일단 성공하면 이렇게 반환될 겁니다
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setUserSeq(new Long(1));
        userInfoDto.setUserId("userIdSample");
        userInfoDto.setUserNickname("userNicknameSample");
        userInfoDto.setUserProfile("user/profile/Sample/jpg");
        userInfoDtoList.add(userInfoDto);

        userInfoDto.setUserSeq(new Long(2));
        userInfoDto.setUserId("userIdSample2");
        userInfoDto.setUserNickname("userNicknameSample2");
        userInfoDto.setUserProfile("user/profile/Sample/jpg2");
        userInfoDtoList.add(userInfoDto);

        return new ResponseEntity<>(userInfoDtoList, HttpStatus.OK);

    }

    // 팀원 추가 PUT
    // add
    @PutMapping("/add")
    public ResponseEntity<String> memberAddPut(@RequestHeader("jwt") String jwt, @RequestBody Map<String, Long> req){

        // 리더 권한 없으면 터질 예정임
        // 이미 팀 내 유저면 터질 예정임

        if(jwt==null){
            return new ResponseEntity<>(FAILURE, HttpStatus.UNAUTHORIZED);
        }

        if(req.get("teamSeq")==null || req.get("memberSeq")==null){
            return new ResponseEntity<>(FAILURE, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(SUCCESS, HttpStatus.OK);

    }

    // 팀원 삭제 DELETE
    // remove
    @DeleteMapping("/remove")
    public ResponseEntity<String> memberRemoveDelete(@RequestHeader("jwt") String jwt, @RequestBody Map<String, Long> req){

        // 리더 권한 없으면 터질 예정임
        // 팀 내 유저가 아니면 터질 예정임
        // 스스로는 내보낼 수 없음

        if(jwt==null){
            return new ResponseEntity<>(FAILURE, HttpStatus.UNAUTHORIZED);
        }

        if(req.get("teamSeq")==null || req.get("memberSeq")==null){
            return new ResponseEntity<>(FAILURE, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(SUCCESS, HttpStatus.OK);

    }

    // 팀장 위임 PUT
    // beLeader
    @PutMapping("/beLeader")
    public ResponseEntity<String> memberBeLeaderPut(@RequestHeader("jwt") String jwt, @RequestBody Map<String, Long> req){

        // 리더 권한 없으면 터질 예정임
        // 팀 내 유저가 아니면 터질 예정임

        if(jwt==null){
            return new ResponseEntity<>(FAILURE, HttpStatus.UNAUTHORIZED);
        }

        if(req.get("teamSeq")==null || req.get("memberSeq")==null){
            return new ResponseEntity<>(FAILURE, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(SUCCESS, HttpStatus.OK);

    }

    // 팀 탈퇴 DELETE
    // quit/{teamSeq}
    @DeleteMapping("/quit/{teamSeq}")
    public ResponseEntity<String> memberQuitDelete(@RequestHeader("jwt") String jwt, @PathVariable Long teamSeq){

        // 리더면 못 나갈 예정임
        // 원래 내 팀이 아니었어도 못 나갈 예정임

        if(jwt==null){
            return new ResponseEntity<>(FAILURE, HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(SUCCESS, HttpStatus.OK);

    }

}