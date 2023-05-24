package org.jitpay.locations.mapper;

import org.jitpay.locations.dto.user.UserDTO;
import org.jitpay.locations.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity mapUserCreateRequest(UserDTO userCreateRequest) {
        return UserEntity.builder()
                .id(userCreateRequest.userId())
                .email(userCreateRequest.email())
                .firstName(userCreateRequest.firstName())
                .secondName(userCreateRequest.secondName())
                .build();
    }

    public void updateUserEntity(UserEntity entityToUpdate, UserDTO updateRequest) {
        entityToUpdate.setEmail(updateRequest.email());
        entityToUpdate.setFirstName(updateRequest.firstName());
        entityToUpdate.setSecondName(updateRequest.secondName());
    }

}
