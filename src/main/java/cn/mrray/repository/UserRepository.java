package cn.mrray.repository;

import cn.mrray.entity.domain.User;

import java.util.List;

public interface UserRepository extends BaseRepository<User> {

    User findByUsername(String uame);

    List<User> findByRole(String role);

}
