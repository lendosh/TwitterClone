package com.vlad.TwiterClone.service;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.vlad.TwiterClone.events.SendMailEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class MailSender {

    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String username;

    @Subscribe
    public void send(SendMailEvent event){
        checkArgument(!StringUtils.isEmpty(event.getEmailTo()), "Email can not be empty!");
        checkArgument(!StringUtils.isEmpty(event.getSubject()), "subject can not be empty!");
        checkArgument(!StringUtils.isEmpty(event.getMessage()), "message can not be empty!");


        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(username);
        mailMessage.setTo(event.getEmailTo());
        mailMessage.setSubject(event.getSubject());
        mailMessage.setText(event.getMessage());

        mailSender.send(mailMessage);
    }
}
