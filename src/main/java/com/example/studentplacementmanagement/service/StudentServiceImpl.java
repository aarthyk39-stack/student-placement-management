package com.example.studentplacementmanagement.service;

import com.example.studentplacementmanagement.dto.request.StudentRequestDTO;
import com.example.studentplacementmanagement.dto.response.StudentResponseDTO;
import com.example.studentplacementmanagement.entity.Student;
import com.example.studentplacementmanagement.entity.User;
import com.example.studentplacementmanagement.enums.Role;
import com.example.studentplacementmanagement.exception.DuplicateResourceException;
import com.example.studentplacementmanagement.exception.ResourceNotFoundException;
import com.example.studentplacementmanagement.mapper.StudentMapper;
import com.example.studentplacementmanagement.repository.StudentRepository;
import com.example.studentplacementmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    private final UserRepository userRepository;

    private final StudentMapper studentMapper;



    @Override
    public StudentResponseDTO saveStudent(
            StudentRequestDTO requestDTO) {

        Student student;


        if (requestDTO.getId() == null) {

            log.info(
                    "Creating student with roll number: {}",
                    requestDTO.getRollNumber()
            );

            if (studentRepository.existsByRollNumber(
                    requestDTO.getRollNumber())) {

                throw new DuplicateResourceException(
                        "Roll number already exists: "
                                + requestDTO.getRollNumber()
                );
            }

            if (studentRepository.existsByUserId(
                    requestDTO.getUserId())) {

                throw new DuplicateResourceException(
                        "Student profile already exists for user ID: "
                                + requestDTO.getUserId()
                );
            }

            User user = getStudentUser(
                    requestDTO.getUserId()
            );


            student = studentMapper.toEntity(requestDTO);

            student.setUser(user);

            student.setIsPlaced(false);
        }


        else {

            log.info(
                    "Updating student with ID: {}",
                    requestDTO.getId()
            );

            student = studentRepository.findById(
                    requestDTO.getId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Student not found with ID: "
                                    + requestDTO.getId()
                    )
            );

            studentRepository.findByRollNumber(
                    requestDTO.getRollNumber()
            ).ifPresent(existingStudent -> {

                if (!existingStudent.getId()
                        .equals(requestDTO.getId())) {

                    throw new DuplicateResourceException(
                            "Roll number already exists: "
                                    + requestDTO.getRollNumber()
                    );
                }
            });


            studentRepository.findByUserId(
                    requestDTO.getUserId()
            ).ifPresent(existingStudent -> {

                if (!existingStudent.getId()
                        .equals(requestDTO.getId())) {

                    throw new DuplicateResourceException(
                            "User is already linked to another student"
                    );
                }
            });


            User user = getStudentUser(
                    requestDTO.getUserId()
            );

            student.setUser(user);

            student.setRollNumber(
                    requestDTO.getRollNumber()
            );

            student.setName(
                    requestDTO.getName()
            );

            student.setPhone(
                    requestDTO.getPhone()
            );

            student.setDepartment(
                    requestDTO.getDepartment()
            );

            student.setDegree(
                    requestDTO.getDegree()
            );

            student.setGraduationYear(
                    requestDTO.getGraduationYear()
            );

            student.setCgpa(
                    requestDTO.getCgpa()
            );

            student.setBacklogs(
                    requestDTO.getBacklogs()
            );


        }

        Student savedStudent =
                studentRepository.save(student);

        log.info(
                "Student saved successfully with ID: {}",
                savedStudent.getId()
        );

        return studentMapper.toResponseDTO(
                savedStudent
        );
    }




    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentById(Long id) {

        log.info(
                "Fetching student with ID: {}",
                id
        );

        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with ID: " + id
                        )
                );

        return studentMapper.toResponseDTO(student);
    }



    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> getAllStudents(
            Pageable pageable) {

        log.info("Fetching all students");

        return studentRepository
                .findAll(pageable)
                .map(studentMapper::toResponseDTO);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> searchStudents(
            String keyword,
            Pageable pageable) {

        log.info(
                "Searching students with keyword: {}",
                keyword
        );

        return studentRepository
                .findByNameContainingIgnoreCaseOrRollNumberContainingIgnoreCase(
                        keyword,
                        keyword,
                        pageable
                )
                .map(studentMapper::toResponseDTO);
    }



    @Override
    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getStudentsByDepartment(
            String department) {

        log.info(
                "Fetching students from department: {}",
                department
        );

        return studentRepository
                .findByDepartmentIgnoreCase(department)
                .stream()
                .map(studentMapper::toResponseDTO)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getEligibleStudents(
            BigDecimal minimumCgpa,
            Integer maximumBacklogs) {

        log.info(
                "Finding eligible students. Minimum CGPA: {}, Maximum Backlogs: {}",
                minimumCgpa,
                maximumBacklogs
        );

        return studentRepository
                .findEligibleStudents(
                        minimumCgpa,
                        maximumBacklogs
                )
                .stream()
                .map(studentMapper::toResponseDTO)
                .toList();
    }



    @Override
    @Transactional(readOnly = true)
    public long getPlacedStudentCount() {

        return studentRepository
                .countByIsPlacedTrue();
    }


    @Override
    @Transactional(readOnly = true)
    public long getUnplacedStudentCount() {

        return studentRepository
                .countByIsPlacedFalse();
    }


    @Override
    public void deleteStudent(Long id) {

        log.info(
                "Deleting student with ID: {}",
                id
        );

        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with ID: " + id
                        )
                );

        studentRepository.delete(student);

        log.info(
                "Student deleted successfully with ID: {}",
                id
        );
    }


    private User getStudentUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + userId
                        )
                );

        if (user.getRole() != Role.STUDENT) {

            throw new IllegalArgumentException(
                    "User must have STUDENT role"
            );
        }

        if (!Boolean.TRUE.equals(
                user.getIsActive())) {

            throw new IllegalArgumentException(
                    "User account is inactive"
            );
        }

        return user;
    }
}