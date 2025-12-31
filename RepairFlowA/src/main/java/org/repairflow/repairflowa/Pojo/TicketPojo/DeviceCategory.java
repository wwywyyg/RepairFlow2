package org.repairflow.repairflowa.Pojo.TicketPojo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author guangyang
 * @date 12/30/25 22:33
 * @description TODO: Description
 *
 */

@Entity
@Data
@Table(name = "device_category")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64, unique = true)
    private String name;
}
