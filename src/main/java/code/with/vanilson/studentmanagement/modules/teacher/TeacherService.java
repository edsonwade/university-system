package code.with.vanilson.studentmanagement.modules.teacher;

import code.with.vanilson.studentmanagement.common.exception.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherService {
    private final TeacherRepository repository;

    public Teacher createTeacher(Teacher teacher) {
        if (repository.existsByEmail(teacher.getEmail())) {
            throw new ResourceAlreadyExistsException("teacher.email_exists", teacher.getEmail());
        }
        return repository.save(teacher);
    }

    public List<Teacher> getAllTeachers() {
        return repository.findAll();
    }

    public Teacher getTeacherById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new code.with.vanilson.studentmanagement.common.exception.ResourceNotFoundException(
                        "teacher.not_found", id));
    }
}
