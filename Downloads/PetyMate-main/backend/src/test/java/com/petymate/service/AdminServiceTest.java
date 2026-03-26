package com.petymate.service;

import com.petymate.dto.AdminDto;
import com.petymate.entity.*;
import com.petymate.exception.ResourceNotFoundException;
import com.petymate.repository.*;
import com.petymate.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminService Tests")
class AdminServiceTest {

    @InjectMocks private AdminServiceImpl adminService;
    @Mock private UserRepository userRepository;
    @Mock private PetRepository petRepository;
    @Mock private MatchRequestRepository matchRequestRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private VetRepository vetRepository;
    @Mock private TrainerRepository trainerRepository;
    @Mock private ReviewRepository reviewRepository;

    @Test @DisplayName("Dashboard: returns stats")
    void getDashboard() {
        when(userRepository.count()).thenReturn(100L);
        when(userRepository.countByIsBannedFalse()).thenReturn(95L);
        when(userRepository.countTodayRegistrations()).thenReturn(5L);
        when(petRepository.count()).thenReturn(200L);
        when(petRepository.countByStatus(Pet.PetStatus.ACTIVE)).thenReturn(150L);
        when(matchRequestRepository.count()).thenReturn(300L);
        when(matchRequestRepository.countSuccessfulMatches()).thenReturn(50L);
        when(orderRepository.count()).thenReturn(80L);
        when(orderRepository.countTodayOrders()).thenReturn(3L);
        when(orderRepository.calculateMonthlyRevenue()).thenReturn(BigDecimal.valueOf(50000));
        when(orderRepository.calculateTotalRevenue()).thenReturn(BigDecimal.valueOf(200000));
        when(vetRepository.countByIsVerifiedFalse()).thenReturn(2L);
        when(trainerRepository.countByIsVerifiedFalse()).thenReturn(1L);

        AdminDto.DashboardResponse resp = adminService.getDashboard();

        assertEquals(100, resp.getTotalUsers());
        assertEquals(95, resp.getActiveUsers());
        assertEquals(5, resp.getTodayRegistrations());
        assertEquals(200, resp.getTotalPets());
        assertEquals(150, resp.getActivePetListings());
        assertEquals(50, resp.getSuccessfulMatches());
        assertEquals(BigDecimal.valueOf(50000), resp.getMonthlyRevenue());
    }

    @Test @DisplayName("Ban user: success")
    void banUser_Success() {
        User user = User.builder().id(1L).isBanned(false).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        adminService.banUser(1L);
        assertTrue(user.getIsBanned());
    }

    @Test @DisplayName("Verify vet: success")
    void verifyVet_Success() {
        Vet vet = Vet.builder().id(1L).isVerified(false).build();
        when(vetRepository.findById(1L)).thenReturn(Optional.of(vet));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        adminService.verifyVet(1L);
        assertTrue(vet.getIsVerified());
    }

    @Test @DisplayName("Update order status: success")
    void updateOrderStatus_Success() {
        Order order = Order.builder().id(1L).status(Order.OrderStatus.PAID).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        AdminDto.UpdateOrderStatusRequest req = new AdminDto.UpdateOrderStatusRequest();
        req.setStatus("SHIPPED"); req.setTrackingNumber("TRACK123");
        adminService.updateOrderStatus(1L, req);
        assertEquals(Order.OrderStatus.SHIPPED, order.getStatus());
        assertEquals("TRACK123", order.getTrackingNumber());
    }

    @Test @DisplayName("Ban user: not found")
    void banUser_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> adminService.banUser(999L));
    }
}
