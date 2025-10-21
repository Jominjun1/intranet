package com.example.tag_dev.USER.Service;

import com.example.tag_dev.Common.DTO.DeptDTO;
import com.example.tag_dev.Common.Model.Dept_Info;
import com.example.tag_dev.Common.Repository.DeptRepository;
import com.example.tag_dev.Config.JwtTokenProvider;
import com.example.tag_dev.LOG.Model.DeptLog;
import com.example.tag_dev.LOG.Model.LoginLog;
import com.example.tag_dev.LOG.Repository.DeptLogRepository;
import com.example.tag_dev.LOG.Repository.LoginLogRepository;
import com.example.tag_dev.USER.DTO.UserDTO;
import com.example.tag_dev.USER.Model.User;
import com.example.tag_dev.USER.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LoginLogRepository loginLogRepository;
    private final DeptRepository deptRepository;
    private final DeptLogRepository deptLogRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    // 아이디 찾기
    public Optional<User> findLoginId(UserDTO userDTO) {
        return userRepository.findByNameAndEmailOrPhone(userDTO.getUser_name(), userDTO.getUser_email(), userDTO.getUser_phone_num());
    }

    // 비밀번호 찾기
    public Optional<User> findPassword(UserDTO userDTO) {
        return userRepository.findByUserNameAndLoginIdAndUserEmailAndUserPhoneNum(userDTO.getUser_name(), userDTO.getLogin_id(), userDTO.getUser_email(), userDTO.getUser_phone_num());
    }

    // ====================================== 관리자 관련 =====================================//
    // 사용자 수정/삭제 ( 관리자 기능 )
    public ResponseEntity<?> updateUserInfo(String userId, UserDTO userDto) {
        if (userId == null || userId.isBlank() || !userId.matches("\\d+")) {
            return ResponseEntity.badRequest().body("잘못된 사용자 ID");
        }

        Optional<User> userOpt = userRepository.findById(Long.parseLong(userId));
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자 없음");
        }

        User user = userOpt.get();

        // DTO의 값들을 User 엔티티에 수동으로 매핑
        if (userDto.getUser_name() != null && !userDto.getUser_name().trim().isEmpty()) {
            user.setUserName(userDto.getUser_name());
        }
        if (userDto.getUser_en_name() != null && !userDto.getUser_en_name().trim().isEmpty()) {
            user.setUser_en_name(userDto.getUser_en_name());
        }
        if (userDto.getUser_email() != null && !userDto.getUser_email().trim().isEmpty()) {
            user.setUserEmail(userDto.getUser_email());
        }
        if (userDto.getUser_phone_num() != null && !userDto.getUser_phone_num().trim().isEmpty()) {
            user.setUserPhoneNum(userDto.getUser_phone_num());
        }
        if (userDto.getUser_acl() != null) {
            user.setUser_acl(userDto.getUser_acl().toString());
        }
        if (userDto.getDept_cd() != null && !userDto.getDept_cd().trim().isEmpty()) {
            user.setDept_cd(userDto.getDept_cd());
        }
        if (userDto.getLogin_id() != null && !userDto.getLogin_id().trim().isEmpty()) {
            user.setLoginId(userDto.getLogin_id());
        }
        if (userDto.getUser_job() != null && !userDto.getUser_job().trim().isEmpty()) {
            user.setUser_job(userDto.getUser_job());
        }
        if (userDto.getUser_stat() != null && !userDto.getUser_stat().trim().isEmpty()) {
            user.setUser_stat(userDto.getUser_stat());
        }
        if (userDto.getHire_dt() != null) {
            user.setHire_dt(userDto.getHire_dt());
        }

        // 업데이트 정보 설정
        user.setUpdate_dt(new Date());
        user.setUpdate_id("관리자"); // 임시로 1L 설정

        userRepository.save(user);

        // 로그 생성
        createUserLog(user, "관리자"); // 임시로 1L 설정

        return ResponseEntity.ok("사용자 정보 수정 완료");
    }

    // 권한 변경 ( 관리자 기능 )
    public ResponseEntity<?> changeAcl(Long userId, String userAcl) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setUser_acl(userAcl);
            userRepository.save(user);

            LoginLog loginLog = new LoginLog();
            loginLog.setLoginId(user.getLoginId());
            loginLog.setUpdate_dt(new Date());
            loginLog.setUpdate_id("관리자");
            loginLog.setStatus("권한수정");
            loginLogRepository.save(loginLog);

            return ResponseEntity.ok("권한 수정 완료");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 유저를 찾을 수 없음");
        }
    }

    // 사용자 전체 조회 ( 관리자 기능 )
    public ResponseEntity<?> getAllUser() {
        List<User> users = userRepository.findAll();
        
        // User 엔티티를 UserDTO로 변환
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUser_id(user.getUserId());
            userDTO.setUser_name(user.getUserName());
            userDTO.setUser_en_name(user.getUser_en_name());
            userDTO.setUser_email(user.getUserEmail());
            userDTO.setUser_phone_num(user.getUserPhoneNum());
            userDTO.setUser_acl(user.getUser_acl() != null ? Long.parseLong(user.getUser_acl()) : null);
            userDTO.setLogin_id(user.getLoginId());
            userDTO.setDept_cd(user.getDept_cd());
            userDTO.setUser_job(user.getUser_job());
            userDTO.setUser_stat(user.getUser_stat());
            userDTO.setReg_id(user.getReg_id());
            userDTO.setReg_dt(user.getReg_dt());
            userDTO.setUpdate_id(user.getUpdate_id());
            userDTO.setUpdate_dt(user.getUpdate_dt());
            userDTO.setLogin_dt(user.getLogin_dt());
            userDTO.setHire_dt(user.getHire_dt());
            userDTO.setChange_password_dt(user.getChange_password_dt());
            userDTO.setFail_login_cnt(user.getFail_login_cnt());
            userDTOs.add(userDTO);
        }
        
        return ResponseEntity.ok(userDTOs);
    }


    // 해당 사용자 비밀번호 변경 ( 관리자 기능 )
    public ResponseEntity<?> changePassword(UserDTO userDTO, String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지않음");
        }
        Optional<User> user = userRepository.findByLoginId(userDTO.getLogin_id());
        if (user.isPresent()) {
            User users = user.get();
            users.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            userRepository.save(users);

            LoginLog loginLog = new LoginLog();
            loginLog.setLoginId(users.getLoginId());
            loginLog.setUpdate_dt(new Date());
            loginLog.setUpdate_id("관리자");
            loginLog.setStatus("비밀번호 변경");
            loginLogRepository.save(loginLog);

            return ResponseEntity.ok("변경 성공");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호 변경중 오류 발생");
        }

    }

    // 사용자 생성 ( 관리자 기능 )
    public ResponseEntity<?> createUser(UserDTO userDTO) {

        // 로그인 ID 중복 확인
        if (userRepository.findByLoginId(userDTO.getLogin_id()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용중인 로그인 ID");
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
            user.setReg_id("관리자");

            userRepository.save(user);

            // 로그 기록
            LoginLog loginLog = new LoginLog();
            loginLog.setLoginId(user.getLoginId());
            loginLog.setStatus("사용자생성");
            loginLog.setRegDt(new Date());
            loginLog.setUpdate_id("관리자");
            loginLogRepository.save(loginLog);

            return ResponseEntity.ok("사용자가 성공적으로 생성되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 생성 중 오류가 발생: " + e.getMessage());
        }
    }

    // 사용자 비밀번호 변경 ( 관리자 기능 )
    public ResponseEntity<?> changeUserPasswordByAdmin(String loginId, UserDTO userDTO) {

        try {
            Optional<User> userOpt = userRepository.findByLoginId(loginId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                user.setUpdate_dt(new Date());
                user.setUpdate_id("관리자");
                userRepository.save(user);

                // 로그 기록
                LoginLog loginLog = new LoginLog();
                loginLog.setLoginId(loginId);
                loginLog.setStatus("관리자비밀번호변경");
                loginLog.setUpdate_dt(new Date());
                loginLog.setUpdate_id("관리자");
                loginLogRepository.save(loginLog);

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
    public ResponseEntity<?> createDept(DeptDTO deptDTO) {
        try {
            Dept_Info deptInfo = new Dept_Info();
            deptInfo.setDeptCode(deptDTO.getDeptCode());
            deptInfo.setDept(deptDTO.getDept());
            deptInfo.setRegDt(new Date());
            deptInfo.setStatus("Y");
            deptInfo.setUserName("관리자");

            // 상위 부서 코드 설정
            if (deptDTO.getParentDeptCode() != null && !deptDTO.getParentDeptCode().isBlank()) {
                deptRepository.findByDeptCode(deptDTO.getParentDeptCode())
                        .ifPresent(deptInfo::setParentDept);
            }

            deptRepository.save(deptInfo);

            // 하위 부서 연결 (선택)
            if (deptDTO.getChildDeptCodes() != null && !deptDTO.getChildDeptCodes().isEmpty()) {
                for (String childCode : deptDTO.getChildDeptCodes()) {
                    if (childCode == null || childCode.isBlank()) continue;
                    deptRepository.findByDeptCode(childCode).ifPresent(child -> {
                        child.setParentDept(deptInfo);
                        deptRepository.save(child);
                    });
                }
            }

            DeptLog deptLog = new DeptLog();
            deptLog.setStatus("생성");
            deptLog.setUserName("관리자");
            deptLog.setRegDt(deptDTO.getRegDt());
            deptLog.setDeptCode(deptDTO.getDeptCode());
            deptLog.setDept(deptDTO.getDept());
            deptLogRepository.save(deptLog);

            return ResponseEntity.ok(deptInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("부서 등록 중 오류가 발생: " + e.getMessage());
        }
    }

    // 부서 목록 조회
    public ResponseEntity<?> getDeptList() {
        try {
            List<Dept_Info> deptList = deptRepository.findAll();
            return ResponseEntity.ok(deptList);
        } catch (Exception e) {
            System.err.println("부서 목록 조회 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("부서 목록 조회 중 오류가 발생: " + e.getMessage());
        }
    }

    // 부서 조회
    public ResponseEntity<?> getDept(String dept, String deptCode) {
        try {
            boolean hasSearchCondition = (dept != null && !dept.trim().isEmpty()) || (deptCode != null && !deptCode.trim().isEmpty());
            if (!hasSearchCondition) {
                return ResponseEntity.ok("아무것도 없음");
            }
            Optional<Dept_Info> deptInfo = deptRepository.findByDeptCode(dept);

            return ResponseEntity.ok("부서 조회");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("부서 수정 중 오류가 발생: " + e.getMessage());
        }
    }

    // 부서 수정/삭제 ( 관리자 기능 )
    public ResponseEntity<?> updateDept(String deptCode, DeptDTO deptDTO) {

        Optional<Dept_Info> deptOpt = deptRepository.findByDeptCode(deptCode);
        if (deptOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("부서 없음");
        }

        Dept_Info deptInfo = deptOpt.get();

        // 기본 필드 업데이트
        if (deptDTO.getDept() != null) deptInfo.setDept(deptDTO.getDept());
        if (deptDTO.getStatus() != null) deptInfo.setStatus(deptDTO.getStatus());

        // 상위 부서 업데이트
        if (deptDTO.getParentDeptCode() != null) {
            if (deptDTO.getParentDeptCode().isBlank()) {
                deptInfo.setParentDept(null);
            } else {
                deptRepository.findByDeptCode(deptDTO.getParentDeptCode())
                        .ifPresent(deptInfo::setParentDept);
            }
        }

        // 업데이트 정보 설정
        deptInfo.setUpdateDt(new Date());
        deptInfo.setUpdateUser("관리자");
        deptRepository.save(deptInfo);

        // 부서 로그 생성
        createDeptLog(deptDTO);

        return ResponseEntity.ok("부서 수정 완료");
    }

    // null이 아닌 프로퍼티만 가져오는 헬퍼 메서드
    private String[] getNullPropertyNames(Object source) {
        return getStrings(source);
    }

    public static String[] getStrings(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null || (srcValue instanceof String && ((String) srcValue).isEmpty())) {
                emptyNames.add(pd.getName());
            }
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    // 사용자 로그 생성 헬퍼 메서드
    private void createUserLog(User user, String updateUserId) {
        LoginLog loginLog = new LoginLog();
        loginLog.setLoginId(user.getLoginId());
        loginLog.setUpdate_dt(new Date());
        loginLog.setUpdate_id(updateUserId);

        if ("N".equals(user.getUser_stat())) {
            loginLog.setStatus("삭제");
        } else {
            loginLog.setStatus("정보수정");
        }

        loginLogRepository.save(loginLog);
    }

    // 부서 로그 생성 헬퍼 메서드
    private void createDeptLog(DeptDTO deptDTO) {
        DeptLog deptLog = new DeptLog();
        deptLog.setDeptCode(deptDTO.getDeptCode());
        deptLog.setDept(deptDTO.getDept());

        if ("Y".equals(deptDTO.getStatus())) {
            deptLog.setStatus("삭제");
        } else {
            deptLog.setStatus("사용중");
        }

        deptLog.setRegDt(deptDTO.getRegDt());
        deptLog.setUserName(deptDTO.getUserName());
        deptLog.setUpdateDt(new Date());
        deptLog.setUpdateUser("관리자");

        deptLogRepository.save(deptLog);
    }

}