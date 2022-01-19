package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateFilterDTO {
    private Long id;
    private String code;
    private String name;
    private Map<String, Object> configs;

}
