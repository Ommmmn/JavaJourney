package com.petymate.controller;

import com.petymate.dto.*;
import com.petymate.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<VetDto.AppointmentOrderResponse>> createOrder(
            Authentication auth, @Valid @RequestBody VetDto.CreateAppointmentOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success(appointmentService.createOrder(auth.getName(), request)));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<VetDto.AppointmentResponse>> verify(
            Authentication auth, @Valid @RequestBody VetDto.VerifyAppointmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Appointment confirmed!", appointmentService.verify(auth.getName(), request)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<PagedResponse<VetDto.AppointmentResponse>>> getMyAppointments(
            Authentication auth, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(appointmentService.getMyAppointments(auth.getName(), PageRequest.of(page, size))));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable Long id, Authentication auth) {
        appointmentService.cancelAppointment(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Appointment cancelled", null));
    }
}
