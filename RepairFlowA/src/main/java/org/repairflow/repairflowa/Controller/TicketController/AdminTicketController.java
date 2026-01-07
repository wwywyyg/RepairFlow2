package org.repairflow.repairflowa.Controller.TicketController;

import lombok.RequiredArgsConstructor;
import org.repairflow.repairflowa.Exception.Response.ApiResponse;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.TicketResponse;
import org.repairflow.repairflowa.Services.TicketServices.TicketServices;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author guangyang
 * @date 12/31/25 08:35
 * @description TODO: Description
 */


@RestController
@RequestMapping("/auth/admin/ticket")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminTicketController {
    private final TicketServices ticketServices;


    //    ADMIN

    // read all tickets
    @GetMapping("/list-all")
    public ResponseEntity<ApiResponse<Page<TicketResponse>>> getAllTickets(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        Page<TicketResponse> pages = ticketServices.listAllTickets(pageable);
        return ResponseEntity.ok(ApiResponse.success("all tickets retrieved successfully",pages));
    }

    // read employee own ticket
    @GetMapping("/list-employee-tickets/{id}")
    public ResponseEntity<ApiResponse<Page<TicketResponse>>> getEmployeeTickets(@PathVariable Long id) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("updatedAt").descending());
        Page<TicketResponse> pages = ticketServices.listEmployeeTickets(id, pageable);
        return ResponseEntity.ok(ApiResponse.success("employee-tickets retrieved successfully",pages));
    }

    // read one ticket
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicket(@PathVariable Long id) {
        TicketResponse ticketResponse =ticketServices.getTicket(id);
        return ResponseEntity.ok(ApiResponse.success("ticket retrieved successfully",ticketResponse));
    }
}
