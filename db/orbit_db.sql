-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 26, 2026 at 08:15 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `orbit_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `chat_members`
--

CREATE TABLE `chat_members` (
  `conversation_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `nickname` varchar(50) DEFAULT NULL,
  `role` enum('MEMBER','ADMIN') DEFAULT 'MEMBER',
  `joined_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `left_at` timestamp NULL DEFAULT NULL,
  `encrypted_key` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `chat_members`
--

INSERT INTO `chat_members` (`conversation_id`, `user_id`, `nickname`, `role`, `joined_at`, `left_at`, `encrypted_key`) VALUES
(10, 5, NULL, 'MEMBER', '2026-03-26 18:54:53', NULL, NULL),
(10, 6, NULL, 'MEMBER', '2026-03-26 18:54:53', NULL, NULL),
(16, 5, NULL, 'MEMBER', '2026-03-26 19:03:55', NULL, NULL),
(16, 7, NULL, 'MEMBER', '2026-03-26 19:03:55', NULL, NULL),
(17, 6, NULL, 'MEMBER', '2026-03-26 19:08:21', NULL, NULL),
(17, 7, NULL, 'MEMBER', '2026-03-26 19:08:21', NULL, NULL),
(18, 5, NULL, 'MEMBER', '2026-03-26 19:09:19', NULL, 'W5xN/UHxhH9bCVjYZ+jx45Q6n4RI8pUS6k5GazoM+Qo='),
(18, 6, NULL, 'MEMBER', '2026-03-26 19:09:19', NULL, 'zCkTc9BgOuFd+bOzqfURQzk2qwguC+yIO7mFbyF1f8A='),
(18, 7, NULL, 'MEMBER', '2026-03-26 19:09:19', NULL, 'mfPIGtHStL/zORiUisPKkllQSY/KQUuJ/Ja4w+rC8Gk=');

-- --------------------------------------------------------

--
-- Table structure for table `conversations`
--

CREATE TABLE `conversations` (
  `id` int(11) NOT NULL,
  `type` varchar(50) DEFAULT NULL,
  `group_name` varchar(100) DEFAULT NULL,
  `group_photo_url` varchar(255) DEFAULT NULL,
  `custom_emoji` varchar(10) DEFAULT '?',
  `created_by` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `conversations`
--

INSERT INTO `conversations` (`id`, `type`, `group_name`, `group_photo_url`, `custom_emoji`, `created_by`, `created_at`) VALUES
(10, 'DIRECT', NULL, NULL, '👍', NULL, '2026-03-26 18:54:53'),
(16, 'DIRECT', NULL, NULL, '👍', NULL, '2026-03-26 19:03:55'),
(17, 'DIRECT', NULL, NULL, '👍', NULL, '2026-03-26 19:08:21'),
(18, 'GROUP', 'da', NULL, '👍', NULL, '2026-03-26 19:09:19');

-- --------------------------------------------------------

--
-- Table structure for table `friendships`
--

CREATE TABLE `friendships` (
  `id` int(11) NOT NULL,
  `requester_id` int(11) NOT NULL,
  `receiver_id` int(11) NOT NULL,
  `status` enum('PENDING','ACCEPTED','BLOCKED') DEFAULT 'PENDING',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `friendships`
--

INSERT INTO `friendships` (`id`, `requester_id`, `receiver_id`, `status`, `created_at`, `updated_at`) VALUES
(10, 7, 5, 'ACCEPTED', '2026-03-26 18:49:53', '2026-03-26 18:50:06'),
(11, 6, 5, 'ACCEPTED', '2026-03-26 18:51:18', '2026-03-26 18:51:23'),
(12, 7, 6, 'ACCEPTED', '2026-03-26 18:52:07', '2026-03-26 18:52:12');

-- --------------------------------------------------------

--
-- Table structure for table `messages`
--

CREATE TABLE `messages` (
  `id` int(11) NOT NULL,
  `conversation_id` int(11) NOT NULL,
  `sender_id` int(11) NOT NULL,
  `encrypted_content` text NOT NULL,
  `attachment_url` varchar(255) DEFAULT NULL,
  `is_pinned` tinyint(1) DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `messages`
--

INSERT INTO `messages` (`id`, `conversation_id`, `sender_id`, `encrypted_content`, `attachment_url`, `is_pinned`, `created_at`) VALUES
(26, 10, 6, '1nshM0nz4TNKGsRtxYd9YA==', NULL, 0, '2026-03-26 19:08:25'),
(27, 10, 5, 'fFK+c0/nqh17ZdQwbdUaFA==', NULL, 0, '2026-03-26 19:08:48'),
(28, 18, 6, 'ZKxn8Z0kYm82IlODnZ351Q==', NULL, 0, '2026-03-26 19:09:31'),
(29, 18, 5, '5B5Cb1LCevOL8L5khSA1sw==', NULL, 0, '2026-03-26 19:09:37'),
(30, 18, 6, 'RU0jkN7MD21YDFDJFdLIOg==', NULL, 0, '2026-03-26 19:09:43'),
(31, 18, 5, 'xEgXeLkbqf/KIFjwcNLPeQ==', NULL, 0, '2026-03-26 19:10:24'),
(32, 18, 7, 'YjQEiXz+zpf8Jn3pKd8ZYjCJ0SZq6zY7KWhKtsu2GGc=', NULL, 0, '2026-03-26 19:10:54'),
(33, 18, 6, '4xzKEvKFJQHsETEJvGA3Mw==', NULL, 0, '2026-03-26 19:11:07');

-- --------------------------------------------------------

--
-- Table structure for table `message_reactions`
--

CREATE TABLE `message_reactions` (
  `message_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `reaction_emoji` varchar(10) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `posts`
--

CREATE TABLE `posts` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `content` text DEFAULT NULL,
  `media_url` varchar(255) DEFAULT NULL,
  `feeling_emoji` varchar(50) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `stories`
--

CREATE TABLE `stories` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `media_url` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `expires_at` timestamp NOT NULL DEFAULT (current_timestamp() + interval 24 hour)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `full_name` varchar(32) NOT NULL,
  `email` varchar(255) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `full_name`, `email`, `username`, `password`, `created_at`) VALUES
(5, 'Howardo', 'h@gmai.com', 'howard', '1934d8bfaed57f11f8af9175540fce4b2ff0492e6b258e73ae2f16bc8c339d4c', '2026-03-26 08:55:20'),
(6, 'kh', 'k@gmail.com', 'kh', '1fcff1fe7e601e10bb94291b44e0c531fd855f5c9335805f1f7865b576c21828', '2026-03-26 12:17:56'),
(7, 'hello', 'hi@gmail.com', 'hel', 'd6a81f224bbf2f7c22baddbd5d40730eb20cfb0b3d74e10cab61788214caceb1', '2026-03-26 17:59:36');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `chat_members`
--
ALTER TABLE `chat_members`
  ADD PRIMARY KEY (`conversation_id`,`user_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `conversations`
--
ALTER TABLE `conversations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `created_by` (`created_by`);

--
-- Indexes for table `friendships`
--
ALTER TABLE `friendships`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_friendship` (`requester_id`,`receiver_id`),
  ADD KEY `receiver_id` (`receiver_id`);

--
-- Indexes for table `messages`
--
ALTER TABLE `messages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `conversation_id` (`conversation_id`),
  ADD KEY `sender_id` (`sender_id`);

--
-- Indexes for table `message_reactions`
--
ALTER TABLE `message_reactions`
  ADD PRIMARY KEY (`message_id`,`user_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `posts`
--
ALTER TABLE `posts`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `stories`
--
ALTER TABLE `stories`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `conversations`
--
ALTER TABLE `conversations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `friendships`
--
ALTER TABLE `friendships`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `messages`
--
ALTER TABLE `messages`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- AUTO_INCREMENT for table `posts`
--
ALTER TABLE `posts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `stories`
--
ALTER TABLE `stories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `chat_members`
--
ALTER TABLE `chat_members`
  ADD CONSTRAINT `chat_members_ibfk_1` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `chat_members_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `conversations`
--
ALTER TABLE `conversations`
  ADD CONSTRAINT `conversations_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `friendships`
--
ALTER TABLE `friendships`
  ADD CONSTRAINT `friendships_ibfk_1` FOREIGN KEY (`requester_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `friendships_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `messages`
--
ALTER TABLE `messages`
  ADD CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `message_reactions`
--
ALTER TABLE `message_reactions`
  ADD CONSTRAINT `message_reactions_ibfk_1` FOREIGN KEY (`message_id`) REFERENCES `messages` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `message_reactions_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `posts`
--
ALTER TABLE `posts`
  ADD CONSTRAINT `posts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `stories`
--
ALTER TABLE `stories`
  ADD CONSTRAINT `stories_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
