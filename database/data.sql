-- ScholarConnect - Seed Data
-- Database: scholarship_portal

USE scholarship_portal;

-- 1. USERS SEED
-- Passwords:
-- Admin: Admin@123
-- Reviewer: Reviewer@123
-- Student: Student@123
-- (Spring Boot DataInitializer will also verify/encode these automatically on first startup)

INSERT INTO users (id, full_name, email, mobile_number, password, date_of_birth, gender, role, enabled, created_at)
VALUES
(1, 'System Administrator', 'admin@scholarconnect.gov.in', '9876543210', '$2a$10$e88yR4b8NcmYvYq6gZ9.pe0m/K7g75gK/C4n1a37c9uXb8n1e5V5a', '1985-05-15', 'Male', 'ROLE_ADMIN', 1, NOW()),
(2, 'Dr. Arvinder Singh', 'reviewer1@scholarconnect.gov.in', '9876543211', '$2a$10$e88yR4b8NcmYvYq6gZ9.pe0m/K7g75gK/C4n1a37c9uXb8n1e5V5a', '1988-08-20', 'Male', 'ROLE_REVIEWER', 1, NOW()),
(3, 'Prof. Meenakshi Sundaram', 'reviewer2@scholarconnect.gov.in', '9876543212', '$2a$10$e88yR4b8NcmYvYq6gZ9.pe0m/K7g75gK/C4n1a37c9uXb8n1e5V5a', '1990-11-12', 'Female', 'ROLE_REVIEWER', 1, NOW()),
(4, 'Rahul Sharma', 'rahul.sharma@example.com', '9876543220', '$2a$10$e88yR4b8NcmYvYq6gZ9.pe0m/K7g75gK/C4n1a37c9uXb8n1e5V5a', '2004-03-14', 'Male', 'ROLE_STUDENT', 1, NOW()),
(5, 'Priya Patel', 'priya.patel@example.com', '9876543221', '$2a$10$e88yR4b8NcmYvYq6gZ9.pe0m/K7g75gK/C4n1a37c9uXb8n1e5V5a', '2003-07-22', 'Female', 'ROLE_STUDENT', 1, NOW()),
(6, 'Ananya Verma', 'ananya.verma@example.com', '9876543222', '$2a$10$e88yR4b8NcmYvYq6gZ9.pe0m/K7g75gK/C4n1a37c9uXb8n1e5V5a', '2004-10-05', 'Female', 'ROLE_STUDENT', 1, NOW()),
(7, 'Vikram Singh', 'vikram.singh@example.com', '9876543223', '$2a$10$e88yR4b8NcmYvYq6gZ9.pe0m/K7g75gK/C4n1a37c9uXb8n1e5V5a', '2003-01-18', 'Male', 'ROLE_STUDENT', 1, NOW()),
(8, 'Sneha Reddy', 'sneha.reddy@example.com', '9876543224', '$2a$10$e88yR4b8NcmYvYq6gZ9.pe0m/K7g75gK/C4n1a37c9uXb8n1e5V5a', '2004-06-30', 'Female', 'ROLE_STUDENT', 1, NOW())
ON DUPLICATE KEY UPDATE email=email;

-- 2. STUDENT PROFILES SEED
INSERT INTO student_profiles (user_id, category, state, district, college_name, university_name, course, current_year, academic_percentage, annual_family_income, address)
VALUES
(4, 'General', 'Maharashtra', 'Pune', 'College of Engineering Pune (COEP)', 'Savitribai Phule Pune University', 'B.Tech Computer Engineering', 3, 85.50, 420000.00, 'Flat 302, Green Avenue, Shivajinagar, Pune, Maharashtra - 411005'),
(5, 'OBC', 'Gujarat', 'Ahmedabad', 'L.D. College of Engineering', 'Gujarat Technological University', 'B.Tech Information Technology', 3, 79.20, 240000.00, '12, Shanti Kunj, Navrangpura, Ahmedabad, Gujarat - 380009'),
(6, 'SC', 'Delhi', 'New Delhi', 'Delhi Technological University', 'DTU State University', 'B.Tech Electronics & Comm', 2, 88.00, 180000.00, 'House 45, Sector 9, Rohini, New Delhi - 110085'),
(7, 'ST', 'Rajasthan', 'Jaipur', 'Malaviya National Institute of Technology', 'MNIT Jaipur', 'B.Tech Mechanical Engineering', 3, 62.40, 290000.00, 'Village Sanganer, Post Road, Jaipur, Rajasthan - 302029'),
(8, 'EWS', 'Telangana', 'Hyderabad', 'JNTU College of Engineering', 'Jawaharlal Nehru Tech University', 'B.Tech Data Science', 3, 82.10, 210000.00, 'Plot 88, KPHB Colony, Kukatpally, Hyderabad, Telangana - 500072')
ON DUPLICATE KEY UPDATE category=VALUES(category);

-- 3. SCHOLARSHIPS SEED (10 Realistic demo records)
INSERT INTO scholarships (id, scholarship_name, provider, description, amount, minimum_percentage, maximum_family_income, eligible_categories, eligible_states, eligible_courses, education_level, application_start_date, application_deadline, status, created_at)
VALUES
(1, 'National Merit Scholarship Scheme 2026', 'Ministry of Education, Govt. of India', 'Awarded to exceptionally talented undergraduate students across technical and conventional degrees to pursue higher education without financial hindrance.', 50000.00, 80.00, 600000.00, 'ALL', 'ALL', 'B.Tech,B.Sc,B.Com,B.A,MBBS', 'Undergraduate', '2026-01-01', '2026-12-31', 'ACTIVE', NOW()),

(2, 'Post-Matric Financial Assistance for SC/ST', 'Ministry of Social Justice and Empowerment', 'Comprehensive tuition and maintenance grant designed to support Scheduled Caste and Scheduled Tribe students pursuing recognized university programs.', 75000.00, 50.00, 250000.00, 'SC,ST', 'ALL', 'ALL', 'Undergraduate', '2026-01-15', '2026-12-31', 'ACTIVE', NOW()),

(3, 'AICTE Pragati Scholarship for Girl Students', 'All India Council for Technical Education', 'Scheme providing empowerment and tuition aid exclusively for meritorious girl scholars admitted to AICTE-approved technical undergraduate degree programs.', 50000.00, 60.00, 800000.00, 'ALL', 'ALL', 'B.Tech,B.E,B.Arch,B.Pharm', 'Undergraduate', '2026-02-01', '2026-11-30', 'ACTIVE', NOW()),

(4, 'Ishan Uday Special Scholarship for North Eastern Region', 'University Grants Commission (UGC)', 'Special scheme to foster higher education access and improve Gross Enrolment Ratio among candidates domiciled in the North Eastern States.', 78000.00, 60.00, 450000.00, 'ALL', 'Assam,Meghalaya,Manipur,Mizoram,Nagaland,Tripura,Arunachal Pradesh,Sikkim', 'ALL', 'Undergraduate', '2026-01-10', '2026-12-15', 'ACTIVE', NOW()),

(5, 'Top Class Education Scheme for PwD Candidates', 'Department of Empowerment of Persons with Disabilities', 'Special fellowship ensuring full financial coverage including books, equipment allowance, and course fees for students with benchmark physical disabilities.', 120000.00, 50.00, 600000.00, 'ALL', 'ALL', 'ALL', 'ALL', '2026-01-01', '2026-12-31', 'ACTIVE', NOW()),

(6, 'Begum Hazrat Mahal National Minority Scholarship', 'Maulana Azad Education Foundation', 'Direct benefit assistance for meritorious female students belonging to notified national minority communities (Muslim, Christian, Sikh, Buddhist, Jain, Parsi).', 30000.00, 55.00, 200000.00, 'Minority,Muslim,Christian,Sikh,Buddhist,Jain,Parsi', 'ALL', 'ALL', 'Undergraduate', '2026-02-15', '2026-12-20', 'ACTIVE', NOW()),

(7, 'Reliance Foundation Undergraduate Fellowship', 'Reliance Foundation Educational Trust', 'Flagship merit-cum-means endowment recognizing outstanding future innovators and engineering leaders with broad leadership mentoring and grant funding.', 100000.00, 75.00, 500000.00, 'ALL', 'ALL', 'B.Tech,B.E,B.Sc,BCA', 'Undergraduate', '2026-01-01', '2026-12-31', 'ACTIVE', NOW()),

(8, 'Prime Minister Armed Forces Wards Welfare Fund', 'Welfare and Rehabilitation Board (WARB)', 'Scholarship dedicated to wards and widows of Central Armed Police Forces & Assam Rifles personnel martyred or disabled in the line of duty.', 36000.00, 60.00, 1000000.00, 'ALL', 'ALL', 'B.Tech,MBBS,B.Pharm,BBA,B.Com', 'Undergraduate', '2026-01-01', '2026-12-31', 'ACTIVE', NOW()),

(9, 'INSPIRE Innovation in Science Pursuit Fellowship', 'Department of Science & Technology (DST)', 'Premier scholarship targeting students in pure, foundational, and applied mathematical sciences to incentivize long-term research careers.', 80000.00, 85.00, 800000.00, 'ALL', 'ALL', 'B.Sc,Int. M.Sc,BS-MS,B.Tech', 'Undergraduate', '2026-01-05', '2026-12-31', 'ACTIVE', NOW()),

(10, 'State Post-Graduate Academic Excellence Award', 'State Higher Education Council', 'State-sponsored stipend awarded to high-performing postgraduate masters scholars pursuing sciences, engineering, and social policy.', 60000.00, 65.00, 500000.00, 'ALL', 'ALL', 'M.Tech,M.Sc,MCA,MBA,M.Com', 'Postgraduate', '2026-01-01', '2026-12-31', 'ACTIVE', NOW())
ON DUPLICATE KEY UPDATE scholarship_name=VALUES(scholarship_name);

-- 4. APPLICATIONS SEED
INSERT INTO applications (id, application_reference, student_id, scholarship_id, application_date, status, reviewer_id, remarks, created_at, updated_at)
VALUES
(1, 'SC-2026-00101', 4, 1, '2026-02-10 10:30:00', 'SUBMITTED', NULL, 'Application successfully submitted and awaiting administrative reviewer assignment.', '2026-02-10 10:30:00', '2026-02-10 10:30:00'),
(2, 'SC-2026-00102', 5, 3, '2026-02-12 14:15:00', 'UNDER_REVIEW', 2, 'Assigned to reviewer Dr. Arvinder Singh. Academic marksheets under scrutiny.', '2026-02-12 14:15:00', '2026-02-14 11:20:00'),
(3, 'SC-2026-00103', 6, 2, '2026-01-20 09:45:00', 'APPROVED', 2, 'All criteria satisfied. Valid caste certificate and bonafide verified. Final scholarship sanctioned.', '2026-01-20 09:45:00', '2026-01-28 16:30:00'),
(4, 'SC-2026-00104', 7, 2, '2026-01-22 16:00:00', 'REJECTED', 3, 'Annual family income exceeded the declared threshold ceiling of Rs 2,50,000 as per submitted Tahsildar records.', '2026-01-22 16:00:00', '2026-01-29 12:10:00'),
(5, 'SC-2026-00105', 8, 7, '2026-02-05 11:10:00', 'ADDITIONAL_INFORMATION_REQUIRED', 3, 'Please re-upload a clear gazetted Bonafide Certificate with college stamp and seal.', '2026-02-05 11:10:00', '2026-02-15 15:45:00')
ON DUPLICATE KEY UPDATE application_reference=VALUES(application_reference);

-- 5. DOCUMENTS SEED
INSERT INTO documents (id, application_id, document_type, original_filename, stored_filename, file_path, content_type, file_size, uploaded_at, verification_status)
VALUES
(1, 1, 'AADHAAR', 'Aadhaar_Rahul_Sharma.pdf', 'demo_doc_1.pdf', 'uploads/documents/demo_doc_1.pdf', 'application/pdf', 245000, NOW(), 'PENDING'),
(2, 1, 'MARKSHEET', 'Semester4_Marksheet_Rahul.pdf', 'demo_doc_2.pdf', 'uploads/documents/demo_doc_2.pdf', 'application/pdf', 520000, NOW(), 'PENDING'),
(3, 2, 'AADHAAR', 'Priya_Patel_UIDAI.pdf', 'demo_doc_3.pdf', 'uploads/documents/demo_doc_3.pdf', 'application/pdf', 310000, NOW(), 'VERIFIED'),
(4, 2, 'INCOME_CERTIFICATE', 'Income_Tahsildar_2025.pdf', 'demo_doc_4.pdf', 'uploads/documents/demo_doc_4.pdf', 'application/pdf', 415000, NOW(), 'PENDING'),
(5, 3, 'CASTE_CERTIFICATE', 'SC_Caste_Certificate_Ananya.pdf', 'demo_doc_5.pdf', 'uploads/documents/demo_doc_5.pdf', 'application/pdf', 380000, NOW(), 'VERIFIED'),
(6, 3, 'BONAFIDE', 'DTU_Bonafide_Certificate.pdf', 'demo_doc_6.pdf', 'uploads/documents/demo_doc_6.pdf', 'application/pdf', 290000, NOW(), 'VERIFIED'),
(7, 4, 'INCOME_CERTIFICATE', 'Income_Certificate_Vikram.pdf', 'demo_doc_7.pdf', 'uploads/documents/demo_doc_7.pdf', 'application/pdf', 340000, NOW(), 'REJECTED'),
(8, 5, 'BONAFIDE', 'College_Bonafide_Sneha.pdf', 'demo_doc_8.pdf', 'uploads/documents/demo_doc_8.pdf', 'application/pdf', 180000, NOW(), 'REJECTED')
ON DUPLICATE KEY UPDATE document_type=VALUES(document_type);

-- 6. APPLICATION STATUS HISTORY SEED
INSERT INTO application_status_history (id, application_id, previous_status, new_status, changed_by, comments, changed_at)
VALUES
(1, 1, NULL, 'SUBMITTED', 'rahul.sharma@example.com', 'Application submitted by candidate.', '2026-02-10 10:30:00'),
(2, 2, NULL, 'SUBMITTED', 'priya.patel@example.com', 'Application submitted by candidate.', '2026-02-12 14:15:00'),
(3, 2, 'SUBMITTED', 'UNDER_REVIEW', 'admin@scholarconnect.gov.in', 'Assigned application to reviewer Dr. Arvinder Singh.', '2026-02-14 11:20:00'),
(4, 3, NULL, 'SUBMITTED', 'ananya.verma@example.com', 'Application submitted by candidate.', '2026-01-20 09:45:00'),
(5, 3, 'SUBMITTED', 'UNDER_REVIEW', 'admin@scholarconnect.gov.in', 'Assigned to reviewer Dr. Arvinder Singh.', '2026-01-22 10:00:00'),
(6, 3, 'UNDER_REVIEW', 'APPROVED', 'admin@scholarconnect.gov.in', 'All documents verified and approved. Merit criteria satisfied.', '2026-01-28 16:30:00'),
(7, 4, NULL, 'SUBMITTED', 'vikram.singh@example.com', 'Application submitted by candidate.', '2026-01-22 16:00:00'),
(8, 4, 'SUBMITTED', 'UNDER_REVIEW', 'admin@scholarconnect.gov.in', 'Assigned to reviewer Prof. Meenakshi Sundaram.', '2026-01-24 11:00:00'),
(9, 4, 'UNDER_REVIEW', 'REJECTED', 'admin@scholarconnect.gov.in', 'Annual income exceeds ceiling threshold.', '2026-01-29 12:10:00'),
(10, 5, NULL, 'SUBMITTED', 'sneha.reddy@example.com', 'Application submitted by candidate.', '2026-02-05 11:10:00'),
(11, 5, 'SUBMITTED', 'UNDER_REVIEW', 'admin@scholarconnect.gov.in', 'Assigned to reviewer Prof. Meenakshi Sundaram.', '2026-02-08 14:30:00'),
(12, 5, 'UNDER_REVIEW', 'ADDITIONAL_INFORMATION_REQUIRED', 'reviewer2@scholarconnect.gov.in', 'Please re-upload a clear gazetted Bonafide Certificate with college stamp and seal.', '2026-02-15 15:45:00')
ON DUPLICATE KEY UPDATE changed_by=VALUES(changed_by);

-- 7. REVIEWS SEED
INSERT INTO reviews (id, application_id, reviewer_id, comments, recommendation, reviewed_at)
VALUES
(1, 2, 2, 'Candidate has strong academic performance (79.2%). Aadhaar verified. Income certificate under verification.', 'RECOMMEND_APPROVAL', '2026-02-14 11:30:00'),
(2, 3, 2, 'SC certificate authenticated via state portal. Bonafide verified with DTU registrar. Recommend sanction of full scholarship.', 'RECOMMEND_APPROVAL', '2026-01-27 15:00:00'),
(3, 4, 3, 'Submitted income tax/Tahsildar documents show household annual earnings of Rs 2,90,000, which is higher than maximum ceiling of Rs 2,50,000.', 'RECOMMEND_REJECTION', '2026-01-28 14:20:00'),
(4, 5, 3, 'Bonafide certificate has blurred signature and missing official seal. Requesting clear student bonafide.', 'REQUEST_MORE_INFO', '2026-02-15 15:40:00')
ON DUPLICATE KEY UPDATE recommendation=VALUES(recommendation);

-- 8. NOTIFICATIONS SEED
INSERT INTO notifications (id, user_id, title, message, is_read, created_at)
VALUES
(1, 4, 'Application Received', 'Your application (Ref: SC-2026-00101) for National Merit Scholarship Scheme 2026 has been successfully received.', 0, '2026-02-10 10:30:00'),
(2, 5, 'Review In Progress', 'Your application (Ref: SC-2026-00102) is currently under review by Dr. Arvinder Singh.', 0, '2026-02-14 11:20:00'),
(3, 6, 'Scholarship Approved!', 'Congratulations! Your application (Ref: SC-2026-00103) for Post-Matric Financial Assistance has been approved for Rs 75,000.', 1, '2026-01-28 16:30:00'),
(4, 7, 'Application Status Update', 'Your application (Ref: SC-2026-00104) has been rejected. Reason: Annual family income exceeded ceiling threshold.', 1, '2026-01-29 12:10:00'),
(5, 8, 'Action Required: Re-upload Document', 'Please re-upload a clear gazetted Bonafide Certificate with college stamp for Ref: SC-2026-00105.', 0, '2026-02-15 15:45:00')
ON DUPLICATE KEY UPDATE title=VALUES(title);

