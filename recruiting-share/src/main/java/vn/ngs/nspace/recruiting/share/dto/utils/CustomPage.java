package vn.ngs.nspace.recruiting.share.dto.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CustomPage<T> {
    private Integer pageSize;
    private Integer page;
    private Integer total;
    private List<T> content;
}
