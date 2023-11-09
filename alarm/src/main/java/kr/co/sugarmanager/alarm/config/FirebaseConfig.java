package kr.co.sugarmanager.alarm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
@Configuration
public class FirebaseConfig {
    @Value(value = "${firebase}")
    private String firebaseSDKPath;

    @PostConstruct
    public void init() {
        log.info(firebaseSDKPath);
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(firebaseSDKPath).getInputStream()))
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (FileNotFoundException e) {
            log.info("Firebase SDK Path FileNotfoundException" + e.getMessage());
        } catch (IOException e) {
            log.info("Firebase SDK Path IOException" + e.getMessage());
        }
    }
}
