package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<User> users;

        if (CollectionUtils.isNotEmpty(ids)) {
            users = userRepository.findUsersByIdIn(ids, pageable);
        } else {
            users = userRepository.findAllUsers(pageable);
        }
        return UserMapper.toListOfUserDto(users);
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest request) {
        User user = userRepository.save(UserMapper.toUser(request));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
        userRepository.deleteById(userId);
    }
}