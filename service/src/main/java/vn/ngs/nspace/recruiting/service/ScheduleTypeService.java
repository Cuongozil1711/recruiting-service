package vn.ngs.nspace.recruiting.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.ScheduleType;
import vn.ngs.nspace.recruiting.repo.ScheduleTypeRepo;
import vn.ngs.nspace.recruiting.share.dto.ScheduleTypeDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional
@Log4j2
public class ScheduleTypeService {

    private final ScheduleTypeRepo _repo;

    public ScheduleTypeService(ScheduleTypeRepo repo) {
        _repo = repo;
    }

    public Page<ScheduleTypeDTO> getPageSchedule(String search, Pageable pageable) {
        Page<ScheduleType> scheduleTypePage = _repo.getPageScheduleType(search, pageable);
        List<ScheduleTypeDTO> scheduleTypeDTOS = toDTOs(scheduleTypePage.getContent());

        return new PageImpl<ScheduleTypeDTO>(scheduleTypeDTOS, scheduleTypePage.getPageable(), scheduleTypePage.getTotalElements());
    }

    public ScheduleTypeDTO create(Long cid, String uid, ScheduleTypeDTO scheduleTypeDTO) {
        valid(scheduleTypeDTO);
        ScheduleType scheduleType = _repo.save(ScheduleType.of(cid, uid, scheduleTypeDTO));

        return toDTO(scheduleType);
    }

    public ScheduleTypeDTO update(Long cid, String uid, Long id, ScheduleTypeDTO request) {
        valid(request);
        ScheduleType scheduleType = _repo.save(ScheduleType.of(cid, uid, request));

        return toDTO(scheduleType);
    }

    public void delete(Long cid, String uid, List<Long> ids) {
        List<ScheduleType> scheduleTypes = _repo.findByIdIn(new HashSet<>(ids));

        scheduleTypes.forEach(
                e -> {
                    e.setStatus(Constants.ENTITY_INACTIVE);
                    e.setUpdateBy(uid);
                    e.setModifiedDate(new Date());
                    _repo.save(e);
                }
        );
    }

    public ScheduleTypeDTO toDTO(ScheduleType scheduleType) {
        return MapperUtils.map(scheduleType, ScheduleTypeDTO.class);
    }

    public void valid(ScheduleTypeDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new BusinessException("employee-not-found");
        }
        if (dto.getCode() == null || dto.getCode().isBlank()) {
            throw new BusinessException("asset-not-found");
        }
    }

    public List<ScheduleTypeDTO> toDTOs(List<ScheduleType> scheduleTypes) {
        List<ScheduleTypeDTO> scheduleTypeDTOS = new ArrayList<>();

        scheduleTypes.forEach(
                e -> {
                    scheduleTypeDTOS.add(toDTO(e));
                }
        );

        return scheduleTypeDTOS;
    }
}
