package com.example.tag_dev.USER.Service;

import com.example.tag_dev.SYSTEM.DTO.DeptDTO;
import com.example.tag_dev.SYSTEM.Model.Dept_Info;
import com.example.tag_dev.SYSTEM.Repository.DeptRepository;
import com.example.tag_dev.Config.JwtTokenProvider;
import com.example.tag_dev.USER.DTO.UserDTO;
import com.example.tag_dev.USER.Model.User;
import com.example.tag_dev.LOG.LogRepository.DeptLogRepository;
import com.example.tag_dev.LOG.Model.DeptLog;
import com.example.tag_dev.LOG.Model.UserLog;
import com.example.tag_dev.LOG.LogRepository.UserLogRepository;
import com.example.tag_dev.USER.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    private final UserLogRepository userLogRepository;
    private final DeptRepository deptRepository;
    private final DeptLogRepository deptLogRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserLogRepository userLogRepository, DeptRepository deptRepository, DeptLogRepository deptLogRepository, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userLogRepository = userLogRepository;
        this.deptRepository = deptRepository;
        this.deptLogRepository = deptLogRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    // ======================== 로그인 관련 ==============================//
    // 로그인
    public ResponseEntity<?> login(UserDTO userDTO , HttpServletRequest request) {
        Optional<User> userOpt = userRepository.findByLoginId(userDTO.getLogin_id());

        if(userOpt.isPresent()){
            User user = userOpt.get();
            UserLog userLog = new UserLog();
            boolean match = passwordEncoder.matches(userDTO.getPassword(), user.getPassword());
            if(match){
                System.out.println("[로그인] 비밀번호 일치, 로그인 성공");
                user.setFail_login_cnt(0L);
                String token = jwtTokenProvider.generateToken(
                        user.getUserName(),
                        user.getUser_acl(),
                        user.getUserId()
                );
                user.setJwt_token(token);
                user.setLogin_dt(new Date());
                long expiryMillis = System.currentTimeMillis() + 7L * 24 * 60;
                Date expiryDate = new Date(expiryMillis);
                user.setJwt_expiry_pt(expiryDate);

                userRepository.save(user);

                userLog.setLoginId(user.getLoginId());
                userLog.setStatus("성공");
                userLog.setIp_addr(getClientIP(request));
                userLog.setHttp_refr(request.getHeader("referer"));
                userLog.setRegDt(new Date());

                userLogRepository.save(userLog);

                return ResponseEntity.ok(Map.of(
                        "token" , token,
                        "user_id" , user.getUserId(),
                        "user_name" , user.getUserName(),
                        "login_id" , user.getLoginId(),
                        "user_acl" , user.getUser_acl(),
                        "user_email" , user.getUserEmail()
                ));
            } else{
                System.out.println("[로그인] 비밀번호 불일치, 로그인 실패");
                long failCount = user.getFail_login_cnt() != null ? user.getFail_login_cnt(): 0L;
                failCount++;
                user.setFail_login_cnt(failCount);
                if(failCount >=5 ){
                    user.setUser_stat("LOCK");
                    user.setUser_acl("0");
                    userRepository.save(user);

                    userLog.setLoginId(user.getLoginId());
                    userLog.setStatus("잠김");
                    userLog.setIp_addr(getClientIP(request));
                    userLog.setHttp_refr(request.getHeader("referer"));
                    userLog.setRegDt(new Date());

                    userLogRepository.save(userLog);

                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                userRepository.save(user);

                userLog.setLoginId(user.getLoginId());
                userLog.setStatus("실패");
                userLog.setIp_addr(getClientIP(request));
                userLog.setHttp_refr(request.getHeader("referer"));
                userLog.setRegDt(new Date());

                userLogRepository.save(userLog);

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호 일치 하지 않음");
            }
        } else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 사용자");
        }
    }
    // 로그아웃
    public ResponseEntity<?> logout(String jwtToken ,  HttpServletRequest request){
        if(!jwtTokenProvider.validateToken(jwtToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않는 토큰");
        }
        Long userId = jwtTokenProvider.extractUserId(jwtToken);
        if(userId != null){
            Optional<User> userOpt = userRepository.findById(userId);
            if(userOpt.isPresent()){
                User user = userOpt.get();
                user.setJwt_token(null);
                user.setJwt_expiry_pt(null);
                userRepository.save(user);

                UserLog userLog = new UserLog();

                userLog.setLoginId(user.getLoginId());
                userLog.setStatus("로그아웃");
                userLog.setIp_addr(getClientIP(request));
                userLog.setHttp_refr(request.getHeader("referer"));
                userLog.setRegDt(new Date());

                userLogRepository.save(userLog);

            }
        }
        return ResponseEntity.ok("로그아웃 성공");
    }

    // ID 중복 확인
    public Optional<User> checkLoginId(String loginId){
        return userRepository.findByLoginId(loginId);
    }

    // 아이디 찾기
    public Optional<User> findLoginId(UserDTO userDTO) {
        return userRepository.findByNameAndEmailOrPhone(userDTO.getUser_name() , userDTO.getUser_email() , userDTO.getUser_phone_num());
    }

    // 비밀번호 찾기
    public Optional<User> findPassword(UserDTO userDTO){
        return userRepository.findByUserNameAndLoginIdAndUserEmailAndUserPhoneNum(userDTO.getUser_name() , userDTO.getLogin_id() , userDTO.getUser_email() , userDTO.getUser_phone_num());
    }

    // 회원 가입
    public ResponseEntity<?> register(UserDTO userDTO , HttpServletRequest request) {
        if(userRepository.findByLoginId(userDTO.getLogin_id()).isPresent()){
            throw new IllegalArgumentException("이미 사용중인 로그인 ID");
        }
        User user = new User();
        user.setUserName(userDTO.getUser_name());
        user.setUser_en_name(userDTO.getUser_en_name());
        user.setLoginId(userDTO.getLogin_id());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setUserEmail(userDTO.getUser_email());
        user.setUserPhoneNum(userDTO.getUser_phone_num());
        user.setUser_stat("PENDING");

        userRepository.save(user);

        UserLog userLog = new UserLog();
        userLog.setLoginId(user.getLoginId());
        userLog.setIp_addr(getClientIP(request));
        userLog.setHttp_refr(request.getHeader("referer"));
        userLog.setRegDt(new Date());
        userLog.setStatus("회원가입");
        userLogRepository.save(userLog);

        return ResponseEntity.ok(user);
    }

    // ====================================== 관리자 관련 =====================================//
    // 사용자 업데이트 ( 관리자 기능 )
    public ResponseEntity<?> updateUserInfo(String userId, UserDTO userDto, String jwtToken) {
        if(!jwtTokenProvider.validateToken(jwtToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<User> userOpt = userRepository.findById(Long.parseLong(userId));

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean isUpdated = false;

            if (userDto.getUser_name() != null && !userDto.getUser_name().isEmpty()) {
                user.setUserName(user.getUserName());
                isUpdated = true;
            }
            if (userDto.getUser_en_name() != null && !userDto.getUser_en_name().isEmpty()) {
                user.setUser_en_name(user.getUser_en_name());
                isUpdated = true;
            }
            if (userDto.getUser_email() != null && !userDto.getUser_email().isEmpty()) {
                user.setUserEmail(user.getUserEmail());
                isUpdated = true;
            }
            if (userDto.getUser_phone_num() != null && !userDto.getUser_phone_num().isEmpty()) {
                user.setUserPhoneNum(user.getUserPhoneNum());
                isUpdated = true;
            }
            if (userDto.getDept_cd() != null && !userDto.getDept_cd().isEmpty()) {
                user.setDept_cd(user.getDept_cd());
                isUpdated = true;
            }
            if (userDto.getUser_job() != null && !userDto.getUser_job().isEmpty()) {
                user.setUser_job(user.getUser_job());
                isUpdated = true;
            }
            if (userDto.getUser_stat() != null && !userDto.getUser_stat().isEmpty()) {
                user.setUser_stat(user.getUser_stat());
                isUpdated = true;
            }
            if (userDto.getHire_dt() != null) {
                user.setHire_dt(user.getHire_dt());
                isUpdated = true;
            }

            if (isUpdated) {
                user.setUpdate_dt(new Date());
                Long updateUserId = jwtTokenProvider.extractUserId((jwtToken));
                user.setUpdate_id(updateUserId);
                userRepository.save(user);

                UserLog userLog = new UserLog();
                userLog.setLoginId(user.getLoginId());
                userLog.setUpdate_dt(new Date());
                userLog.setUpdate_id(jwtTokenProvider.extractUserId((jwtToken)));
                userLog.setStatus("정보수정");
                userLogRepository.save(userLog);

                return ResponseEntity.ok("사용자 정보 수정 완료");
            } else {
                return ResponseEntity.ok("수정할 내용이 없습니다.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자 없음");
        }
    }

    // 권한 변경 ( 관리자 기능 )
    public ResponseEntity<?> changeAcl(String token, Long userId, String userAcl) {
        if(!jwtTokenProvider.validateToken(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setUser_acl(userAcl);
            userRepository.save(user);

            UserLog userLog = new UserLog();
            userLog.setLoginId(user.getLoginId());
            userLog.setUpdate_dt(new Date());
            userLog.setUpdate_id(jwtTokenProvider.extractUserId((token)));
            userLog.setStatus("권한수정");
            userLogRepository.save(userLog);

            return ResponseEntity.ok("권한 수정 완료");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 유저를 찾을 수 없음");
        }
    }

    // 사용자 전체 조회 ( 관리자 기능 )
    public ResponseEntity<?> getAllUser(String token) {
        if(!jwtTokenProvider.validateToken(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }


    // 해당 사용자 비밀번호 변경 ( 관리자 기능 )
    public ResponseEntity<?> changePassword(UserDTO userDTO, String token) {
        if(!jwtTokenProvider.validateToken(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지않음");
        }
        Optional<User> user = userRepository.findByLoginId(userDTO.getLogin_id());
        if(user.isPresent()){
            User users = user.get();
            users.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            userRepository.save(users);

            UserLog userLog = new UserLog();
            userLog.setLoginId(users.getLoginId());
            userLog.setUpdate_dt(new Date());
            userLog.setUpdate_id(jwtTokenProvider.extractUserId((token)));
            userLog.setStatus("비밀번호 변경");
            userLogRepository.save(userLog);

            return ResponseEntity.ok("변경 성공");
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("<UNK>");
        }

    }

    // 사용자 생성 ( 관리자 기능 )
    public ResponseEntity<?> createUser(String token, UserDTO userDTO) {
        // JWT 토큰 검증
        if(!jwtTokenProvider.validateToken(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // 로그인 ID 중복 확인
        if(userRepository.findByLoginId(userDTO.getLogin_id()).isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용중인 로그인 ID입니다.");
        }
        
        try {
            // 새 사용자 생성
            User user = new User();
            user.setUserName(userDTO.getUser_name());
            user.setUser_en_name(userDTO.getUser_en_name());
            user.setLoginId(userDTO.getLogin_id());
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setUserEmail(userDTO.getUser_email());
            user.setUserPhoneNum(userDTO.getUser_phone_num());
            user.setDept_cd(userDTO.getDept_cd());
            user.setUser_job(userDTO.getUser_job());
            user.setUser_acl(String.valueOf(userDTO.getUser_acl()));
            user.setUser_stat(userDTO.getUser_stat());
            user.setHire_dt(userDTO.getHire_dt());
            user.setReg_dt(new Date());
            user.setReg_id(jwtTokenProvider.extractUserId(token));
            
            userRepository.save(user);
            
            // 로그 기록
            UserLog userLog = new UserLog();
            userLog.setLoginId(user.getLoginId());
            userLog.setStatus("사용자생성");
            userLog.setRegDt(new Date());
            userLog.setUpdate_id(jwtTokenProvider.extractUserId((token)));
            userLogRepository.save(userLog);
            
            return ResponseEntity.ok("사용자가 성공적으로 생성되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 생성 중 오류가 발생: " + e.getMessage());
        }
    }
    // 사용자 삭제 ( 관리자 기능 )
    public ResponseEntity<?> deleteUser(String loginId, String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            userRepository.deleteByLoginId(loginId);
            UserLog userLog = new UserLog();
            userLog.setLoginId(loginId);
            userLog.setStatus("삭제됨");
            userLog.setUpdate_dt(new Date());
            userLog.setUpdate_id(jwtTokenProvider.extractUserId((token)));
            userLogRepository.save(userLog);

            return ResponseEntity.ok("사용자가 삭제 완료.");
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 삭제 중 오류가 발생: " + e.getMessage());
        }
    }

    // 사용자 비밀번호 변경 ( 관리자 기능 )
    public ResponseEntity<?> changeUserPasswordByAdmin(String loginId, UserDTO userDTO, String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            Optional<User> userOpt = userRepository.findByLoginId(loginId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                user.setUpdate_dt(new Date());
                user.setUpdate_id(jwtTokenProvider.extractUserId(token));
                userRepository.save(user);

                // 로그 기록
                UserLog userLog = new UserLog();
                userLog.setLoginId(loginId);
                userLog.setStatus("관리자비밀번호변경");
                userLog.setUpdate_dt(new Date());
                userLog.setUpdate_id(jwtTokenProvider.extractUserId(token));
                userLogRepository.save(userLog);

                return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비밀번호 변경 중 오류가 발생: " + e.getMessage());
        }
    }

    //========================================= 부서 관련 ===================================//
    // 부서 등록 ( 관리자 기능 )
    public ResponseEntity<?> createDept(String token, DeptDTO deptDTO) {
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try{
            Dept_Info deptInfo = new Dept_Info();
            deptInfo.setDeptCode(deptDTO.getDeptCode());
            deptInfo.setDept(deptDTO.getDept());
            deptInfo.setRegDt(new Date());
            deptInfo.setUserName(jwtTokenProvider.extractUserName(token));

            deptRepository.save(deptInfo);

            DeptLog deptLog = new DeptLog();
            deptLog.setDeptStatus(deptDTO.getDeptStatus());
            deptLog.setUserName(jwtTokenProvider.extractUserName(token));
            deptLog.setRegDt(deptDTO.getRegDt());
            deptLog.setDeptCode(deptDTO.getDeptCode());
            deptLog.setDept(deptDTO.getDept());
            deptLogRepository.save(deptLog);

            return ResponseEntity.ok(deptInfo);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("부서 등록 중 오류가 발생: " + e.getMessage());
        }
    }
    // 부서 조회
    public ResponseEntity<?> getDept(String dept , String deptCode) {
        try{
            boolean hasSearchCondition = (dept != null && !dept.trim().isEmpty()) || (deptCode != null && !deptCode.trim().isEmpty());
            if(!hasSearchCondition){
                return ResponseEntity.ok("아무것도 없음");
            }
            Optional<Dept_Info> deptInfo = deptRepository.findByDeptCode(dept);

            return ResponseEntity.ok("부서 조회");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("부서 수정 중 오류가 발생: " + e.getMessage());
        }
    }
    // 부서 수정 ( 관리자 기능 )
    public ResponseEntity<?> updateDept(String deptCode, String token , DeptDTO deptDTO) {
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<Dept_Info> deptOpt = deptRepository.findByDeptCode(deptCode);
        if (deptOpt.isPresent()) {
            Dept_Info deptInfo = deptOpt.get();
            boolean isUpdated = false;
            if(deptDTO.getDeptCode() != null && !deptDTO.getDeptCode().trim().isEmpty()){
                deptInfo.setDeptCode(deptDTO.getDeptCode());
                isUpdated = true;
            }
            if(deptDTO.getDept() != null && !deptDTO.getDept().trim().isEmpty()){
                deptInfo.setDept(deptDTO.getDept());
                isUpdated = true;
            }
            if(deptDTO.getDeptStatus() != null && !deptDTO.getDeptStatus().trim().isEmpty()){
                deptInfo.setDeptStatus(deptDTO.getDeptStatus());
                isUpdated = true;
            }
            if(isUpdated){
                deptInfo.setUpdateDt(new Date());
                deptInfo.setUpdateUser(jwtTokenProvider.extractUserName(token));
                deptRepository.save(deptInfo);

                DeptLog deptLog = new DeptLog();
                deptLog.setDeptCode(deptDTO.getDeptCode());
                deptLog.setDept(deptDTO.getDept());
                deptLog.setDeptStatus(deptDTO.getDeptStatus());
                deptLog.setRegDt(deptDTO.getRegDt());
                deptLog.setUserName(deptDTO.getUserName());
                deptLog.setUserName(jwtTokenProvider.extractUserName(token));
                deptLog.setUpdateDt(new Date());
                deptLog.setUpdateUser(jwtTokenProvider.extractUserName(token));
                deptLogRepository.save(deptLog);

                return ResponseEntity.ok("부서 수정 완료");
            }else{
                return ResponseEntity.ok("수정할 내용 없음");
            }
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("부서 없음");
        }
    }

    // IP 변환 메소드
    public static String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }

        ip = request.getHeader("Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getHeader("WL-Proxy-Client-IP"); // WebLogic
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

}
