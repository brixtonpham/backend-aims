package Project_ITSS.service;

import Project_ITSS.repository.UserViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserViewService {
    
    private final UserViewRepository userRepository;

    @Autowired
    public UserViewService(UserViewRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean checkUserRole(int userId) {
        String role = userRepository.verifyUserRole(userId);
        return "ProductManager".equalsIgnoreCase(role);
    }

    public boolean verifyUserRole(String role) {
        return role.equalsIgnoreCase("ProductManager");
    }
}