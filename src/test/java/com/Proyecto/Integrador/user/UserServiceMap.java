package com.Proyecto.Integrador.user;

import com.Proyecto.Integrador.dto.UserDto;
import com.Proyecto.Integrador.entity.UserMongoEntity;
import com.Proyecto.Integrador.exception.UserNotFoundException;
import com.Proyecto.Integrador.repository.UserMongoRepository;
import com.Proyecto.Integrador.service.UserServiceMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserServiceMapTest {

    @Mock
    private UserMongoRepository userMongoRepository;

    @InjectMocks
    private UserServiceMap userServiceMap;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void allTest() {
        UserMongoEntity user1 = new UserMongoEntity();
        user1.setId("1");
        user1.setName("John Doe");
        user1.setEmail("john@example.com");

        UserMongoEntity user2 = new UserMongoEntity();
        user2.setId("2");
        user2.setName("Jane Doe");
        user2.setEmail("jane@example.com");

        when(userMongoRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<UserDto> result = userServiceMap.all();

        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("jane@example.com", result.get(1).getEmail());

        verify(userMongoRepository, times(1)).findAll();
    }

    @Test
    void findByIdTest() {
        UserMongoEntity user = new UserMongoEntity();
        user.setId("1");
        user.setName("John Doe");
        user.setEmail("john@example.com");

        when(userMongoRepository.findById("1")).thenReturn(Optional.of(user));

        Optional<UserDto> result = userServiceMap.findById("1");

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        assertEquals("john@example.com", result.get().getEmail());

        verify(userMongoRepository, times(1)).findById("1");
    }

    @Test
    void findByIdNotFoundTest() {
        when(userMongoRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userServiceMap.findById("1"));

        verify(userMongoRepository, times(1)).findById("1");
    }

    @Test
    void saveTest() {
        UserDto userDto = new UserDto("1", "John Doe", "john@example.com");
        UserMongoEntity user = new UserMongoEntity();
        user.setId("1");
        user.setName("John Doe");
        user.setEmail("john@example.com");

        when(userMongoRepository.save(any(UserMongoEntity.class))).thenReturn(user);

        UserDto result = userServiceMap.save(userDto);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());

        verify(userMongoRepository, times(1)).save(any(UserMongoEntity.class));
    }

    @Test
    void updateTest() {
        UserDto userDto = new UserDto("1", "John Doe", "john@example.com");
        UserMongoEntity user = new UserMongoEntity();
        user.setId("1");
        user.setName("John Doe");
        user.setEmail("john@example.com");

        when(userMongoRepository.findById("1")).thenReturn(Optional.of(user));
        when(userMongoRepository.save(any(UserMongoEntity.class))).thenReturn(user);

        UserDto result = userServiceMap.update(userDto, "1");

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());

        verify(userMongoRepository, times(1)).findById("1");
        verify(userMongoRepository, times(1)).save(any(UserMongoEntity.class));
    }

    @Test
    void updateNotFoundTest() {
        UserDto userDto = new UserDto("1", "John Doe", "john@example.com");

        when(userMongoRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userServiceMap.update(userDto, "1"));

        verify(userMongoRepository, times(1)).findById("1");
        verify(userMongoRepository, times(0)).save(any(UserMongoEntity.class));
    }

    @Test
    void deleteByIdTest() {
        UserMongoEntity user = new UserMongoEntity();
        user.setId("1");
        user.setName("John Doe");
        user.setEmail("john@example.com");

        when(userMongoRepository.findById("1")).thenReturn(Optional.of(user));
        doNothing().when(userMongoRepository).delete(any(UserMongoEntity.class));

        userServiceMap.deleteById("1");

        verify(userMongoRepository, times(1)).findById("1");
        verify(userMongoRepository, times(1)).delete(any(UserMongoEntity.class));
    }

    @Test
    void deleteByIdNotFoundTest() {
        when(userMongoRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userServiceMap.deleteById("1"));

        verify(userMongoRepository, times(1)).findById("1");
        verify(userMongoRepository, times(0)).delete(any(UserMongoEntity.class));
    }
}
