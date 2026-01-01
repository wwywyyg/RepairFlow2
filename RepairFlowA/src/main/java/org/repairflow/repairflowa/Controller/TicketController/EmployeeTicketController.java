package org.repairflow.repairflowa.Controller.TicketController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.repairflow.repairflowa.Exception.Response.ApiResponse;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.EmployeeDto.TicketQuoteReq;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.EmployeeDto.TicketStatusUpdateReq;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.TicketResponse;
import org.repairflow.repairflowa.Security.CurrentUser;
import org.repairflow.repairflowa.Security.UserPrincipal;
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
 * @date 11/25/25 PM10:53
 * @description TODO: Description
 */

@RestController
@RequestMapping("/auth/employee/tickets")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
public class EmployeeTicketController {
    private final TicketServices ticketServices;

    // list all available Tickets
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<Page<TicketResponse>>>  availableTickets(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
//        return ticketServices.listAvailableTickets(pageable);
        Page<TicketResponse> pages = ticketServices.listAvailableTickets(pageable);
        return ResponseEntity.ok(ApiResponse.success("available tickets retrieved successfully",pages));
    }

//    list all employee own tickets
    @GetMapping("/own-tickets")
    public ResponseEntity<ApiResponse<Page<TicketResponse>>> listOwnTickets(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @CurrentUser UserPrincipal me) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        Page<TicketResponse> pages = ticketServices.listEmployeeOwnTickets(me.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Employee own tickets retrieved successfully",pages));
    }

//    get employee ticket
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicket(@PathVariable Long id, @CurrentUser UserPrincipal me) {
        TicketResponse ticketResponse = ticketServices.getEmployeeTicket(id, me.getUserId());
        return ResponseEntity.ok(ApiResponse.success("ticket retrieved successfully",ticketResponse));
    }






    // claim ticket
    @PostMapping("/{id}/claim")
    public ResponseEntity<ApiResponse<TicketResponse>> claimTicket(@PathVariable Long id, @CurrentUser UserPrincipal me) {
//        return ticketServices.claimTicket(id, me.getUserId());
        TicketResponse ticketResponse = ticketServices.claimTicket(id,me.getUserId());
        return ResponseEntity.ok(ApiResponse.success("ticket claim successfully",ticketResponse));
    }

    // make quote
    @PutMapping("/{id}/quote")
    public ResponseEntity<ApiResponse<TicketResponse>> quoteTicket(@PathVariable Long id, @Valid @RequestBody TicketQuoteReq quoteReq, @CurrentUser UserPrincipal me) {
//        return ticketServices.setQuote(id,me.getUserId(), quoteReq);
        TicketResponse ticketResponse = ticketServices.setQuote(id,me.getUserId(), quoteReq);
        return ResponseEntity.ok(ApiResponse.success("ticket quote successfully",ticketResponse));
    }

    // update Status
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TicketResponse>> updateStatus(@PathVariable Long id, @Valid @RequestBody TicketStatusUpdateReq updateReq, @CurrentUser UserPrincipal me) {
//        return ticketServices.updateEmployeeStatus(id, me.getUserId(), updateReq);
        TicketResponse ticketResponse = ticketServices.updateEmployeeStatus(id,me.getUserId(), updateReq);
        return ResponseEntity.ok(ApiResponse.success("ticket status update successfully",ticketResponse));
    }

//    ADMIN

//    // read all tickets
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/admin/list-all")
//    public ResponseEntity<ApiResponse<Page<TicketResponse>>> getAllTickets(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
//        Page<TicketResponse> pages = ticketServices.listAllTickets(pageable);
//        return ResponseEntity.ok(ApiResponse.success("all tickets retrieved successfully",pages));
//    }
//
//    // read employee own ticket
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/admin/list-employee-tickets/{id}")
//    public ResponseEntity<ApiResponse<Page<TicketResponse>>> getEmployeeTickets(@PathVariable Long id) {
//        Pageable pageable = PageRequest.of(0, 10, Sort.by("updatedAt").descending());
//        Page<TicketResponse> pages = ticketServices.listEmployeeTickets(id, pageable);
//        return ResponseEntity.ok(ApiResponse.success("employee-tickets retrieved successfully",pages));
//    }
//
//    // read one ticket
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/admin/{id}")
//    public ResponseEntity<ApiResponse<TicketResponse>> getTicket(@PathVariable Long id) {
//        TicketResponse ticketResponse =ticketServices.getTicket(id);
//        return ResponseEntity.ok(ApiResponse.success("ticket retrieved successfully",ticketResponse));
//    }
}
