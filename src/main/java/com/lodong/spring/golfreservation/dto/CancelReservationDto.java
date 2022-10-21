package com.lodong.spring.golfreservation.dto;

import lombok.Data;

@Data
public class CancelReservationDto {
    private String userId;
    private String reservationType;
    private String reservationId;
}
