package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
	List<Customer> findByName(String name);

	@Query(value = """
			SELECT c FROM Customer c
			WHERE c.createdAt 
			BETWEEN :start AND :end 
			AND c.address LIKE :area
			AND c.points > 0
			AND c.name LIKE :keyword OR c.phone LIKE :keyword OR c.email LIKE :keyword
			""")
	List<Customer> findAllByDateAndAreaAndHasPointAndKeyword(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, 
			@Param("area") String area, @Param("keyword") String keyword);

	@Query(value = """
			SELECT c FROM Customer c
			WHERE c.createdAt 
			BETWEEN :start AND :end 
			AND c.address LIKE :area
			AND c.points = 0
			AND c.name LIKE :keyword OR c.phone LIKE :keyword OR c.email LIKE :keyword
			""")
	List<Customer> findAllByDateAndAreaAndNoPointAndKeyword(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
			@Param("area") String area, @Param("keyword") String keyword);
	
	@Query(value = """
			SELECT c FROM Customer c
			WHERE c.createdAt 
			BETWEEN :start AND :end 
			AND c.address LIKE :area
			AND c.points > 0
			""")
	List<Customer> findAllByDateAndAreaAndHasPoint(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, 
			@Param("area") String area);

	@Query(value = """
			SELECT c FROM Customer c
			WHERE c.createdAt 
			BETWEEN :start AND :end 
			AND c.address LIKE :area
			AND c.points = 0
			""")
	List<Customer> findAllByDateAndAreaAndNoPoint(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
			@Param("area") String area);

	@Query(value = """
			SELECT c FROM Customer c
			WHERE c.createdAt 
			BETWEEN :start AND :end 
			AND c.address LIKE :area
			AND c.name LIKE :keyword OR c.phone LIKE :keyword OR c.email LIKE :keyword
			""")
	List<Customer> findAllByDateAndAreaAndKeyword(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, 
			@Param("area") String area, @Param("keyword") String keyword);
	
	@Query(value = """
			SELECT c FROM Customer c
			WHERE c.createdAt 
			BETWEEN :start AND :end 
			AND
			c.address LIKE :area
			""")
	List<Customer> findAllByDateAndArea(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, 
			@Param("area") String area);

	@Query(value = """
			SELECT c FROM Customer c 
			WHERE c.createdAt 
			BETWEEN :start AND :end 
			AND c.points > 0
			AND c.name LIKE :keyword OR c.phone LIKE :keyword OR c.email LIKE :keyword
			""")
	List<Customer> findAllByDateAndHasPointAndKeyword(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
			@Param("keyword") String keyword);

	@Query("""
			SELECT c FROM Customer c 
			WHERE c.createdAt 
			BETWEEN :start AND :end 
			AND c.points = 0
			AND c.name LIKE :keyword OR c.phone LIKE :keyword OR c.email LIKE :keyword
			""")
	List<Customer> findAllByDateAndNoPointAndKeyword(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
			@Param("keyword") String keyword);
	
	@Query(value = """
			SELECT c FROM Customer c 
			WHERE c.createdAt 
			BETWEEN :start AND :end 
			AND c.points > 0
			""")
	List<Customer> findAllByDateAndHasPoint(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	@Query("""
			SELECT c FROM Customer c 
			WHERE c.createdAt 
			BETWEEN :start AND :end 
			AND c.points = 0
			""")
	List<Customer> findAllByDateAndNoPoint(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	@Query("""
			SELECT c FROM Customer c 
			WHERE c.createdAt 
			BETWEEN :start AND :end
			AND c.name LIKE :keyword OR c.phone LIKE :keyword OR c.email LIKE :keyword
			""")
	List<Customer> findAllByCreateDateAndKeyword(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
			@Param("keyword") String keyword);
	
	@Query("""
			SELECT c FROM Customer c 
			WHERE c.createdAt 
			BETWEEN :start AND :end
			""")
	List<Customer> findAllByCreateDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	@Query("""
			SELECT c FROM Customer c 
			WHERE c.address LIKE :area
			AND c.points > 0
			AND c.name LIKE :keyword OR c.phone LIKE :keyword OR c.email LIKE :keyword
			""")
	List<Customer> findAllByAddressAndHasPointsAndKeyword(@Param("area") String area, @Param("keyword") String keyword);

	@Query("""
			SELECT c FROM Customer c 
			WHERE c.address LIKE :area
			AND c.points = 0
			AND c.name LIKE :keyword OR c.phone LIKE :keyword OR c.email LIKE :keyword
			""")
	List<Customer> findAllByAddressAndNoPointsAndKeyword(@Param("area") String area, @Param("keyword") String keyword);
	
	List<Customer> findAllByAddressLikeAndPointsGreaterThan(String area, Integer point);

	List<Customer> findAllByAddressLikeAndPointsLessThanEqual(String area, Integer point);

	@Query("""
			SELECT c FROM Customer c 
			WHERE c.address LIKE :area
			AND c.name LIKE :keyword OR c.phone LIKE :keyword OR c.email LIKE :keyword
			""")
	List<Customer> findAllByAddressAndKeyword(@Param("area") String area, @Param("keyword") String keyword);
	
	List<Customer> findAllByAddressLike(String area);

	@Query("""
			SELECT c FROM Customer c 
			WHERE c.address LIKE :area
			AND c.points > 0
			AND c.name LIKE :keyword OR c.phone LIKE :keyword OR c.email LIKE :keyword
			""")
	List<Customer> findAllByHasPointsAndKeyword(@Param("keyword") String keyword);

	@Query("""
			SELECT c FROM Customer c 
			WHERE c.points = 0
			AND c.name LIKE :keyword OR c.phone LIKE :keyword OR c.email LIKE :keyword
			""")
	List<Customer> findAllByNoPointsAndKeyword(@Param("keyword") String keyword);
	
	List<Customer> findAllByPointsGreaterThan(Integer point);

	List<Customer> findAllByPointsLessThanEqual(Integer point);
	
	@Query("""
			SELECT c FROM Customer c 
			WHERE c.name LIKE :keyword OR c.phone LIKE :keyword OR c.email LIKE :keyword
			""")
	List<Customer> findAllByKeyword(@Param("keyword") String keyword);

	Optional<Customer> findByEmail(String email);
}
