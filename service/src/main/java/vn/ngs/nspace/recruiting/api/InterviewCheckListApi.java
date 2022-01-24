package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.ngs.nspace.recruiting.model.InterviewCheckList;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListRepo;
import vn.ngs.nspace.recruiting.service.InterviewCheckListService;

@RestController
@RequestMapping("interview")
@RequiredArgsConstructor
@Tag(name = "InterviewCheckList", description = "API for CRUD Interview CheckList")
public class InterviewCheckListApi {
    private final InterviewCheckListService service;
    private final InterviewCheckListRepo repo;

//    protected ResponseEntity
}
