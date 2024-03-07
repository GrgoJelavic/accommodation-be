package com.evoapartments.accommodationbe.domain.calendar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    @NotNull
    @NotBlank
    @NotEmpty
    private String summary;

    @NotNull
    @NotBlank
    @NotEmpty
    private String location;

    @NotNull
    @NotBlank
    @NotEmpty
    private String description;

    @NotNull
    @NotBlank
    @NotEmpty
    private String startDate;

    @NotNull
    @NotBlank
    @NotEmpty
    private String endDate;
}