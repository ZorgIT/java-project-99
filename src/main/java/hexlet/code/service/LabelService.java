package hexlet.code.service;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
        String name = labelCreateDTO.getName() != null ? labelCreateDTO.getName().trim() : "";
        if (name.isEmpty() || name.length() < 3 || name.length() > 1000) {
            throw new IllegalArgumentException("Label name must be between 3 and 1000 characters and not blank");
        }
        if (labelRepository.existsByName(name)) {
            throw new IllegalArgumentException("Label with name '" + name + "' already exists");
        }
        labelCreateDTO.setName(name); // Update DTO with sanitized name
        Label label = labelMapper.map(labelCreateDTO);
        if (label.getCreatedAt() == null) {
            label.setCreatedAt(LocalDate.now());
        }
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
