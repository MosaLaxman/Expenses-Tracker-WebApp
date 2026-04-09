package com.SpringBootMVC.ExpensesTracker.controller;

import com.SpringBootMVC.ExpensesTracker.DTO.NotificationDTO;
import com.SpringBootMVC.ExpensesTracker.entity.Client;
import com.SpringBootMVC.ExpensesTracker.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    NotificationService notificationService;

    @Test
    void shouldReturnNotificationsForAuthenticatedSession() throws Exception {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(1);
        dto.setMessage("test");
        when(notificationService.getNotifications(anyInt(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(dto)));

        MockHttpSession session = new MockHttpSession();
        Client client = new Client();
        client.setId(1);
        session.setAttribute("client", client);

        mockMvc.perform(get("/api/notifications").session(session))
                .andExpect(status().isOk());
    }
}
