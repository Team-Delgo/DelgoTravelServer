package com.delgo.api.repository.specification;

import com.delgo.api.domain.Place;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlaceSpecification {
    public static Specification<Place> searchPlace(Map<String, Object> searchKey){
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for(String key : searchKey.keySet()){
                predicates.add(criteriaBuilder.like(root.get(key), "%"+searchKey.get(key)+"%"));;
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

//    public static Specification<Place> equalTodoId(Long todoId) {
//        return ((root, query, criteriaBuilder) -> {
//                // 1) equal
//                return criteriaBuilder.equal(root.get("todoId"), todoId);
//            });
//    }
//
//    public static Specification<Place> likeContents(String contents) {
//        return ((root, query, criteriaBuilder) -> {
//            // 2) like
//            return criteriaBuilder.like(root.get("contents"), "%" + contents + "%");
//        });
//    }
//
//    public static Specification<Place> betweenCreatedDatetime(LocalDateTime startDatetime, LocalDateTime endDatetime) {
//        return ((root, query, criteriaBuilder) -> {
//            // 3) between
//            return criteriaBuilder.between(root.get("createdDatetime"), startDatetime, endDatetime);
//        });
//    }
}