package com.delgo.api.service;

import com.delgo.api.comm.CommService;
import com.delgo.api.domain.EditorNote;
import com.delgo.api.repository.EditorNoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EditorNoteService extends CommService {

    private final EditorNoteRepository editorNoteRepository;

    public List<EditorNote> getEditorNoteList() {
        return editorNoteRepository.findAll();
    }

    public EditorNote getEditorNoteByPlaceId(int placeId) {
        return editorNoteRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new NullPointerException("NOT FOUND EDITOR NOTE"));
    }

    public boolean isEditorNoteExist(int placeId) {
        Optional<EditorNote> option = editorNoteRepository.findByPlaceId(placeId);
        return option.isPresent();
    }

}
