package com.pickle.server.notification.controller;

import com.pickle.server.notification.dto.NoticeReqDto.CreateNotificationRequest;
import com.pickle.server.notification.service.SseService;
import com.pickle.server.user.domain.User;
import com.pickle.server.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sse")
public class SseController {
    private final SseService sseService;
    private final UserService userService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId,
            @ApiIgnore @AuthenticationPrincipal User user
    ){
        Long userId = user.getId();
        return sseService.subscribe(userId, lastEventId);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Void> sendNotification(
            @PathVariable("userId") Long userId,
            @RequestBody CreateNotificationRequest request
    ){
        User user = userService.getUserById(userId);
        sseService.notice(user, request.getType(), request.getContent(), request.getRelatedUrl());

        return ResponseEntity.ok().build();
    }
}
