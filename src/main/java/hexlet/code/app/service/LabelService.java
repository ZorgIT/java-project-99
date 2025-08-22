package hexlet.code.app.service;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.LabelMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LabelService {

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Autowired
    public LabelService(LabelRepository labelRepository, LabelMapper labelMapper) {
        this.labelRepository = labelRepository;
        this.labelMapper = labelMapper;
    }

    @Transactional(readOnly = true)
    public List<LabelDTO> getAllLabels() {
        return labelRepository.findAll().stream()
                .map(labelMapper::map)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LabelDTO getLabelById(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found with id: " + id));
        return labelMapper.map(label);
    }

    public LabelDTO createLabel(LabelCreateDTO labelCreateDTO) {
        if (labelRepository.existsByName(labelCreateDTO.getName())) {
            throw new IllegalArgumentException("Label with this name already exists");
        }
        Label label = labelMapper.map(labelCreateDTO);
        return labelMapper.map(labelRepository.save(label));
    }

    public LabelDTO updateLabel(Long id, LabelUpdateDTO labelUpdateDTO) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found with id: " + id));

        if (!label.getName().equals(labelUpdateDTO.getName())
                && labelRepository.existsByName(labelUpdateDTO.getName())) {
            throw new IllegalArgumentException("Label with this name already exists");
        }

        labelMapper.update(labelUpdateDTO, label);
        return labelMapper.map(labelRepository.save(label));
    }

    public void deleteLabel(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found with id: " + id));
        if (!label.getTasks().isEmpty()) {
            throw new IllegalStateException("Cannot delete label associated with tasks");
        }
        labelRepository.delete(label);
    }
}
