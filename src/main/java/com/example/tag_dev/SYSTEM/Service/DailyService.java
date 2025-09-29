package com.example.tag_dev.SYSTEM.Service;

import com.example.tag_dev.LOG.Repository.DailyReportLogRepository;
import com.example.tag_dev.SYSTEM.DTO.DailyDTO;
import com.example.tag_dev.SYSTEM.Model.DailyReport_info;
import com.example.tag_dev.SYSTEM.Model.Daily_Project_R;
import com.example.tag_dev.SYSTEM.Repository.DailyReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyService {

    private final DailyReportRepository dailyReportRepository;
    private final DailyReportLogRepository dailyReportLogRepository;

    public DailyReport_info createReport(String userName, DailyDTO dto) {
        DailyReport_info report = new DailyReport_info();
        report.setUserName(userName);
        report.setTxt(dto.getTxt());
        report.setHour(dto.getHour());
        report.setMinute(dto.getMinute());
        report.setProject_name(dto.getProject_name());
        report.setProject_leader(dto.getProject_leader());
        report.setDailyStatus("N");
        report.setStatus("Y");
        report.setCreateDt(new Date());

        // 저장 전 Task 연결
        if (dto.getTasks() != null && !dto.getTasks().isEmpty()) {
            List<Daily_Project_R> tasks = dto.getTasks().stream().map(t -> {
                Daily_Project_R task = new Daily_Project_R();
                task.setTaskDescription(t.getTaskDescription());
                task.setHour(t.getHour());
                task.setMinute(t.getMinute());
                task.setStatus("Y");
                task.setDailyReportInfo(report);
                return task;
            }).toList();
            report.getReportProjects().addAll(tasks);
        }

        return dailyReportRepository.save(report);
    }

    // 특정 날짜 자신의 보고 조회
    public List<DailyReport_info> getReportsByUser(String userName, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date start = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date end = cal.getTime();

        return dailyReportRepository.findByUserNameAndCreateDtBetween(userName, start, end);
    }

    // 보고 수정 (Task 포함)
    public DailyReport_info updateReport(String userName, DailyDTO dto) {
        DailyReport_info report = dailyReportRepository.findById(userName)
                .orElseThrow(() -> new RuntimeException("보고가 존재하지 않습니다."));

        report.setTxt(dto.getTxt());
        report.setHour(dto.getHour());
        report.setMinute(dto.getMinute());
        report.setProject_name(dto.getProject_name());
        report.setProject_leader(dto.getProject_leader());
        report.setDailyStatus("N"); // 수정하면 승인 초기화

        // Task 업데이트: 기존 Task 삭제 후 새 Task 추가
        report.getReportProjects().clear();
        if (dto.getTasks() != null && !dto.getTasks().isEmpty()) {
            List<Daily_Project_R> tasks = dto.getTasks().stream().map(t -> {
                Daily_Project_R task = new Daily_Project_R();
                task.setTaskDescription(t.getTaskDescription());
                task.setHour(t.getHour());
                task.setMinute(t.getMinute());
                task.setStatus("Y");
                task.setDailyReportInfo(report);
                return task;
            }).toList();
            report.getReportProjects().addAll(tasks);
        }

        return dailyReportRepository.save(report);
    }

    // 보고 삭제 (상태 변경)
    public DailyReport_info deleteReport(String userName, Long id) {
        DailyReport_info report = dailyReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("보고서를 찾을 수 없음 ID=" + id));

        if (!report.getUserName().equals(userName)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }
        // 상태만 N으로 변경
        report.setStatus("N");

        return dailyReportRepository.save(report);
    }
}
