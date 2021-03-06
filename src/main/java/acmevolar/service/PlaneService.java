
package acmevolar.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import acmevolar.model.Airline;
import acmevolar.model.Plane;
import acmevolar.projections.PlaneListAttributes;
import acmevolar.repository.AirlineRepository;
import acmevolar.repository.PlaneRepository;
import acmevolar.repository.springdatajpa.SpringDataPlaneRepository;
import acmevolar.service.exceptions.DuplicatedPetNameException;

@Service
public class PlaneService {

	private PlaneRepository				planeRepository;
	private AirlineRepository			airlineRepository;
	private SpringDataPlaneRepository	springPlaneRepository;


	@Autowired
	public PlaneService(final PlaneRepository planeRepository, final SpringDataPlaneRepository springPlaneRepository, final AirlineRepository airlineRepository) {
		this.planeRepository = planeRepository;
		this.springPlaneRepository = springPlaneRepository;
		this.airlineRepository = airlineRepository;
	}

	@Transactional(readOnly = true)
	public Plane findPlaneById(final int id) throws DataAccessException {
		return this.planeRepository.findById(id);
	}

	@Transactional(readOnly = true)
	public Plane findPlaneByReference(final String reference) throws DataAccessException {
		return this.planeRepository.findByReference(reference);
	}

	@Transactional(rollbackFor = DuplicatedPetNameException.class)
	@CacheEvict(cacheNames = "planesByAirline", allEntries = true)
	public void savePlane(final Plane plane) throws DataAccessException {
		this.planeRepository.save(plane);
	}

	@CacheEvict(cacheNames = "planesByAirline", allEntries = true)
	public void deleteById(final int id) throws DataAccessException {
		this.planeRepository.deleteById(id);
	}

	@CacheEvict(cacheNames = "planesByAirline", allEntries = true)
	public void deletePlane(final Plane plane) throws DataAccessException {
		this.planeRepository.deleteById(plane.getId());
	}
	/*
	 * public void updatePlane(final Plane plane) throws DataAccessException {
	 * Integer id = plane.getId(); // extract id of a plane
	 * Plane plane2 = this.findPlaneById(id); // we know the original plane with that id
	 * this.deletePlane(plane2); // we delete the original
	 * this.planeRepository.save(plane); // we replace with the updated version
	 * }
	 */
	@Transactional(readOnly = true)
	public Collection<Plane> findPlanes() throws DataAccessException {
		return this.planeRepository.findAll();
	}

	//Airline
	@Transactional(readOnly = true)
	public Airline findAirlineByUsername(final String username) throws DataAccessException {
		return this.airlineRepository.findByUsername(username);
	}
	@Transactional(readOnly = true)
	@Cacheable("planesByAirline")
	public Collection<Plane> getAllPlanesFromAirline(final String airline) {
		return this.planeRepository.findPlanesbyAirline(airline);
	}

	public List<Plane> getAllPlanesFromAirline(final Airline airline) {
		return this.springPlaneRepository.findPlanesByAirlineId(airline.getId());
	}

	@Transactional(readOnly = true)
	@Cacheable("planesByAirline")
	public List<PlaneListAttributes> findPlaneListAttributes(final String username) throws DataAccessException {
		return this.planeRepository.findAllAirlinePlaneListAttributes(username);
	}

}
