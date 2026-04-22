CREATE TABLE schedules (
    schedule_id VARCHAR(36) PRIMARY KEY,
    class_name VARCHAR(255) NOT NULL,
    class_type VARCHAR(100) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    room_id VARCHAR(36) NOT NULL,
    room_name VARCHAR(255) NOT NULL,
    room_capacity INTEGER NOT NULL,
    trainer_id VARCHAR(36) NOT NULL,
    max_capacity INTEGER NOT NULL,
    enrolled_count INTEGER NOT NULL,
    class_session_id VARCHAR(36) NOT NULL UNIQUE,
    session_date DATE NOT NULL,
    session_status VARCHAR(50) NOT NULL
);

INSERT INTO schedules (
    schedule_id,
    class_name,
    class_type,
    start_time,
    end_time,
    room_id,
    room_name,
    room_capacity,
    trainer_id,
    max_capacity,
    enrolled_count,
    class_session_id,
    session_date,
    session_status
)
VALUES
    ('81000000-0000-0000-0000-000000000001', 'Sunrise Yoga', 'Yoga', '2026-04-10 09:00:00', '2026-04-10 10:00:00', 'r101', 'Studio A', 20, '11111111-1111-1111-1111-111111111111', 20, 1, 'a1a1a1a1-1111-1111-1111-111111111111', '2026-04-10', 'SCHEDULED'),
    ('81000000-0000-0000-0000-000000000002', 'Power Spin', 'Spin', '2026-04-10 10:15:00', '2026-04-10 11:00:00', 'r102', 'Cycle Room', 16, '22222222-2222-2222-2222-222222222222', 16, 1, 'b2b2b2b2-2222-2222-2222-222222222222', '2026-04-10', 'SCHEDULED'),
    ('81000000-0000-0000-0000-000000000003', 'Boxing Basics', 'Boxing', '2026-04-11 08:30:00', '2026-04-11 09:30:00', 'r103', 'Combat Room', 18, '33333333-3333-3333-3333-333333333333', 18, 1, 'c3c3c3c3-3333-3333-3333-333333333333', '2026-04-11', 'SCHEDULED'),
    ('81000000-0000-0000-0000-000000000004', 'Pilates Flow', 'Pilates', '2026-04-12 14:00:00', '2026-04-12 15:00:00', 'r101', 'Studio A', 20, '44444444-4444-4444-4444-444444444444', 20, 0, 'd4d4d4d4-4444-4444-4444-444444444444', '2026-04-12', 'CANCELLED'),
    ('81000000-0000-0000-0000-000000000005', 'Strength Circuit', 'Strength', '2026-04-13 11:00:00', '2026-04-13 12:00:00', 'r104', 'Weights Room', 24, '55555555-5555-5555-5555-555555555555', 24, 1, 'e5e5e5e5-5555-5555-5555-555555555555', '2026-04-13', 'SCHEDULED'),
    ('81000000-0000-0000-0000-000000000006', 'Lunch HIIT', 'HIIT', '2026-04-13 12:15:00', '2026-04-13 13:00:00', 'r104', 'Weights Room', 24, '66666666-6666-6666-6666-666666666666', 24, 0, 'f6f6f6f6-6666-6666-6666-666666666666', '2026-04-13', 'SCHEDULED'),
    ('81000000-0000-0000-0000-000000000007', 'Dance Cardio', 'Dance', '2026-04-14 16:00:00', '2026-04-14 17:00:00', 'r105', 'Studio B', 18, '77777777-7777-7777-7777-777777777777', 18, 1, 'a7a7a7a7-7777-7777-7777-777777777777', '2026-04-14', 'SCHEDULED');
