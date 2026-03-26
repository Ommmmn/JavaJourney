package com.petymate.service;

import com.petymate.dto.PagedResponse;
import com.petymate.dto.VetDto;
import org.springframework.data.domain.Pageable;

public interface AppointmentService {
    VetDto.AppointmentOrderResponse createOrder(String email, VetDto.CreateAppointmentOrderRequest request);
    VetDto.AppointmentResponse verify(String email, VetDto.VerifyAppointmentRequest request);
    PagedResponse<VetDto.AppointmentResponse> getMyAppointments(String email, Pageable pageable);
    void cancelAppointment(Long id, String email);
}
