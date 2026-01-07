package org.repairflow.repairflowa.Controller.ChatController;

import org.repairflow.repairflowa.Exception.BusinessException;
import org.repairflow.repairflowa.Exception.ErrorCode;
import org.repairflow.repairflowa.Exception.Response.ApiResponse;
import org.repairflow.repairflowa.Exception.Response.ChatMessageResponse;
import org.repairflow.repairflowa.Pojo.ChatPojo.ChatMessageMapper;
import org.repairflow.repairflowa.Security.CurrentUser;
import org.repairflow.repairflowa.Security.UserPrincipal;
import org.repairflow.repairflowa.Services.ChatServices.ChatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * @author guangyang
 * @date 1/2/26 23:04
 * @description TODO: Description
 */

@RestController
@RequestMapping("/tickets")

public class ChatRestController {
    @Value("${app.upload-dir}")
    private String uploadDir;
    private final ChatService chatService;

    public ChatRestController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/{ticketId}/messages")
    public Page<ChatMessageResponse> history(@PathVariable Long ticketId,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "50") int size) {

        return chatService.getChatHistory(ticketId, page, size).map(m -> ChatMessageMapper.toResponse(m, null));
    }


    @PostMapping("/{ticketId}/messages/image")
    public ApiResponse<Map<String, String>> uploadImage(
            @PathVariable Long ticketId,
            @RequestParam("file") MultipartFile file,
            @CurrentUser UserPrincipal me
    ) throws IOException {

        // 1) basic Verification
        if (file.isEmpty()) throw new BusinessException(ErrorCode.BAD_REQUEST, "File is empty");
        String ct = file.getContentType();
        if (ct == null || !ct.startsWith("image/")) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Only image files are allowed");
        }

        // 2) save file
        Files.createDirectories(Path.of(uploadDir));
        String ext = Optional.ofNullable(file.getOriginalFilename())
                .filter(n -> n.contains("."))
                .map(n -> n.substring(n.lastIndexOf(".")))
                .orElse(".jpg");

        String filename = "ticket-" + ticketId + "-" + UUID.randomUUID() + ext;
        Path dest = Path.of(uploadDir, filename);
        file.transferTo(dest);

        // 3) return local url
        String url = "/uploads/" + filename;
        return ApiResponse.success("uploaded", Map.of("url", url));
    }



}
