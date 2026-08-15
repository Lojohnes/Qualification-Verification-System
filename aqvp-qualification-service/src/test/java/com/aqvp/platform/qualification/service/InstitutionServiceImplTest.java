package com.aqvp.platform.qualification.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aqvp.platform.qualification.domain.Institution;
import com.aqvp.platform.qualification.dto.InstitutionRequestDto;
import com.aqvp.platform.qualification.dto.InstitutionResponseDto;
import com.aqvp.platform.qualification.exception.DuplicateResourceException;
import com.aqvp.platform.qualification.exception.EntityNotFoundException;
import com.aqvp.platform.qualification.mapper.InstitutionMapper;
import com.aqvp.platform.qualification.repository.InstitutionRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InstitutionServiceImplTest {

    @Mock
    private InstitutionRepository institutionRepository;

    @Mock
    private InstitutionMapper institutionMapper;

    @InjectMocks
    private InstitutionServiceImpl institutionService;

    private InstitutionRequestDto requestDto;
    private Institution institution;
    private InstitutionResponseDto responseDto;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        requestDto = new InstitutionRequestDto("Test University", "TESTU", "A test university", true);
        institution = Institution.builder()
            .id(id)
            .name("Test University")
            .code("TESTU")
            .description("A test university")
            .active(true)
            .build();
        responseDto = new InstitutionResponseDto(id, "Test University", "TESTU", "A test university", true, null, null, null, null, 1L);
    }

    @Test
    void createInstitution_success() {
        when(institutionRepository.existsByCode("TESTU")).thenReturn(false);
        when(institutionMapper.toEntity(requestDto)).thenReturn(institution);
        when(institutionRepository.save(institution)).thenReturn(institution);
        when(institutionMapper.toResponseDto(institution)).thenReturn(responseDto);

        final InstitutionResponseDto result = institutionService.createInstitution(requestDto);

        assertNotNull(result);
        assertEquals("TESTU", result.code());
        verify(institutionRepository).save(institution);
    }

    @Test
    void createInstitution_duplicateCode_throwsException() {
        when(institutionRepository.existsByCode("TESTU")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> institutionService.createInstitution(requestDto));
        verify(institutionRepository, never()).save(any());
    }

    @Test
    void getInstitutionById_success() {
        when(institutionRepository.findById(id)).thenReturn(Optional.of(institution));
        when(institutionMapper.toResponseDto(institution)).thenReturn(responseDto);

        final InstitutionResponseDto result = institutionService.getInstitutionById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
    }

    @Test
    void getInstitutionById_notFound_throwsException() {
        when(institutionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> institutionService.getInstitutionById(id));
    }

    @Test
    void updateInstitution_success() {
        final InstitutionRequestDto updateDto = new InstitutionRequestDto("Updated University", "TESTU", "Updated description", true);
        final InstitutionResponseDto updatedResponseDto = new InstitutionResponseDto(id, "Updated University", "TESTU", "Updated description", true, null, null, null, null, 2L);

        when(institutionRepository.findById(id)).thenReturn(Optional.of(institution));
        when(institutionRepository.save(institution)).thenReturn(institution);
        when(institutionMapper.toResponseDto(institution)).thenReturn(updatedResponseDto);

        final InstitutionResponseDto result = institutionService.updateInstitution(id, updateDto);

        assertNotNull(result);
        assertEquals("Updated University", result.name());
        verify(institutionMapper).updateEntity(updateDto, institution);
        verify(institutionRepository).save(institution);
    }

    @Test
    void deactivateInstitution_success() {
        when(institutionRepository.findById(id)).thenReturn(Optional.of(institution));
        when(institutionRepository.save(institution)).thenReturn(institution);

        institutionService.deactivateInstitution(id);

        assertFalse(institution.getActive());
        verify(institutionRepository).save(institution);
    }
}
