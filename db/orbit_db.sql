-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 26, 2026 at 08:29 PM
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
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `avatar` longtext DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `full_name`, `email`, `username`, `password`, `created_at`, `avatar`) VALUES
(5, 'Howardo', 'h@gmai.com', 'howard', '1934d8bfaed57f11f8af9175540fce4b2ff0492e6b258e73ae2f16bc8c339d4c', '2026-03-26 08:55:20', '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'),
(6, 'kh', 'k@gmail.com', 'kh', '1fcff1fe7e601e10bb94291b44e0c531fd855f5c9335805f1f7865b576c21828', '2026-03-26 12:17:56', NULL),
(7, 'hello', 'hi@gmail.com', 'hel', 'd6a81f224bbf2f7c22baddbd5d40730eb20cfb0b3d74e10cab61788214caceb1', '2026-03-26 17:59:36', NULL);

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
