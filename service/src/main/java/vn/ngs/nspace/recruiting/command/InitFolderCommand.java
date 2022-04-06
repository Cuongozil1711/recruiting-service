package vn.ngs.nspace.recruiting.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class InitFolderCommand {
    private long cid;
    private String uid;

    private List<String> groupPaths;
    private List<String> groupPathIds;
    private List<String> names;
    private List<Long> gids;
    private String application;
}
