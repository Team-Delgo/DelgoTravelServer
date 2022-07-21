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
}
