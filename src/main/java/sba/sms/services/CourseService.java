package sba.sms.services;

import org.hibernate.HibernateError;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import sba.sms.dao.CourseI;
import sba.sms.models.Course;
import sba.sms.utils.HibernateUtil;

import java.util.List;
import java.util.logging.Logger;

/**
 * CourseService is a concrete class. This class implements the
 * CourseI interface, overrides all abstract service methods and
 * provides implementation for each method.
 */
public class CourseService implements CourseI {
    private static final Logger LOGGER = Logger.getLogger(CourseService.class.getName());

    /**
     * Retrieves all courses from the database.
     * @return a list of courses or an empty list if none found.
     */
    @Override
    public List<Course> getAllCourses() {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            List<Course> courses = session.createQuery("from Course", Course.class).list();
            transaction.commit();
            LOGGER.info("Retrieved all courses successfully.");
            return courses;
        } catch (HibernateError e) {
            if (transaction != null) transaction.rollback();
            LOGGER.severe("Failed to retrieve all courses: " + e.getMessage());
            return null; // Consider returning an empty list instead of null for better error handling.
        }
    }

    /**
     * Retrieves a specific course by its ID.
     * @param id the ID of the course to retrieve
     * @return the found course, or null if no course is found.
     */
    @Override
    public Course getCourseById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Course course = session.get(Course.class, id);
            if (course != null) {
                LOGGER.info("Found course: " + course.getName());
            } else {
                LOGGER.warning("No course found with ID: " + id);
            }
            return course;
        } catch (HibernateException e) {
            LOGGER.severe("Error retrieving course with ID " + id + ": " + e.getMessage());
            return null;
        }
    }
}
