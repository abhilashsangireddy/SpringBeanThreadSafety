package com.experiment.SpringBeanThreadSafety.services;

import com.experiment.SpringBeanThreadSafety.entities.UserDto;
import com.experiment.SpringBeanThreadSafety.entities.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public int totalCreatedUsers = 0;

    public int addUser(UserDto userDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setName(userDto.getName());
        userEntity.setEmailId(userDto.getEmailId());
        saveUserToDB(userEntity);
        return totalCreatedUsers;
    }

    private void saveUserToDB(UserEntity userEntity) {
        totalCreatedUsers++;
        System.out.println("Thread "+  Thread.currentThread().getName() + ": user saved to db");
    }
}
