package com.vlad.TwiterClone.controller;

import com.vlad.TwiterClone.domain.Message;
import com.vlad.TwiterClone.domain.User;
import com.vlad.TwiterClone.repos.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Controller
public class MainController {
    @Autowired
    private MessageRepo messageRepo;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/")
    public String greeting(Map<String, Object> model) {
        return "greeting";
    }

    @GetMapping("/main")
    public String main(
            @RequestParam(required = false, defaultValue = "") String filter,
            Model model,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Message> page;

        if (filter != null && !filter.isEmpty()) {
            page = messageRepo.findByTag(filter, pageable);
        } else {
            page = messageRepo.findAll(pageable);
        }

        model.addAttribute("page", page);
        model.addAttribute("url", "/main");
        model.addAttribute("filter", filter);

        return "main";
    }

    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal User user,
            @Valid Message message,
            BindingResult bindingResult,
            Model model,
            @RequestParam("file") MultipartFile file,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ) throws IOException {
        message.setAuthor(user);

        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);

            model.mergeAttributes(errorsMap);
            model.addAttribute("message", message);
        } else {
            saveFile(message, file);

            model.addAttribute("message", null);

            messageRepo.save(message);
        }

        Iterable<Message> page = messageRepo.findAll(pageable);


        model.addAttribute("page", page);
        model.addAttribute("url", "/main");
        return "main";
    }

    private void saveFile(@Valid Message message, @RequestParam("file") MultipartFile file) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFilename));

            message.setFilename(resultFilename);
        }
    }

    @GetMapping("/user-messages/{user}")
    public String userMessages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable User user,
            Model model,
            @RequestParam(required = false) Message message,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ) {

            Set<Message> page = user.getMessages();

            model.addAttribute("userChannel", user);
            model.addAttribute("subscriptionsCount", user.getSubscriptions().size());
            model.addAttribute("subscribersCount", user.getSubscribers().size());
            model.addAttribute("isSubscriber", user.getSubscribers().contains(currentUser));
            model.addAttribute("page", messageRepo.findAll(pageable));
            model.addAttribute("url", "/user-messages/" + user.getId());
            model.addAttribute("message", message);
            model.addAttribute("messages", page);
            model.addAttribute("isCurrentUser", currentUser.equals(user));
            return "userMessages";

    }

    @PostMapping("/user-messages/{user}")
    public String updateMessage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long user,
            Model model,
            @RequestParam("id") Message message,
            @RequestParam("text") String text,
            @RequestParam("tag") String tag,
            @RequestParam("file") MultipartFile file,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ) throws IOException {
        if (message.getAuthor().equals(currentUser)) {
            if (!StringUtils.isEmpty(text)) {
                message.setText(text);
            }

            if (!StringUtils.isEmpty(tag)) {
                message.setTag(tag);
            }

            saveFile(message, file);

            messageRepo.save(message);
        }
        model.addAttribute("page", messageRepo.findAll(pageable));
        model.addAttribute("url", "/user-messages/" + user);
        return "redirect:/user-messages/" + user;
    }

    @GetMapping(path = "/user-messages/deleteById/{messageId}")
    public String deleteById(
            @PathVariable(name = "messageId") Long messageId,
            Model model,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
            ){
        messageRepo.deleteById(messageId);

        model.addAttribute("page", messageRepo.findAll(pageable));
        model.addAttribute("url", "/main" );
        return "redirect:/main";
    }
}
