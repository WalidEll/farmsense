package ma.farmsense.repository;

import ma.farmsense.entity.Device;
import ma.farmsense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {

    Optional<Device> findByDeviceId(String deviceId);

    List<Device> findByUser(User user);

    Optional<Device> findByIdAndUser(UUID id, User user);
}
