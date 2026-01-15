package code.with.vanilson.studentmanagement.modules.course;

import code.with.vanilson.studentmanagement.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DegreeService {
    private final DegreeRepository repository;

    public Degree createDegree(Degree degree) {
        return repository.save(degree);
    }

    public List<Degree> getAllDegrees() {
        return repository.findAll();
    }

    public Degree getDegreeById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("degree.not_found", id));
    }
}
