package ru.practicum.ewm.main.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.dto.UserDto;
import ru.practicum.ewm.main.dto.UserDtoFromRequest;
import ru.practicum.ewm.main.exception.UserNotFoundException;
import ru.practicum.ewm.main.model.User;
import ru.practicum.ewm.main.repository.UserRepository;
import ru.practicum.ewm.main.services.UserService;
import ru.practicum.ewm.main.common.MyPageRequest;
import ru.practicum.ewm.main.mapper.UserDtoMapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsersByIds(Set<Long> ids, Integer from, Integer size) {
        if (ids != null && !ids.isEmpty()) {
            return userRepository.findAllById(ids).
                    stream()
                    .map(UserDtoMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAll(MyPageRequest.of(from, size))
                    .stream()
                    .map(UserDtoMapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        checkUserExists(id);
        userRepository.deleteById(id);
    }

    private void checkUserExists(Long id) {
        if(!userRepository.existsById(id)) {
            throw new UserNotFoundException(String.format("user with id = %d not found", id));
        }
    }

    @Override
    @Transactional
    public UserDto addNewUser(UserDtoFromRequest userDtoFromRequest) {
        User user = UserDtoMapper.toUser(userDtoFromRequest);
        User savedUser = userRepository.save(user);
        return UserDtoMapper.toUserDto(savedUser);
    }
}
