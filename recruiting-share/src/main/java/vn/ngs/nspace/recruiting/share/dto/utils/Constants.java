package vn.ngs.nspace.recruiting.share.dto.utils;

import java.util.Arrays;
import java.util.List;

public class Constants extends vn.ngs.nspace.lib.utils.Constants {

    public static final Long DEFAULT_PARENT_GROUP_ID = 0L;
    public static final String GROUP_NAME_RECRUITING = "GroupRCID";
    public static final String GROUP_DESCRIPTION_RECRUITING = "auto-generate-by-uploadFile";
    public static final String REFERENCE_GROUP_SERVICE_APPLICATION = "ticket-service";
    public static final String REFERENCE_RC_FILE_UPLOAD = "rc-file-upload";
    public static final String EMAIL_TYPE_INVITED_INTERVIEW = "invited_interview";
    public static final String NOITY_TYPE_INVITED_INTERVIEW = "change_deadline";
    public static final String EMAIL_TYPE_INVITED_ONBOARDING = "invited_onboarding";
    public final static String RECRUITING_EMAIL = "email";
    public final static String HCM_SERVICE_RECRUITING = "recruiting-service";

    public final static String RECRUITMENT_REQUEST_CODE_REGEX = "^[a-zA-Z0-9]{1,15}$";

    public static final Long RANGED_SALARY = 1L;
    public static final Long AGREE_SALARY = 2L;
    public static final Long UP_TO_SALARY = 3L;
    public static final Integer GET_ALL = 1;

    public static enum JOB_APPLICATION_TYPE {
        job
    }

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

    public enum INTERVIEW_RESULT_STATE {
        PENDING, REVIEWED

    }

    public enum Experience {
        UNDER_0,
        UNDER_30,
        UNDER_40,
        UNDER_55,
        UNDER_65
    }

    public enum JOB_APP_STATE {
        DONE

    }

    public enum CANDIDATE_STATE {
        INTERVIEWING, OFFERING, FAILED, HIRED
    }

    public enum JOB_APPLICATION_STATE {
        INIT, OFFERING, FAILED, HIRED
    }

    public static final String JOB_APPLICATION_STATE_DONE = "DONE";

    public enum EMAIL_SENT_REF {
        CANDIDATE, EMPLOYEE, INVOLVE
    }


    public enum CMD_TABLE_ACTION {
        DELETE, UPDATE
    }

    public enum HCM_RECRUITMENT {
        INIT, //m???i
        APPLIED, //???? ???ng tuy???n
        APPROVING, // ??ang duy???t
        APPROVED, //???? duy???t
        INTERVIEW_INVITED, // ch??? ph???ng v???n
        INTERVIEWED, //???? ph???ng v???n
        PASSED, // ???? ?????t
        OL_SENT, // g???i ofer
        OL_ACCEPTED,// ch???p nh???n offer
        ONBOARDED, // ???? onboard
        STAFF, // nh??n vi??n
        DENIED, // ???? t??? ch???i
    }

    public enum HCM_RECRUITMENT_ONBOARD {
        PENDING // ch??? x??? l??
        ,PROCESSING // ??ang x??? l??
        ,COMPLETE // ho??n th??nh
        ,CANCEL // hu??? b???
    }

    public enum HCM_RECRUITMENT_REVIEW {
        ACHIEVED //?????t
        , NOTACHIEVED // kh??ng ?????t
    }

    public enum HCM_RECRUITMENT_REQUEST{
        INIT,
        APPROVED,
        DENIED,
        EXTEND  // gia han
    }

    public static final Integer IS_BLACK_LIST = 1;
    public static final Integer IS_NOT_BLACK_LIST = 0;

    public enum RECRUITMENT_NEWS_STATE {
        DONE, //HO??N TH??NH
        CANCEL,
        PROCESSING, // ??ANG TH???C HI???N
        INIT, //M???I
        END, //K???T TH??C
        EXTEND //GIA H???N
    }
}
