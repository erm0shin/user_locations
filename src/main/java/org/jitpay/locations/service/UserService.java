package org.jitpay.locations.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jitpay.locations.dto.user.UserDTO;
import org.jitpay.locations.exception.AlreadyExistsException;
import org.jitpay.locations.exception.NotFoundException;
import org.jitpay.locations.mapper.UserMapper;
import org.jitpay.locations.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDTO createUser(final UserDTO userDTO) {
        userRepository.findById(userDTO.userId())
                .ifPresent(userEntity -> {
                    throw new AlreadyExistsException(
                            String.format("User with id = '%s' already exists", userDTO.userId())
                    );
                });

        final var entity = userMapper.mapUserCreateRequest(userDTO);
        userRepository.save(entity);
        return userDTO;
    }

    @Transactional
    public UserDTO updateUserInfo(final UserDTO userDTO) {
        final var userEntity = userRepository.findById(userDTO.userId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("No user was found for update by id = '%s'", userDTO.userId())
                ));
        userMapper.updateUserEntity(userEntity, userDTO);
        userRepository.save(userEntity);
        return userDTO;
    }

}
