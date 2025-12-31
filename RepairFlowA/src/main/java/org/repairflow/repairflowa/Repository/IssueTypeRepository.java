package org.repairflow.repairflowa.Repository;

import org.repairflow.repairflowa.Pojo.TicketPojo.IssueType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author guangyang
 * @date 12/30/25 22:13
 * @description TODO: Description
 */
public interface IssueTypeRepository extends JpaRepository<IssueType, Long> {
    List<IssueType> findByDeviceCategory_Id(Long deviceCategoryId);
}
