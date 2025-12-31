package org.repairflow.repairflowa.Services.TicketServices;

import lombok.RequiredArgsConstructor;
import org.repairflow.repairflowa.Pojo.TicketPojo.IssueType;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.TicketMetaDto.CategoryMapper;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.TicketMetaDto.DeviceCategoryDto;
import org.repairflow.repairflowa.Pojo.TicketPojo.TicketDto.TicketMetaDto.IssueTypeDto;
import org.repairflow.repairflowa.Repository.DeviceCategoryRepository;
import org.repairflow.repairflowa.Repository.IssueTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author guangyang
 * @date 12/30/25 22:35
 * @description TODO: Description
 */

@Service
@RequiredArgsConstructor
public class TicketMetaService {

    private final DeviceCategoryRepository deviceCategoryRepository;
    private final IssueTypeRepository issueTypeRepository;

    public List<DeviceCategoryDto> listDeviceCategories() {
        return deviceCategoryRepository.findAll()
                .stream()
                .map(CategoryMapper::toDeviceCategoryDto)
                .toList();
    }

    public List<IssueTypeDto> listIssueTypes(Long deviceCategoryId) {
        List<IssueType> list = (deviceCategoryId == null)
                ? issueTypeRepository.findAll()
                : issueTypeRepository.findByDeviceCategory_Id(deviceCategoryId);

        return list.stream()
                .map(CategoryMapper::toIssueTypeDto)
                .toList();
    }
}
