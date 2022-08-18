package com.delgo.api.repository;

import com.delgo.api.domain.EditorNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EditorNoteRepository extends JpaRepository<EditorNote, Integer> {

    List<EditorNote> findByPlaceId(int placeId);

    List<EditorNote> findByOrder(int order);
}
