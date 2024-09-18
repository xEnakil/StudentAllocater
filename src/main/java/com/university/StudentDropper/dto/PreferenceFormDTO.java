package com.university.StudentDropper.dto;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PreferenceFormDTO {

    List<PreferenceDTO> preferences;
}
