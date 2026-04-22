CREATE TABLE trainers (
    trainer_id VARCHAR(36) PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    specialty VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL
);

CREATE TABLE trainer_certifications (
    certification_id UUID PRIMARY KEY,
    trainer_id VARCHAR(36) NOT NULL,
    certificate_name VARCHAR(255) NOT NULL,
    issued_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    CONSTRAINT fk_trainer_certifications_trainer
        FOREIGN KEY (trainer_id) REFERENCES trainers (trainer_id) ON DELETE CASCADE
);

INSERT INTO trainers (trainer_id, full_name, email, specialty, active)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'Sophia Tran', 'sophia.tran@gym.local', 'Yoga', TRUE),
    ('22222222-2222-2222-2222-222222222222', 'Marcus Bell', 'marcus.bell@gym.local', 'Spin', TRUE),
    ('33333333-3333-3333-3333-333333333333', 'Naomi Brooks', 'naomi.brooks@gym.local', 'Boxing', TRUE),
    ('44444444-4444-4444-4444-444444444444', 'Julian Park', 'julian.park@gym.local', 'Pilates', TRUE),
    ('55555555-5555-5555-5555-555555555555', 'Ava Chen', 'ava.chen@gym.local', 'Strength', TRUE),
    ('66666666-6666-6666-6666-666666666666', 'Leo Carter', 'leo.carter@gym.local', 'HIIT', TRUE),
    ('77777777-7777-7777-7777-777777777777', 'Mila Singh', 'mila.singh@gym.local', 'Dance', TRUE);

INSERT INTO trainer_certifications (certification_id, trainer_id, certificate_name, issued_date, expiry_date)
VALUES
    ('10000000-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 'Registered Yoga Instructor', '2024-01-15', '2027-01-15'),
    ('10000000-0000-0000-0000-000000000002', '22222222-2222-2222-2222-222222222222', 'Indoor Cycling Coach', '2024-02-20', '2027-02-20'),
    ('10000000-0000-0000-0000-000000000003', '33333333-3333-3333-3333-333333333333', 'BoxFit Trainer', '2024-03-10', '2027-03-10'),
    ('10000000-0000-0000-0000-000000000004', '44444444-4444-4444-4444-444444444444', 'Pilates Foundation', '2024-04-05', '2027-04-05'),
    ('10000000-0000-0000-0000-000000000005', '55555555-5555-5555-5555-555555555555', 'Strength and Conditioning', '2024-05-01', '2027-05-01'),
    ('10000000-0000-0000-0000-000000000006', '66666666-6666-6666-6666-666666666666', 'HIIT Performance', '2024-06-12', '2027-06-12'),
    ('10000000-0000-0000-0000-000000000007', '77777777-7777-7777-7777-777777777777', 'Dance Cardio Basics', '2024-07-18', '2027-07-18');
