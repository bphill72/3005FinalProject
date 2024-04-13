\c comp3005_final

INSERT INTO members (first_name, last_name, fee, registration_date) VALUES
('John', 'Doe', '10', '2023-09-01'),
('Jane', 'Smith', '20', '2023-09-01'),
('Jim', 'Beam', '30', '2023-09-02');

INSERT INTO equipment (equipment_name, last_maintained_date) VALUES
('Dumbbells', '2023-09-01'),
('Benches', '2023-09-01'),
('Leg press', '2023-09-02');

INSERT INTO profiles (member_id, mem_weight, mem_height, user_name, user_pass) VALUES
(1, 150, 177, 'john_doe', 'password1'),
(2, 130, 165, 'jane_smith', 'password2'),
(3, 222, 173, 'jim_beam', 'password3');


INSERT INTO goals (profile_id, exercise, goal_weight, goal_reps, goal_sets, current_weight, current_reps, current_sets) VALUES
(1, 'Bench Press', 225, 5, 3, 205, 7, 3),
(1, 'Squat', 315, 8, 3, 275, 7, 3),
(1, 'Deadlift', 355, 8, 3, 285, 5, 3);


INSERT INTO goals (profile_id, exercise, goal_weight, goal_reps, goal_sets, current_weight, current_reps, current_sets) VALUES
(2, 'Bench Press', 200, 7, 4, 165, 7, 5),
(2, 'Squat', 400, 4, 2, 315, 5, 3),
(2, 'Deadlift', 285, 10, 5, 205, 12, 3);

INSERT INTO goals (profile_id, exercise, goal_weight, goal_reps, goal_sets, current_weight, current_reps, current_sets) VALUES
(3, 'Bench Press', 175, 1, 1, 135, 5, 2),
(3, 'Squat', 315, 2, 1, 275, 1, 1),
(3, 'Deadlift', 275, 1, 1, 225, 1, 1);

INSERT INTO billing (member_id, amount_owed, payment_date_due, payment_status) VALUES
(1, 10, '2023-10-01', 'Unpaid'),
(2, 20, '2023-10-01', 'Paid'),
(3, 30, '2023-10-02', 'Unpaid');

INSERT INTO sessions (room_id, trainer_id, start_time, end_time, week_day, capacity, current) VALUES
(1, 1, '08:00', '09:00', 'Monday', 10, 5),
(2, 2, '10:00', '11:00', 'Tuesday', 15, 8),
(3, 3, '12:00', '13:00', 'Friday', 20, 10);

INSERT INTO rooms (room_capacity) VALUES
(10),
(15),
(20);

INSERT INTO trainers (first_name, last_name) VALUES
('Michael', 'Jordan'),
('Lebron', 'James'),
('Haroon', 'Rashid');

INSERT INTO availability (trainer_id, start_time, end_time, week_day) 
VALUES
(1, '00:00', '23:59', 'Monday'),
(1, '00:00', '23:59', 'Tuesday'),
(1, '00:00', '23:59', 'Wednesday'),
(1, '00:00', '23:59', 'Thursday'),
(1, '00:00', '23:59', 'Friday'),
(1, '00:00', '23:59', 'Saturday'),
(1, '00:00', '23:59', 'Sunday'),
(2, '00:00', '23:59', 'Monday'),
(2, '00:00', '23:59', 'Tuesday'),
(2, '00:00', '23:59', 'Wednesday'),
(2, '00:00', '23:59', 'Thursday'),
(2, '00:00', '23:59', 'Friday'),
(2, '00:00', '23:59', 'Saturday'),
(2, '00:00', '23:59', 'Sunday'),
(3, '00:00', '23:59', 'Monday'),
(3, '00:00', '23:59', 'Tuesday'),
(3, '00:00', '23:59', 'Wednesday'),
(3, '00:00', '23:59', 'Thursday'),
(3, '00:00', '23:59', 'Friday'),
(3, '00:00', '23:59', 'Saturday'),
(3, '00:00', '23:59', 'Sunday');
