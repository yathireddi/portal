package com.scholarconnect.service;

import com.scholarconnect.entity.*;
import com.scholarconnect.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final StudentProfileRepository profileRepository;
    private final ScholarshipRepository scholarshipRepository;
    private final ApplicationRepository applicationRepository;
    private final DocumentRepository documentRepository;
    private final ApplicationStatusHistoryRepository historyRepository;
    private final ReviewRepository reviewRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           StudentProfileRepository profileRepository,
                           ScholarshipRepository scholarshipRepository,
                           ApplicationRepository applicationRepository,
                           DocumentRepository documentRepository,
                           ApplicationStatusHistoryRepository historyRepository,
                           ReviewRepository reviewRepository,
                           NotificationRepository notificationRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.scholarshipRepository = scholarshipRepository;
        this.applicationRepository = applicationRepository;
        this.documentRepository = documentRepository;
        this.historyRepository = historyRepository;
        this.reviewRepository = reviewRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("Checking and initializing ScholarConnect seed data...");

        // 1. Initialize or refresh users with accurate BCrypt passwords
        User admin = initUser("admin@scholarconnect.gov.in", "System Administrator", "9876543210",
                "Admin@123", Role.ROLE_ADMIN, LocalDate.of(1985, 5, 15), "Male");

        User rev1 = initUser("reviewer1@scholarconnect.gov.in", "Dr. Arvinder Singh", "9876543211",
                "Reviewer@123", Role.ROLE_REVIEWER, LocalDate.of(1988, 8, 20), "Male");

        User rev2 = initUser("reviewer2@scholarconnect.gov.in", "Prof. Meenakshi Sundaram", "9876543212",
                "Reviewer@123", Role.ROLE_REVIEWER, LocalDate.of(1990, 11, 12), "Female");

        User s1 = initUser("rahul.sharma@example.com", "Rahul Sharma", "9876543220",
                "Student@123", Role.ROLE_STUDENT, LocalDate.of(2004, 3, 14), "Male");

        User s2 = initUser("priya.patel@example.com", "Priya Patel", "9876543221",
                "Student@123", Role.ROLE_STUDENT, LocalDate.of(2003, 7, 22), "Female");

        User s3 = initUser("ananya.verma@example.com", "Ananya Verma", "9876543222",
                "Student@123", Role.ROLE_STUDENT, LocalDate.of(2004, 10, 5), "Female");

        User s4 = initUser("vikram.singh@example.com", "Vikram Singh", "9876543223",
                "Student@123", Role.ROLE_STUDENT, LocalDate.of(2003, 1, 18), "Male");

        User s5 = initUser("sneha.reddy@example.com", "Sneha Reddy", "9876543224",
                "Student@123", Role.ROLE_STUDENT, LocalDate.of(2004, 6, 30), "Female");

        // 2. Initialize student profiles
        initProfile(s1, "General", "Maharashtra", "Pune", "College of Engineering Pune (COEP)",
                "Savitribai Phule Pune University", "B.Tech Computer Engineering", 3, new BigDecimal("85.50"),
                new BigDecimal("420000.00"), "Flat 302, Green Avenue, Shivajinagar, Pune - 411005");

        initProfile(s2, "OBC", "Gujarat", "Ahmedabad", "L.D. College of Engineering",
                "Gujarat Technological University", "B.Tech Information Technology", 3, new BigDecimal("79.20"),
                new BigDecimal("240000.00"), "12, Shanti Kunj, Navrangpura, Ahmedabad - 380009");

        initProfile(s3, "SC", "Delhi", "New Delhi", "Delhi Technological University",
                "DTU State University", "B.Tech Electronics & Comm", 2, new BigDecimal("88.00"),
                new BigDecimal("180000.00"), "House 45, Sector 9, Rohini, New Delhi - 110085");

        initProfile(s4, "ST", "Rajasthan", "Jaipur", "Malaviya National Institute of Technology",
                "MNIT Jaipur", "B.Tech Mechanical Engineering", 3, new BigDecimal("62.40"),
                new BigDecimal("290000.00"), "Village Sanganer, Post Road, Jaipur - 302029");

        initProfile(s5, "EWS", "Telangana", "Hyderabad", "JNTU College of Engineering",
                "Jawaharlal Nehru Tech University", "B.Tech Data Science", 3, new BigDecimal("82.10"),
                new BigDecimal("210000.00"), "Plot 88, KPHB Colony, Kukatpally, Hyderabad - 500072");

        // 3. Initialize Scholarships if missing
        if (scholarshipRepository.count() == 0) {
            initScholarships();
        }

        // 4. Initialize Demo Applications if missing
        if (applicationRepository.count() == 0) {
            initApplications(s1, s2, s3, s4, s5, rev1, rev2, admin);
        }

        logger.info("ScholarConnect seed data ready.");
    }

    private User initUser(String email, String fullName, String mobile, String rawPassword,
                          Role role, LocalDate dob, String gender) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            // Refresh password hash to ensure exact password encoder compatibility
            user.setPassword(passwordEncoder.encode(rawPassword));
        } else {
            user = new User();
            user.setEmail(email);
            user.setFullName(fullName);
            user.setMobileNumber(mobile);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setRole(role);
            user.setDateOfBirth(dob);
            user.setGender(gender);
            user.setEnabled(true);
        }
        return userRepository.save(user);
    }

    private void initProfile(User user, String category, String state, String district,
                             String college, String university, String course, int year,
                             BigDecimal percentage, BigDecimal income, String address) {
        Optional<StudentProfile> opt = profileRepository.findByUserId(user.getId());
        StudentProfile p = opt.orElseGet(StudentProfile::new);
        p.setUser(user);
        p.setCategory(category);
        p.setState(state);
        p.setDistrict(district);
        p.setCollegeName(college);
        p.setUniversityName(university);
        p.setCourse(course);
        p.setCurrentYear(year);
        p.setAcademicPercentage(percentage);
        p.setAnnualFamilyIncome(income);
        p.setAddress(address);
        profileRepository.save(p);
    }

    private void initScholarships() {
        createScholarship("National Merit Scholarship Scheme 2026", "Ministry of Education, Govt. of India",
                "Awarded to exceptionally talented undergraduate students across technical and conventional degrees to pursue higher education without financial hindrance.",
                new BigDecimal("50000.00"), new BigDecimal("80.00"), new BigDecimal("600000.00"),
                "ALL", "ALL", "B.Tech,B.Sc,B.Com,B.A,MBBS", "Undergraduate",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));

        createScholarship("Post-Matric Financial Assistance for SC/ST", "Ministry of Social Justice and Empowerment",
                "Comprehensive tuition and maintenance grant designed to support Scheduled Caste and Scheduled Tribe students pursuing recognized university programs.",
                new BigDecimal("75000.00"), new BigDecimal("50.00"), new BigDecimal("250000.00"),
                "SC,ST", "ALL", "ALL", "Undergraduate",
                LocalDate.of(2026, 1, 15), LocalDate.of(2026, 12, 31));

        createScholarship("AICTE Pragati Scholarship for Girl Students", "All India Council for Technical Education",
                "Scheme providing empowerment and tuition aid exclusively for meritorious girl scholars admitted to AICTE-approved technical undergraduate degree programs.",
                new BigDecimal("50000.00"), new BigDecimal("60.00"), new BigDecimal("800000.00"),
                "ALL", "ALL", "B.Tech,B.E,B.Arch,B.Pharm", "Undergraduate",
                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 11, 30));

        createScholarship("Ishan Uday Special Scholarship for North Eastern Region", "University Grants Commission (UGC)",
                "Special scheme to foster higher education access and improve Gross Enrolment Ratio among candidates domiciled in the North Eastern States.",
                new BigDecimal("78000.00"), new BigDecimal("60.00"), new BigDecimal("450000.00"),
                "ALL", "Assam,Meghalaya,Manipur,Mizoram,Nagaland,Tripura,Arunachal Pradesh,Sikkim", "ALL", "Undergraduate",
                LocalDate.of(2026, 1, 10), LocalDate.of(2026, 12, 15));

        createScholarship("Top Class Education Scheme for PwD Candidates", "Department of Empowerment of Persons with Disabilities",
                "Special fellowship ensuring full financial coverage including books, equipment allowance, and course fees for students with benchmark physical disabilities.",
                new BigDecimal("120000.00"), new BigDecimal("50.00"), new BigDecimal("600000.00"),
                "ALL", "ALL", "ALL", "ALL",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));

        createScholarship("Begum Hazrat Mahal National Minority Scholarship", "Maulana Azad Education Foundation",
                "Direct benefit assistance for meritorious female students belonging to notified national minority communities (Muslim, Christian, Sikh, Buddhist, Jain, Parsi).",
                new BigDecimal("30000.00"), new BigDecimal("55.00"), new BigDecimal("200000.00"),
                "Minority,Muslim,Christian,Sikh,Buddhist,Jain,Parsi", "ALL", "ALL", "Undergraduate",
                LocalDate.of(2026, 2, 15), LocalDate.of(2026, 12, 20));

        createScholarship("Reliance Foundation Undergraduate Fellowship", "Reliance Foundation Educational Trust",
                "Flagship merit-cum-means endowment recognizing outstanding future innovators and engineering leaders with broad leadership mentoring and grant funding.",
                new BigDecimal("100000.00"), new BigDecimal("75.00"), new BigDecimal("500000.00"),
                "ALL", "ALL", "B.Tech,B.E,B.Sc,BCA", "Undergraduate",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));

        createScholarship("Prime Minister Armed Forces Wards Welfare Fund", "Welfare and Rehabilitation Board (WARB)",
                "Scholarship dedicated to wards and widows of Central Armed Police Forces & Assam Rifles personnel martyred or disabled in the line of duty.",
                new BigDecimal("36000.00"), new BigDecimal("60.00"), new BigDecimal("1000000.00"),
                "ALL", "ALL", "B.Tech,MBBS,B.Pharm,BBA,B.Com", "Undergraduate",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));

        createScholarship("INSPIRE Innovation in Science Pursuit Fellowship", "Department of Science & Technology (DST)",
                "Premier scholarship targeting students in pure, foundational, and applied mathematical sciences to incentivize long-term research careers.",
                new BigDecimal("80000.00"), new BigDecimal("85.00"), new BigDecimal("800000.00"),
                "ALL", "ALL", "B.Sc,Int. M.Sc,BS-MS,B.Tech", "Undergraduate",
                LocalDate.of(2026, 1, 5), LocalDate.of(2026, 12, 31));

        createScholarship("State Post-Graduate Academic Excellence Award", "State Higher Education Council",
                "State-sponsored stipend awarded to high-performing postgraduate masters scholars pursuing sciences, engineering, and social policy.",
                new BigDecimal("60000.00"), new BigDecimal("65.00"), new BigDecimal("500000.00"),
                "ALL", "ALL", "M.Tech,M.Sc,MCA,MBA,M.Com", "Postgraduate",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));
    }

    private void createScholarship(String name, String provider, String desc, BigDecimal amount,
                                   BigDecimal minPct, BigDecimal maxInc, String cats, String states,
                                   String courses, String level, LocalDate start, LocalDate deadline) {
        Scholarship s = new Scholarship();
        s.setScholarshipName(name);
        s.setProvider(provider);
        s.setDescription(desc);
        s.setAmount(amount);
        s.setMinimumPercentage(minPct);
        s.setMaximumFamilyIncome(maxInc);
        s.setEligibleCategories(cats);
        s.setEligibleStates(states);
        s.setEligibleCourses(courses);
        s.setEducationLevel(level);
        s.setApplicationStartDate(start);
        s.setApplicationDeadline(deadline);
        s.setStatus("ACTIVE");
        scholarshipRepository.save(s);
    }

    private void initApplications(User s1, User s2, User s3, User s4, User s5,
                                  User rev1, User rev2, User admin) {
        List<Scholarship> list = scholarshipRepository.findAll();
        if (list.size() < 7) return;

        // 1. S1 -> Sch 1 (SUBMITTED)
        Application a1 = new Application("SC-2026-00101", s1, list.get(0), ApplicationStatus.SUBMITTED, "Application successfully submitted.");
        applicationRepository.save(a1);
        historyRepository.save(new ApplicationStatusHistory(a1, null, "SUBMITTED", s1.getEmail(), "Submitted by student."));
        initDocument(a1, "AADHAAR", "Aadhaar_Rahul.pdf", "demo_doc_1.pdf", "PENDING");
        initDocument(a1, "MARKSHEET", "Marksheet_Rahul.pdf", "demo_doc_2.pdf", "PENDING");

        // 2. S2 -> Sch 3 (UNDER_REVIEW)
        Application a2 = new Application("SC-2026-00102", s2, list.get(2), ApplicationStatus.UNDER_REVIEW, "Assigned to reviewer Dr. Arvinder Singh.");
        a2.setReviewer(rev1);
        applicationRepository.save(a2);
        historyRepository.save(new ApplicationStatusHistory(a2, null, "SUBMITTED", s2.getEmail(), "Submitted by student."));
        historyRepository.save(new ApplicationStatusHistory(a2, "SUBMITTED", "UNDER_REVIEW", admin.getEmail(), "Assigned to Dr. Arvinder Singh."));
        initDocument(a2, "AADHAAR", "Aadhaar_Priya.pdf", "demo_doc_3.pdf", "VERIFIED");
        initDocument(a2, "INCOME_CERTIFICATE", "Income_Priya.pdf", "demo_doc_4.pdf", "PENDING");
        reviewRepository.save(new Review(a2, rev1, "Candidate has strong academic performance (79.2%). Aadhaar verified.", "RECOMMEND_APPROVAL"));

        // 3. S3 -> Sch 2 (APPROVED)
        Application a3 = new Application("SC-2026-00103", s3, list.get(1), ApplicationStatus.APPROVED, "All criteria satisfied. Approved for sanction.");
        a3.setReviewer(rev1);
        applicationRepository.save(a3);
        historyRepository.save(new ApplicationStatusHistory(a3, null, "SUBMITTED", s3.getEmail(), "Submitted by student."));
        historyRepository.save(new ApplicationStatusHistory(a3, "SUBMITTED", "UNDER_REVIEW", admin.getEmail(), "Assigned to reviewer."));
        historyRepository.save(new ApplicationStatusHistory(a3, "UNDER_REVIEW", "APPROVED", admin.getEmail(), "Final sanction approved."));
        initDocument(a3, "CASTE_CERTIFICATE", "Caste_Ananya.pdf", "demo_doc_5.pdf", "VERIFIED");
        initDocument(a3, "BONAFIDE", "Bonafide_Ananya.pdf", "demo_doc_6.pdf", "VERIFIED");
        reviewRepository.save(new Review(a3, rev1, "SC certificate verified. Recommend sanction.", "RECOMMEND_APPROVAL"));

        // 4. S4 -> Sch 2 (REJECTED)
        Application a4 = new Application("SC-2026-00104", s4, list.get(1), ApplicationStatus.REJECTED, "Annual family income exceeded threshold ceiling.");
        a4.setReviewer(rev2);
        applicationRepository.save(a4);
        historyRepository.save(new ApplicationStatusHistory(a4, null, "SUBMITTED", s4.getEmail(), "Submitted by student."));
        historyRepository.save(new ApplicationStatusHistory(a4, "SUBMITTED", "UNDER_REVIEW", admin.getEmail(), "Assigned to reviewer."));
        historyRepository.save(new ApplicationStatusHistory(a4, "UNDER_REVIEW", "REJECTED", admin.getEmail(), "Income exceeds ceiling threshold."));
        initDocument(a4, "INCOME_CERTIFICATE", "Income_Vikram.pdf", "demo_doc_7.pdf", "REJECTED");
        reviewRepository.save(new Review(a4, rev2, "Income document shows ₹2,90,000, higher than ceiling ₹2,50,000.", "RECOMMEND_REJECTION"));

        // 5. S5 -> Sch 7 (ADDITIONAL_INFORMATION_REQUIRED)
        Application a5 = new Application("SC-2026-00105", s5, list.get(6), ApplicationStatus.ADDITIONAL_INFORMATION_REQUIRED, "Please re-upload a clear gazetted Bonafide Certificate with college seal.");
        a5.setReviewer(rev2);
        applicationRepository.save(a5);
        historyRepository.save(new ApplicationStatusHistory(a5, null, "SUBMITTED", s5.getEmail(), "Submitted by student."));
        historyRepository.save(new ApplicationStatusHistory(a5, "SUBMITTED", "UNDER_REVIEW", admin.getEmail(), "Assigned to reviewer."));
        historyRepository.save(new ApplicationStatusHistory(a5, "UNDER_REVIEW", "ADDITIONAL_INFORMATION_REQUIRED", rev2.getEmail(), "Re-upload bonafide certificate with college seal."));
        initDocument(a5, "BONAFIDE", "Bonafide_Sneha.pdf", "demo_doc_8.pdf", "REJECTED");
        reviewRepository.save(new Review(a5, rev2, "Bonafide has missing seal. Requesting student re-upload.", "REQUEST_MORE_INFO"));

        // Notifications
        notificationRepository.save(new Notification(s1, "Application Received", "Your application (Ref: SC-2026-00101) has been received."));
        notificationRepository.save(new Notification(s2, "Under Review", "Your application (Ref: SC-2026-00102) is under review."));
        notificationRepository.save(new Notification(s3, "Scholarship Sanctioned!", "Your application (Ref: SC-2026-00103) has been approved for ₹75,000."));
        notificationRepository.save(new Notification(s4, "Application Decision", "Application (Ref: SC-2026-00104) was rejected. Check tracking for details."));
        notificationRepository.save(new Notification(s5, "Action Required", "Please re-upload college bonafide certificate with official seal for SC-2026-00105."));
    }

    private void initDocument(Application app, String type, String originalName, String storedName, String status) {
        Document d = new Document();
        d.setApplication(app);
        d.setDocumentType(type);
        d.setOriginalFilename(originalName);
        d.setStoredFilename(storedName);
        d.setFilePath("uploads/documents/" + storedName);
        d.setContentType("application/pdf");
        d.setFileSize(317L);
        d.setVerificationStatus(status);
        documentRepository.save(d);
    }
}
