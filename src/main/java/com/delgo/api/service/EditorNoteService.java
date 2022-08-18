package com.delgo.api.service;

import com.delgo.api.comm.CommService;
import com.delgo.api.domain.EditorNote;
import com.delgo.api.repository.EditorNoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EditorNoteService extends CommService {

    private final EditorNoteRepository editorNoteRepository;

    public List<EditorNote> getEditorNoteList() {
        return editorNoteRepository.findAll();
    }

    public List<EditorNote> getEditorNoteByPlaceId(int placeId) {
        return editorNoteRepository.findByPlaceId(placeId);
    }

    public List<EditorNote> getThumbnail() {
        return editorNoteRepository.findByOrder(1);
    }

    public boolean isEditorNoteExist(int placeId) {
        List<EditorNote> editList = editorNoteRepository.findByPlaceId(placeId);
        return !editList.isEmpty();
    }

}
