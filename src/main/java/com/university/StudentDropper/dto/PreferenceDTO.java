package com.university.StudentDropper.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PreferenceDTO {
    private Long destinationId;
    private Integer ranking;
    private String destinationName;

    public PreferenceDTO(Long destinationId, int ranking) {
        this.destinationId = destinationId;
        this.ranking = ranking;
    }
}
