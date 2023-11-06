package kr.co.sugarmanager.alarmchallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AlarmChallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlarmChallengeApplication.class, args);
    }

}
