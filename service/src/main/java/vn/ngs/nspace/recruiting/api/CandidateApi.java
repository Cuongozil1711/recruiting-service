package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.DateUtil;
import vn.ngs.nspace.lib.utils.MapUtils;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.CandidateFilter;
import vn.ngs.nspace.recruiting.repo.CandidateRepo;
import vn.ngs.nspace.recruiting.service.CandidateService;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import java.util.*;

@RestController
@RequestMapping("candidate")
@RequiredArgsConstructor

@Tag(name = "Candidate", description = "Candidate API")
public class CandidateApi {
    private final CandidateService _service;
    private final CandidateRepo _repo;


    @PostMapping("/search")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all Candidate Order"
            , description = "Search by condition : name, gender, wardCode, phone, email,..."
            , tags = { "Candidate" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity search(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Payload filter") @RequestParam(name = "search",defaultValue = "#") String search
            , Pageable pageable) {
        try{

//        String fullname = MapUtils.getString(condition, "fullname", "all");
//        Long gender = MapUtils.getLong(condition, "gender", -1l);
//        String wardCode = MapUtils.getString(condition, "wardCode", "all");
//        String phone = MapUtils.getString(condition, "phone", "all");
//        String email = MapUtils.getString(condition, "email", "all");
      //      Logger logger = LoggerFactory.getLogger(CandidateApi.class);
    //        logger.trace("A TRACE Message {cid}="+cid);
        Page<Candidate> page = _repo.search(cid, search, pageable);
        List<CandidateDTO> dtos = _service.toDTOs(cid, uid, page.getContent());
        return ResponseUtils.handlerSuccess(new PageImpl(dtos, pageable, page.getTotalElements()));

        }catch (Exception e){
            return ResponseUtils.handlerException(e);
        }

    }

    @PostMapping("/create-list")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "create list candidate"
            , description = "create list candidate"
            ,tags = {"Candidate"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity creates(@RequestHeader Long cid
            , @RequestHeader String uid
            , @RequestBody List<CandidateDTO> dtos){
        try{
            return ResponseUtils.handlerSuccess(_service.create(cid, uid, dtos));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create single Candidate"
            , description = "Create single Candidate"
            , tags = { "Candidate" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity create(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @RequestBody CandidateDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.create(cid, uid, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping("/creates")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create list Candidate"
            , description = "Create list Candidate"
            , tags = { "Candidate" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity create(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "List of candidate") @RequestBody List<CandidateDTO> dtos) {
        try {
            return ResponseUtils.handlerSuccess(_service.create(cid, uid, dtos));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update Candidate by Id"
            , description = "Update Candidate by Id"
            , tags = { "Candidate" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity updateById(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Id of record")  @PathVariable(value = "id") Long id
            , @RequestBody CandidateDTO dto){
        try {
           return ResponseUtils.handlerSuccess(_service.update(cid, uid, id,dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get Candidate by Id"
            , description = "Get Candidate by Id"
            , tags = { "Candidate" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getById(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Id of record")  @PathVariable(value = "id") Long id){
        try {
            Candidate candidate = _repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(Candidate.class, id));
            return ResponseUtils.handlerSuccess(_service.toDTO(candidate));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/update-filter")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Update search Candidate filter"
            , description = "Update search Candidate filter with configs is JSON Object"
            , tags = { "Candidate", "Search" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity updateFilter(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Payload update") @RequestBody CandidateFilter request){
        try {
            return ResponseUtils.handlerSuccess(_service.updateFilter(cid, uid, request));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/delete")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "delete list Candidate",
            description = "API for delete list Candidate")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity deteleList(
            @Parameter(description = "ID of company")
            @RequestHeader Long cid
            ,@Parameter(description = "ID of userID")
            @RequestHeader String uid
            , @RequestBody List<Long> ids){
        try {
            _service.delete(cid, uid , ids);
            return ResponseUtils.handlerSuccess();
        } catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }
    @PostMapping("/uploadFile")
    public void uploadFile(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @RequestParam("file") MultipartFile file){
        try {

              _service.uploadFile(uid,cid,file);

        } catch (Exception ex) {
             ResponseUtils.handlerException(ex);
        }
    }


    @PostMapping("/filter")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "candidate-filter list Candidate",
            description = "API for candidate-filter list Candidate"
            , tags = { "Candidate", "Search" })

    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity filter(
            @Parameter(description = "ID of company") @RequestHeader Long cid
            ,@Parameter(description = "ID of userID") @RequestHeader String uid
            , @RequestBody Map<String, Object> condition
            , Pageable pageable){
        try{
            Long applyPosition = MapUtils.getLong(condition, "applyPosition", -1l);
            Long gender = MapUtils.getLong(condition, "gender", -1l);
            List<Long> languages = new ArrayList<>();
            if(condition.containsKey("language")){
                languages = (List<Long>)condition.get("language");
            }
            if(languages.isEmpty() || !condition.containsKey("language")){
                languages.add(-1l);
            }

            List<Long> educationLevel = new ArrayList<>();
            if(condition.containsKey("educationLevel")){
                educationLevel = (List<Long>)condition.get("educationLevel");
            }
            if(educationLevel.isEmpty() || !condition.containsKey("educationLevel")){
                educationLevel.add(-1l);
            }

            String educateLocation = MapUtils.getString(condition, "educateLocation", "all").toLowerCase(Locale.ROOT);
            String industry = MapUtils.getString(condition,"industry", "all").toLowerCase(Locale.ROOT);
            String ageLess = MapUtils.getString(condition,"ageLess", "all");
            String lastPosition = MapUtils.getString(condition,"lastPosition","all").toLowerCase(Locale.ROOT);
            Double fromExp = MapUtils.getDouble(condition,"fromExp",0.0d);
            Double toExp = MapUtils.getDouble(condition,"toExp",1000.0d);
            String expUnit = MapUtils.getString(condition,"expUnit","month");

            Date yearLess = null;
            if(!ageLess.equals("all") && !ageLess.equals(Constants.Experience.UNDER_0.name())){
                Integer year = Integer.parseInt(ageLess.split("_")[1]);
                yearLess = DateUtil.addDate(new Date(), "year",-year);
            }

            if (expUnit.equals("year")){
               fromExp = fromExp * 12;
               toExp = toExp * 12;
            }

            Page<Candidate> page = _repo.filter(cid,applyPosition,gender,languages,educationLevel,educateLocation,industry, yearLess,lastPosition,fromExp,toExp, pageable);
            List<CandidateDTO> dtos = _service.toDTOs(cid, uid, page.getContent());
            return ResponseUtils.handlerSuccess(new PageImpl(dtos, pageable, page.getTotalElements()));

        }catch (Exception e){
            return ResponseUtils.handlerException(e);
        }

    }
}
