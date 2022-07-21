package com.delgo.api.repository;

import com.delgo.api.domain.EditorNote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EditorNoteRepository extends JpaRepository<EditorNote, Integer> {
}
