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
public class FreeBusyDTO {
    private String calendarId;

    @NotNull
    @NotBlank
    @NotEmpty
    private String timeMin;

    @NotNull
    @NotBlank
    @NotEmpty
    private String timeMax;
}
