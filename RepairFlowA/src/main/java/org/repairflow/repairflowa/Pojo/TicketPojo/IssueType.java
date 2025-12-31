package org.repairflow.repairflowa.Pojo.TicketPojo;

import jakarta.persistence.*;
import lombok.*;


/**
 * @author guangyang
 * @date 12/30/25 22:08
 * @description TODO: Description
 */


@Entity
@Table(name = "issue_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String name;

    // for connect issue and category, choose phone only show phone issues
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_category_id")
    private DeviceCategory deviceCategory;

}
