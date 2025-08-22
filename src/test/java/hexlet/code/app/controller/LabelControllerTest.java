package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.service.LabelService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class LabelControllerTest {

    @TestConfiguration
    static class MockConfig {
        @Bean
        public LabelService labelService() {
            return Mockito.mock(LabelService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LabelService labelService;

    private LabelDTO createLabelDTO(Long id, String name) {
        LabelDTO dto = new LabelDTO();
        dto.setId(id);
        dto.setName(name);
        return dto;
    }

    private LabelCreateDTO createLabelCreateDTO(String name) {
        LabelCreateDTO dto = new LabelCreateDTO();
        dto.setName(name);
        return dto;
    }

    private LabelUpdateDTO createLabelUpdateDTO(String name) {
        LabelUpdateDTO dto = new LabelUpdateDTO();
        dto.setName(name);
        return dto;
    }

    private final LabelDTO label1 = createLabelDTO(1L, "Urgent");
    private final LabelDTO label2 = createLabelDTO(2L, "Bug");

    @Test
    @WithMockUser
    void getAllLabelsShouldReturnList() throws Exception {
        when(labelService.getAllLabels()).thenReturn(List.of(label1, label2));

        mockMvc.perform(get("/api/labels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Urgent"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Bug"));
    }

    @Test
    @WithMockUser
    void getLabelByIdShouldReturnLabel() throws Exception {
        when(labelService.getLabelById(1L)).thenReturn(label1);

        mockMvc.perform(get("/api/labels/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Urgent"));
    }

    @Test
    @WithMockUser
    void createLabelShouldReturnCreatedLabel() throws Exception {
        LabelCreateDTO createDTO = createLabelCreateDTO("Feature");
        LabelDTO createdLabel = createLabelDTO(3L, "Feature");
        when(labelService.createLabel(any(LabelCreateDTO.class))).thenReturn(createdLabel);

        mockMvc.perform(post("/api/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Feature"));
    }

    @Test
    @WithMockUser
    void updateLabelShouldReturnUpdatedLabel() throws Exception {
        LabelUpdateDTO updateDTO = createLabelUpdateDTO("Critical");
        LabelDTO updatedLabel = createLabelDTO(1L, "Critical");
        when(labelService.updateLabel(any(Long.class), any(LabelUpdateDTO.class))).thenReturn(updatedLabel);

        mockMvc.perform(put("/api/labels/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Critical"));
    }

    @Test
    @WithMockUser
    void deleteLabelShouldReturnNoContent() throws Exception {
        doNothing().when(labelService).deleteLabel(1L);

        mockMvc.perform(delete("/api/labels/1"))
                .andExpect(status().isNoContent());
    }
}
