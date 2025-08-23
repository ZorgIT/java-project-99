package hexlet.code.controller;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.service.LabelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/labels")
public class LabelController {

    private final LabelService labelService;

    @Autowired
    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @GetMapping
    public ResponseEntity<List<LabelDTO>> getAllLabels() {
        List<LabelDTO> labels = labelService.getAllLabels();
        long totalCount = labels.size();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(totalCount))
                .body(labels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabelDTO> getLabelById(@PathVariable Long id) {
        return ResponseEntity.ok(labelService.getLabelById(id));
    }

    @PostMapping
    public ResponseEntity<LabelDTO> createLabel(@RequestBody @Valid LabelCreateDTO labelCreateDTO) {
        System.out.println(labelCreateDTO.getName());
        LabelDTO createdLabel = labelService.createLabel(labelCreateDTO);
        return ResponseEntity
                .created(URI.create("/api/labels/" + createdLabel.getId()))
                .body(createdLabel);
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<LabelDTO> updateLabel(@PathVariable Long id,
                                                @RequestBody @Valid LabelUpdateDTO labelUpdateDTO) {
        return ResponseEntity.ok(labelService.updateLabel(id, labelUpdateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabel(@PathVariable Long id) {
        labelService.deleteLabel(id);
        return ResponseEntity.noContent().build();
    }
}
