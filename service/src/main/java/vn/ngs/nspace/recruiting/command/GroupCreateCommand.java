package vn.ngs.nspace.recruiting.command;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GroupCreateCommand {

    private long companyId;
    private String userId;
    private String code;
    private String type;
    private String name;
    private long parentId;
    private long leaderId;
    private String description;

    public GroupCreateCommand(long companyId, String userId, String code, String type, String name, String description){
        this.companyId = companyId;
        this.userId = userId;
        this.code = code;
        this.type = type;
        this.name = name;
        this.description = description;
    }
}
