
package acmevolar.repository.springdatajpa;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import acmevolar.model.Plane;
import acmevolar.repository.PlaneRepository;

public interface SpringDataPlaneRepository extends PlaneRepository, Repository<Plane, Integer> {
	
	@Query("SELECT p FROM Plane p WHERE p.airline.id =:id")
	List<Plane> findPlanesByAirlineId(int id) throws DataAccessException;
	
}
