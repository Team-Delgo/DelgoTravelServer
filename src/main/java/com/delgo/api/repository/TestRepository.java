package com.delgo.api.repository;

import com.delgo.api.domain.place.Place;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class TestRepository {

    private final EntityManager em;

    @Autowired
    public TestRepository(EntityManager em) {
        this.em = em;
    }

    public Place save(Place place) {
        em.persist(place);
        return place;
    }

    public Optional<Place> findByName(long name) {
        List<Place> result = em.createQuery("select p from Place p where p.name =:name",Place.class)
                .setParameter("name",name)
                .getResultList();

        return result.stream().findAny();
    }

    public Optional<Place> findById(String id) {
        Place place = em.find(Place.class, id);
        return Optional.ofNullable(place);
    }

    public List<Place> findAll() {
        return em.createQuery("select p from Place p", Place.class)
                .getResultList();
    }

    public Optional<Place> findOne() {
        List<Place> result = em.createQuery("select p from Place p", Place.class)
                .getResultList();

        return result.stream().findFirst();
    }
}
