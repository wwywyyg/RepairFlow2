package org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.TicketMetaDto;

import org.repairflow.repairflowa.Pojo.TicketPojo.DeviceCategory;
import org.repairflow.repairflowa.Pojo.TicketPojo.IssueType;

/**
 * @author guangyang
 * @date 12/30/25 22:21
 * @description TODO: Description
 */
public class CategoryMapper {
    public static DeviceCategoryDto toDeviceCategoryDto(DeviceCategory c) {
        return new DeviceCategoryDto(c.getId(), c.getName());
    }

    public static IssueTypeDto toIssueTypeDto(IssueType it) {
        Long categoryId = it.getDeviceCategory() != null ? it.getDeviceCategory().getId() : null;
        return new IssueTypeDto(it.getId(), it.getName(), categoryId);
    }
}
