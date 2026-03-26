package com.petymate.service.impl;

import com.petymate.config.RazorpayConfig;
import com.petymate.dto.PagedResponse;
import com.petymate.dto.VetDto;
import com.petymate.entity.*;
import com.petymate.exception.*;
import com.petymate.repository.*;
import com.petymate.service.AppointmentService;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacUtils;
import org.json.JSONObject;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Slf4j
public class AppointmentServiceImpl implements AppointmentService {

    private final VetAppointmentRepository appointmentRepository;
    private final VetRepository vetRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final RazorpayClient razorpayClient;
    private final RazorpayConfig razorpayConfig;

    @Override @Transactional
    public VetDto.AppointmentOrderResponse createOrder(String email, VetDto.CreateAppointmentOrderRequest req) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Vet vet = vetRepository.findById(req.getVetId()).orElseThrow(() -> new ResourceNotFoundException("Vet not found"));
        petRepository.findById(req.getPetId()).orElseThrow(() -> new ResourceNotFoundException("Pet not found"));

        if (appointmentRepository.existsByVetIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                req.getVetId(), req.getDate(), req.getTime(), VetAppointment.AppointmentStatus.CANCELLED)) {
            throw new IllegalArgumentException("Vet is not available at this time slot");
        }

        int amountPaise = vet.getConsultationFee().multiply(BigDecimal.valueOf(100)).intValue();
        try {
            JSONObject options = new JSONObject();
            options.put("amount", amountPaise); options.put("currency", "INR");
            options.put("receipt", "appt_" + UUID.randomUUID());
            com.razorpay.Order order = razorpayClient.orders.create(options);

            VetAppointment appt = VetAppointment.builder()
                    .user(user).vet(vet).pet(petRepository.findById(req.getPetId()).orElseThrow())
                    .appointmentDate(req.getDate()).appointmentTime(req.getTime())
                    .mode(VetAppointment.AppointmentMode.valueOf(req.getMode()))
                    .notes(req.getNotes()).totalFee(vet.getConsultationFee())
                    .razorpayOrderId(order.get("id")).status(VetAppointment.AppointmentStatus.PENDING).build();
            appointmentRepository.save(appt);

            return VetDto.AppointmentOrderResponse.builder()
                    .razorpayOrderId(order.get("id")).amount(amountPaise).currency("INR").keyId(razorpayConfig.getKeyId()).build();
        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to create appointment order", e);
        }
    }

    @Override @Transactional
    public VetDto.AppointmentResponse verify(String email, VetDto.VerifyAppointmentRequest req) {
        String payload = req.getRazorpayOrderId() + "|" + req.getRazorpayPaymentId();
        String expected = new HmacUtils("HmacSHA256", razorpayConfig.getKeySecret()).hmacHex(payload);
        if (!expected.equals(req.getRazorpaySignature())) throw new PaymentVerificationException("Invalid signature");

        VetAppointment appt = appointmentRepository.findByRazorpayOrderId(req.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        appt.setRazorpayPaymentId(req.getRazorpayPaymentId());
        appt.setRazorpaySignature(req.getRazorpaySignature());
        appt.setStatus(VetAppointment.AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appt);

        Vet vet = appt.getVet();
        vet.setTotalAppointments(vet.getTotalAppointments() + 1);
        vetRepository.save(vet);

        return mapToResponse(appt);
    }

    @Override
    public PagedResponse<VetDto.AppointmentResponse> getMyAppointments(String email, Pageable pageable) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Page<VetAppointment> page = appointmentRepository.findByUserIdOrderByAppointmentDateDesc(user.getId(), pageable);
        return PagedResponse.<VetDto.AppointmentResponse>builder()
                .content(page.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .page(page.getNumber()).size(page.getSize())
                .totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).last(page.isLast()).build();
    }

    @Override @Transactional
    public void cancelAppointment(Long id, String email) {
        VetAppointment appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        if (!appt.getUser().getEmail().equals(email)) throw new UnauthorizedException("Not your appointment");
        LocalDateTime apptDt = LocalDateTime.of(appt.getAppointmentDate(), appt.getAppointmentTime());
        if (apptDt.isBefore(LocalDateTime.now().plusHours(24)))
            throw new IllegalArgumentException("Cannot cancel within 24 hours of appointment");
        appt.setStatus(VetAppointment.AppointmentStatus.CANCELLED);
        appointmentRepository.save(appt);
    }

    private VetDto.AppointmentResponse mapToResponse(VetAppointment a) {
        return VetDto.AppointmentResponse.builder()
                .id(a.getId()).userId(a.getUser().getId())
                .appointmentDate(a.getAppointmentDate()).appointmentTime(a.getAppointmentTime())
                .mode(a.getMode().name()).status(a.getStatus().name())
                .notes(a.getNotes()).vetNotes(a.getVetNotes()).totalFee(a.getTotalFee())
                .createdAt(a.getCreatedAt()).build();
    }
}
