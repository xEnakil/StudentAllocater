package com.university.StudentDropper.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllDTO {

    private String studentName;
    private String studentSurname;
    private String preferredDestinationName;
    private Integer preferenceRank;
    private String allocatedDestinationName;
    private Integer allocatedRank;
}
