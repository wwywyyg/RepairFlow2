package org.repairflow.repairflowa.Controller.TicketController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.repairflow.repairflowa.Exception.Response.ApiResponse;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.TicketCreateReq;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.TicketResponse;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketStatus;
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
 * @date 11/25/25 PM9:05
 * @description TODO: Description
 */
@RestController
@RequestMapping("/auth/customer/tickets")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerTicketController {
    private final TicketServices ticketServices;

    // create Ticket
    @PostMapping
    public ResponseEntity<ApiResponse<TicketResponse>> createTicket(@RequestBody @Valid TicketCreateReq createReq, @CurrentUser UserPrincipal me) {
        TicketResponse ticketResponse = ticketServices.createTicket(createReq, me.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Ticket created", ticketResponse));
//        return ticketServices.createTicket(createReq,me.getUserId());
    }

    // read one ticket
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketResponse>> details(@PathVariable Long id,@CurrentUser UserPrincipal me) {
        TicketResponse ticketResponse = ticketServices.getCustomerTicket(id, me.getUserId());
//        return ticketServices.getCustomerTicket(id,me.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Read Ticket details", ticketResponse));
    }

    // read all tickets
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TicketResponse>>>  listTickets(@RequestParam(required = false) TicketStatus status ,
                                            @RequestParam(defaultValue = "0")int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @CurrentUser UserPrincipal me) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
//        return ticketServices.listCustomerTickets(me.getUserId(), status, pageable);
        Page<TicketResponse> pages = ticketServices.listCustomerTickets(me.getUserId(), status,pageable);
        return ResponseEntity.ok(ApiResponse.success("Tickets retrieved successfully",pages));
    }


    // approve quote
    @PostMapping("/{id}/approve-quote")
    public ResponseEntity<ApiResponse<TicketResponse>> approveQuote(@PathVariable Long id,@CurrentUser UserPrincipal me) {
//        return ticketServices.approveQuote(id,me.getUserId());
        TicketResponse ticketResponse = ticketServices.approveQuote(id, me.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Quote approved", ticketResponse));
    }

    // confirm paid
    @PostMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<TicketResponse>> confirmPaid(@PathVariable Long id, @CurrentUser UserPrincipal me){
//        return ticketServices.confirmAndMarkPaid(id, me.getUserId());
        TicketResponse ticketResponse = ticketServices.confirmAndMarkPay(id, me.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Ticket  marked as paid", ticketResponse));
    }

    // mark delivered
    @PostMapping("/{id}/mark-delivered")
    public ResponseEntity<ApiResponse<TicketResponse>> markDelivered(@PathVariable Long id,@CurrentUser UserPrincipal me) {
//        return ticketServices.markDelivered(id,me.getUserId());
        TicketResponse ticketResponse = ticketServices.markDelivered(id, me.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Ticket  marked as delivered", ticketResponse));
    }



}
