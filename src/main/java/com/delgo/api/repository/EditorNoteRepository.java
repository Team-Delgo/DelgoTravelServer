package com.delgo.api.repository;

import com.delgo.api.domain.EditorNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EditorNoteRepository extends JpaRepository<EditorNote, Integer> {

    Optional<EditorNote> findByPlaceId(int placeId);
}
