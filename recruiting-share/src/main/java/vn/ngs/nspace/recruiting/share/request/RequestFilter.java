package vn.ngs.nspace.recruiting.share.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RequestFilter {
    private String typeRequest;
    private Long pic;
    private String state;
    private Date workFrom;
    private Date workTo;
}
