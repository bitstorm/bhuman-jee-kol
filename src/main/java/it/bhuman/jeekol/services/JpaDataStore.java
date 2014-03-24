package it.bhuman.jeekol.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.bhuman.jeekol.entities.Course;
import it.bhuman.jeekol.entities.Student;
import it.bhuman.jeekol.entities.Student.Gender;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;

@Startup
@Singleton
public class JpaDataStore {
	
	@PersistenceUnit(unitName = "test")
    private EntityManagerFactory entityManagerFactory;
	@Resource(lookup = "java:global/jdbc/MyDS")
	private DataSource dataSource;
	
	private EntityManager entityManager;
	private CriteriaQuery<Course> allCoursesCriteria;
	
	@PostConstruct
    void init()
    {
		entityManager = entityManagerFactory.createEntityManager();
		
		persistTestEntities();
		executeFemMaleQuery();
		createQueryCriteria();
    }

	private void executeFemMaleQuery() {
		try {
			Connection connection = dataSource.getConnection();
		
			Statement statement = connection.createStatement();
			
			statement.execute("update course c1 set femmaleratio = ( \n" +
          "select ( select count(*) from student s1 where s1.gender = 0 and s1.id in (select student_id from attendees where course_id = c1.id)) \n" + 
          "  || '/'  \n" +
          " || ( select count(*) from student s1 where s1.gender = 1 and s1.id in (select student_id from attendees where course_id = c1.id)))");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void createQueryCriteria() {
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		allCoursesCriteria = builder.createQuery(Course.class);
		Root<Course> root = allCoursesCriteria.from(Course.class);
		allCoursesCriteria.select(root);
	}

	private void persistTestEntities() {
		Student s0 = new Student(0, "Andrea", Gender.MALE);
        Student s1 = new Student(1, "Mariano", Gender.MALE);
        Student s2 = new Student(2, "Silvio", Gender.MALE);
        Student s3 = new Student(3, "Stefano", Gender.MALE);
        Student s4 = new Student(4, "Francesca", Gender.FEMALE);
        Student s5 = new Student(5, "Manuela", Gender.FEMALE);
        Student s6 = new Student(6, "Rita", Gender.FEMALE);
        Student s7 = new Student(7, "Paolo", Gender.MALE);
        Student s8 = new Student(8, "Nicoletta", Gender.FEMALE);
        Student s9 = new Student(9, "Bernardo",  Gender.MALE);
        
        Set<Student> ss0 = new HashSet<Student>();
        
        ss0.add(s0);
        ss0.add(s1);
        ss0.add(s6);
        
        Set<Student> ss1 = new HashSet<Student>();
        
        ss1.add(s1);
        ss1.add(s2);
        ss1.add(s7);
        
        Set<Student> ss2 = new HashSet<Student>();
        
        ss2.add(s4);
        ss2.add(s5);
        ss2.add(s6);
        
        Set<Student> ss3 = new HashSet<Student>();
        
        ss3.add(s2);
        ss3.add(s3);
        ss3.add(s4);
        ss3.add(s5);
        
        Set<Student> ss4 = new HashSet<Student>();
        
        ss4.add(s0);
        ss4.add(s1);
        ss4.add(s6);
        ss4.add(s8);
        ss4.add(s9);
        
        persistCollection(ss0, ss1, ss2, ss3, ss4);
        
        HashSet<Course> courses = new HashSet<Course>();
        
        Course c0 = new Course(0, "Analisi 1", 2014);
        
        c0.setAttendees(ss0);
        
        Course c1 = new Course(1, "Analisi 2", 2014);
        c1.setAttendees(ss1);
        
        Course c2 = new Course(2, "Fisica 1", 2014);
        c2.setAttendees(ss2);
        
        Course c3 = new Course(3, "Fisica 2", 2014);

        c3.setAttendees(ss3);
        
        Course c4 = new Course(4, "Fondameni di Informatica 1", 2014);

        c4.setAttendees(ss4);
        
        entityManager.persist(c1);
        entityManager.persist(c2);
        entityManager.persist(c3);
        entityManager.persist(c4);
        
        entityManager.flush();
		entityManager.clear();
	}

	private void persistCollection(Collection<?>... collections) {
		for (Collection<?> collection : collections) {
			for (Object object : collection) {
				entityManager.persist(object);
			}
		}
		
	}
	
	public Set<Course> findAllCourses()
    {
		List<Course> courses = entityManager.createQuery( allCoursesCriteria ).getResultList();
        return new HashSet<Course>(courses);
    }
    
    public Course findCourseById(long id)
    {
    	return entityManager.find(Course.class, id);
    }
    
    public Set<Student> findStudensByCourseId(long id)
    {
    	Course course = entityManager.find(Course.class, id);
    	return course != null ? course.getAttendees() : Collections.<Student>emptySet();
    }
    
}
