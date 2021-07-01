package com.pragmatest.services;

import com.pragmatest.models.User;
import com.pragmatest.models.UserEntity;
import com.pragmatest.repositories.UserRepository;
import com.pragmatest.utils.UserUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Optional<User> getUserById(Long id) {
        Optional<UserEntity> userEntityInDb = userRepository.findById(id);

        if (userEntityInDb.isEmpty()){
            return Optional.empty();
        }

        UserEntity userEntity = userEntityInDb.get();

        User user = modelMapper.map(userEntity, User.class);

        return Optional.of(user);
    }

    @Override
    public List<User> getAllUsers() {

        List<UserEntity> userEntities = userRepository.findAll();

        Type returnType = new TypeToken<List<User>>() {}.getType();

        List<User> users = modelMapper.map(userEntities, returnType);

        return users;
    }

    @Override
    public Optional<User> saveUser(User user) {
        int age = user.getAge();
        boolean isAdult = userUtils.isAdult(age);

        Optional<UserEntity> savedUserEntity = Optional.empty();

        UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        if (isAdult) {
            savedUserEntity = Optional.of(userRepository.save(userEntity));
        }

        if (savedUserEntity.isEmpty()) {
            return Optional.empty();
        }

        userEntity = savedUserEntity.get();

        User savedUser = modelMapper.map(userEntity, User.class);

        return Optional.of(savedUser);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
