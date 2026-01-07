package org.repairflow.repairflowa.Controller.Common;

import lombok.RequiredArgsConstructor;
import org.repairflow.repairflowa.Exception.Response.ApiResponse;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.TicketMetaDto.DeviceCategoryDto;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.TicketMetaDto.IssueTypeDto;
import org.repairflow.repairflowa.Services.TicketServices.TicketMetaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author guangyang
 * @date 12/30/25 22:36
 * @description TODO: Description
 */

@RestController
@RequestMapping("/meta")
@RequiredArgsConstructor
public class TicketMetaController {

    private final TicketMetaService ticketMetaService;

    @GetMapping("/device-categories")
    public ResponseEntity<ApiResponse<List<DeviceCategoryDto>>> getDeviceCategories() {
        List<DeviceCategoryDto> list = ticketMetaService.listDeviceCategories();
        return ResponseEntity.ok(ApiResponse.success("device categories retrieved", list));
    }

        @GetMapping("/issue-types")
    public ResponseEntity<ApiResponse<List<IssueTypeDto>>> getIssueTypes(
            @RequestParam(required = false) Long deviceCategoryId
    ) {
        List<IssueTypeDto> list = ticketMetaService.listIssueTypes(deviceCategoryId);
        return ResponseEntity.ok(ApiResponse.success("issue types retrieved", list));
    }
}
