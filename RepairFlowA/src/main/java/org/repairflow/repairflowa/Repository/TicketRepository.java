package org.repairflow.repairflowa.Repository;

import org.repairflow.repairflowa.Pojo.TicketPojo.Ticket;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author guangyang
 * @date 11/25/25 AM6:43
 * @description TODO: Description
 */
public interface TicketRepository  extends JpaRepository<Ticket, Long> {
    Page<Ticket> findByCustomer_Id(Long customerId, Pageable pageable);

    Page<Ticket> findByCustomer_IdAndStatus(Long customerId, TicketStatus status, Pageable pageable);

    Page<Ticket> findByEmployee_Id(Long employeeId, Pageable pageable);

    Page<Ticket> findByEmployee_IdAndStatus(Long employeeId, TicketStatus status, Pageable pageable);

    Page<Ticket> findByEmployeeIsNull(Pageable pageable);

    Integer id(Long id);
}
