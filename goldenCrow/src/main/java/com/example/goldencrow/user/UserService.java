package com.example.goldencrow.user;

import com.example.goldencrow.common.CryptoUtil;
import com.example.goldencrow.user.dto.UserInfoDto;
import com.example.goldencrow.user.entity.UserEntity;
import com.example.goldencrow.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    // 회원가입
    public Map<String, String> signupService(String userId, String userPassword, String userNickname){

        Map<String, String> res = new HashMap<>();

        // 존재하는 아이디인지 체크
        if(userRepository.findUserEntityByUserId(userId).isPresent()) {
            // 존재할 경우, result에서 결과를 전달함
            res.put("result", "duplicate error");
            System.out.println("duplicate error");

        } else {
            // 존재하지 않을 경우, 회원가입 진행

            // 아이디와 닉네임만 받아서 엔티티 생성
            // 비밀번호 인코딩해서 엔티티에 기록
            // 리프레시토큰 발급해와서 엔티티에 기록
            UserEntity userEntity = new UserEntity(userId, userNickname);
            userEntity.setUserPassWord(CryptoUtil.Sha256.hash(userPassword));
            userEntity.setUserRefresh("");

            // 일단 이 상태로 등록한 다음
            userRepository.saveAndFlush(userEntity);
            System.out.println("일단 저장");

            // userSeq를 다시 읽어와서
            // 액세스 토큰과 리프레시 토큰을 만듦
            Long userSeq = userRepository.findUserEntityByUserId(userId).get().getUserSeq();
            userEntity.setUserSeq(userSeq);
            userEntity.setUserRefresh(jwtService.createRefresh(userSeq));

            // 리프레시 토큰 저장된 상태로 업데이트
            userRepository.save(userEntity);
            System.out.println("업데이트");

            // 액세스 토큰 발급
            String jwt = jwtService.createAccess(userSeq);
            res.put("result", "success");
            res.put("jwt", jwt);

        }

        return res;
    }

    // 로그인
    public Map<String, String> loginService(String userId, String userPassword) {

        Map<String, String> res = new HashMap<>();

        // 존재하는 아이디인지 체크
        if(userRepository.findUserEntityByUserId(userId).isPresent()) {

            // 존재할 경우 일치하는지 검증
            UserEntity userEntity = userRepository.findUserEntityByUserId(userId).get();
            String checkPassword = CryptoUtil.Sha256.hash(userPassword);

            if(userEntity.getUserPassWord().equals(checkPassword)){
                // 만약 비번이 같으면
                // 액세스 토큰 발급
                String jwt = jwtService.createAccess(userEntity.getUserSeq());
                res.put("result", "success");
                res.put("jwt", jwt);
            } else {
                // 비번이 틀렸으면
                res.put("result", "wrong password");
            }

        } else {
            res.put("result", "have to sign up");
        }

        return res;
    }

    // 회원정보조회
    public UserInfoDto infoService(String jwt){

        UserInfoDto userInfoDto = new UserInfoDto();

        try {
            UserEntity userEntity = userRepository.findById(jwtService.JWTtoUserSeq(jwt)).get();
            userInfoDto = new UserInfoDto(userEntity);
            userInfoDto.setResult(UserInfoDto.Result.SUCCESS);

        } catch (Exception e) {
            userInfoDto.setResult(UserInfoDto.Result.FAILURE);
            throw new RuntimeException(e);
        }

        return userInfoDto;

    }

    // 닉네임수정
    public String editNicknameService(String jwt, Map<String, String> req){

        // jwt 체크는 인터셉터에서 해서 넘어왔을테니까

        try {
            // jwt에서 userSeq를 뽑아내고
            Long userSeq = jwtService.JWTtoUserSeq(jwt);
            System.out.println(userSeq);

            // userSeq로 userEntity를 뽑아낸 다음
            UserEntity userEntity = userRepository.findById(userSeq).get();

            // userEntity의 닉네임 부분을 req에서 꺼내온 값으로 수정
            userEntity.setUserNickname(req.get("userNickname"));

            // saveAndFlush
            userRepository.saveAndFlush(userEntity);

            // 성공 여부 반환
            return "success";

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }


    }

    // 프로필사진 수정
    public String editProfileService(String jwt, Map<String, String> req){

        // jwt 체크는 인터셉터에서 해서 넘어왔을테니까

        try {
            // jwt에서 userSeq를 뽑아내고
            Long userSeq = jwtService.JWTtoUserSeq(jwt);

            // userSeq로 userEntity를 뽑아낸 다음
            UserEntity userEntity = userRepository.findById(userSeq).get();

            // userEntity의 프로필 부분을 req에서 꺼내온 값으로 수정
            userEntity.setUserProfile(req.get("userProfile"));

            // saveAndFlush
            userRepository.saveAndFlush(userEntity);

            // 성공 여부 반환
            return "success";

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }

    }

    // 비밀번호 수정
    public String editPasswordService(String jwt, Map<String, String> req){

        // jwt 체크는 인터셉터에서 해서 넘어왔을테니까

        try {
            // jwt에서 userSeq를 뽑아내고
            Long userSeq = jwtService.JWTtoUserSeq(jwt);

            // userSeq로 userEntity를 뽑아낸 다음
            UserEntity userEntity = userRepository.findById(userSeq).get();

            // 유저패스워드가 같은지 확인
            String originPW = userEntity.getUserPassWord();
            String testPW = CryptoUtil.Sha256.hash(req.get("userPassword"));

            if(originPW.equals(testPW)) {
                // userEntity의 비밀번호 부분을 req에서 꺼내온 값으로 수정
                userEntity.setUserPassWord(CryptoUtil.Sha256.hash(req.get("userNewPassword")));
                userRepository.saveAndFlush(userEntity);
                return "success";

            } else {
                return "error";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }

    }

    // 회원탈퇴
    public String quitUser(String jwt) {

        try {
            // 사용자 정보 불러와서 체크하고
            // jwt에서 userSeq를 뽑아내고
            Long userSeq = jwtService.JWTtoUserSeq(jwt);

            // userSeq로 userEntity를 뽑아낸 다음
            UserEntity userEntity = userRepository.findById(userSeq).get();

            // 해당 유저 삭제
            userRepository.delete(userEntity);
            return "success";

        } catch (Exception e) {
            e.printStackTrace();
            return "error";

        }

    }

    // 개인 환경세팅 저장
    public String personalPost(String jwt, UserInfoDto data) {

        try {
            // 사용자 정보 불러와서 체크하고
            // jwt에서 userSeq를 뽑아내고
            Long userSeq = jwtService.JWTtoUserSeq(jwt);

            // userSeq로 userEntity를 뽑아낸 다음
            UserEntity userEntity = userRepository.findById(userSeq).get();

            // 받아온 json을 String으로 바꾸기
            JSONObject jsonObject = new JSONObject(data);
            String settings = jsonObject.toString();
            userEntity.setUserSettings(settings);

            // 해당 부분 등록 (수정)
            userRepository.saveAndFlush(userEntity);

            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }

    }

    // 개인 환경세팅 조회
    public UserInfoDto personalGet(String jwt) {

        ObjectMapper mapper = new ObjectMapper();
        UserInfoDto userInfoDto = new UserInfoDto();

        try {
            // 사용자 정보 불러와서 체크하고
            // jwt에서 userSeq를 뽑아내고
            Long userSeq = jwtService.JWTtoUserSeq(jwt);

            // userSeq로 userEntity를 뽑아낸 다음
            UserEntity userEntity = userRepository.findById(userSeq).get();
            String data = userEntity.getUserSettings();

            userInfoDto = mapper.readValue(data, UserInfoDto.class);
            userInfoDto.setResult(UserInfoDto.Result.SUCCESS);

        } catch (Exception e) {
            e.printStackTrace();
            userInfoDto.setResult(UserInfoDto.Result.FAILURE);
        }

        return userInfoDto;

    }

    // 마이페이지 조회
    public UserInfoDto mypage(Long userSeq) {

        UserInfoDto userInfoDto = new UserInfoDto();

        try {
            UserEntity userEntity = userRepository.findById(userSeq).get();
            userInfoDto = new UserInfoDto(userEntity);
            userInfoDto.setResult(UserInfoDto.Result.SUCCESS);

        } catch (Exception e) {
            userInfoDto.setResult(UserInfoDto.Result.FAILURE);
            throw new RuntimeException(e);
        }

        return userInfoDto;


    }

}
