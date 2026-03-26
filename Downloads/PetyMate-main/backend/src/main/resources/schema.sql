-- ============================================================
-- PetyMate Database Schema — MySQL 8.0
-- All tables prefixed with pety_
-- ============================================================

CREATE DATABASE IF NOT EXISTS petymate_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE petymate_db;

-- 1. Users
CREATE TABLE pety_users (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                 VARCHAR(100)  NOT NULL,
    email                VARCHAR(150)  NOT NULL UNIQUE,
    password_hash        VARCHAR(255)  NOT NULL,
    phone                VARCHAR(15),
    city                 VARCHAR(100),
    state                VARCHAR(100),
    pincode              VARCHAR(10),
    profile_photo_url    VARCHAR(500),
    role                 ENUM('USER','ADMIN') DEFAULT 'USER',
    subscription_tier    ENUM('FREE','BASIC','PREMIUM') DEFAULT 'FREE',
    subscription_expiry  DATETIME,
    refresh_token        TEXT,
    is_verified          BOOLEAN DEFAULT FALSE,
    is_banned            BOOLEAN DEFAULT FALSE,
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email),
    INDEX idx_users_city (city),
    INDEX idx_users_role (role)
) ENGINE=InnoDB;

-- 2. Pets
CREATE TABLE pety_pets (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id             BIGINT NOT NULL,
    name                 VARCHAR(100),
    species              ENUM('DOG','CAT','RABBIT','BIRD','FISH','HAMSTER','REPTILE','OTHER'),
    breed                VARCHAR(100),
    age_months           INT,
    gender               ENUM('MALE','FEMALE'),
    color                VARCHAR(50),
    weight_kg            DECIMAL(5,2),
    vaccination_status   BOOLEAN DEFAULT FALSE,
    neutered             BOOLEAN DEFAULT FALSE,
    pedigree_certified   BOOLEAN DEFAULT FALSE,
    has_own_space        BOOLEAN DEFAULT FALSE,
    health_status        TEXT,
    bio                  TEXT,
    city                 VARCHAR(100),
    state                VARCHAR(100),
    pincode              VARCHAR(10),
    latitude             DECIMAL(10,8),
    longitude            DECIMAL(11,8),
    listing_type         ENUM('MATING','SALE','ADOPTION'),
    price                DECIMAL(10,2),
    status               ENUM('ACTIVE','INACTIVE','SOLD') DEFAULT 'ACTIVE',
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_pets_owner FOREIGN KEY (owner_id) REFERENCES pety_users(id) ON DELETE CASCADE,
    INDEX idx_pets_listing (species, listing_type, city, status),
    INDEX idx_pets_location (latitude, longitude),
    INDEX idx_pets_owner (owner_id),
    INDEX idx_pets_status (status)
) ENGINE=InnoDB;

-- 3. Pet Photos
CREATE TABLE pety_pet_photos (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id       BIGINT NOT NULL,
    photo_url    VARCHAR(500) NOT NULL,
    is_primary   BOOLEAN DEFAULT FALSE,
    order_index  INT DEFAULT 0,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_photos_pet FOREIGN KEY (pet_id) REFERENCES pety_pets(id) ON DELETE CASCADE,
    INDEX idx_photos_pet (pet_id)
) ENGINE=InnoDB;

-- 4. Match Requests
CREATE TABLE pety_match_requests (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    requester_id       BIGINT NOT NULL,
    receiver_id        BIGINT NOT NULL,
    requester_pet_id   BIGINT NOT NULL,
    receiver_pet_id    BIGINT NOT NULL,
    status             ENUM('PENDING','ACCEPTED','REJECTED') DEFAULT 'PENDING',
    message            TEXT,
    unlocked           BOOLEAN DEFAULT FALSE,
    unlock_payment_id  VARCHAR(200),
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_match_requester FOREIGN KEY (requester_id) REFERENCES pety_users(id),
    CONSTRAINT fk_match_receiver FOREIGN KEY (receiver_id) REFERENCES pety_users(id),
    CONSTRAINT fk_match_req_pet FOREIGN KEY (requester_pet_id) REFERENCES pety_pets(id),
    CONSTRAINT fk_match_rec_pet FOREIGN KEY (receiver_pet_id) REFERENCES pety_pets(id),
    UNIQUE KEY uk_match_pets (requester_pet_id, receiver_pet_id),
    INDEX idx_match_requester (requester_id),
    INDEX idx_match_receiver (receiver_id),
    INDEX idx_match_status (status)
) ENGINE=InnoDB;

-- 5. Subscriptions
CREATE TABLE pety_subscriptions (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id               BIGINT NOT NULL,
    plan                  ENUM('BASIC','PREMIUM') NOT NULL,
    razorpay_order_id     VARCHAR(200),
    razorpay_payment_id   VARCHAR(200),
    razorpay_signature    VARCHAR(500),
    amount                DECIMAL(10,2),
    currency              VARCHAR(10) DEFAULT 'INR',
    status                ENUM('PENDING','ACTIVE','EXPIRED','CANCELLED') DEFAULT 'PENDING',
    started_at            DATETIME,
    expires_at            DATETIME,
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_sub_user FOREIGN KEY (user_id) REFERENCES pety_users(id),
    INDEX idx_sub_user (user_id),
    INDEX idx_sub_status (status)
) ENGINE=InnoDB;

-- 6. Products
CREATE TABLE pety_products (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(200) NOT NULL,
    category        ENUM('FOOD','TOYS','GROOMING','MEDICINE','ACCESSORIES','HOUSING') NOT NULL,
    species_tags    VARCHAR(200),
    description     TEXT,
    price           DECIMAL(10,2) NOT NULL,
    original_price  DECIMAL(10,2),
    stock_qty       INT DEFAULT 0,
    brand           VARCHAR(100),
    photo_url       VARCHAR(500),
    rating          DECIMAL(3,2) DEFAULT 0.00,
    review_count    INT DEFAULT 0,
    is_featured     BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_product_category (category, species_tags),
    INDEX idx_product_featured (is_featured),
    INDEX idx_product_brand (brand)
) ENGINE=InnoDB;

-- 7. Orders
CREATE TABLE pety_orders (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id               BIGINT NOT NULL,
    razorpay_order_id     VARCHAR(200),
    razorpay_payment_id   VARCHAR(200),
    razorpay_signature    VARCHAR(500),
    total_amount          DECIMAL(10,2),
    status                ENUM('PENDING','PAID','PROCESSING','SHIPPED','DELIVERED','CANCELLED') DEFAULT 'PENDING',
    shipping_name         VARCHAR(100),
    shipping_phone        VARCHAR(15),
    shipping_address      TEXT,
    shipping_city         VARCHAR(100),
    shipping_state        VARCHAR(100),
    shipping_pincode      VARCHAR(10),
    tracking_number       VARCHAR(100),
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES pety_users(id),
    INDEX idx_order_user (user_id),
    INDEX idx_order_status (status)
) ENGINE=InnoDB;

-- 8. Order Items
CREATE TABLE pety_order_items (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id     BIGINT NOT NULL,
    product_id   BIGINT NOT NULL,
    qty          INT NOT NULL,
    unit_price   DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_oi_order FOREIGN KEY (order_id) REFERENCES pety_orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_oi_product FOREIGN KEY (product_id) REFERENCES pety_products(id),
    INDEX idx_oi_order (order_id)
) ENGINE=InnoDB;

-- 9. Vets
CREATE TABLE pety_vets (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                 VARCHAR(150) NOT NULL,
    specialization       VARCHAR(200),
    qualification        VARCHAR(300),
    experience_years     INT,
    city                 VARCHAR(100),
    state                VARCHAR(100),
    pincode              VARCHAR(10),
    phone                VARCHAR(15),
    email                VARCHAR(150),
    photo_url            VARCHAR(500),
    consultation_fee     DECIMAL(10,2),
    available_days       VARCHAR(100),
    available_hours      VARCHAR(50),
    rating               DECIMAL(3,2) DEFAULT 0.00,
    review_count         INT DEFAULT 0,
    total_appointments   INT DEFAULT 0,
    is_verified          BOOLEAN DEFAULT FALSE,
    bio                  TEXT,
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_vet_city (city),
    INDEX idx_vet_verified (is_verified),
    INDEX idx_vet_specialization (specialization)
) ENGINE=InnoDB;

-- 10. Vet Appointments
CREATE TABLE pety_vet_appointments (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id               BIGINT NOT NULL,
    vet_id                BIGINT NOT NULL,
    pet_id                BIGINT NOT NULL,
    appointment_date      DATE NOT NULL,
    appointment_time      TIME NOT NULL,
    mode                  ENUM('ONLINE','CLINIC') NOT NULL,
    status                ENUM('PENDING','CONFIRMED','COMPLETED','CANCELLED') DEFAULT 'PENDING',
    notes                 TEXT,
    vet_notes             TEXT,
    total_fee             DECIMAL(10,2),
    razorpay_order_id     VARCHAR(200),
    razorpay_payment_id   VARCHAR(200),
    razorpay_signature    VARCHAR(500),
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_appt_user FOREIGN KEY (user_id) REFERENCES pety_users(id),
    CONSTRAINT fk_appt_vet FOREIGN KEY (vet_id) REFERENCES pety_vets(id),
    CONSTRAINT fk_appt_pet FOREIGN KEY (pet_id) REFERENCES pety_pets(id),
    INDEX idx_appt_user (user_id),
    INDEX idx_appt_vet (vet_id),
    INDEX idx_appt_date (appointment_date, appointment_time)
) ENGINE=InnoDB;

-- 11. Trainers
CREATE TABLE pety_trainers (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                 VARCHAR(150) NOT NULL,
    specialization       ENUM('OBEDIENCE','AGILITY','BEHAVIOR_CORRECTION','PUPPY_TRAINING',
                              'TRICK_TRAINING','THERAPY_DOG','PROTECTION','MULTI') NOT NULL,
    species_expertise    VARCHAR(200),
    experience_years     INT,
    certification        VARCHAR(300),
    bio                  TEXT,
    city                 VARCHAR(100),
    state                VARCHAR(100),
    pincode              VARCHAR(10),
    phone                VARCHAR(15),
    email                VARCHAR(150),
    photo_url            VARCHAR(500),
    session_fee_per_hour DECIMAL(10,2),
    session_modes        VARCHAR(200),
    available_days       VARCHAR(100),
    available_hours      VARCHAR(50),
    rating               DECIMAL(3,2) DEFAULT 0.00,
    review_count         INT DEFAULT 0,
    total_sessions       INT DEFAULT 0,
    is_verified          BOOLEAN DEFAULT FALSE,
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_trainer_city (city),
    INDEX idx_trainer_verified (is_verified),
    INDEX idx_trainer_specialization (specialization)
) ENGINE=InnoDB;

-- 12. Training Sessions
CREATE TABLE pety_training_sessions (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id               BIGINT NOT NULL,
    trainer_id            BIGINT NOT NULL,
    pet_id                BIGINT NOT NULL,
    session_date          DATE NOT NULL,
    session_time          TIME NOT NULL,
    duration_hours        INT DEFAULT 1,
    mode                  ENUM('HOME_VISIT','TRAINING_CENTER','ONLINE','GROUP_CLASS') NOT NULL,
    focus_area            VARCHAR(200),
    pet_current_issues    TEXT,
    status                ENUM('PENDING','CONFIRMED','COMPLETED','CANCELLED') DEFAULT 'PENDING',
    total_fee             DECIMAL(10,2),
    trainer_notes         TEXT,
    razorpay_order_id     VARCHAR(200),
    razorpay_payment_id   VARCHAR(200),
    razorpay_signature    VARCHAR(500),
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ts_user FOREIGN KEY (user_id) REFERENCES pety_users(id),
    CONSTRAINT fk_ts_trainer FOREIGN KEY (trainer_id) REFERENCES pety_trainers(id),
    CONSTRAINT fk_ts_pet FOREIGN KEY (pet_id) REFERENCES pety_pets(id),
    INDEX idx_ts_user (user_id),
    INDEX idx_ts_trainer (trainer_id),
    INDEX idx_ts_date (session_date, session_time)
) ENGINE=InnoDB;

-- 13. Training Packages
CREATE TABLE pety_training_packages (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    trainer_id      BIGINT NOT NULL,
    name            VARCHAR(150) NOT NULL,
    description     TEXT,
    sessions_count  INT NOT NULL,
    price           DECIMAL(10,2) NOT NULL,
    validity_days   INT NOT NULL,
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_tp_trainer FOREIGN KEY (trainer_id) REFERENCES pety_trainers(id),
    INDEX idx_tp_trainer (trainer_id)
) ENGINE=InnoDB;

-- 14. User Packages
CREATE TABLE pety_user_packages (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id              BIGINT NOT NULL,
    package_id           BIGINT NOT NULL,
    trainer_id           BIGINT NOT NULL,
    sessions_remaining   INT NOT NULL,
    razorpay_payment_id  VARCHAR(200),
    expires_at           DATE,
    purchased_at         DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_up_user FOREIGN KEY (user_id) REFERENCES pety_users(id),
    CONSTRAINT fk_up_package FOREIGN KEY (package_id) REFERENCES pety_training_packages(id),
    CONSTRAINT fk_up_trainer FOREIGN KEY (trainer_id) REFERENCES pety_trainers(id),
    INDEX idx_up_user (user_id)
) ENGINE=InnoDB;

-- 15. Reviews
CREATE TABLE pety_reviews (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    reviewer_id   BIGINT NOT NULL,
    target_type   ENUM('VET','PRODUCT','TRAINER','PET_LISTING') NOT NULL,
    target_id     BIGINT NOT NULL,
    rating        INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment       TEXT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_user FOREIGN KEY (reviewer_id) REFERENCES pety_users(id),
    UNIQUE KEY uk_review (reviewer_id, target_type, target_id),
    INDEX idx_review_target (target_type, target_id)
) ENGINE=InnoDB;

-- 16. Chatbot Sessions
CREATE TABLE pety_chatbot_sessions (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT,
    session_token   VARCHAR(200) UNIQUE NOT NULL,
    messages_json   LONGTEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_chat_user FOREIGN KEY (user_id) REFERENCES pety_users(id),
    INDEX idx_chat_token (session_token)
) ENGINE=InnoDB;
