CREATE DATABASE comp3005_final;

\c comp3005_final

CREATE TABLE equipment (
       equipment_id SERIAL PRIMARY KEY,
       equipment_name TEXT NOT NULL,
       last_maintained_date DATE NOT NULL
);

CREATE TABLE members (
     member_id SERIAL PRIMARY KEY,
     first_name TEXT NOT NULL,
     last_name TEXT NOT NULL,
     fee INTEGER NOT NULL,
     registration_date DATE
);

CREATE TABLE trainers (
      trainer_id SERIAL PRIMARY KEY,
      first_name TEXT NOT NULL,
      last_name TEXT NOT NULL
);

CREATE TABLE billing (
     billing_id SERIAL PRIMARY KEY,
     member_id INT NOT NULL,
     amount_owed INTEGER,
     payment_date_due DATE NOT NULL,
     payment_status TEXT NOT NULL,
     FOREIGN KEY (member_id) REFERENCES members(member_id)
);

CREATE TABLE profiles (
    profile_id SERIAL PRIMARY KEY,
    member_id INT NOT NULL,
    mem_weight INTEGER NOT NULL,
    mem_height INTEGER NOT NULL,
    user_name TEXT NOT NULL,
    user_pass TEXT NOT NULL,
    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);

CREATE TABLE availability (
    availability_id SERIAL PRIMARY KEY,
    trainer_id INT REFERENCES trainers(trainer_id),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    week_day TEXT NOT NULL
);

CREATE TABLE rooms (
   room_id SERIAL PRIMARY KEY,
   room_capacity INTEGER NOT NULL
);

CREATE TABLE sessions (
      session_id SERIAL PRIMARY KEY,
      room_id INT NOT NULL,
      trainer_id INT NOT NULL,
      start_time TIME NOT NULL,
      end_time TIME NOT NULL,
      week_day TEXT NOT NULL,
      capacity INTEGER,
      current INTEGER,
      FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE,
      FOREIGN KEY (trainer_id) REFERENCES trainers(trainer_id) ON DELETE CASCADE
);

CREATE TABLE goals (
   goal_id SERIAL PRIMARY KEY,
   profile_id INT NOT NULL,
   goal_weight INTEGER NOT NULL,
   goal_reps INTEGER NOT NULL,
   goal_sets INTEGER NOT NULL,
   FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE
);