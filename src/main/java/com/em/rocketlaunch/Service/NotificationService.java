package com.em.rocketlaunch.Service;

import com.em.rocketlaunch.Model.Launch;
import com.em.rocketlaunch.Model.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendNotificationForLaunch(Launch launch, String subjectPrefix) {
        List<String> emailAddresses = loadEmailAddresses();
        try {
            sendNotificationEmail(emailAddresses, subjectPrefix, launch);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendNotificationForUpdate(Update update, String subjectPrefix) {
        List<String> emailAddresses = loadEmailAddresses();
        try {
            sendNotificationEmail(emailAddresses, subjectPrefix, update);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private List<String> loadEmailAddresses() {
        LoaderOptions loaderOptions = new LoaderOptions();
        Yaml yaml = new Yaml(new Constructor(Map.class, loaderOptions));
        try (InputStream inputStream = new ClassPathResource("email-list.yaml").getInputStream()) {
            Map<String, List<String>> data = yaml.load(inputStream);
            return data.get("emails");
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void sendNotificationEmail(List<String> emails, String subjectPrefix, Launch launch) throws MessagingException {
        for (String email : emails) {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setSubject(subjectPrefix + " - " + launch.getName());
            String content = loadEmailTemplate(launch);
            helper.setText(content, true);
            helper.setTo(email);
            mailSender.send(message);
        }
    }

    private String loadEmailTemplate(Launch launch) {
        try (InputStream inputStream = new ClassPathResource("email-template.html").getInputStream()) {
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            SimpleDateFormat outputFormat = new SimpleDateFormat("d MMM, yyyy 'at' HH:mm");
            Date launchDateTime = inputFormat.parse(launch.getNet());
            String formattedLaunchTime = outputFormat.format(launchDateTime);
            content = content.replace("{{launchName}}", launch.getName());
            content = content.replace("{{launchTime}}", formattedLaunchTime);
            return content;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void sendNotificationEmail(List<String> emails, String subjectPrefix, Update update) throws MessagingException {
        for (String email : emails) {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setSubject(subjectPrefix + " - " + update.getComment());
            String content = loadEmailTemplate(update);
            helper.setText(content, true);
            helper.setTo(email);
            mailSender.send(message);
        }
    }

    private String loadEmailTemplate(Update update) {
        try (InputStream inputStream = new ClassPathResource("update-email-template.html").getInputStream()) {
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            content = content.replace("{{updateComment}}", update.getComment());
            content = content.replace("{{updateInfoUrl}}", update.getInfoUrl());
            return content;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
