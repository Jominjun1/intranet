package com.example.tag_dev.SYSTEM.Service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SystemService {

    public ResponseEntity<?> getHoliday(int year) {
        String url = "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getHoliDeInfo" +
                "?solYear=" + year +
                "&numOfRows=50" +
                "&pageNo=1" +
                "&ServiceKey=c7563e56f76c2d361681ecdd3191025b52d6828862a6a14f0522521f10dfac0e&_type=json";

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(url, String.class);

        return ResponseEntity.ok(result);
    }
}
