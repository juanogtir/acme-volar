/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package acmevolar.repository.springdatajpa;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import acmevolar.model.Airline;
import acmevolar.projections.AirlineListAttributes;
import acmevolar.repository.AirlineRepository;
import acmevolar.repository.OwnerRepository;

/**
 * Spring Data JPA specialization of the {@link OwnerRepository} interface
 *
 * @author Michael Isvy
 * @since 15.1.2013
 */
public interface SpringDataAirlineRepository extends AirlineRepository, Repository<Airline, Integer> {

	@Override
	@Query("SELECT airline FROM Airline airline WHERE airline.id =:id")
	Airline findById(@Param("id") int id);

	@Override
	@Query("SELECT airline FROM Airline airline WHERE airline.user.username =:username")
	Airline findByUsername(@Param("username") String username);
	
	@Override
	@Query("SELECT a.id AS id, a.identification AS identification, a.name AS name FROM Airline a")
	List<AirlineListAttributes> findAllAirlinesAttributes();

}
