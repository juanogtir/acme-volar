
package acmevolar.repository.springdatajpa;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import acmevolar.model.Plane;
import acmevolar.repository.PlaneRepository;

public interface SpringDataPlaneRepository extends PlaneRepository, Repository<Plane, Integer> {

	@Override
	@Query("SELECT plane FROM Plane plane WHERE plane.airline.user.username =:airline ")
	List<Plane> findPlanesbyAirline(@Param("airline") String airline) throws DataAccessException;
}
