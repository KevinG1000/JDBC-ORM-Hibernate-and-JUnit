package sba.sms.services;

import lombok.extern.java.Log;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import sba.sms.dao.StudentI;
import sba.sms.models.Course;
import sba.sms.models.Student;
import sba.sms.utils.HibernateUtil;

import java.util.List;
import java.util.logging.Logger;

/**
 * StudentService is a concrete class. This class implements the
 * StudentI interface, overrides all abstract service methods and
 * provides implementation for each method. Lombok @Log used to
 * generate a logger file.
 */
@Log
public class StudentService implements StudentI {
    private static final Logger LOGGER = Logger.getLogger(StudentService.class.getName());

    @Override
    public void createStudent(Student student) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(student);
            transaction.commit();
            LOGGER.info("Student created: " + student.getEmail());
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            LOGGER.severe("Creation failed for student: " + student.getEmail() + ", error: " + e.getMessage());
        }
    }

    @Override
    public Student getStudentByEmail(String email) {
        if (!isValidEmail(email)) {
            LOGGER.warning("Invalid email format for: " + email);
            return null;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Student> query = session.createQuery("from Student where email = :email", Student.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        } catch (HibernateException e) {
            LOGGER.severe("Failed to retrieve student with email " + email + ": " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Student> getAllStudents() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Student", Student.class).list();
        } catch (HibernateException e) {
            LOGGER.severe("Failed to retrieve all students: " + e.getMessage());
            return null;
        }
    }

    /**
     * Validates that the student's email is not null and contains '@'.
     *
     * @param email the email to validate
     * @return true if the email is valid, false otherwise
     */
    public boolean isValidEmail(String email) {
        boolean valid = email != null && email.contains("@");
        LOGGER.info("Validating email: " + email + " - Valid: " + valid);
        return valid;
    }
}
