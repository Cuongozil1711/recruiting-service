package vn.ngs.nspace.recruiting.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.person.share.dto.*;

import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EmployeeReq {
    private PersonDTO person;
    private ProfileDTO perProfile;
    private Map<String, IdentifyDTO> perIdentifies;
    private Map<String, AddressDTO> perAddresses;
    private Map<String, ContactDTO> perContacts;
    private Set<CertificateDTO> perCerts;
    private Set<DiplomaDTO> perDiplomas;
    private Set<RelativeDTO> perRelatives;
    private Set<WorkExpDTO> perWorkExps;
    private EmployeeDTO employee;
    private boolean autoGenerateCode = false;
}
