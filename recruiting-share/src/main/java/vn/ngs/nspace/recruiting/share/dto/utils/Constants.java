package vn.ngs.nspace.recruiting.share.dto.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants extends vn.ngs.nspace.lib.utils.Constants {
    public static List<String> onboardCheckList
            = Arrays.asList(
            "notice-new-member",
            "asset",
            "account",
            "buddy",
            "profile",
            "contract",
            "other-affair",
            "welcome",
            "training",
            "coaching");

    public enum Experience{
        UNDER_30,
        UNDER_40,
        UNDER_55,
        UNDER_65
    }

    public enum JOB_APP_STATE {
        DONE
    }

    public enum CANDIDATE_STATE {
        INTERVIEWING
        , OFFERING
        , FAILED
        , HIRED
    }

    public enum JOB_APPLICATION_STATE {
        INIT
        , OFFERING
        , FAILED
        , HIRED
    }

    public static final String JOB_APPLICATION_STATE_DONE = "DONE";
}
