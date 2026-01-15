package code.with.vanilson.studentmanagement.modules.student;

import code.with.vanilson.studentmanagement.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository repository;

    public StudentDto createStudent(StudentDto dto) {
        Student student = Student.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .dateOfBirth(dto.getDateOfBirth())
                .address(dto.getAddress())
                .phoneNumber(dto.getPhoneNumber())
                .build();
        Student saved = repository.save(student);
        return mapToDto(saved);
    }

    @Cacheable(value = "students", key = "#id")
    public StudentDto getStudent(Long id) {
        Student student = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("student.not_found", id));
        return mapToDto(student);
    }

    public List<StudentDto> getAllStudents() {
        return repository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "students", key = "#id")
    public StudentDto updateStudent(Long id, StudentDto dto) {
        Student student = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("student.not_found", id));

        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setAddress(dto.getAddress());
        student.setPhoneNumber(dto.getPhoneNumber());

        Student updated = repository.save(student);
        return mapToDto(updated);
    }

    @CacheEvict(value = "students", key = "#id")
    public void deleteStudent(Long id) {
        repository.deleteById(id);
    }

    private StudentDto mapToDto(Student student) {
        return StudentDto.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .dateOfBirth(student.getDateOfBirth())
                .address(student.getAddress())
                .phoneNumber(student.getPhoneNumber())
                .build();
    }
}
