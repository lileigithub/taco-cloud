package tacos.security.repository;

import org.springframework.data.repository.CrudRepository;
import tacos.security.entity.User;

/**
 * 用户数据访问接口
 */
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);

    /**
     * 检查用户名是否存在
     */
    Boolean existsByUsername(String username);
}
