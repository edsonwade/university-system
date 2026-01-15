package code.with.vanilson.studentmanagement.modules.notification;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
}
