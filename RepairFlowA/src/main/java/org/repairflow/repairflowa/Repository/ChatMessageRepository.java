package org.repairflow.repairflowa.Repository;


import org.repairflow.repairflowa.Pojo.ChatPojo.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author guangyang
 * @date 1/2/26 15:56
 * @description TODO: Description
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findByTicket_IdOrderByCreatedAtAsc(Long ticketId, Pageable pageable);

}
