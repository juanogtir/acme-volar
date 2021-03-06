
package acmevolar.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.query.Param;

import acmevolar.model.Plane;
import acmevolar.projections.PlaneListAttributes;

public interface PlaneRepository {

	Plane findById(int id) throws DataAccessException;

	Collection<Plane> findAll() throws DataAccessException;

	void deleteById(int id);

	List<Plane> findPlanesByAirlineId(int id) throws DataAccessException;

	List<Plane> findPlanesbyAirline(@Param("airline") String airline) throws DataAccessException;

	void save(Plane plane) throws DataAccessException;

	Plane findByReference(String reference);

	List<PlaneListAttributes> findAllAirlinePlaneListAttributes(String username) throws DataAccessException;
}
