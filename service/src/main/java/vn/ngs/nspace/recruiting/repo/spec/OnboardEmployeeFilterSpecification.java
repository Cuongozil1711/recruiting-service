package vn.ngs.nspace.recruiting.repo.spec;

import org.springframework.data.jpa.domain.Specification;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.request.OnboardEmployeeFilterRequest;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class OnboardEmployeeFilterSpecification implements Specification<Candidate> {

    private OnboardEmployeeFilterRequest request;

    public OnboardEmployeeFilterSpecification(OnboardEmployeeFilterRequest request) {
        this.request = request;
    }

    @Override
    public Predicate toPredicate(Root<Candidate> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (request.getName() != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("full_name")), "%" + this.request.getName().toUpperCase() + "%"));
        }

        if (request.getCode() != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("code")), "%" + this.request.getCode().toUpperCase() + "%"));
        }

        if (request.getState() != null) {
            predicates.add(criteriaBuilder.equal(root.get("state"), this.request.getState()));
        }

        if (request.getOrgRecruitingId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("org_recruting_id"), this.request.getOrgRecruitingId()));
        }

        if (request.getGender() != null) {
            predicates.add(criteriaBuilder.equal(root.get("gender"), this.request.getGender()));
        }

        predicates.add(criteriaBuilder.equal(root.get("status"), Constants.ENTITY_ACTIVE));

        return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
    }
}
